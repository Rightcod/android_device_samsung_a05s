package org.lineageos.a05sextras;

import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class GameModeTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    private boolean isGameModeActive() {
        return MainActivity.getProp("persist.sys.game_mode", "0").equals("1");
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;
        boolean active = isGameModeActive();
        tile.setState(active ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setSubtitle(active ? "90 FPS & DND" : "Выкл");
        tile.updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean active = isGameModeActive();
        boolean target = !active;
        MainActivity.setProp("persist.sys.game_mode", target ? "1" : "0");
        try {
            Settings.Global.putInt(getContentResolver(), "heads_up_notifications_enabled", target ? 0 : 1);
        } catch (Exception ignored) {}
        updateTile();
    }
}
