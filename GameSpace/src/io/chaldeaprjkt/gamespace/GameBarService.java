package io.chaldeaprjkt.gamespace;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.Choreographer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.util.List;

public class GameBarService extends Service implements Choreographer.FrameCallback {

    private static final String CHANNEL_ID = "gamespace_channel";
    private static final int NOTIFICATION_ID = 777;

    private WindowManager windowManager;
    private GameManager gameManager;
    private View pillView;
    private View hudView;
    private WindowManager.LayoutParams pillParams;
    private WindowManager.LayoutParams hudParams;

    private boolean isPillShowing = false;
    private boolean isHudShowing = false;

    private TextView textPillFps;
    private TextView textHudFps;
    private Button btnHudDnd;
    private Button btnHud90hz;

    private long lastFrameTimeNanos = 0;
    private int frameCount = 0;
    private long fpsTimerStart = 0;
    private int currentFps = 90;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable topAppChecker = new Runnable() {
        @Override
        public void run() {
            checkTopApp();
            handler.postDelayed(this, 1500);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        gameManager = new GameManager(this);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        createNotificationChannel();
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Game Space")
            .setContentText("Game Bar service active")
            .setSmallIcon(R.drawable.ic_gamepad)
            .build();
        startForeground(NOTIFICATION_ID, notification);

        initViews();
        handler.post(topAppChecker);
        Choreographer.getInstance().postFrameCallback(this);
    }

    private void createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel(
            CHANNEL_ID,
            "Game Space Service",
            NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm != null) {
            nm.createNotificationChannel(chan);
        }
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);

        // 1. Pill View
        pillView = inflater.inflate(R.layout.gamebar_pill, null);
        textPillFps = pillView.findViewById(R.id.pill_fps_text);

        pillParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        );
        pillParams.gravity = Gravity.TOP | Gravity.START;
        pillParams.x = 0;
        pillParams.y = 400;

        // Pill touch listener (drag Y + tap)
        pillView.setOnTouchListener(new View.OnTouchListener() {
            private int initialY;
            private float initialTouchY;
            private long touchStartTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = pillParams.y;
                        initialTouchY = event.getRawY();
                        touchStartTime = System.currentTimeMillis();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        pillParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        if (isPillShowing) {
                            windowManager.updateViewLayout(pillView, pillParams);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        long duration = System.currentTimeMillis() - touchStartTime;
                        if (duration < 250 && Math.abs(event.getRawY() - initialTouchY) < 15) {
                            showHud();
                        }
                        return true;
                }
                return false;
            }
        });

        // 2. HUD View
        hudView = inflater.inflate(R.layout.gamebar_hud, null);
        textHudFps = hudView.findViewById(R.id.hud_fps_value);
        btnHudDnd = hudView.findViewById(R.id.btn_hud_dnd);
        btnHud90hz = hudView.findViewById(R.id.btn_hud_90hz);

        hudParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        );
        hudParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        hudParams.y = 120;

        // HUD buttons
        hudView.findViewById(R.id.btn_hud_close).setOnClickListener(v -> hideHud());

        hudView.findViewById(R.id.btn_hud_boost).setOnClickListener(v -> boostMemory());

        btnHudDnd.setOnClickListener(v -> toggleDnd());

        btnHud90hz.setOnClickListener(v -> toggle90Hz());

        hudView.findViewById(R.id.btn_hud_shot).setOnClickListener(v -> takeScreenshot());
    }

    private void boostMemory() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
            if (procs != null) {
                for (ActivityManager.RunningAppProcessInfo pi : procs) {
                    for (String pkg : pi.pkgList) {
                        if (!pkg.equals(getPackageName()) && !gameManager.isGame(pkg)) {
                            am.killBackgroundProcesses(pkg);
                        }
                    }
                }
            }
        }
        Toast.makeText(this, R.string.msg_ram_cleared, Toast.LENGTH_SHORT).show();
    }

    private void toggleDnd() {
        int headsUp = Settings.Global.getInt(getContentResolver(), "heads_up_notifications_enabled", 1);
        int newStatus = (headsUp == 1) ? 0 : 1;
        Settings.Global.putInt(getContentResolver(), "heads_up_notifications_enabled", newStatus);
        updateHudButtons();
    }

    private void toggle90Hz() {
        float peak = Settings.System.getFloat(getContentResolver(), "peak_refresh_rate", 90.0f);
        float target = (peak > 70.0f) ? 60.0f : 90.0f;
        Settings.System.putFloat(getContentResolver(), "peak_refresh_rate", target);
        Settings.System.putFloat(getContentResolver(), "min_refresh_rate", target);
        updateHudButtons();
    }

    private void updateHudButtons() {
        int headsUp = Settings.Global.getInt(getContentResolver(), "heads_up_notifications_enabled", 1);
        boolean dndActive = (headsUp == 0);
        btnHudDnd.setText(dndActive ? "DND: ON" : "DND: OFF");

        float peak = Settings.System.getFloat(getContentResolver(), "peak_refresh_rate", 90.0f);
        boolean is90 = (peak > 70.0f);
        btnHud90hz.setText(is90 ? "90Hz: ON" : "90Hz: OFF");
    }

    private void takeScreenshot() {
        hideHud();
        try {
            Intent intent = new Intent("android.intent.action.GLOBAL_SCREENSHOT");
            sendBroadcast(intent);
        } catch (Exception ignored) {}
    }

    private void showPill() {
        if (!isPillShowing && gameManager.isFloatingGameBarEnabled()) {
            try {
                windowManager.addView(pillView, pillParams);
                isPillShowing = true;
            } catch (Exception ignored) {}
        }
    }

    private void hidePill() {
        if (isPillShowing) {
            try {
                windowManager.removeView(pillView);
            } catch (Exception ignored) {}
            isPillShowing = false;
        }
    }

    private void showHud() {
        hidePill();
        if (!isHudShowing) {
            updateHudButtons();
            try {
                windowManager.addView(hudView, hudParams);
                isHudShowing = true;
            } catch (Exception ignored) {}
        }
    }

    private void hideHud() {
        if (isHudShowing) {
            try {
                windowManager.removeView(hudView);
            } catch (Exception ignored) {}
            isHudShowing = false;
        }
        showPill();
    }

    private String getTopPackage() {
        try {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            UsageEvents events = usm.queryEvents(time - 3000, time);
            UsageEvents.Event currentEvent = new UsageEvents.Event();
            String top = null;
            while (events.hasNextEvent()) {
                events.getNextEvent(currentEvent);
                if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                    top = currentEvent.getPackageName();
                }
            }
            return top;
        } catch (Exception e) {
            return null;
        }
    }

    private void checkTopApp() {
        String top = getTopPackage();
        if (top != null && gameManager.isGame(top)) {
            if (!isPillShowing && !isHudShowing) {
                showPill();
            }
        } else {
            if (isPillShowing) hidePill();
            if (isHudShowing) {
                try {
                    windowManager.removeView(hudView);
                } catch (Exception ignored) {}
                isHudShowing = false;
            }
        }
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (fpsTimerStart == 0) {
            fpsTimerStart = frameTimeNanos;
        }
        frameCount++;

        if (frameTimeNanos - fpsTimerStart >= 1_000_000_000L) { // 1 second
            currentFps = frameCount;
            frameCount = 0;
            fpsTimerStart = frameTimeNanos;

            if (textPillFps != null) {
                textPillFps.setText(String.valueOf(currentFps));
            }
            if (textHudFps != null) {
                textHudFps.setText(currentFps + " FPS");
            }
        }

        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(topAppChecker);
        hidePill();
        if (isHudShowing) {
            try {
                windowManager.removeView(hudView);
            } catch (Exception ignored) {}
            isHudShowing = false;
        }
    }
}
