package org.lineageos.a05sextras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "A05sBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Ensure 90Hz default
            try {
                float peak = Settings.System.getFloat(context.getContentResolver(), "peak_refresh_rate", -1.0f);
                if (peak < 0) {
                    Settings.System.putFloat(context.getContentResolver(), "peak_refresh_rate", 90.0f);
                    Settings.System.putFloat(context.getContentResolver(), "min_refresh_rate", 60.0f);
                }
            } catch (Exception ignored) {}

            // Ensure restricted_networking_mode is disabled
            try {
                Settings.Global.putInt(context.getContentResolver(), "restricted_networking_mode", 0);
            } catch (Throwable ignored) {}

            // Ensure LTE (4G) is enabled for all active SIM subscriptions
            try {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                SubscriptionManager sm = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                if (sm != null && tm != null) {
                    List<SubscriptionInfo> subs = sm.getActiveSubscriptionInfoList();
                    if (subs != null) {
                        for (SubscriptionInfo sub : subs) {
                            TelephonyManager subTm = tm.createForSubscriptionId(sub.getSubscriptionId());
                            long allowed = subTm.getAllowedNetworkTypesForReason(TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER);
                            // Ensure LTE (1 << 12 = 4096) and LTE_CA (1 << 18 = 262144) and 2G/3G are enabled
                            long desired = allowed | (1L << 12) | (1L << 18) | (1L << 14) | (1L << 15);
                            if (allowed != desired) {
                                subTm.setAllowedNetworkTypesForReason(TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER, desired);
                                Log.i(TAG, "Updated allowed network types for sub " + sub.getSubscriptionId() + ": " + allowed + " -> " + desired);
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                Log.w(TAG, "Failed to verify allowed network types: " + t.getMessage());
            }

            // Start native call audio routing service
            try {
                Log.i(TAG, "Starting CallAudioService on boot");
                context.startService(new Intent(context, CallAudioService.class));
            } catch (Throwable t) {
                Log.e(TAG, "Failed to start CallAudioService: " + t.getMessage());
            }
        }
    }
}
