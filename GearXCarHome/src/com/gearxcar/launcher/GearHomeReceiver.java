package com.gearxcar.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class GearHomeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        final MediaPlayer volsound = MediaPlayer.create(context, R.raw.volsound);
        volsound.start();
        Intent intentHome = new Intent(context, MainActivity.class);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        context.startActivity(intentHome);
        
    }

}
