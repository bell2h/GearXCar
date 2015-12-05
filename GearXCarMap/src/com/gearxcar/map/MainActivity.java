package com.gearxcar.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity
        implements OnMapReadyCallback {

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

    double x, y;

    Timer timer;
    LocationManager lm;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    GoogleMap mMap = null;
    Marker mMarker = null;
    static LatLng GWANGHWAMOON = new LatLng(37.574515, 126.976930);
    static LatLng GWANGHWAMOON_A = new LatLng(38.574515, 126.976930);
    static LatLng SRND = new LatLng(37.46458, 127.02391);
    LatLng mCurLocation = SRND;
    boolean mIsMapReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps_enabled && !network_enabled) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "nothing is enabled",
                    duration);
            toast.show();
        }

        if (gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListenerGps);
        if (network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListenerNetwork);
        timer = new Timer();
        timer.schedule(new GetLastLocation(), 20000);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }
    
    private BroadcastReceiver mGearEventReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (mIsMapReady == false) return;
            
            final MediaPlayer flick = MediaPlayer.create(context, R.raw.pop_drip);
            flick.start();
            
            String event = intent.getStringExtra("event");
            final float STEP_VERTICAL = 1500.f;
            final float STEP_HORIZONTAL = 1200.f;
            if (event.equals(GEAR_EVENT_GESTURE_LEFT)) {
                mMap.animateCamera(CameraUpdateFactory.scrollBy(-STEP_HORIZONTAL, 0.f));
            }
            if (event.equals(GEAR_EVENT_GESTURE_RIGHT)) {
                mMap.animateCamera(CameraUpdateFactory.scrollBy(STEP_HORIZONTAL, 0.f));
            }
            if (event.equals(GEAR_EVENT_GESTURE_DOWN)) {
                mMap.animateCamera(CameraUpdateFactory.scrollBy(0.f, STEP_VERTICAL));
            }
            if (event.equals(GEAR_EVENT_GESTURE_UP)) {
                mMap.animateCamera(CameraUpdateFactory.scrollBy(0.f, -STEP_VERTICAL));
            }
            if (event.equals(GEAR_EVENT_ROTARYDETENT_CW)) {
                // zoom +
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
            if (event.equals(GEAR_EVENT_ROTARYDETENT_CCW)) {
                // zoom -
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
            if (event.equals(GEAR_EVENT_GESTURE_TAP)) {
                updateMap();
            }
        }
        
    };

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(mGearEventReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        IntentFilter intentFilter = new IntentFilter(ACTION_GEAR_EVENT);
        this.registerReceiver(mGearEventReceiver, intentFilter);
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mIsMapReady = true;
                
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(mCurLocation)
                .draggable(true)
                .title("Me")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.delorean)));
        updateMap();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        
        
        new LongOperation().execute("");
    }
    
    private void updateMap() {
        if (!mIsMapReady) return;
        mMap.animateCamera(CameraUpdateFactory.newLatLng(mCurLocation));
        mMarker.setPosition(mCurLocation);
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            x = location.getLatitude();
            y = location.getLongitude();
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "gps enabled " + x + "\n" + y,
                    duration);
            toast.show();
            
            
            mCurLocation = new LatLng(x, y);
            updateMap();
            
            if(!isDirectionDrawn) {
                new LongOperation().execute("");
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status,
                Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            x = location.getLatitude();
            y = location.getLongitude();
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context,
                    "network enabled" + x + "\n" + y, duration);
            toast.show();

            mCurLocation = new LatLng(x, y);
            updateMap();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status,
                Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        
        private void toast(String msg) {
            Handler handler = new Handler(Looper.getMainLooper());
            final String sMsg = msg;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context,
                            sMsg, duration);
                    toast.show();
                }
            }, 0);
        }
        @Override
        public void run() {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);

            Location net_loc = null, gps_loc = null;
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime()) {
                    x = gps_loc.getLatitude();
                    y = gps_loc.getLongitude();
                    toast("gps lastknown " + x + "\n" + y);
                } else {
                    x = net_loc.getLatitude();
                    y = net_loc.getLongitude();
                    toast("network lastknown " + x + "\n" + y);
                }

            }

            if (gps_loc != null) {
                {
                    x = gps_loc.getLatitude();
                    y = gps_loc.getLongitude();
                    toast("gps lastknown " + x + "\n" + y);
                }

            }
            if (net_loc != null) {
                {
                    x = net_loc.getLatitude();
                    y = net_loc.getLongitude();
                    toast("network lastknown " + x + "\n" + y);

                }
            }
            toast("no last know avilable");
        }
    }
    
    
    

    boolean isDirectionDrawn = false;;
    private class LongOperation extends AsyncTask<String, Void, PolylineOptions> {

        private PolylineOptions getDirection() {
            try {
                GMapV2Direction md = new GMapV2Direction();

                Document doc = md.getDocument(mCurLocation, SRND,
                        GMapV2Direction.MODE_TRANSIT);

                ArrayList<LatLng> directionPoint = md.getDirection(doc);
                PolylineOptions rectLine = new PolylineOptions().width(9).color(
                        Color.RED);

                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                isDirectionDrawn = true;
                
                Log.d("MAP", "LO getDirection : rectLine - " + rectLine.getPoints().size());

                return rectLine;
            }
            catch (Exception e)
            {
                ///possible error:
                ///java.lang.IllegalStateException: Error using newLatLngBounds(LatLngBounds, int): Map size can't be 0. Most likely, layout has not yet occured for the map view.  Either wait until layout has occurred or use newLatLngBounds(LatLngBounds, int, int, int) which allows you to specify the map's dimensions.
                Log.e("MAP", "LO " + e.toString());
                return null;
            }

        }

        @Override
        protected PolylineOptions doInBackground(String... params) {
            PolylineOptions polylineOptions = null;
            try {
                polylineOptions = getDirection();
            } catch (Exception e) {
                Log.e("MAP", "LO doInBackground " + e.toString());
                Thread.interrupted();
            }
            return polylineOptions;
        }

        @Override
        protected void onPostExecute(PolylineOptions result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you

            mMap.clear();///TODO: clean the path only.

            mMap.addMarker(new MarkerOptions()
                            .position(SRND)
                            .title("Seoul R&D")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            
            );
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(mCurLocation)
                    .draggable(true)
                    .title("my car")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.delorean)));

            mMap.addPolyline(result);
            updateMap();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}