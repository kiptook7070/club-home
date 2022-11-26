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
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.testing.clubhome.Pages.Calender;
import com.testing.clubhome.Pages.MainActivity;
import com.testing.clubhome.Pages.premiun;
import com.testing.clubhome.R;
import com.testing.clubhome.Room.RoomHome;
import com.testing.clubhome.Room.creaeRoom;
import com.testing.clubhome.Room.room;
import com.testing.clubhome.supporting.FullLengthListView;
import com.testing.clubhome.supporting.roomSearchAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class MainDashboard extends Fragment {

    TextView userName;
    ImageButton calender;
    LinearLayout createRoom,empty;
    FullLengthListView roomsList;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase,clubReference,userReference,roomReference;
    FirebaseUser user;
    ArrayList<String> onGoingRoom=new ArrayList<>();
    roomSearchAdapter roomSarch;
    boolean prouser=false;
    int roomremaing;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_main_dashboard, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase=firebaseDatabase.getReference("UsersInfo").child(user.getUid());
        userReference=firebaseDatabase.getReference("Connections");
        clubReference=firebaseDatabase.getReference("ClubsInfo");
        roomReference=firebaseDatabase.getReference("RoomsInfo");
        userName=view.findViewById(R.id.userName);
        calender=view.findViewById(R.id.calender);
        createRoom=view.findViewById(R.id.createRoom);
        empty=view.findViewById(R.id.empty);
        roomsList=view.findViewById(R.id.roomsList);



        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createtheRoom();
            }
        });

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Calender.class);
                startActivity(intent);

            }
        });

        //Check for Ongoing rooms
        mDatabase.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("pro").exists()){

                    prouser=true;
                }else{
                    prouser=false;
                }
                if (dataSnapshot.child("roomremaining").exists()) {
                    roomremaing= Integer.parseInt(dataSnapshot.child("roomremaining").getValue().toString());
                }
                String name=dataSnapshot.child("name").getValue().toString();
                userName.setText(name);
                if(dataSnapshot.child("Room Participated").exists()){
                    final String onGoingRoomString = dataSnapshot.child("Room Participated").getValue().toString();




                    roomReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.getResult().exists()) {
                                if(task.getResult().child(onGoingRoomString).exists()) {

                                }
                                else{
                                    mDatabase.child("Room Participated").removeValue();

                                }
                            }else{
                                mDatabase.child("Room Participated").removeValue();
                            }
                        }
                    });
                }

            }
        });

        //Adding Room List
        roomReference.child("onGoing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onGoingRoom.clear();
                if(snapshot.exists()) {
                    if( snapshot.exists()) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            if (s.getValue().toString().equals("Public")) {
                                onGoingRoom.add(s.getKey());
                            }
                        }
                    }
                }
                if (getActivity()!=null){
                    roomSarch = new roomSearchAdapter(getActivity(), onGoingRoom);
                    roomsList.setAdapter(roomSarch);
                }
                if(!onGoingRoom.isEmpty()) {

                    empty.setVisibility(View.INVISIBLE);
                    roomsList.setVisibility(View.VISIBLE);
                }else {
                    empty.setVisibility(View.VISIBLE);
                    roomsList.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){

            }
        });

        roomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(VoiceService.getInstance()==null){
                    roomOpen(onGoingRoom.get(position));
                }else{
                    if(!VoiceService.getInstance().getRoomId().equals(onGoingRoom.get(position))){
                        Toast.makeText(getContext(),"Leave the previous room",Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        return view;
    }

    private void createtheRoom() {
        if(!internet_connection()){
            noInternet();
        }
        else {
            final Dialog createRoom = new Dialog(getActivity());
            createRoom.setContentView(R.layout.activity_create_room);
            createRoom.setCancelable(true);
            createRoom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            createRoom.setCanceledOnTouchOutside(true);
            createRoom.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            createRoom.getWindow().setGravity(Gravity.BOTTOM);

            createRoom.show();

            final String[] roomId = new String[1];
            ImageView back = createRoom.findViewById(R.id.back);
            Button create = createRoom.findViewById(R.id.createRoom);
            EditText roomName = createRoom.findViewById(R.id.roomName);
            EditText shortDis = createRoom.findViewById(R.id.shortDiscription);
            TextView purchase=createRoom.findViewById(R.id.purchase);
            LinearLayout timeLayout=createRoom.findViewById(R.id.timeLayout);

            SwitchCompat switchCompat = createRoom.findViewById(R.id.privacy);

            timeLayout.setVisibility(View.GONE);

            if (prouser){
                purchase.setVisibility(View.GONE);
            }

            purchase.setText("You have "+roomremaing+" rooms this month purchase pro band for unlimited room");

            purchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent createClub = new Intent(getActivity(), premiun.class);
                    startActivity(createClub);
                }
            });
            final boolean[] privacy = {false};

            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    privacy[0] = isChecked;
                }
            });



            Calendar cal = Calendar.getInstance();
            final Date[] date1 = {cal.getTime()};

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createRoom.dismiss();
                }
            });

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (prouser||(roomremaing>0)){
                        if (roomName.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Enter Room Name", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Random random = new Random();
                            if (!prouser){
                                roomremaing =roomremaing-1;
                                mDatabase.child("roomremaining").setValue(roomremaing);
                            }
                            int random1 = random.nextInt(100);
                            int random2 = random.nextInt(100);
                            int random3 = random.nextInt(100);
                            int random4 = random.nextInt(100);
                            int random5 = random.nextInt(100);
                            roomId[0] = random1 +""+ random2+""+random3+""+random4+""+random5+"";
                            roomReference.child(roomId[0]).child("Assets").child("room name").setValue(roomName.getText().toString());
                            String time=date1[0].getYear()+ "/" + date1[0].getMonth()+ "/" +date1[0].getDate()+ " " +date1[0].getHours()+ ":"+date1[0].getMinutes() ;
                            roomReference.child(roomId[0]).child("Assets").child("time").setValue(time);
                            roomReference.child(roomId[0]).child("Assets").child("short Description").setValue(shortDis.getText().toString());
                            if (privacy[0]) {
                                roomReference.child(roomId[0]).child("Assets").child("Privacy").setValue("Private");
                            } else {
                                roomReference.child(roomId[0]).child("Assets").child("Privacy").setValue("Public");
                            }

                            roomReference.child(roomId[0]).child("Assets").child("Started").setValue("Started");
                            if (privacy[0]) {
                                roomReference.child("onGoing").child(roomId[0]).setValue("Private");
                            } else {
                                roomReference.child("onGoing").child(roomId[0]).setValue("Public");
                            }

                            mDatabase.child("Room Participated").setValue(roomId[0]);


                            roomReference.child(roomId[0]).child("Peoples").child(user.getUid()).setValue("Owner").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    createRoom.dismiss();

                                    joinroom(roomId[0],true);
                                }
                            });



                        }
                    }else {
                        Toast.makeText(getActivity(), "Rooms for this month has been ended ", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
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

            getStarted.setVisibility(View.INVISIBLE);

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

                        getStarted.setVisibility(View.GONE);
                        roomStarted.setVisibility(View.VISIBLE);
                        joinRoom.setText("Join Room");



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
                    }
                    else {
                        room.dismiss();
                        Toast.makeText(getActivity(),"Room has ended",Toast.LENGTH_LONG).show();
                    }
                }
            });

            joinRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    room.dismiss();
                    if (joinRoom.getText().toString().equals("Join Room")) {
                        //Room has Started

                        mDatabase.child("Room Participated").setValue(roomId);

                        roomReference.child(roomId).child("Peoples").child(user.getUid()).setValue(positionroom[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                joinroom(roomId,true);
                            }
                        });

//
                    } else {
                        //Notify when room started

                    }


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
            
            whoIsInside.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    room.dismiss();
                    joinroom(roomId,false);
                }
            });
        }
    }

    private void joinroom(String roomId,boolean join) {
        Intent club = new Intent(getActivity(), com.testing.clubhome.Room.room.class);
        club.putExtra("roomid", roomId);
        club.putExtra("join", join);
        startActivity(club);
    }

    boolean internet_connection() {
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


}