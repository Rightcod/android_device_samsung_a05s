package io.chaldeaprjkt.gamespace;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameManager {
    private static final String PREF_NAME = "gamespace_prefs";
    private static final String KEY_GAMES = "game_packages";
    private static final String KEY_GAMEBAR = "enable_gamebar";
    private static final String KEY_INITIALIZED = "initialized";

    private static final List<String> DEFAULT_POPULAR_GAMES = Arrays.asList(
        "com.axlebolt.standoff2",
        "com.tencent.ig",
        "com.pubg.krmobile",
        "com.pubg.imobile",
        "com.vng.pubgmobile",
        "com.dts.freefireth",
        "com.dts.freefiremax",
        "com.miHoYo.GenshinImpact",
        "com.activision.callofduty.shooter",
        "com.riotgames.league.wildrift",
        "com.mojang.minecraftpe",
        "com.carxtech.sr",
        "com.epicgames.fortnite",
        "com.supercell.brawlstars",
        "com.supercell.clashroyale",
        "com.supercell.clashofclans",
        "com.roblox.client"
    );

    private final SharedPreferences prefs;
    private final PackageManager pm;

    public GameManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pm = context.getPackageManager();
        initDefaultGamesIfNeeded();
    }

    private void initDefaultGamesIfNeeded() {
        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            Set<String> games = new HashSet<>();
            // Add popular game signatures
            games.addAll(DEFAULT_POPULAR_GAMES);

            // Auto-detect games already installed on device
            try {
                List<ApplicationInfo> apps = pm.getInstalledApplications(0);
                for (ApplicationInfo app : apps) {
                    if (app.category == ApplicationInfo.CATEGORY_GAME) {
                        games.add(app.packageName);
                    }
                }
            } catch (Exception ignored) {}

            prefs.edit()
                .putStringSet(KEY_GAMES, games)
                .putBoolean(KEY_GAMEBAR, true)
                .putBoolean(KEY_INITIALIZED, true)
                .apply();
        }
    }

    public Set<String> getGames() {
        return new HashSet<>(prefs.getStringSet(KEY_GAMES, new HashSet<>()));
    }

    public void setGames(Set<String> games) {
        prefs.edit().putStringSet(KEY_GAMES, games).apply();
    }

    public void addGame(String pkg) {
        Set<String> games = getGames();
        games.add(pkg);
        setGames(games);
    }

    public void removeGame(String pkg) {
        Set<String> games = getGames();
        games.remove(pkg);
        setGames(games);
    }

    public boolean isGame(String pkg) {
        if (pkg == null) return false;
        return getGames().contains(pkg);
    }

    public boolean isFloatingGameBarEnabled() {
        return prefs.getBoolean(KEY_GAMEBAR, true);
    }

    public void setFloatingGameBarEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_GAMEBAR, enabled).apply();
    }
}
