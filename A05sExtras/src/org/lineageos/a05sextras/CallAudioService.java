package org.lineageos.a05sextras;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Native in-call audio routing daemon for Samsung Galaxy A05s (Qualcomm Bengal).
 *
 * Samsung's audio.primary.bengal.so Audio HAL requires explicit voice session parameters:
 *   - "g_call_sim_slot=0x01" (SIM 1, VSID 0x11c05000) or "0x02" (SIM 2, VSID 0x11dc5000)
 *   - "g_call_state=514" (Active call)
 *   - "g_call_state=1" (Idle / Call ended)
 *
 * This service monitors real-time telephony call states across both SIM slots and routes
 * audio directly via AudioManager.setParameters, including audio HAL recovery when needed.
 */
public class CallAudioService extends Service {
    private static final String TAG = "A05sCallAudioService";

    private AudioManager mAudioManager;
    private TelephonyManager mTelephonyManager;
    private SubscriptionManager mSubscriptionManager;

    private HandlerThread mWorkerThread;
    private Handler mHandler;

    private boolean mCallActive = false;
    private int mActiveSlot = 0; // 0 for SIM 1, 1 for SIM 2
    private final Map<Integer, Integer> mSlotCallStates = new HashMap<>();
    private final Map<Integer, TelephonyCallback> mCallbacks = new HashMap<>();

    private static void setProp(String key, String val) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("set", String.class, String.class);
            method.invoke(null, key, val);
        } catch (Throwable t) {
            Log.w(TAG, "setProp failed for " + key + ": " + t.getMessage());
        }
    }

    private static String getProp(String key, String def) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class, String.class);
            return (String) method.invoke(null, key, def);
        } catch (Throwable t) {
            return def;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Initializing CallAudioService");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        mWorkerThread = new HandlerThread("A05sCallAudioWorker");
        mWorkerThread.start();
        mHandler = new Handler(mWorkerThread.getLooper());

        setupSubscriptionListeners();
        mHandler.post(mWatchdogRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "CallAudioService onStartCommand");
        mHandler.post(this::refreshCallState);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "CallAudioService onDestroy");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mWorkerThread != null) {
            mWorkerThread.quitSafely();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setupSubscriptionListeners() {
        if (mSubscriptionManager == null) return;

        mSubscriptionManager.addOnSubscriptionsChangedListener(
                new SubscriptionManager.OnSubscriptionsChangedListener() {
                    @Override
                    public void onSubscriptionsChanged() {
                        mHandler.post(() -> registerTelephonyCallbacks());
                    }
                });

        registerTelephonyCallbacks();
    }

    private synchronized void registerTelephonyCallbacks() {
        if (mTelephonyManager == null || mSubscriptionManager == null) return;

        try {
            List<SubscriptionInfo> subInfos = mSubscriptionManager.getActiveSubscriptionInfoList();
            if (subInfos == null || subInfos.isEmpty()) {
                // Register default
                registerCallbackForSub(-1, 0);
                return;
            }

            for (SubscriptionInfo info : subInfos) {
                int subId = info.getSubscriptionId();
                int slotId = info.getSimSlotIndex();
                registerCallbackForSub(subId, slotId);
            }
        } catch (Throwable t) {
            Log.w(TAG, "Failed to register callbacks: " + t.getMessage());
        }
    }

    private void registerCallbackForSub(int subId, int slotId) {
        if (mCallbacks.containsKey(slotId)) {
            return;
        }

        try {
            TelephonyManager tm = (subId >= 0)
                    ? mTelephonyManager.createForSubscriptionId(subId)
                    : mTelephonyManager;

            SlotCallCallback callback = new SlotCallCallback(slotId);
            tm.registerTelephonyCallback(getMainExecutor(), callback);
            mCallbacks.put(slotId, callback);
            Log.i(TAG, "Registered TelephonyCallback for slot " + slotId + " (subId=" + subId + ")");
        } catch (Throwable t) {
            Log.w(TAG, "registerCallbackForSub error for slot " + slotId + ": " + t.getMessage());
        }
    }

    private class SlotCallCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private final int mSlot;

        public SlotCallCallback(int slot) {
            this.mSlot = slot;
        }

        @Override
        public void onCallStateChanged(int state) {
            mHandler.post(() -> handleCallStateChange(mSlot, state));
        }
    }

    private synchronized void handleCallStateChange(int slot, int state) {
        Log.d(TAG, "Slot " + slot + " state changed to " + state);
        mSlotCallStates.put(slot, state);
        evaluateRouting();
    }

    private synchronized void evaluateRouting() {
        boolean anyActive = false;
        int activeSlot = -1;

        // Check if any slot is in OFFHOOK (2)
        for (Map.Entry<Integer, Integer> entry : mSlotCallStates.entrySet()) {
            if (entry.getValue() == TelephonyManager.CALL_STATE_OFFHOOK) {
                anyActive = true;
                activeSlot = entry.getKey();
                break;
            }
        }

        // If none is OFFHOOK, check for RINGING (1)
        if (!anyActive) {
            for (Map.Entry<Integer, Integer> entry : mSlotCallStates.entrySet()) {
                if (entry.getValue() == TelephonyManager.CALL_STATE_RINGING) {
                    anyActive = true;
                    activeSlot = entry.getKey();
                    break;
                }
            }
        }

        if (anyActive) {
            if (activeSlot < 0) activeSlot = 0;
            if (!mCallActive || mActiveSlot != activeSlot) {
                Log.i(TAG, "Active call on slot " + activeSlot + " (previous active: " + mCallActive + ")");
                if (!mCallActive) {
                    // Restart audio HAL on new call to clear stuck routing (from phh call fix)
                    Log.i(TAG, "Restarting vendor.audio-hal for clean call session");
                    setProp("ctl.restart", "vendor.audio-hal");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {}
                }
                mCallActive = true;
                mActiveSlot = activeSlot;
            }
            applyRoutingParameters(mActiveSlot, true);
        } else {
            if (mCallActive) {
                Log.i(TAG, "All calls idle, resetting g_call_state=1");
                mCallActive = false;
                applyRoutingParameters(mActiveSlot, false);
            }
        }
    }

    private void applyRoutingParameters(int slot, boolean active) {
        if (mAudioManager == null) return;

        try {
            if (active) {
                String slotHex = (slot == 1) ? "0x02" : "0x01";
                Log.d(TAG, "Applying: g_call_sim_slot=" + slotHex + ", g_call_state=514");
                mAudioManager.setParameters("g_call_sim_slot=" + slotHex);
                mAudioManager.setParameters("g_call_state=514");
            } else {
                Log.d(TAG, "Applying: g_call_state=1");
                mAudioManager.setParameters("g_call_state=1");
            }
        } catch (Throwable t) {
            Log.e(TAG, "Error applying audio parameters: " + t.getMessage());
        }
    }

    private void refreshCallState() {
        if (mTelephonyManager == null) return;
        try {
            int globalState = mTelephonyManager.getCallState();
            if (globalState != TelephonyManager.CALL_STATE_IDLE && !mCallActive) {
                Log.i(TAG, "Global call state is active (" + globalState + ") while service state is idle. Forcing evaluation.");
                handleCallStateChange(0, globalState);
            }
        } catch (Throwable ignored) {}
    }

    // Safety watchdog: polls state every 500ms while a call is active (to keep routing solid)
    // and every 2500ms when idle.
    private final Runnable mWatchdogRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mCallActive) {
                    // Periodic parameter keep-alive while call is active
                    applyRoutingParameters(mActiveSlot, true);
                    refreshCallState();
                    mHandler.postDelayed(this, 500);
                } else {
                    refreshCallState();
                    mHandler.postDelayed(this, 2500);
                }
            } catch (Throwable t) {
                mHandler.postDelayed(this, 2500);
            }
        }
    };
}
