package com.testing.clubhome.Room;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testing.clubhome.Backend.VoiceService;
import com.testing.clubhome.Club.ClubHome;
import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.Fragment.MainDashboard;
import com.testing.clubhome.Pages.Calender;
import com.testing.clubhome.R;
import com.testing.clubhome.supporting.FullLengthListView;
import com.testing.clubhome.supporting.roomUserAdapter;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class room extends AppCompatActivity{

    Fragment mainShowFragment;
    LinearLayout descussionbar,clubLayout;
    TextView discussionText,clubName,leaveTheRoom,roomName,description,time;
    ImageView tostage,todiscussion;
    boolean disscussionforaudio=true,join;
    ImageView back;
    String roomId,clubId="";
    int sec=0;
    String positioninroom="Owner";
    int secfors=0;
    private InterstitialAd mInterstitialAd;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference clubReference,userReference,roomReference, mDatabase,roomReference1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        time=findViewById(R.id.time);
        description=findViewById(R.id.description);
        roomName=findViewById(R.id.roomName);
        descussionbar=findViewById(R.id.descussionbar);
        discussionText=findViewById(R.id.discussionText);
        tostage=findViewById(R.id.tostage);
        todiscussion=findViewById(R.id.todiscussion);
        back=findViewById(R.id.back);
        leaveTheRoom=findViewById(R.id.leaveTheRoom);
        clubName=findViewById(R.id.clubName);
        clubLayout=findViewById(R.id.clubLayout);
        Intent intent= getIntent();
        roomId =intent.getStringExtra("roomid");
        join=intent.getBooleanExtra("join",true);

        clubLayout.setVisibility(View.GONE);

        //FireBase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("UsersInfo").child(user.getUid());
        clubReference = database.getReference("ClubsInfo");
        roomReference = database.getReference("RoomsInfo").child(roomId);
        userReference=database.getReference("Connections");

        roomReference1 = database.getReference("RoomsInfo");

        MobileAds.initialize(room.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        if(! Constants.PRO) {

            mInterstitialAd= new InterstitialAd(room.this);
            mInterstitialAd.setAdUnitId(getString(R.string.Interstial_app_id));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
        findRoomData();
        roomReference1.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().child(roomId).child("Assets").child("time").exists()){
                    Date date = new Date();
                    DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    String writtenDate =(task.getResult().child(roomId).child("Assets").child("time").getValue().toString());
                    try {
                        date = format.parse(writtenDate);
                        secfors=date.getHours()*3600 +date.getMinutes()*60;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    timer();
                }
            }
        });
        roomReference.child("Audio On").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()){
                    roomReference.child("Audio On").child(user.getUid()).removeValue();
                }
            }
        });
        roomReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().child("Assets").child("room name").exists()) {
                     roomName.setText(task.getResult().child("Assets").child("room name").getValue().toString());
                }
                description.setText(task.getResult().child("Assets").child("short Description").getValue().toString());

                clubLayout.setVisibility(View.VISIBLE);
                if (task.getResult().child("Assets").child("hosted by").exists()){
                    clubId=task.getResult().child("Assets").child("hosted by").getValue().toString();
                    clubReference.child(clubId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task1) {
                            clubName.setText(task1.getResult().child("Assets").child("club name").getValue().toString());
                        }
                    });
                }else{
                    clubLayout.setVisibility(View.GONE);
                }

            }
        });

        if (join){
            discussionText.setText("Discussion");
            tostage.setVisibility(View.VISIBLE);
            todiscussion.setVisibility(View.INVISIBLE);

        }else {
            discussionText.setText("");
            tostage.setVisibility(View.INVISIBLE);
            todiscussion.setVisibility(View.INVISIBLE);

        }


        //Default mainDashboard
        mainShowFragment=new RoomHome(roomId,join);
        getSupportFragmentManager().beginTransaction().replace(R.id.roomFragment,mainShowFragment).commit();

        descussionbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (join){

                    if (disscussionforaudio){
                        disscussionforaudio=false;
                        discussionText.setText("To Stage");
                        tostage.setVisibility(View.INVISIBLE);
                        todiscussion.setVisibility(View.VISIBLE);
                        mainShowFragment=null;
                        mainShowFragment=new textChannel(roomId);
                        getSupportFragmentManager().beginTransaction().replace(R.id.roomFragment,mainShowFragment).commit();

                    }else{
                        disscussionforaudio=true;
                        discussionText.setText("Discussion");
                        tostage.setVisibility(View.VISIBLE);
                        todiscussion.setVisibility(View.INVISIBLE);
                        mainShowFragment=null;
                        mainShowFragment=new RoomHome(roomId,join);
                        getSupportFragmentManager().beginTransaction().replace(R.id.roomFragment,mainShowFragment).commit();
                    }

                }

            }
        });

        leaveTheRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! Constants.PRO){

                    if(mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }

                }
                leaveChannel();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        clubLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent club = new Intent(getApplicationContext(), ClubHome.class);
                club.putExtra("clubId", clubId);
                startActivity(club);

            }
        });

        initAgoraEngineAndJoinChannel();

    }

    private void leaveChannel() {
        if (VoiceService.getInstance()!=null) {
            if (VoiceService.getInstance().checkingRtc()) {
                VoiceService.getInstance().leavetheChannel();
                clearApplicationData();
                mDatabase.child("Room Participated").removeValue();
            }
        }
        roomReference.child("Peoples").child(user.getUid()).removeValue();
        finish();
    }

    private void joinChannel() {
        if(join) {
            VoiceService.getInstance().jointheChannel();
        }
    }

    private void findRoomData() {



        roomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int owner=0;
                    for (DataSnapshot s : snapshot.child("Peoples").getChildren()) {
                        if (s.getValue().toString().equals("Owner")) {
                            owner++;
                        }
                    }
                    if (owner==0) {
                        if (VoiceService.getInstance()!=null){
                            Toast.makeText(room.this,"Room has ended1",Toast.LENGTH_SHORT ).show();
                        }
                        roomReference1.child("onGoing").child(roomId).removeValue();
                        roomReference.removeValue();
                        if (!clubId.isEmpty()){
                        clubReference.child(clubId).child("Rooms").child(roomId).removeValue();
                        }
                        leaveChannel();

                    }

                    if (join){

                    if ( !snapshot.child("Peoples").child(user.getUid()).exists()) {
                        if (VoiceService.getInstance()!=null){
                            Toast.makeText(room.this,"you are removed",Toast.LENGTH_SHORT).show();
                        }
                        if(snapshot.child("Audio On").child(user.getUid()).exists()){
                            roomReference.child("Audio On").child(user.getUid()).removeValue();
                        }
                        leaveChannel();
                    }else{

                    }

                    }


                }
                else{
                    if (VoiceService.getInstance()!=null){
                       Toast.makeText(room.this,"Room has ended",Toast.LENGTH_LONG ).show();
                    }
                    leaveChannel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clearApplicationData() {
        try {
            File dir = getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        }catch (Exception e){

        }
    }

    private void initAgoraEngineAndJoinChannel(){
        Intent intent=new Intent(this, VoiceService.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("roomName", roomName.getText().toString());
        intent.putExtra("join", join);
        boolean isspeaker;
        if (positioninroom.equals("Owner")||positioninroom.equals("Onstage")){
            isspeaker=true;
        }else {
            isspeaker=false;
        }
        intent.putExtra("position",isspeaker);
        startService(intent);
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }

        }
        return dir.delete();
    }

    public void timer() {
        final Handler hd = new Handler();

        Calendar cal = Calendar.getInstance();
        final Date[] date1 = {cal.getTime()};
        int s= date1[0].getHours()*3600 +date1[0].getMinutes()*60;
        sec=s-secfors;
        if (sec<0){
            sec=0;
        }
        int hours_var = sec / 3600;
        int minutes_var = (sec % 3600) / 60;
        int secs_var = sec % 60;

        String time_value = String.format(Locale.getDefault(),
                "%d:%02d:%02d", hours_var, minutes_var, secs_var);

        time.setText(time_value);

        hd.post(new Runnable() {
            @Override

            public void run() {
                int hours_var = sec / 3600;
                int minutes_var = (sec % 3600) / 60;
                int secs_var = sec % 60;

                String time_value = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours_var, minutes_var, secs_var);

                time.setText(time_value);
                sec++;
                hd.postDelayed(this, 1000);
            }
        });
    }

}