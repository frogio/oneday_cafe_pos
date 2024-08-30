package com.example.oneday_cafe_pos;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class CheckForeground implements Application.ActivityLifecycleCallbacks {

    private static CheckForeground instance;

    public static void init(Application app) {
        if (instance == null) {
            instance = new CheckForeground();
            app.registerActivityLifecycleCallbacks(instance);
        }
    }

    public static CheckForeground _getInstance() {
        return instance;
    }

    private CheckForeground() {
    }

    private int mAppStatus;

    public int getAppStatus() {
        return mAppStatus;
    }
/*
    // check if app is return foreground
    public boolean isBackground() {
        return mAppStatus.ordinal() == AppStatus.BACKGROUND.ordinal();
    }
*/

    public static final int BACKGROUND = 0;
    public static final int RETURNED_TO_FOREGROUND = 1;
    public static final int FOREGROUND = 2;

    // running activity count
    private int running = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++running == 1) {
            mAppStatus = RETURNED_TO_FOREGROUND;
        } else if (running > 1) {
            mAppStatus = FOREGROUND;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (--running == 0) {
            mAppStatus = BACKGROUND;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }


}