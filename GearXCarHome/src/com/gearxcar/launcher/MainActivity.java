package com.gearxcar.launcher;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.Toast;

import com.gearxcar.launcher.R;
import com.gearxcar.launcher.view.*;

public class MainActivity extends Activity {
	public static final String TAG = "Activiy Sample";
	private static MainActivity instance = null;
	
	private Gallery mGallery;
	
	public static final String CATEGORY_GEARXCAR_APPS = "com.gearxcar.intent.category.APPS";
	
    public static String ACTION_GEAR_EVENT = "com.gearxcar.action.EVENT";
    
    public static String GEAR_EVENT_ROTARYDETENT_CW = "com.gearxcar.event.rotarydetent.cw";
    public static String GEAR_EVENT_ROTARYDETENT_CCW = "com.gearxcar.event.rotarydetent.ccw";
    public static String GEAR_EVENT_GESTURE_LEFT = "com.gearxcar.event.gesture.left";
    public static String GEAR_EVENT_GESTURE_RIGHT = "com.gearxcar.event.gesture.right";
    public static String GEAR_EVENT_GESTURE_DOWN = "com.gearxcar.event.gesture.down";
    public static String GEAR_EVENT_GESTURE_UP = "com.gearxcar.event.gesture.up";
    public static String GEAR_EVENT_GESTURE_TAP = "com.gearxcar.event.gesture.tap";
    public static String GEAR_EVENT_GESTURE_LONGPRESS = "com.gearxcar.event.gesture.longpress";
    public static String GEAR_EVENT_UNKNOWN = "com.gearxcar.event.unknown";
    
	private BroadcastReceiver mGearEventReceiver;
	
	private ComponentName mOldPreferredHome;
	private WakeLock mWakeLock;
	
	public static MainActivity getInstance() {
	    return instance;
	}
	
	public static boolean setPreferredHomeActivity (Context context, String packageName, String className) {
	    ComponentName oldPreferredActivity = getPreferredHomeActivity(context);
	    if (oldPreferredActivity != null && packageName.equals(oldPreferredActivity.getPackageName()) && className.equals(oldPreferredActivity.getClassName())) {
	       return false; }
	    if (oldPreferredActivity != null) {
	       context.getPackageManager().clearPackagePreferredActivities(oldPreferredActivity.getPackageName()); }
	    IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
	    filter.addCategory(Intent.CATEGORY_HOME);
	    filter.addCategory(Intent.CATEGORY_DEFAULT);
	    ComponentName[] currentHomeActivities = getActivitiesListByActionAndCategory(context, Intent.ACTION_MAIN, Intent.CATEGORY_HOME);
	    ComponentName newPreferredActivity = new ComponentName(packageName, className);
	    context.getPackageManager().addPreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, currentHomeActivities, newPreferredActivity);
	    return true; }

	 private static ComponentName getPreferredHomeActivity (Context context) {
	    ArrayList<IntentFilter> filters = new ArrayList<IntentFilter>();
	    List<ComponentName> componentNames = new ArrayList<ComponentName>();
	    context.getPackageManager().getPreferredActivities(filters, componentNames, null);
	    for (int i = 0; i < filters.size(); i++) {
	       IntentFilter filter = filters.get(i);
	       if (filter.hasAction(Intent.ACTION_MAIN) && filter.hasCategory(Intent.CATEGORY_HOME)) {
	          return componentNames.get(i); }}
	    return null; }
	 
	 private static ComponentName[] getActivitiesListByActionAndCategory (Context context, String action, String category) {
	     Intent queryIntent = new Intent(action);
	     queryIntent.addCategory(category);
	     List<ResolveInfo> resInfos = context.getPackageManager().queryIntentActivities(queryIntent, PackageManager.MATCH_DEFAULT_ONLY);
	     ComponentName[] componentNames = new ComponentName[resInfos.size()];
	     for (int i = 0; i < resInfos.size(); i++) {
	        ActivityInfo activityInfo = resInfos.get(i).activityInfo;
	        componentNames[i] = new ComponentName(activityInfo.packageName, activityInfo.name); }
	     return componentNames; }
	 
	 List<AppInfo> queryApps(String category, boolean isGearXCarApp) {
	     PackageManager pm = getPackageManager();
	     Intent i = new Intent(Intent.ACTION_MAIN);
	     i.addCategory(category);
	     List<ResolveInfo> rsvList = pm.queryIntentActivities(i, 0);
	     ListIterator<ResolveInfo> it = rsvList.listIterator();
	     List<AppInfo> appList = new ArrayList<AppInfo>();
	     while(it.hasNext()) {
	         ResolveInfo rsvInfo = it.next();
	         AppInfo appInfo = new AppInfo((String)rsvInfo.loadLabel(pm), rsvInfo.loadIcon(pm), 
	                 rsvInfo.activityInfo.packageName, isGearXCarApp);
	         appList.add(appInfo);
	     }
	     return appList;
	 }
	
	@SuppressWarnings("deprecation")
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		List<AppInfo> gxcApps = queryApps(CATEGORY_GEARXCAR_APPS, true);
		List<AppInfo> defaultApps = queryApps(Intent.CATEGORY_LAUNCHER, false);
		gxcApps.addAll(defaultApps);
		//PackageManager pm = getPackageManager();
		//Intent i = new Intent(Intent.ACTION_MAIN);
		//i.addCategory(CATEGORY_GEARXCAR_APPS/*Intent.CATEGORY_LAUNCHER*/);
		//List<ResolveInfo> rsvList = pm.queryIntentActivities(i, 0);
		
		AppIconViewAdapter adapter = new AppIconViewAdapter(gxcApps);
		mGallery = (Gallery) this.findViewById(R.id.gallery);
		mGallery.setAdapter(adapter);
		mGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // TODO Auto-generated method stub
                launchApp(position);
            }
		    
		});
	
		//mOldPreferredHome = this.getPreferredHomeActivity(this);
		//this.setPreferredHomeActivity(this, this.getPackageName(), this.getLocalClassName());
	
		instance = this;
		
		// only for activity
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "GearXCar");
        mWakeLock.acquire();
	}

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        instance = null;
        //this.setPreferredHomeActivity(this, mOldPreferredHome.getPackageName(), mOldPreferredHome.getClassName());
        // only for activity
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mWakeLock.release();
        super.onDestroy();
    }
	
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GEAR_EVENT);
        
        mGearEventReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                final MediaPlayer noti = MediaPlayer.create(context, R.raw.noti);
                final MediaPlayer volsound = MediaPlayer.create(context, R.raw.volsound);
                final MediaPlayer flick = MediaPlayer.create(context, R.raw.pop_drip);
                
                String event = intent.getStringExtra("event");
                if (event.equals(GEAR_EVENT_GESTURE_LEFT) ||
                        event.equals(GEAR_EVENT_ROTARYDETENT_CCW)) {
                    flick.start();
                    mGallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                }
                if (event.equals(GEAR_EVENT_GESTURE_RIGHT) ||
                        event.equals(GEAR_EVENT_ROTARYDETENT_CW)) {
                    flick.start();
                    mGallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                }
                if (event.equals(GEAR_EVENT_GESTURE_TAP)) {
                    noti.start();
                    // launch the selected app
                    int position = mGallery.getSelectedItemPosition();
                    launchApp(position);
                }
            }
            
        };
        this.registerReceiver(mGearEventReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(mGearEventReceiver);
        super.onPause();
    }
	
    private void launchApp(int position) {
        AppInfo appInfo = (AppInfo)mGallery.getItemAtPosition(position);
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appInfo.mPackageName);
        startActivity(launchIntent);
        if (!appInfo.mIsGearXCarApp) {
            String warning = "This app is not a GearXCar app.\n You may not be able to contol the app.";
            Toast.makeText(this, warning, Toast.LENGTH_SHORT).show();
        }
    }
}
