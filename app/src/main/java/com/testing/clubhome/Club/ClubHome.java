package com.testing.clubhome.Club;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.testing.clubhome.Backend.VoiceService;
import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.Pages.Help;
import com.testing.clubhome.Pages.MainActivity;
import com.testing.clubhome.Pages.Report;
import com.testing.clubhome.Pages.premiun;
import com.testing.clubhome.Profiles.ProfileActivity;
import com.testing.clubhome.R;
import com.testing.clubhome.Room.RoomHome;
import com.testing.clubhome.supporting.FullLengthListView;
import com.testing.clubhome.supporting.UserListAdapter;
import com.testing.clubhome.supporting.inviteListAdapter;
import com.testing.clubhome.supporting.roomSearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ClubHome extends AppCompatActivity {
    TextView clubName;
    LinearLayout peoplesLayout,roomsLayout,roomSelector,peopleSelector;
    Button join;
    ImageButton setting;
    View roomBar,peoplesBar;
    roomSearchAdapter roomSarch;

    private InterstitialAd mInterstitialAd;
    int numbersofroom=0,numberofliverooom=0;
    Boolean memberOfClub;
    String positionInClub,clubId,nameOfClub;

    TextView shortDescription,description,ownerText,moderatorText,connectedText;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    FullLengthListView owners,moderators,connected;
    DatabaseReference clubReference,userReference,personReference,roomReference;
    LinearLayout noModerator,noConnected;
    LinearLayout inviteOwner,inviteModerator,inviteConnection,createRoom;

    UserListAdapter ownerAdapter,connectedAdapter,moderatorAdapter;
    FullLengthListView roomList;
    boolean privacyOfClub;
    int totalRoomsCount=0;


    List<String> ownerList=new ArrayList();
    List<String> moderatorList=new ArrayList();
    List<String> connectedList=new ArrayList();
    List<String> onGoingRoom=new ArrayList<>();
    List<String> upComingRoom=new ArrayList<>();

    List<String> myConnection=new ArrayList();

    private int ownerNumber,moderatorNumber,connectionNumber;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_home);
        roomSelector=findViewById(R.id.roomSelector);
        peopleSelector=findViewById(R.id.peopleSelector);
        owners=findViewById(R.id.owners);
        connected=findViewById(R.id.connected);
        moderators=findViewById(R.id.moderators);
        noModerator=findViewById(R.id.noModerator);
        noConnected=findViewById(R.id.noConnected);
        roomBar=findViewById(R.id.roomBar);
        peoplesBar=findViewById(R.id.peoplesBar);


        ownerText=findViewById(R.id.ownerText);
        moderatorText=findViewById(R.id.moderatorText);
        connectedText=findViewById(R.id.connectedText);

        roomsLayout=findViewById(R.id.rooms);
        peoplesLayout=findViewById(R.id.peoples);
        join=findViewById(R.id.join);
        clubName=findViewById(R.id.clubName);
        roomList=findViewById(R.id.roomsList);
        createRoom=findViewById(R.id.createRoom);

        inviteOwner=findViewById(R.id.inviteOwner);
        inviteModerator=findViewById(R.id.inviteModerator);
        inviteConnection=findViewById(R.id.inviteConnection);

        setting=findViewById(R.id.setting);

        shortDescription=findViewById(R.id.shortDescription);
        description=findViewById(R.id.Description);



        Intent intent= getIntent();
        clubId =intent.getStringExtra("clubId");

        //FireBase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        clubReference = database.getReference("ClubsInfo").child(clubId);
        roomReference = database.getReference("RoomsInfo");
        userReference=database.getReference("Connections");
        personReference=database.getReference("UsersInfo");
        userReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot d :task.getResult().child(user.getUid()).getChildren()){
                    if(d.getValue().toString().equals("C")) {
                        myConnection.add(d.getKey());
                    }
                }

            }
        });
        MobileAds.initialize(ClubHome.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        if( !Constants.PRO) {
            AdView madview=findViewById(R.id.adView);
            AdRequest adRequest=new AdRequest.Builder().build();
            madview.loadAd(adRequest);

            mInterstitialAd= new InterstitialAd(ClubHome.this);
            mInterstitialAd.setAdUnitId(getString(R.string.Interstial_app_id));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }

        clubReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Assets").exists()) {

                    nameOfClub = snapshot.child("Assets").child("club name").getValue().toString();
                    clubName.setText(nameOfClub);
                    totalRoomsCount=0;
                    shortDescription.setText(snapshot.child("Assets").child("club Short Description").getValue().toString());
                    description.setText(snapshot.child("Assets").child("club Description").getValue().toString());
                    if (snapshot.child("Assets").child("totalRoomsCount").exists()) {
                        totalRoomsCount= Integer.parseInt(snapshot.child("Assets").child("totalRoomsCount").getValue().toString());
                    }
                    privacyOfClub=snapshot.child("Assets").child("Privacy").getValue().toString().equals("Private");
                    upComingRoom.clear();
                    onGoingRoom.clear();
                    if(snapshot.child("Rooms").exists()){
                        for(DataSnapshot s:snapshot.child("Rooms").getChildren()){
                            if(s.getValue().toString().equals("Not Started")){
                                upComingRoom.add(s.getKey());
                            }else {
                                onGoingRoom.add(s.getKey());
                            }
                        }
                    }
                    numbersofroom=0;
                    if (snapshot.child("Assets").child("totalRoomsCount").exists()) {
                        numbersofroom=Integer.parseInt(snapshot.child("Assets").child("totalRoomsCount").getValue().toString());
                    }
                    numberofliverooom=0;
                    for(DataSnapshot d:snapshot.child("Rooms").getChildren()){
                        numberofliverooom++;
                    }

                    List<String>all=new ArrayList<>();
                    all.addAll(upComingRoom);
                    all.addAll(onGoingRoom);
                    roomSarch = new roomSearchAdapter(ClubHome.this, all);
                    roomSarch.notifyDataSetChanged();
                    roomList.setAdapter(roomSarch);



                    if (snapshot.child("Peoples").child(user.getUid()).exists()) {
                        memberOfClub = true;
                        positionInClub = snapshot.child("Peoples").child(user.getUid()).getValue().toString();
                        if(positionInClub.equals("Owner")||positionInClub.equals("Moderator")){
                            createRoom.setVisibility(View.VISIBLE);
                        }else{
                            createRoom.setVisibility(View.GONE);
                        }

                    } else {
                        createRoom.setVisibility(View.GONE);
                        memberOfClub = false;
                        positionInClub = null;
                    }

                    ownerList.clear();
                    moderatorList.clear();
                    connectedList.clear();
                    ownerNumber=0;
                    moderatorNumber=0;
                    connectionNumber=0;
                    for(DataSnapshot data :snapshot.child("Peoples").getChildren()){
                        if(data.getValue().toString().equals("Owner")){
                            ownerList.add(data.getKey());
                            ownerNumber++;
                        }else if(data.getValue().toString().equals("Moderator")){
                            moderatorList.add(data.getKey());
                            moderatorNumber++;
                        }else{
                            connectedList.add(data.getKey());
                            connectionNumber++;
                        }
                    }
                    ownerText.setText("Owners  ("+ownerNumber+")");
                    moderatorText.setText("Moderators  ("+moderatorNumber+")");
                    connectedText.setText("Connections  ("+connectionNumber+")");

                    peoplesList();
                    checkForStructure();
                }
                else {
                   finish();
                   Toast.makeText(ClubHome.this,"Club is not available.",Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //default container
        roomBar.setVisibility(View.VISIBLE);
        peoplesBar.setVisibility(View.INVISIBLE);
        roomsLayout.setVisibility(View.VISIBLE);
        peoplesLayout.setVisibility(View.GONE);

        //join
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clubReference.child("Peoples").child(user.getUid()).setValue("C");
            }
        });

        //select
        roomSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roomBar.setVisibility(View.VISIBLE);
                peoplesBar.setVisibility(View.INVISIBLE);
                roomsLayout.setVisibility(View.VISIBLE);
                peoplesLayout.setVisibility(View.GONE);

            }
        });

        peopleSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomBar.setVisibility(View.INVISIBLE);
                peoplesBar.setVisibility(View.VISIBLE);

                roomsLayout.setVisibility(View.GONE);
                peoplesLayout.setVisibility(View.VISIBLE);
            }
        });

        //invite
        inviteOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(positionInClub.equals("Owner")) {
                    invite("Owner");
                }
            }
        });

        inviteModerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(positionInClub.equals("Owner")||positionInClub.equals("Moderator")) {
                    invite("Moderator");
                }
            }
        });

        inviteConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invite("Connection");
            }
        });

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createtheRoom();
            }
        });

        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String>all=new ArrayList<>();
                all.addAll(upComingRoom);
                all.addAll(onGoingRoom);
                if(VoiceService.getInstance()==null){
                    roomOpen(all.get(position));
                }else{
                    if(!VoiceService.getInstance().getRoomId().equals(all.get(position))){
                        //leave previous room
                        Toast.makeText(ClubHome.this,"Leave the previous room",Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    private void createtheRoom() {
        if(!internet_connection()){
            noInternet();
        }
        else {
            final Dialog createRoom = new Dialog(ClubHome.this);
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


            purchase.setVisibility(View.GONE);
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

                        if (roomName.getText().toString().isEmpty()) {
                            Toast.makeText(ClubHome.this, "Enter Room Name", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Random random = new Random();

                            int random1 = random.nextInt(100);
                            int random2 = random.nextInt(100);
                            int random3 = random.nextInt(100);
                            int random4 = random.nextInt(100);
                            int random5 = random.nextInt(100);
                            roomId[0] = random1 +""+ random2+""+random3+""+random4+""+random5+"";
                            roomReference.child(roomId[0]).child("Assets").child("hosted by").setValue(clubId);
                            roomReference.child(roomId[0]).child("Assets").child("room name").setValue(roomName.getText().toString());
                            String time=date1[0].getYear()+ "/" + date1[0].getMonth()+ "/" +date1[0].getDate()+ " " +date1[0].getHours()+ ":"+date1[0].getMinutes() ;
                            roomReference.child(roomId[0]).child("Assets").child("time").setValue(time);
                            roomReference.child(roomId[0]).child("Assets").child("short Description").setValue(shortDis.getText().toString());


                            clubReference.child("Assets").child("totalRoomsCount").setValue(totalRoomsCount+1);

                            if (privacy[0]) {
                                roomReference.child(roomId[0]).child("Assets").child("Privacy").setValue("Private");
                            } else {
                                roomReference.child(roomId[0]).child("Assets").child("Privacy").setValue("Public");
                            }
                            clubReference.child("Rooms").child(roomId[0]).setValue("Started");

                            roomReference.child(roomId[0]).child("Assets").child("Started").setValue("Started");
                            if (privacy[0]) {
                                roomReference.child("onGoing").child(roomId[0]).setValue("Private");
                            } else {
                                roomReference.child("onGoing").child(roomId[0]).setValue("Public");
                            }


                            personReference.child(user.getUid()).child("Room Participated").setValue(roomId[0]);


                            roomReference.child(roomId[0]).child("Peoples").child(user.getUid()).setValue("Owner").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    createRoom.dismiss();

                                    joinroom(roomId[0],true);
                                }
                            });



                        }

               }



            });
        }
    }

    private void roomOpen(String roomId){
        if(!internet_connection()){
            noInternet();
        }else {
            final Dialog room = new Dialog(ClubHome.this);
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



                        clubLayout.setVisibility(View.GONE);
                    }
                    else {
                        room.dismiss();
                        Toast.makeText(ClubHome.this,"Room has ended",Toast.LENGTH_LONG).show();
                    }
                }
            });

            joinRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    room.dismiss();
                    if (joinRoom.getText().toString().equals("Join Room")) {
                        //Room has Started

                        userReference.child(user.getUid()).child("Room Participated").setValue(roomId);

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

                    room.dismiss();

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
        Intent club = new Intent(ClubHome.this, com.testing.clubhome.Room.room.class);
        club.putExtra("roomid", roomId);
        club.putExtra("join", join);
        startActivity(club);
    }

    private void invite(String whomToInvite){
        final Dialog inviting = new Dialog(this);
        inviting.setContentView(R.layout.invite_for_club);
        inviting.setCancelable(true);
        inviting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        inviting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        inviting.getWindow().setGravity(Gravity.BOTTOM);
        inviting.show();

        //intialization
        TextView title=inviting.findViewById(R.id.whomToInvite);
        TextView body=inviting.findViewById(R.id.info);
        ImageView back=inviting.findViewById(R.id.back);
        ListView followers=inviting.findViewById(R.id.followers);

        inviteListAdapter inviteAdapter = new inviteListAdapter(this,myConnection,whomToInvite,clubId,clubName.getText().toString());

        followers.setAdapter(inviteAdapter);
        followers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.getAdapter().getView(position,view,parent).setBackgroundResource(R.color.colorCream);
            }
        });

        title.setText("Invite "+whomToInvite+" to "+nameOfClub );

        if(whomToInvite.equals("Owner")){
            body.setText("Owner can manage rooms, positions and setting ");

        }else if(whomToInvite.equals("Moderator")){
            body.setText("Moderator can manage rooms and positions");
        }else{
            body.setText("Connected Users can get notification for the Space");
        }




        //onClick
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviting.dismiss();
            }
        });


    }

    private void checkForStructure() {
        if(memberOfClub){
            //he is member of the club
            join.setVisibility(View.GONE);
            setting.setVisibility(View.VISIBLE);

            if(positionInClub.equals("Owner")){
                inviteOwner.setVisibility(View.VISIBLE);
                inviteModerator.setVisibility(View.VISIBLE);
                inviteConnection.setVisibility(View.VISIBLE);

            }else if(positionInClub.equals("Moderator")){
                inviteOwner.setVisibility(View.GONE);
                inviteModerator.setVisibility(View.VISIBLE);
                inviteConnection.setVisibility(View.VISIBLE);

            }else{

                inviteOwner.setVisibility(View.GONE);
                inviteModerator.setVisibility(View.GONE);
                inviteConnection.setVisibility(View.VISIBLE);
            }

        }else{
            //he is not member of the club

            join.setVisibility(View.VISIBLE);
            setting.setVisibility(View.GONE);
            inviteOwner.setVisibility(View.GONE);
            inviteModerator.setVisibility(View.GONE);
            inviteConnection.setVisibility(View.GONE);

        }
    }

    public void setting(View view) {
        settingNavigation();
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

    protected void settingNavigation(){
        final Dialog nav = new Dialog(this);
        nav.setContentView(R.layout.club_setting_nav);
        nav.setCancelable(true);
        nav.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nav.setCanceledOnTouchOutside(true);
        nav.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        nav.getWindow().setGravity(Gravity.BOTTOM);

        nav.show();
        //initialization
        LinearLayout share=nav.findViewById(R.id.share);
        LinearLayout invite=nav.findViewById(R.id.invite);
        LinearLayout privacy=nav.findViewById(R.id.privacy);
        LinearLayout reportGroup=nav.findViewById(R.id.reportGroup);
        LinearLayout help=nav.findViewById(R.id.help);
        LinearLayout rules=nav.findViewById(R.id.rules);
        LinearLayout editClub=nav.findViewById(R.id.editClub);
        LinearLayout leaveGroup=nav.findViewById(R.id.leaveClub);
        LinearLayout lastRow=nav.findViewById(R.id.lastRow);
        LinearLayout makePrivate=nav.findViewById(R.id.makePrivate);


        ImageView back=nav.findViewById(R.id.back);
        TextView title=nav.findViewById(R.id.toolTest);

        if(memberOfClub) {
            if(positionInClub.equals("Owner")){
                //owner tool
                title.setText("Owner Tools");
            }else if(positionInClub.equals("Moderator")){
                //moderator tool
                title.setText("Moderator Tools");
                editClub.setVisibility(View.INVISIBLE);
                makePrivate.setVisibility(View.INVISIBLE);

            }else{
                //connection tool
                title.setText("Tools");
                makePrivate.setVisibility(View.INVISIBLE);
                editClub.setVisibility(View.INVISIBLE);
            }

        }else {
            title.setText("Tools");
            lastRow.setVisibility(View.INVISIBLE);
        }

        //Onclick

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.dismiss();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Constants.PlayStoreLink);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Clubhouse");
                startActivity(Intent.createChooser(intent,"Share Using"));
                nav.dismiss();
            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invite("Connection");
                nav.dismiss();
            }
        });

        reportGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent club=new Intent(getApplicationContext(), Report.class);
                club.putExtra("Id",clubId);
                club.putExtra("which","Club");
                startActivity(club);
                nav.dismiss();
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent
                Intent club=new Intent(ClubHome.this, Help.class);
                club.putExtra("purpose","Privacy");
                club.putExtra("which","Room");
                startActivity(club);
                nav.dismiss();
            }
        });

        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent
                Intent club=new Intent(ClubHome.this, Help.class);
                club.putExtra("purpose","Rules");
                club.putExtra("which","Room");
                startActivity(club);
                nav.dismiss();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent
                Intent club=new Intent(ClubHome.this, Help.class);
                club.putExtra("purpose","Help");
                club.putExtra("which","Room");
                startActivity(club);
                nav.dismiss();
            }
        });

        editClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent

                Intent club=new Intent(getApplicationContext(), EditClub.class);
                club.putExtra("clubId",clubId);
                startActivity(club);
                nav.dismiss();
            }
        });

        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ownerList.size()==1 && positionInClub.equals("Owner")){
                    clubReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            nav.dismiss();
                            finish();
                        }
                    });
                }else {
                    clubReference.child("Peoples").child(user.getUid()).removeValue();
                }
                personReference.child(user.getUid()).child("Club").child(clubId).removeValue();
                nav.dismiss();
            }
        });

    }

    protected void peoplesList(){
        //Add to list of Owner

        ownerAdapter=new UserListAdapter(ClubHome.this,ownerList);
        owners.setAdapter(ownerAdapter);
        owners.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ClubHome.this, ProfileActivity.class);
                intent.putExtra("user",ownerList.get(position));
                startActivity(intent);
            }
        });
        owners.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(positionInClub.equals("Owner")){
                    if(!user.getUid().equals(ownerList.get(position))){
                        //Dialog Box for setting of user

                        longClickDiolog("OwnerToOwner",ownerList.get(position));
                    }
                }
                return false;
            }
        });


        //Add to list of Moderator
        if(moderatorList.isEmpty()){
            noModerator.setVisibility(View.VISIBLE);
            moderators.setVisibility(View.GONE);

        }else{
            noModerator.setVisibility(View.GONE);
            moderators.setVisibility(View.VISIBLE);
            moderatorAdapter=new UserListAdapter(ClubHome.this,moderatorList);
            moderators.setAdapter(moderatorAdapter);
            moderators.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(ClubHome.this, ProfileActivity.class);
                    intent.putExtra("user",moderatorList.get(position));
                    startActivity(intent);
                }
            });
            moderators.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if(positionInClub.equals("Owner")){
                        //Dialog Box for setting of user
                        longClickDiolog("OwnerToModerator",moderatorList.get(position));

                    }
                    return false;
                }
            });


        }

        //Add to list of Connected
        if(connectedList.isEmpty()){
            noConnected.setVisibility(View.VISIBLE);
            connected.setVisibility(View.GONE);

        }else{
            noConnected.setVisibility(View.GONE);
            connected.setVisibility(View.VISIBLE);
            connectedAdapter=new UserListAdapter(ClubHome.this,connectedList);
            connected.setAdapter(connectedAdapter);
            connected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(ClubHome.this, ProfileActivity.class);
                    intent.putExtra("user",connectedList.get(position));
                    startActivity(intent);
                }
            });
            connected.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if(positionInClub.equals("Owner")){
                        //Dialog Box for setting of user
                        longClickDiolog("OwnerToConnection",connectedList.get(position));

                    }else if(positionInClub.equals("Moderator")){
                        longClickDiolog("ModeratorToConnection",connectedList.get(position));
                    }
                    return false;
                }
            });

        }

    }

    private void longClickDiolog(String whatFor,String userClickedOn) {
        List <String> dialogOnLongPress= new ArrayList<String>();

        if(whatFor.equals("OwnerToOwner")){
            dialogOnLongPress.clear();
            dialogOnLongPress.add("Remove from Club");
            dialogOnLongPress.add("Change to Moderator");
            dialogOnLongPress.add("Change to Connection");

        }else if(whatFor.equals("OwnerToModerator")){
            dialogOnLongPress.clear();
            dialogOnLongPress.add("Remove from Club");
            dialogOnLongPress.add("Change to Owner");
            dialogOnLongPress.add("Change to Connection");

        }else if(whatFor.equals("OwnerToConnection")){
            dialogOnLongPress.clear();
            dialogOnLongPress.add("Remove from Club");
            dialogOnLongPress.add("Change to Moderator");
            dialogOnLongPress.add("Change to Owner");

        }else if(whatFor.equals("ModeratorToConnection")){
            dialogOnLongPress.clear();
            dialogOnLongPress.add("Remove from Club");
            dialogOnLongPress.add("Change to Moderator");
        }
        final Dialog nav = new Dialog(this);
        nav.setContentView(R.layout.list_dialog);
        nav.setCancelable(true);
        nav.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nav.setCanceledOnTouchOutside(true);
        nav.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        nav.getWindow().setGravity(Gravity.BOTTOM);
        nav.show();

        ArrayAdapter arrayAdapter=new ArrayAdapter(this,R.layout.text_center,dialogOnLongPress);
        ListView list=nav.findViewById(R.id.setting);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    clubReference.child("Peoples").child(userClickedOn).removeValue();
                    personReference.child(userClickedOn).child("Club").child(clubId).removeValue();
                }
               else if(position==1){
                    if(whatFor.equals("OwnerToOwner")||whatFor.equals("ModeratorToConnection")||whatFor.equals("OwnerToConnection")){
                        clubReference.child("Peoples").child(userClickedOn).setValue("Moderator");
                        personReference.child(userClickedOn).child("Club").child(clubId).setValue("Moderator");
                    }else if(whatFor.equals("OwnerToModerator")){
                        clubReference.child("Peoples").child(userClickedOn).setValue("Owner");
                        personReference.child(userClickedOn).child("Club").child(clubId).setValue("Owner");
                    }
               }
               else if(position==2){
                    if(whatFor.equals("OwnerToOwner")||whatFor.equals("OwnerToModerator")){
                        clubReference.child("Peoples").child(userClickedOn).setValue("Connection");
                        personReference.child(userClickedOn).child("Club").child(clubId).setValue("Connection");
                    }else if(whatFor.equals("OwnerToConnection")){
                        clubReference.child("Peoples").child(userClickedOn).setValue("Owner");
                        personReference.child(userClickedOn).child("Club").child(clubId).setValue("Owner");
                    }else if(whatFor.equals("ModeratorToConnection")){
                        clubReference.child("Peoples").child(userClickedOn).setValue("Moderator");
                        personReference.child(userClickedOn).child("Club").child(clubId).setValue("Moderator");
                    }
               }




            }
        });
    }

    public void inviting(View view) {
        invite(view.getTag().toString());
    }

}