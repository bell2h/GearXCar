package com.gearxcar.launcher;

import java.util.List;

import com.gearxcar.launcher.view.AppIconView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AppIconViewAdapter extends BaseAdapter {
    List<AppInfo> appList;

    AppIconViewAdapter(List<AppInfo> appList) {
        super();
        this.appList = appList;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return appList.size();
    }

    @Override
    public AppInfo getItem(int position) {
        // TODO Auto-generated method stub
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        AppIconView view;
        Context context = parent.getContext();
        if (convertView == null) {
            view = new AppIconView(context);
        } else {
            view = (AppIconView) convertView;
        }
        AppInfo appInfo = appList.get(position);
        view.setAppTitle(appInfo.mTitle);
        view.setAppIcon(appInfo.mIcon);
        view.setGearXCarIcon(appInfo.mIsGearXCarApp);
        view.setTag(appInfo);
        
        return view;
    }    
}
