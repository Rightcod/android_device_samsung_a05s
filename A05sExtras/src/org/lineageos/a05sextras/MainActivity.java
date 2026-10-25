package org.lineageos.a05sextras;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Switch;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Locale;

public class MainActivity extends Activity {

    private Switch switchRefreshRate;
    private TextView summaryRefreshRate;
    private Switch switchGameSpoof;
    private Switch switchPhotosSpoof;
    private Switch switchPifSpoof;
    private Switch switchDt2sStatusbar;
    private Switch switchDt2sLockscreen;
    private TextView textLiveCharging;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable chargingUpdater = new Runnable() {
        @Override
        public void run() {
            updateChargingInfo();
            handler.postDelayed(this, 2000);
        }
    };

    public static String getProp(String key, String def) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class, String.class);
            return (String) method.invoke(null, key, def);
        } catch (Exception e) {
            return def;
        }
    }

    public static void setProp(String key, String val) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("set", String.class, String.class);
            method.invoke(null, key, val);
        } catch (Exception ignored) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Display 90Hz
        switchRefreshRate = findViewById(R.id.switch_refresh_rate);
        summaryRefreshRate = findViewById(R.id.summary_refresh_rate);
        float peakRate = Settings.System.getFloat(getContentResolver(), "peak_refresh_rate", 90.0f);
        boolean is90 = peakRate > 70.0f;
        switchRefreshRate.setChecked(is90);
        updateRefreshRateSummary(is90);
        switchRefreshRate.setOnCheckedChangeListener((btn, isChecked) -> {
            float target = isChecked ? 90.0f : 60.0f;
            Settings.System.putFloat(getContentResolver(), "peak_refresh_rate", target);
            Settings.System.putFloat(getContentResolver(), "min_refresh_rate", isChecked ? 60.0f : 60.0f);
            updateRefreshRateSummary(isChecked);
        });

        // Gaming Spoof
        switchGameSpoof = findViewById(R.id.switch_game_spoof);
        boolean gameSpoofOn = getProp("persist.sys.spoof.games", "1").equals("1") || getProp("persist.sys.pixelprops.games", "true").equals("true");
        switchGameSpoof.setChecked(gameSpoofOn);
        switchGameSpoof.setOnCheckedChangeListener((btn, isChecked) -> {
            setProp("persist.sys.spoof.games", isChecked ? "1" : "0");
            setProp("persist.sys.pixelprops.games", isChecked ? "true" : "false");
        });

        // Photos Spoof
        switchPhotosSpoof = findViewById(R.id.switch_photos_spoof);
        boolean photosSpoofOn = getProp("persist.sys.spoof.photos", "1").equals("1") || getProp("persist.sys.pixelprops.gphotos", "true").equals("true");
        switchPhotosSpoof.setChecked(photosSpoofOn);
        switchPhotosSpoof.setOnCheckedChangeListener((btn, isChecked) -> {
            setProp("persist.sys.spoof.photos", isChecked ? "1" : "0");
            setProp("persist.sys.pixelprops.gphotos", isChecked ? "true" : "false");
        });

        // PIF Spoof
        switchPifSpoof = findViewById(R.id.switch_pif_spoof);
        boolean pifSpoofOn = getProp("persist.sys.spoof.pif", "1").equals("1") || getProp("persist.sys.pixelprops.pi", "true").equals("true");
        switchPifSpoof.setChecked(pifSpoofOn);
        switchPifSpoof.setOnCheckedChangeListener((btn, isChecked) -> {
            setProp("persist.sys.spoof.pif", isChecked ? "1" : "0");
            setProp("persist.sys.pixelprops.pi", isChecked ? "true" : "false");
        });

        // DT2S Statusbar
        switchDt2sStatusbar = findViewById(R.id.switch_dt2s_statusbar);
        int dt2sStatus = Settings.System.getInt(getContentResolver(), "double_tap_sleep_gesture", 1);
        switchDt2sStatusbar.setChecked(dt2sStatus == 1);
        switchDt2sStatusbar.setOnCheckedChangeListener((btn, isChecked) -> {
            Settings.System.putInt(getContentResolver(), "double_tap_sleep_gesture", isChecked ? 1 : 0);
        });

        // DT2S Lockscreen
        switchDt2sLockscreen = findViewById(R.id.switch_dt2s_lockscreen);
        int dt2sLock = Settings.System.getInt(getContentResolver(), "double_tap_sleep_lockscreen", 1);
        switchDt2sLockscreen.setChecked(dt2sLock == 1);
        switchDt2sLockscreen.setOnCheckedChangeListener((btn, isChecked) -> {
            Settings.System.putInt(getContentResolver(), "double_tap_sleep_lockscreen", isChecked ? 1 : 0);
        });

        // Live Charging Monitor
        textLiveCharging = findViewById(R.id.text_live_charging);
    }

    private void updateRefreshRateSummary(boolean is90) {
        summaryRefreshRate.setText(is90 ? R.string.summary_refresh_rate_90 : R.string.summary_refresh_rate_60);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(chargingUpdater);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(chargingUpdater);
    }

    private String readSysfs(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                return br.readLine();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void updateChargingInfo() {
        try {
            Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int status = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
            int temp = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) : 0;

            String statusStr = "Разряжается";
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                statusStr = "Заряжается";
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                statusStr = "Заряжено (100%)";
            }

            // Read battery sysfs
            String currStr = readSysfs("/sys/class/power_supply/battery/current_now");
            String voltStr = readSysfs("/sys/class/power_supply/battery/voltage_now");

            long currentMicro = 0;
            long voltMicro = 0;
            if (currStr != null) {
                currentMicro = Math.abs(Long.parseLong(currStr.trim()));
            }
            if (voltStr != null) {
                voltMicro = Math.abs(Long.parseLong(voltStr.trim()));
            }

            // Normalization
            if (voltMicro < 10000 && voltMicro > 0) voltMicro *= 1000000; // was in Volts
            else if (voltMicro < 10000000 && voltMicro > 10000) voltMicro *= 1000; // was in mV

            if (currentMicro < 10000 && currentMicro > 0) currentMicro *= 1000; // was in mA

            double voltageV = voltMicro / 1000000.0;
            double currentA = currentMicro / 1000000.0;
            double watts = voltageV * currentA;
            long currentMa = (long) (currentA * 1000.0);
            long voltageMv = (long) (voltageV * 1000.0);
            double tempC = temp / 10.0;

            StringBuilder sb = new StringBuilder();
            sb.append(statusStr).append(" · ").append(level).append("%\n");
            if (status == BatteryManager.BATTERY_STATUS_CHARGING && watts > 0.05) {
                sb.append(String.format(Locale.US, "Мощность: %.1f Вт (%.2f В · %d мА)\n", watts, voltageV, currentMa));
            } else if (voltageMv > 0) {
                sb.append(String.format(Locale.US, "Напряжение: %d мВ · Ток: %d мА\n", voltageMv, currentMa));
            }
            sb.append(String.format(Locale.US, "Температура батареи: %.1f °C", tempC));

            textLiveCharging.setText(sb.toString());
        } catch (Exception e) {
            textLiveCharging.setText(R.string.battery_status_unknown);
        }
    }
}
