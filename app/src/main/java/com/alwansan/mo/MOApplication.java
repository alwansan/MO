package com.alwansan.mo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Process;

/**
 * Registers process-wide logging only for our disposable test application.
 * Never attempts to read Android global logcat or another app's process.
 */
public final class MOApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        DiagnosticLog.initialize(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(Activity a, Bundle state) {
                DiagnosticLog.log("ACTIVITY_CREATE", a.getClass().getSimpleName()
                        + "; restoredState=" + (state != null));
            }
            @Override public void onActivityStarted(Activity a) {
                DiagnosticLog.log("ACTIVITY_START", a.getClass().getSimpleName());
            }
            @Override public void onActivityResumed(Activity a) {
                DiagnosticLog.log("ACTIVITY_RESUME", a.getClass().getSimpleName());
            }
            @Override public void onActivityPaused(Activity a) {
                DiagnosticLog.log("ACTIVITY_PAUSE", a.getClass().getSimpleName());
            }
            @Override public void onActivityStopped(Activity a) {
                DiagnosticLog.log("ACTIVITY_STOP", a.getClass().getSimpleName());
            }
            @Override public void onActivitySaveInstanceState(Activity a, Bundle state) {
                DiagnosticLog.log("ACTIVITY_SAVE", a.getClass().getSimpleName());
            }
            @Override public void onActivityDestroyed(Activity a) {
                DiagnosticLog.log("ACTIVITY_DESTROY", a.getClass().getSimpleName());
            }
        });
        Thread.UncaughtExceptionHandler old = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, problem) -> {
            DiagnosticLog.crashNow(thread, problem);
            if (old != null) {
                old.uncaughtException(thread, problem);
            } else {
                Process.killProcess(Process.myPid());
                System.exit(10);
            }
        });
    }

    @Override public void onLowMemory() {
        DiagnosticLog.log("LOW_MEMORY", "Application.onLowMemory");
        super.onLowMemory();
    }

    @Override public void onTrimMemory(int level) {
        DiagnosticLog.log("TRIM_MEMORY", "level=" + level);
        super.onTrimMemory(level);
    }
}
