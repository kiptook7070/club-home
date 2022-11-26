package com.testing.clubhome.Backend;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.testing.clubhome.R;
import com.testing.clubhome.Pages.MainActivity;

import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.testing.clubhome.Constant.Constants;
import androidx.annotation.Nullable;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

import android.os.Handler;
import android.os.Looper;
import android.app.*;


public class VoiceService extends Service {
    private String roomId="";
    private RtcEngine mRtcEngine;
    private Activity activity;
    private static VoiceService instance;
    private String roomName;
    private final IRtcEngineEventHandler mRtcEventHandler=new IRtcEngineEventHandler() {


        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Toast.makeText(getApplicationContext(),"Join Channel",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);
        }


    };
    boolean positionasspeaker;
    private Handler uiHandler=new Handler(Looper.getMainLooper());
    private Runnable pinger=new Runnable(){
        @Override
        public void run(){
            uiHandler.postDelayed(this, 30000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), Constants.AGORA_APP_ID, mRtcEventHandler);
        }
        catch (Exception e) {
            stopForeground(true);
            stopSelf();
            e.printStackTrace();
            return;
        }
//        mRtcEngine.enableLocalAudio(true);
//        mRtcEngine.enableAudio();
//        mRtcEngine.setEnableSpeakerphone(true);
//        mRtcEngine.enableAudioVolumeIndication(500, 3, false);
//        mRtcEngine.muteLocalAudioStream(true);
        instance=VoiceService.this;
//        mRtcEngine.adjustRecordingSignalVolume(400);
//        mRtcEngine.adjustPlaybackSignalVolume(400);

    }

    public static VoiceService getInstance(){
        return instance;
    }
    public String getRoomId(){
        return roomId;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        instance=null;
        stopForeground(true);
        stopSelf();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        roomId=intent.getStringExtra("roomId");
        roomName=intent.getStringExtra("roomName");
        positionasspeaker=intent.getBooleanExtra("position",true);
        boolean joined=intent.getBooleanExtra("join",true);
        if(joined) {

            jointheChannel();
        }else {
            instance=null;
        }
        return START_NOT_STICKY;
    }



    public void mutePresed(boolean mute){
        mRtcEngine.muteLocalAudioStream(mute);
    }

    public void setspeaker(boolean mute){
        mRtcEngine.setEnableSpeakerphone(mute);
    }


    public boolean checkingRtc(){
       return mRtcEngine!=null;

    }

    public void jointheChannel() {
        if (checkingRtc()) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            Notification.Builder n = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_icon)
                    .setContentTitle("Ongoing Call")
                    .setContentText(roomName)
                    .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class).putExtra("openCurrentChannel", true), PendingIntent.FLAG_UPDATE_CURRENT));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (nm.getNotificationChannel("ongoing") == null) {
                    NotificationChannel nc = new NotificationChannel("ongoing", "Ongoing calls", NotificationManager.IMPORTANCE_LOW);
                    nm.createNotificationChannel(nc);
                }
                n.setChannelId("ongoing");
            }


            startForeground(10, n.build());


            FirebaseUser user;
            user = FirebaseAuth.getInstance().getCurrentUser();
            // Maximum time set for the room is 1 hour i.e. 3600 seconds
            int timestamp = (int) (System.currentTimeMillis() / 1000 + 3600);
            RtcTokenBuilder token = new RtcTokenBuilder();
            String appId = Constants.AGORA_APP_ID;
            String appCer = Constants.AGORA_APP_CER;
            uiHandler.postDelayed(pinger, 30000);
            String accessToken = token.buildTokenWithUid(appId, appCer, roomId,
                    0, timestamp, timestamp, timestamp, timestamp);

            mRtcEngine.setAudioProfile(io.agora.rtc.Constants.AUDIO_PROFILE_DEFAULT, io.agora.rtc.Constants.AUDIO_SCENARIO_MEETING);
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);

                mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER);
//            }
//            else{
//                mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE);
//            }
//        mRtcEngine?.setAudioProfile(Constants.A, Constants.AUDIO_SCENARIO_MEETING)
            mRtcEngine.joinChannel(accessToken, roomId, "", Integer.parseInt(user.getUid().replaceAll("[\\D]", "")));
//            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
//            mRtcEngine.setEnableSpeakerphone(true);
//            mRtcEngine.adjustAudioMixingVolume(100);
            mRtcEngine.setEnableSpeakerphone(true);
            mRtcEngine.muteLocalAudioStream(true);
            mRtcEngine.enableAudioVolumeIndication(300, 3, true);
        }

    }

    public void becamespeaker(boolean speaker){

        mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER);

    }

    public void leavetheChannel(){
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
        stopForeground(true);
        stopSelf();
        uiHandler.removeCallbacks(pinger);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
