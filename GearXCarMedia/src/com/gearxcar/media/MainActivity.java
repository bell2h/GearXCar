package com.gearxcar.media;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    public final String TAG = "GEARXCAR-MEDIA";
    
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    
    public static final String MUSIC_METACHANGED = "com.android.music.metachanged";
    public static final String MUSIC_PLAYSTATECHANGED = "com.android.music.playstatechanged";
    public static final String MUSIC_PLAYBACKCOMPLETE = "com.android.music.playbackcomplete";
    public static final String MUSIC_QUEUECHANGED = "com.android.music.queuechanged";
    
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
    
    private ImageView mRewindButton;
    private ImageView mPlayButton;
    private ImageView mPauseButton;
    private ImageView mForwardButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        IntentFilter iF = new IntentFilter();
        iF.addAction(MUSIC_METACHANGED);
        iF.addAction(MUSIC_PLAYSTATECHANGED);
        //iF.addAction(MUSIC_PLAYBACKCOMPLETE);
        //iF.addAction(MUSIC_QUEUECHANGED);

        this.registerReceiver(mReceiver, iF);
        
        registerButtonHandler();
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(mReceiver);
        
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
                String event = intent.getStringExtra("event");
                Log.d(TAG, "HH event = " + event);
                if (event.equals(GEAR_EVENT_GESTURE_LEFT) ||
                        event.equals(GEAR_EVENT_ROTARYDETENT_CCW)) {
                    mRewindButton.performClick();
                }
                if (event.equals(GEAR_EVENT_GESTURE_RIGHT) ||
                        event.equals(GEAR_EVENT_ROTARYDETENT_CW)) {
                    mForwardButton.performClick();
                } 
                if (event.equals(GEAR_EVENT_GESTURE_TAP)) {
                    mPlayButton.performClick();
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

    private void registerButtonHandler() {
        mRewindButton = (ImageView)findViewById(R.id.image_rewind);
        mPlayButton = (ImageView)findViewById(R.id.image_play);
        mForwardButton = (ImageView)findViewById(R.id.image_forward);
        
        mRewindButton.setOnClickListener(mButtonListener);
        mPlayButton.setOnClickListener(mButtonListener);
        mForwardButton.setOnClickListener(mButtonListener);
    }
    
    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.d(TAG, "HH onClick = " + v);
            Intent intent = new Intent(SERVICECMD);
            if(v == mPlayButton) {
                intent.putExtra(CMDNAME , CMDTOGGLEPAUSE );
            //} else if(v == mPauseButton) {
            //    intent.putExtra(CMDNAME , CMDPAUSE );
            } else if(v == mRewindButton) {
                intent.putExtra(CMDNAME , CMDPREVIOUS );
            } else if(v == mForwardButton) {
                intent.putExtra(CMDNAME , CMDNEXT );
            }
            sendBroadcast(intent);
        }
    };
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            Log.d(TAG, bundle.toString());
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            while(it.hasNext()) {
                Log.d(TAG, it.next());
            }
            String cmd = intent.getStringExtra("command");
            Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            boolean playing = intent.getBooleanExtra("playing", false);
            Log.d("Music",artist+":"+album+":"+track);
            updateUI(artist, album, track, playing);
        }
        
    };
    
    private void updateUI(String artist, String album, String track, boolean playing) {
        Log.d(TAG, "updateUI :" + artist + " " + album + " " + track + " " + playing);
        TextView titleText, albumText, artistText;
        titleText = (TextView)this.findViewById(R.id.text_title);
        albumText = (TextView)this.findViewById(R.id.text_album);
        artistText = (TextView)findViewById(R.id.text_artist);
        
        if (titleText != null && track != null) {
            titleText.setText(track);
            titleText.setSelected(true);
        }
        if (albumText != null && album != null)
            albumText.setText(album);
        if (artistText != null && artist != null)
            artistText.setText(artist);
        if (playing) {
            mPlayButton.setImageResource(R.drawable.pause_icon);
        } else {
            mPlayButton.setImageResource(R.drawable.play_icon);
        }
    }
    
    public class Album {
        int albumId;
        String name;
        String artist;
        Album(int albumId, String name, String artist) {
            this.albumId = albumId;
            this.name = name;
            this.artist = artist;
        }
        int getAlbumId() { return this.albumId; }
        String getName() { return this.name; }
        String getArtist() { return this.artist; }
    }
    
    private List<Album> getAlbums() {
        Cursor cur = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
          List<Album> albums = new ArrayList<Album>();

          if (cur.moveToFirst()) {
           do {
            int albumIdIndex = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int albumIndex = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistIndex = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumId = cur.getInt(albumIdIndex);
            String name = cur.getString(albumIndex);
            String artist = cur.getString(artistIndex);
            Log.i(TAG, "albumid= " + albumId + ", album= " + name + ", artist=" + artist);

            Album album = new Album(albumId, name, artist);
            if (!albums.contains(album)) {
             albums.add(album);
            }    
           } while (cur.moveToNext());

          }

          return albums;
         }
}
