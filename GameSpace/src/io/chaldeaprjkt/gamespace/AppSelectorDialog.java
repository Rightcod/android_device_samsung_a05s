package io.chaldeaprjkt.gamespace;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppSelectorDialog {

    public interface OnGamesUpdatedListener {
        void onGamesUpdated();
    }

    public static class AppItem implements Comparable<AppItem> {
        public String label;
        public String packageName;
        public Drawable icon;
        public boolean isSelected;

        @Override
        public int compareTo(AppItem other) {
            return this.label.compareToIgnoreCase(other.label);
        }
    }

    public static void show(Context context, GameManager gm, OnGamesUpdatedListener listener) {
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);

        Set<String> currentGames = gm.getGames();
        List<AppItem> appItems = new ArrayList<>();
        Set<String> seenPackages = new HashSet<>();

        for (ResolveInfo ri : resolveInfos) {
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(context.getPackageName())) continue; // Skip GameSpace itself
            if (seenPackages.contains(pkg)) continue;
            seenPackages.add(pkg);

            AppItem item = new AppItem();
            item.label = ri.loadLabel(pm).toString();
            item.packageName = pkg;
            item.icon = ri.loadIcon(pm);
            item.isSelected = currentGames.contains(pkg);
            appItems.add(item);
        }

        Collections.sort(appItems);

        ListView listView = new ListView(context);
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() { return appItems.size(); }
            @Override
            public Object getItem(int pos) { return appItems.get(pos); }
            @Override
            public long getItemId(int pos) { return pos; }
            @Override
            public View getView(int pos, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_app_select, parent, false);
                }
                AppItem item = appItems.get(pos);
                ImageView img = convertView.findViewById(R.id.select_app_icon);
                TextView title = convertView.findViewById(R.id.select_app_name);
                TextView pkg = convertView.findViewById(R.id.select_app_pkg);
                CheckBox check = convertView.findViewById(R.id.select_app_check);

                img.setImageDrawable(item.icon);
                title.setText(item.label);
                pkg.setText(item.packageName);
                check.setChecked(item.isSelected);

                convertView.setOnClickListener(v -> {
                    item.isSelected = !item.isSelected;
                    check.setChecked(item.isSelected);
                });

                return convertView;
            }
        };
        listView.setAdapter(adapter);

        new AlertDialog.Builder(context)
            .setTitle(R.string.dialog_select_games)
            .setView(listView)
            .setPositiveButton(R.string.btn_save, (dialog, which) -> {
                Set<String> newGames = new HashSet<>();
                for (AppItem it : appItems) {
                    if (it.isSelected) {
                        newGames.add(it.packageName);
                    }
                }
                gm.setGames(newGames);
                if (listener != null) {
                    listener.onGamesUpdated();
                }
            })
            .setNegativeButton(R.string.btn_cancel, null)
            .show();
    }
}
