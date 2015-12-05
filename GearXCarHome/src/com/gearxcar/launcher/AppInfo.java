package com.gearxcar.launcher;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String mTitle;
    public Drawable mIcon;
    public String mPackageName;
    boolean mIsGearXCarApp;
    
    AppInfo(String title, Drawable icon, String packageName, boolean isGearXCarApp) {
        mTitle = title;
        mIcon = icon;
        mPackageName = packageName;
        mIsGearXCarApp = isGearXCarApp;
    }
}
