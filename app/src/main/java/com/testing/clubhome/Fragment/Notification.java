package com.testing.clubhome.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.initialization.InitializationStatus;
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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
import com.testing.clubhome.Club.ClubHome;
import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.Pages.MainActivity;
import com.testing.clubhome.Profiles.ProfileActivity;
import com.testing.clubhome.R;
import com.testing.clubhome.supporting.NotificationAdaper;

import java.util.ArrayList;
import java.util.List;


public class Notification extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase,clubReference,roomReference,userDatabase,notificationData;
    FirebaseUser user;
    ListView calenderList;
    LinearLayout empty;

    List<String> notification=new ArrayList<>();

    NotificationAdaper notificationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_notification, container, false);

        empty=view.findViewById(R.id.empty);
        calenderList=view.findViewById(R.id.list);
        firebaseAuth=FirebaseAuth.getInstance();
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        if(! Constants.PRO) {
            AdView madview=view.findViewById(R.id.adView);
            AdRequest adRequest=new AdRequest.Builder().build();
            madview.loadAd(adRequest);


        }
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        clubReference = firebaseDatabase.getReference("ClubsInfo");
        userDatabase= firebaseDatabase.getReference("UsersInfo");
        mDatabase = firebaseDatabase.getReference("UsersInfo").child(user.getUid());
        notificationData=firebaseDatabase.getReference("Notification Data").child(user.getUid());
        roomReference = firebaseDatabase.getReference("RoomsInfo");

        notificationData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notification.clear();
                if(snapshot.exists()) {
                    for (DataSnapshot s:snapshot.getChildren()){
                        notification.add(s.getKey());
                    }

                }
                notificationAdapter=new NotificationAdaper(getActivity(),notification);
                notificationAdapter.notifyDataSetChanged();
                calenderList.setAdapter(notificationAdapter);

                if(!notification.isEmpty()&&notification!=null){
                    empty.setVisibility(View.INVISIBLE);
                }
                else {
                    empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//Advertising Initialization
//        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        AdView madview=view.findViewById(R.id.adView);
//        AdRequest adRequest=new AdRequest.Builder().build();
//        madview.loadAd(adRequest);

        calenderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String []title=notification.get(position).split(" - ");
                if(title[0].equals("Room")){
                    roomOpen(title[1]);
                }else if(title[0].equals("Club")){
                    clubOpen(title[1]);
                }else {
                    userOpen(title[1]);
                }
            }
        });


        return view;
    }

    private void roomOpen(String roomId){
        if(!internet_connection()){
            noInternet();
        }else {
            final Dialog room = new Dialog(getActivity());
            room.setContentView(R.layout.room_dialog);
            room.setCancelable(true);
            room.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            room.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            room.getWindow().setGravity(Gravity.BOTTOM);
            room.show();

            LinearLayout clubLayout = room.findViewById(R.id.clubLayout);
            RelativeLayout roomStarted = room.findViewById(R.id.roomStarted);
            Button getStarted = room.findViewById(R.id.getStarted);
            Button joinRoom = room.findViewById(R.id.joinRoom);
            Button whoIsInside = room.findViewById(R.id.whoIsInside);
            whoIsInside.setVisibility(View.INVISIBLE);

            ImageView back = room.findViewById(R.id.back);
            final String[] positionroom = {"Listener"};
            final boolean[] privacy = {false};

            TextView clubName = room.findViewById(R.id.clubName);
            TextView roomName = room.findViewById(R.id.roomName);
            TextView description = room.findViewById(R.id.description);
            final String[] ClubId = new String[1];

            roomReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.getResult().child(roomId).exists()) {
                        roomName.setText(task.getResult().child(roomId).child("Assets").child("room name").getValue().toString());
                        description.setText(task.getResult().child(roomId).child("Assets").child("short Description").getValue().toString());
                        if (task.getResult().child(roomId).child("Peoples").child(user.getUid()).exists()) {
                            positionroom[0] = task.getResult().child(roomId).child("Peoples").child(user.getUid()).getValue().toString();
                        }

                        privacy[0] = task.getResult().child(roomId).child("Assets").child("Privacy").getValue().toString().equals("Private");

                        if (task.getResult().child(roomId).child("Assets").child("Started").getValue().toString().equals("Not Started")) {
                            if (task.getResult().child(roomId).child("Peoples").child(user.getUid()).getValue().toString().equals("Owner")) {
                                getStarted.setVisibility(View.VISIBLE);
                                roomStarted.setVisibility(View.GONE);
                            } else {
                                getStarted.setVisibility(View.GONE);
                                roomStarted.setVisibility(View.VISIBLE);
                                joinRoom.setText("Notify");
                                //Notification adder

                            }
                        } else {
                            getStarted.setVisibility(View.GONE);
                            roomStarted.setVisibility(View.VISIBLE);
                            joinRoom.setText("Join Room");


                        }


                        if (task.getResult().child(roomId).child("Assets").child("hosted by").exists()) {
                            clubLayout.setVisibility(View.VISIBLE);
                            ClubId[0] = task.getResult().child(roomId).child("Assets").child("hosted by").getValue().toString();

                            clubReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task1) {
                                    String ClubName = task1.getResult().child(ClubId[0]).child("Assets").child("club name").getValue().toString();
                                    clubName.setText(ClubName);
                                }
                            });

                        } else {
                            clubLayout.setVisibility(View.GONE);
                        }
                    } else {
                        room.dismiss();
                        Toast.makeText(getActivity(), "Room ended", Toast.LENGTH_LONG).show();
                    }
                }
            });

            joinRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (joinRoom.getText().toString().equals("Join Room")) {
                        //Room has Started

                        mDatabase.child("Room Participated").setValue(roomId);

                        roomReference.child(roomId).child("Peoples").child(user.getUid()).setValue(positionroom[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    Intent club = new Intent(getActivity(), com.testing.clubhome.Room.room.class);
                                    club.putExtra("roomid", roomId);
                                    club.putExtra("join", true);
                                    startActivity(club);
                            }
                            });
                    } else {
                        //Notify when room started

                    }

                    room.dismiss();
                }
            });

            clubLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent club = new Intent(getActivity(), ClubHome.class);
                    club.putExtra("clubId", ClubId[0]);
                    startActivity(club);

                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    room.dismiss();
                }
            });

            getStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    room.dismiss();
                    if (privacy[0]) {
                        roomReference.child("onGoing").child(roomId).setValue("Private");
                    } else {
                        roomReference.child("onGoing").child(roomId).setValue("Public");
                    }
                    roomReference.child("upComing").child(roomId).removeValue();
                    mDatabase.child("Room Participated").setValue(roomId);
                    roomReference.child(roomId).child("Assets").child("Started").setValue("Started");
                    roomReference.child(roomId).child("Peoples").child(user.getUid()).setValue("Owner").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Intent club = new Intent(getActivity(), com.testing.clubhome.Room.room.class);
                            club.putExtra("roomid", roomId);
                            club.putExtra("join", true);
                            startActivity(club);
                        }
                    });

                }
            });
        }
    }

    public boolean internet_connection() {
        //Check if connected to internet, output accordingly
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected()&& activeNetwork.isAvailable();
        return isConnected;
    }

    public void noInternet(){

        final Dialog dialog2 = new Dialog(getActivity());
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

    private void clubOpen(String clubId){
        Intent intent=new Intent(getActivity(), ClubHome.class);
        intent.putExtra("clubId",clubId);
        startActivity(intent);

    }

    private void userOpen(String userId){
        Intent intent=new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra("user",userId);
        startActivity(intent);
    }
}