package org.lineageos.a05sextras;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class A05sExtrasApp extends Application {
    private static final String TAG = "A05sExtrasApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "A05sExtrasApp created (UID=" + android.os.Process.myUid() + ", PID=" + android.os.Process.myPid() + ")");
        try {
            Intent serviceIntent = new Intent(this, CallAudioService.class);
            startService(serviceIntent);
            Log.i(TAG, "Started CallAudioService directly from Application.onCreate()");
        } catch (Throwable t) {
            Log.e(TAG, "Failed to start CallAudioService: " + t.getMessage(), t);
        }
    }
}
