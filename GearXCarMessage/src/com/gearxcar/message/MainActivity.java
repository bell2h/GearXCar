package com.gearxcar.message;

import java.util.ArrayList;

import com.gearxcar.messagesample.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

public class MainActivity extends Activity {
    private MainActivity mActivity = null;
    private String mSender = "";
    private String mMessage = "";
    
    private Gallery mGallery;
    
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
    private BroadcastReceiver mReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent intent = this.getIntent();
        updateView(intent);
        
        mActivity = this;
    }
    
    private void updateView(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {
            mSender = b.getString("sender");
            mMessage = b.getString("message"); 
        }
        
        TextView senderView = (TextView)this.findViewById(R.id.sender);
        TextView messageView = (TextView)this.findViewById(R.id.message);
        if (!mSender.isEmpty())
            senderView.setText(mSender);
        if (!mMessage.isEmpty()) {
            messageView.setText(mMessage);
            messageView.setMovementMethod(new ScrollingMovementMethod());
        }
        
        ArrayList<String> replyMsgList = new ArrayList<String>();
        replyMsgList.add("Hi there");
        replyMsgList.add("I'm driving now.");
        replyMsgList.add("I'll call back later.");
        replyMsgList.add("Do not text me again.");
        replyMsgList.add("Plz call 911.");
        ReplyMsgViewAdapter adapter = new ReplyMsgViewAdapter(replyMsgList);
        mGallery = (Gallery) this.findViewById(R.id.gallery);
        mGallery.setAdapter(adapter);
        
        mGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // TODO Auto-generated method stub
                replyMsg(position);
            }
            
        });
        
        final MediaPlayer chord = MediaPlayer.create(this, R.raw.music_marimba_chord);
        chord.start();
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d("GearXCar-Message", "onDestroy");
        mActivity = null;
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        updateView(intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GEAR_EVENT);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                final MediaPlayer flick = MediaPlayer.create(context, R.raw.pop_drip);
                flick.start();
                String event = intent.getStringExtra("event");
                if (event.equals(GEAR_EVENT_GESTURE_LEFT) ||
                        event.equals(GEAR_EVENT_ROTARYDETENT_CCW)) {
                    mGallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                }
                if (event.equals(GEAR_EVENT_GESTURE_RIGHT) ||
                        event.equals(GEAR_EVENT_ROTARYDETENT_CW)) {
                    mGallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                }
                if (event.equals(GEAR_EVENT_GESTURE_TAP)) {
                    // reply the selected messsage to sender.
                    int position = mGallery.getSelectedItemPosition();
                    replyMsg(position);
                }
            }
            
        };
        this.registerReceiver(mReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(mReceiver);
        finish();
        super.onPause();
    }
    
    private void replyMsg(int position) {
        String replyMsg = (String)mGallery.getItemAtPosition(position);
        PendingIntent SendPI = PendingIntent.getBroadcast(this,0, new Intent(), 0);
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(mSender,null,replyMsg, SendPI, null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
