package io.chaldeaprjkt.gamespace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GameSpaceActivity extends Activity {

    private GameManager gameManager;
    private LinearLayout gamesContainer;
    private TextView textNoGames;

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
        setContentView(R.layout.activity_gamespace);

        gameManager = new GameManager(this);
        gamesContainer = findViewById(R.id.games_container);
        textNoGames = findViewById(R.id.text_no_games);

        findViewById(R.id.btn_add_games).setOnClickListener(v -> {
            AppSelectorDialog.show(this, gameManager, this::loadGamesList);
        });

        // 1. Floating Game Bar Switch
        Switch switchGamebar = findViewById(R.id.switch_floating_gamebar);
        switchGamebar.setChecked(gameManager.isFloatingGameBarEnabled());
        switchGamebar.setOnCheckedChangeListener((btn, isChecked) -> {
            gameManager.setFloatingGameBarEnabled(isChecked);
            if (isChecked) {
                startService(new Intent(this, GameBarService.class));
            } else {
                stopService(new Intent(this, GameBarService.class));
            }
        });

        // 2. ROG Phone 6 90/120 FPS Spoof
        Switch switchGameSpoof = findViewById(R.id.switch_game_spoof);
        boolean spoofOn = getProp("persist.sys.spoof.games", "1").equals("1") || getProp("persist.sys.pixelprops.games", "true").equals("true");
        switchGameSpoof.setChecked(spoofOn);
        switchGameSpoof.setOnCheckedChangeListener((btn, isChecked) -> {
            setProp("persist.sys.spoof.games", isChecked ? "1" : "0");
            setProp("persist.sys.pixelprops.games", isChecked ? "true" : "false");
        });

        // 3. Game DND
        Switch switchGameDnd = findViewById(R.id.switch_game_dnd);
        int headsUp = Settings.Global.getInt(getContentResolver(), "heads_up_notifications_enabled", 1);
        switchGameDnd.setChecked(headsUp == 0);
        switchGameDnd.setOnCheckedChangeListener((btn, isChecked) -> {
            Settings.Global.putInt(getContentResolver(), "heads_up_notifications_enabled", isChecked ? 0 : 1);
        });

        // 4. Force 90Hz
        Switch switchForce90 = findViewById(R.id.switch_force_90hz);
        float peak = Settings.System.getFloat(getContentResolver(), "peak_refresh_rate", 90.0f);
        switchForce90.setChecked(peak > 70.0f);
        switchForce90.setOnCheckedChangeListener((btn, isChecked) -> {
            float target = isChecked ? 90.0f : 60.0f;
            Settings.System.putFloat(getContentResolver(), "peak_refresh_rate", target);
            Settings.System.putFloat(getContentResolver(), "min_refresh_rate", target);
        });

        loadGamesList();

        // Start floating overlay service if enabled
        if (gameManager.isFloatingGameBarEnabled()) {
            startService(new Intent(this, GameBarService.class));
        }
    }

    private void loadGamesList() {
        gamesContainer.removeAllViews();
        gamesContainer.addView(textNoGames);

        PackageManager pm = getPackageManager();
        Set<String> gamePackages = gameManager.getGames();
        List<GameEntry> installedGames = new ArrayList<>();

        for (String pkg : gamePackages) {
            try {
                ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                String label = pm.getApplicationLabel(ai).toString();
                Drawable icon = pm.getApplicationIcon(ai);
                installedGames.add(new GameEntry(label, pkg, icon));
            } catch (PackageManager.NameNotFoundException ignored) {}
        }

        Collections.sort(installedGames, (a, b) -> a.label.compareToIgnoreCase(b.label));

        if (installedGames.isEmpty()) {
            textNoGames.setVisibility(View.VISIBLE);
        } else {
            textNoGames.setVisibility(View.GONE);
            LayoutInflater inflater = LayoutInflater.from(this);
            for (GameEntry game : installedGames) {
                View itemView = inflater.inflate(R.layout.item_game, gamesContainer, false);
                ImageView iconView = itemView.findViewById(R.id.img_game_icon);
                TextView nameView = itemView.findViewById(R.id.text_game_name);
                TextView pkgView = itemView.findViewById(R.id.text_game_pkg);
                Button playBtn = itemView.findViewById(R.id.btn_play);

                iconView.setImageDrawable(game.icon);
                nameView.setText(game.label);
                pkgView.setText(game.packageName);

                playBtn.setOnClickListener(v -> {
                    Intent launchIntent = pm.getLaunchIntentForPackage(game.packageName);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    }
                });

                gamesContainer.addView(itemView);
            }
        }
    }

    private static class GameEntry {
        String label;
        String packageName;
        Drawable icon;

        GameEntry(String label, String packageName, Drawable icon) {
            this.label = label;
            this.packageName = packageName;
            this.icon = icon;
        }
    }
}
