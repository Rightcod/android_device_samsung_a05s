package org.lineageos.a05sextras;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.telecom.Call;
import android.telecom.InCallService;
import android.telecom.PhoneAccountHandle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Native in-call audio routing service for Samsung Galaxy A05s (Qualcomm Bengal).
 *
 * Integrates directly with Android Telecom as an IN_CALL_SERVICE_NON_UI while maintaining
 * SEC-RIL hardware property monitoring and TelephonyCallback fallbacks.
 *
 * Samsung's audio.primary.bengal.so Audio HAL requires explicit voice session parameters:
 *   - "g_call_sim_slot=1" (SIM 1 / Slot 0, VSID 0x11c05000)
 *   - "g_call_sim_slot=2" (SIM 2 / Slot 1, VSID 0x11dc5000)
 *   - "g_call_state=514" (Active call / in-call routing enabled)
 *   - "g_call_state=1"   (Call idle / ended)
 *
 * NOTE: The Audio HAL parses "g_call_sim_slot" via atoi(), so decimal strings ("1", "2")
 * must be used. Hex strings ("0x01", "0x02") cause atoi() to return 0 and fail.
 */
public class CallAudioService extends InCallService {
    private static final String TAG = "A05sCallAudioService";

    private AudioManager mAudioManager;
    private TelephonyManager mTelephonyManager;
    private SubscriptionManager mSubscriptionManager;

    private HandlerThread mWorkerThread;
    private Handler mHandler;

    private boolean mCallActive = false;
    private int mActiveSlot = 0; // 0 for SIM 1, 1 for SIM 2

    private final List<Call> mTrackedCalls = new CopyOnWriteArrayList<>();
    private final Map<Integer, Integer> mSlotCallStates = new HashMap<>();
    private final Map<Integer, TelephonyCallback> mCallbacks = new HashMap<>();

    private static String getProp(String key, String def) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class, String.class);
            return (String) method.invoke(null, key, def);
        } catch (Throwable e) {
            return def;
        }
    }

    private final Call.Callback mCallCallback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            Log.i(TAG, "Telecom call onStateChanged: state=" + state + ", call=" + call);
            if (mHandler != null) {
                mHandler.post(() -> evaluateAllCallStates());
            }
        }

        @Override
        public void onDetailsChanged(Call call, Call.Details details) {
            if (mHandler != null) {
                mHandler.post(() -> evaluateAllCallStates());
            }
        }
    };

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
        if (mHandler != null) {
            mHandler.post(this::evaluateAllCallStates);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "CallAudioService onBind: action=" + (intent != null ? intent.getAction() : "null"));
        if (intent != null && InCallService.SERVICE_INTERFACE.equals(intent.getAction())) {
            return super.onBind(intent);
        }
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "CallAudioService onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.i(TAG, "Telecom onCallAdded: " + call);
        if (call != null) {
            mTrackedCalls.add(call);
            call.registerCallback(mCallCallback);
        }
        if (mHandler != null) {
            mHandler.post(this::evaluateAllCallStates);
        }
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.i(TAG, "Telecom onCallRemoved: " + call);
        if (call != null) {
            mTrackedCalls.remove(call);
            try {
                call.unregisterCallback(mCallCallback);
            } catch (Throwable ignored) {}
        }
        if (mHandler != null) {
            mHandler.post(this::evaluateAllCallStates);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "CallAudioService onDestroy");
        for (Call c : mTrackedCalls) {
            try {
                c.unregisterCallback(mCallCallback);
            } catch (Throwable ignored) {}
        }
        mTrackedCalls.clear();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mWorkerThread != null) {
            mWorkerThread.quitSafely();
        }
    }

    private void setupSubscriptionListeners() {
        if (mSubscriptionManager == null) return;

        mSubscriptionManager.addOnSubscriptionsChangedListener(
                new SubscriptionManager.OnSubscriptionsChangedListener() {
                    @Override
                    public void onSubscriptionsChanged() {
                        if (mHandler != null) {
                            mHandler.post(() -> registerTelephonyCallbacks());
                        }
                    }
                });

        registerTelephonyCallbacks();
    }

    private synchronized void registerTelephonyCallbacks() {
        if (mTelephonyManager == null || mSubscriptionManager == null) return;

        try {
            List<SubscriptionInfo> subInfos = mSubscriptionManager.getActiveSubscriptionInfoList();
            if (subInfos == null || subInfos.isEmpty()) {
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
            Log.d(TAG, "TelephonyCallback slot " + mSlot + " state=" + state);
            if (mHandler != null) {
                mHandler.post(() -> {
                    mSlotCallStates.put(mSlot, state);
                    evaluateAllCallStates();
                });
            }
        }
    }

    private int getSlotForCall(Call call) {
        try {
            if (call != null) {
                Call.Details details = call.getDetails();
                if (details != null) {
                    PhoneAccountHandle handle = details.getAccountHandle();
                    if (handle != null && handle.getId() != null) {
                        try {
                            int subId = Integer.parseInt(handle.getId());
                            int slot = SubscriptionManager.getSlotIndex(subId);
                            if (slot >= 0) {
                                return slot;
                            }
                            if (mSubscriptionManager != null) {
                                SubscriptionInfo info = mSubscriptionManager.getActiveSubscriptionInfo(subId);
                                if (info != null) {
                                    return info.getSimSlotIndex();
                                }
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } catch (Throwable t) {
            Log.w(TAG, "getSlotForCall error: " + t.getMessage());
        }
        return -1;
    }

    private synchronized void evaluateAllCallStates() {
        boolean anyActive = false;
        int activeSlot = -1;

        // 1. Direct hardware modem RIL indicator (from Samsung libsec-ril)
        String rildSlot1 = getProp("vendor.calls.slot_id1", "0");
        String rildSlot0 = getProp("vendor.calls.slot_id0", "0");
        if ("1".equals(rildSlot1)) {
            anyActive = true;
            activeSlot = 1;
        } else if ("1".equals(rildSlot0)) {
            anyActive = true;
            activeSlot = 0;
        }

        // 2. Authoritative Telecom tracked calls (supports outgoing dialing before RIL connects)
        if (!anyActive) {
            for (Call c : mTrackedCalls) {
                int st = c.getState();
                if (st == Call.STATE_DIALING || st == Call.STATE_CONNECTING ||
                    st == Call.STATE_RINGING || st == Call.STATE_ACTIVE ||
                    st == Call.STATE_HOLDING || st == Call.STATE_DISCONNECTING) {
                    anyActive = true;
                    int slot = getSlotForCall(c);
                    if (slot >= 0) {
                        activeSlot = slot;
                        break;
                    }
                }
            }
        }

        // 3. TelephonyCallback states (with sticky slot protection to prevent duplicate callback stealing)
        if (!anyActive) {
            // If already in a call on activeSlot, check if that slot is still reporting active
            if (mCallActive && mActiveSlot >= 0) {
                Integer currentSlotState = mSlotCallStates.get(mActiveSlot);
                if (currentSlotState != null && (currentSlotState == TelephonyManager.CALL_STATE_OFFHOOK || currentSlotState == TelephonyManager.CALL_STATE_RINGING)) {
                    anyActive = true;
                    activeSlot = mActiveSlot;
                }
            }

            if (!anyActive) {
                for (Map.Entry<Integer, Integer> entry : mSlotCallStates.entrySet()) {
                    if (entry.getValue() == TelephonyManager.CALL_STATE_OFFHOOK ||
                        entry.getValue() == TelephonyManager.CALL_STATE_RINGING) {
                        anyActive = true;
                        activeSlot = entry.getKey();
                        break;
                    }
                }
            }
        }

        // 4. Direct subscription call state query fallback
        if (!anyActive && mSubscriptionManager != null && mTelephonyManager != null) {
            try {
                List<SubscriptionInfo> subs = mSubscriptionManager.getActiveSubscriptionInfoList();
                if (subs != null) {
                    for (SubscriptionInfo sub : subs) {
                        TelephonyManager subTm = mTelephonyManager.createForSubscriptionId(sub.getSubscriptionId());
                        int st = subTm.getCallState();
                        if (st != TelephonyManager.CALL_STATE_IDLE) {
                            anyActive = true;
                            activeSlot = sub.getSimSlotIndex();
                            break;
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }

        if (anyActive) {
            if (activeSlot < 0) activeSlot = 0;
            if (!mCallActive || mActiveSlot != activeSlot) {
                Log.i(TAG, "Active call on slot " + activeSlot + " (previous active: " + mCallActive + ", prev slot: " + mActiveSlot + ")");
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
                // Must be decimal strings: "1" for SIM 1 (Slot 0), "2" for SIM 2 (Slot 1)
                String slotParam = (slot == 1) ? "2" : "1";
                Log.d(TAG, "Applying audio params: g_call_sim_slot=" + slotParam + ", g_call_state=514");
                mAudioManager.setParameters("g_call_sim_slot=" + slotParam);
                mAudioManager.setParameters("g_call_state=514");
            } else {
                Log.d(TAG, "Applying audio params: g_call_state=1");
                mAudioManager.setParameters("g_call_state=1");
            }
        } catch (Throwable t) {
            Log.e(TAG, "Error applying audio parameters: " + t.getMessage());
        }
    }

    // Safety watchdog: enforces parameters every 600ms while a call is active,
    // and checks call states / hardware properties every 1000ms when idle.
    private final Runnable mWatchdogRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mCallActive) {
                    applyRoutingParameters(mActiveSlot, true);
                    mHandler.postDelayed(this, 600);
                } else {
                    evaluateAllCallStates();
                    mHandler.postDelayed(this, 1000);
                }
            } catch (Throwable t) {
                mHandler.postDelayed(this, 1000);
            }
        }
    };
}
