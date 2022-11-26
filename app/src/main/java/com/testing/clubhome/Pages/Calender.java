package com.testing.clubhome.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.testing.clubhome.Club.ClubHome;
import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.R;
import com.testing.clubhome.Room.creaeRoom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testing.clubhome.supporting.CalenderAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Calender extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase,clubReference,roomReference;
    FirebaseUser user;
    ListView calenderList;
    LinearLayout empty;
    private InterstitialAd mInterstitialAd;
    ImageButton add;
    List<String> upcomingRoom=new ArrayList<>();
    List<String> upcomingRoomtime=new ArrayList<>();

    CalenderAdapter calenderAdapter;




    public boolean internet_connection() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        clubReference = firebaseDatabase.getReference("ClubsInfo");
        mDatabase = firebaseDatabase.getReference("UsersInfo").child(user.getUid());
        roomReference = firebaseDatabase.getReference("RoomsInfo");

        add=findViewById(R.id.add);
        calenderList=findViewById(R.id.list);
        empty=findViewById(R.id.empty);

        MobileAds.initialize(Calender.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        if(! Constants.PRO) {
            AdView madview=findViewById(R.id.adView);
            AdRequest adRequest=new AdRequest.Builder().build();
            madview.loadAd(adRequest);

            mInterstitialAd= new InterstitialAd(Calender.this);
            mInterstitialAd.setAdUnitId(getString(R.string.Interstial_app_id));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createtheRoom();
            }
        });

        if(!internet_connection()){
            noInternet();
        }else {
            roomReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    upcomingRoom.clear();
                    upcomingRoomtime.clear();

                    Date date=new Date();
                    Date date1=new Date();
                    DateFormat format=new SimpleDateFormat("yyyy/MM/dd");
                    Calendar cal = Calendar.getInstance();
                    Date todaysDate = cal.getTime();
                    if(task.getResult().exists()) {
                        if (task.getResult().child("upComing").exists()) {

                            for (DataSnapshot s:task.getResult().child("upComing").getChildren()){
                                upcomingRoom.add(s.getKey());
                                upcomingRoomtime.add(s.getValue().toString());
                            }
                        }
                    }



                    //arranging to asscending order
                    String name1="";
                    String room1="";
                    for ( int m=0;m<upcomingRoomtime.size();m++) {
                        name1=upcomingRoomtime.get(m);
                        room1=upcomingRoom.get(m);
                        try {
                            date=format.parse(name1);

                            for (int n=m+1;n<upcomingRoomtime.size();n++){
                                String name2=upcomingRoomtime.get(n);
                                String room2=upcomingRoom.get(n);
                                date1=format.parse(name2);
                                if (date.after(date1)){
                                    upcomingRoomtime.set(m,name2);
                                    upcomingRoomtime.set(n,name1);
                                    upcomingRoom.set(m,room2);
                                    upcomingRoom.set(n,room1);
                                    name1=upcomingRoomtime.get(m);
                                    room1=upcomingRoom.get(m);
                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("abcdefghijlomnopqrs","");
                        }
                    }

                    for ( int m=0;m<upcomingRoomtime.size();m++) {
                        String name=upcomingRoomtime.get(m);
                        try {
                            date=format.parse(name);
                            if (date.after(todaysDate)){

                                String[] ids=upcomingRoomtime.get(m).split(" // ");
                                for (String id:ids){
                                    roomReference.child("upComing").child(id).removeValue();
                                    roomReference.child(id).removeValue();
                                }

                                upcomingRoomtime.remove(m);
                                upcomingRoom.remove(m);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }



                    calenderAdapter=new CalenderAdapter(Calender.this,upcomingRoom);
                    calenderAdapter.notifyDataSetChanged();
                    calenderList.setAdapter(calenderAdapter);



                    if(!upcomingRoomtime.isEmpty()){
                        empty.setVisibility(View.INVISIBLE);
                    }
                    else {
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            });
        }



    }

    public void backPressed(View view) {
      if (! Constants.PRO){

            if(mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

        }
        finish();
    }
    @Override
    public void onBackPressed() {
        if (! Constants.PRO){

            if(mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

        }
        super.onBackPressed();
    }

    private void createtheRoom() {
        if(!internet_connection()){
            noInternet();
        }
        else {
            Intent intent1 = new Intent(getApplicationContext(), creaeRoom.class);
            startActivity(intent1);
        }
    }

}