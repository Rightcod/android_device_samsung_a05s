package org.lineageos.a05sextras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class BootReceiver extends BroadcastReceiver {
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
        }
    }
}
