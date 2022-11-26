package com.testing.clubhome.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.InstanceIdResult;
import java.util.Objects;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.testing.clubhome.Authentication.SignupActivity;
import com.testing.clubhome.Backend.VoiceService;
import com.testing.clubhome.Club.ClubHome;
import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.Fragment.MainDashboard;
import com.testing.clubhome.Fragment.Notification;
import com.testing.clubhome.Fragment.Profile;
import com.testing.clubhome.Fragment.Search;
import com.testing.clubhome.Profiles.ProfileActivity;
import com.testing.clubhome.R;
import com.testing.clubhome.Room.RoomHome;
import com.testing.clubhome.supporting.FullLengthListView;
import com.testing.clubhome.supporting.roomSearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase,clubReference,userReference,roomReference;
    FirebaseUser user;

    FrameLayout forMainFragment;
    ImageButton mainDashboard,search,notification,profile;
    RelativeLayout roomFragment,mainDashboardlaou,searchlaou,notificationlaou,profilelaou;
    TextView roomName;
    Fragment mainShowFragment;

    // 0-> MainDashboard ; 1-> Search ; 2-> Notification ; 3-> Profile
    private int selectedFragment=0;
    private final int PERMISSION_REQ_ID_RECORD_AUDIO=22;

    String roomId="";


    private void Updatetoken(FirebaseUser user){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseDatabase.getInstance().getReference("UsersInfo").child(user.getUid())
                                    .child("token").setValue(Objects.requireNonNull(task.getResult()).getToken());
                        }

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        forMainFragment=findViewById(R.id.forMainFragment);
        mainDashboard=findViewById(R.id.mainDashboard);
        search=findViewById(R.id.search);
        notification=findViewById(R.id.notification);
        profile=findViewById(R.id.profile);
        mainDashboardlaou=findViewById(R.id.mainDashboardlaou);
        searchlaou=findViewById(R.id.searchlaou);
        notificationlaou=findViewById(R.id.notificationlaou);
        profilelaou=findViewById(R.id.profilelaou);
        roomFragment=findViewById(R.id.roomFragment);
        roomName=findViewById(R.id.roomName);

        roomFragment.setVisibility(View.GONE);

        //Default mainDashboard
        mainShowFragment=new MainDashboard();
        getSupportFragmentManager().beginTransaction().replace(R.id.forMainFragment,mainShowFragment).commit();
        mainDashboard.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorMain));
        search.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
        notification.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
        profile.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));


        //OnClick event
        mainDashboardlaou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selector
                mainDashboard.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorMain));
                search.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                notification.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                profile.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                if(selectedFragment!=0){
                    selectedFragment=0;
                    mainShowFragment=null;
                    mainShowFragment=new MainDashboard();
                    getSupportFragmentManager().beginTransaction().replace(R.id.forMainFragment,mainShowFragment).commit();
                }
            }
        });
        searchlaou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selector
                mainDashboard.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                search.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorMain));
                notification.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                profile.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                if(selectedFragment!=1){
                    selectedFragment=1;
                    mainShowFragment=null;
                    mainShowFragment=new Search();
                    getSupportFragmentManager().beginTransaction().replace(R.id.forMainFragment,mainShowFragment).commit();
                }
            }
        });
        notificationlaou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selector
                mainDashboard.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                search.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                notification.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorMain));
                profile.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                if(selectedFragment!=2){
                    selectedFragment=2;
                    mainShowFragment=null;
                    mainShowFragment=new Notification();
                    getSupportFragmentManager().beginTransaction().replace(R.id.forMainFragment,mainShowFragment).commit();
                }
            }
        });
        profilelaou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selector
                mainDashboard.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                search.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                notification.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                profile.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorMain));
                if(selectedFragment!=3){
                    selectedFragment=3;
                    mainShowFragment=null;
                    mainShowFragment=new Profile();
                    getSupportFragmentManager().beginTransaction().replace(R.id.forMainFragment,mainShowFragment).commit();
                }
            }
        });
        mainDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selector
                mainDashboard.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorMain));
                search.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                notification.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                profile.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                if(selectedFragment!=0){
                    selectedFragment=0;
                    mainShowFragment=null;
                    mainShowFragment=new MainDashboard();
                    getSupportFragmentManager().beginTransaction().replace(R.id.forMainFragment,mainShowFragment).commit();
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selector
                mainDashboard.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                search.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorMain));
                notification.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                profile.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                if(selectedFragment!=1){
                    selectedFragment=1;
                    mainShowFragment=null;
                    mainShowFragment=new Search();
                    getSupportFragmentManager().beginTransaction().replace(R.id.forMainFragment,mainShowFragment).commit();
                }
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selector
                mainDashboard.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                search.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                notification.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorMain));
                profile.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                if(selectedFragment!=2){
                    selectedFragment=2;
                    mainShowFragment=null;
                    mainShowFragment=new Notification();
                    getSupportFragmentManager().beginTransaction().replace(R.id.forMainFragment,mainShowFragment).commit();
                }
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selector
                mainDashboard.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                search.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                notification.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorDarkGrey));
                profile.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorMain));
                if(selectedFragment!=3){
                    selectedFragment=3;
                    mainShowFragment=null;
                    mainShowFragment=new Profile();
                    getSupportFragmentManager().beginTransaction().replace(R.id.forMainFragment,mainShowFragment).commit();
                }
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase=firebaseDatabase.getReference("UsersInfo").child(user.getUid());
        userReference=firebaseDatabase.getReference("Connections");
        clubReference=firebaseDatabase.getReference("ClubsInfo");
        roomReference=firebaseDatabase.getReference("RoomsInfo");
        Updatetoken(user);

        //Check for Ongoing rooms
        mDatabase.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Room Participated").exists()){
                    roomId=dataSnapshot.child("Room Participated").getValue().toString();
                    roomReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.getResult().exists()) {
                                if(task.getResult().child(roomId).exists()) {
                                    Intent club = new Intent(MainActivity.this, com.testing.clubhome.Room.room.class);
                                    club.putExtra("roomid", roomId);
                                    club.putExtra("join",true );
                                    startActivity(club);
                                }else{
                                    mDatabase.child("Room Participated").removeValue();
                                    roomId="";
                                }
                            }else{
                                mDatabase.child("Room Participated").removeValue();
                                roomId="";
                            }
                        }
                    });

                }
                if (dataSnapshot.child("pro").exists()) {
                    Constants.PRO=true;
                }else  {
                    Constants.PRO=false;
                }
            }
        });
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Room Participated").exists()){
                    roomId=snapshot.child("Room Participated").getValue().toString();
                    roomReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.getResult().exists()) {
                                if(task.getResult().child(roomId).exists()) {
                                    roomFragment.setVisibility(View.VISIBLE);
                                    roomName.setText(task.getResult().child(roomId).child("Assets").child("room name").getValue().toString());
                                }else{
                                    roomFragment.setVisibility(View.GONE);
                                    mDatabase.child("Room Participated").removeValue();
                                    roomId="";
                                }
                            }else{
                                roomFragment.setVisibility(View.GONE);
                                mDatabase.child("Room Participated").removeValue();
                                roomId="";
                            }
                        }
                    });
                }
                else {
                    roomFragment.setVisibility(View.GONE);
                    roomId="";

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //checking Internet Connection
        if(!internet_connection()){
            noInternet();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.RECORD_AUDIO,PERMISSION_REQ_ID_RECORD_AUDIO)){

            }
        }

        roomFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VoiceService.getInstance()!=null){
                    roomId=VoiceService.getInstance().getRoomId();
                    Intent club = new Intent(MainActivity.this, com.testing.clubhome.Room.room.class);
                    club.putExtra("roomid", roomId);
                    club.putExtra("join",true );
                    startActivity(club);

                }
            }
        });
    }

    private boolean checkSelfPermission(String recordAudio, int permission_req_id_record_audio) {
        if(ContextCompat.checkSelfPermission(this,recordAudio)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{recordAudio},permission_req_id_record_audio);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permisions
            ,@NonNull int[] grantResults){
        switch (requestCode){
            case PERMISSION_REQ_ID_RECORD_AUDIO:{
                if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){

                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    boolean internet_connection() {
        //Check if connected to internet, output accordingly
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected()&& activeNetwork.isAvailable();
        return isConnected;
    }

    public void noInternet(){

        final Dialog dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.nointernet);
        dialog2.setCancelable(true);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCanceledOnTouchOutside(true);
        dialog2.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog2.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog2.show();
        Button button = dialog2.findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
    }

    public void cancelRoom(View view){
        roomFragment.setVisibility(View.GONE);
        if(VoiceService.getInstance()!=null){
            VoiceService.getInstance().leavetheChannel();
            roomReference.child(VoiceService.getInstance().getRoomId()).child("Peoples").child(user.getUid()).removeValue();
        }

        mDatabase.child("Room Participated").removeValue();
    }

}