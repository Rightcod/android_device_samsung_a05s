package org.lineageos.a05sextras;

import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class RefreshRateTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        float current = Settings.System.getFloat(getContentResolver(), "peak_refresh_rate", 90.0f);
        float next = (current > 70.0f) ? 60.0f : 90.0f;
        Settings.System.putFloat(getContentResolver(), "peak_refresh_rate", next);
        Settings.System.putFloat(getContentResolver(), "min_refresh_rate", next == 90.0f ? 60.0f : 60.0f);
        updateTile();
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;
        float current = Settings.System.getFloat(getContentResolver(), "peak_refresh_rate", 90.0f);
        boolean is90 = (current > 70.0f);
        tile.setState(is90 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setSubtitle(is90 ? "90 Гц" : "60 Гц");
        tile.updateTile();
    }
}
