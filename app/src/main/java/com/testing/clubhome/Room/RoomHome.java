package com.testing.clubhome.Room;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testing.clubhome.Backend.VoiceService;
import com.testing.clubhome.Club.ClubHome;
import com.testing.clubhome.Pages.Help;
import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.Pages.Report;
import com.testing.clubhome.R;
import com.testing.clubhome.supporting.FullLengthListView;
import com.testing.clubhome.supporting.inviteRoomlist;
import com.testing.clubhome.supporting.roomUserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public  class RoomHome extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String roomId;

    public boolean joinGroup;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase,clubReference,userReference,roomReference,mainReference;
    private FirebaseUser user;

    RelativeLayout raiseHand;
    ImageView setting,rasinghand;
    TextView moderatorText,raiseHandText,onStageText,listnerText;
    FullLengthListView moderatorList,raiseHandList,onStageList,listnerList;
//    onMessageReadListner messageReadListner;

    List <String> myConnection=new ArrayList<>();

    List <String> owner=new ArrayList<>();
    List <String> listner=new ArrayList<>();
    List <String> onstage=new ArrayList<>();
    List <String> raisehand=new ArrayList<>();

    int ownerNo,listenerNo,onstageNo,raiseHandNo;

    String ClubName="",ClubId="";

    String positionroom="Owner";

    private final int PERMISSION_REQ_ID_RECORD_AUDIO=22;

    roomUserAdapter listenerAdapter,onStageAdapter,raiseHandAdapter;


    private boolean microphoneOn;
    private boolean microphoneprovided=true,speakerenabled=true;

    private String roomname;


    public RoomHome(String roomId,boolean joinTheGroup) {
        // Required empty public constructor
        this.roomId=roomId;
        this.joinGroup=joinTheGroup;
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference("UsersInfo").child(user.getUid());
        userReference=firebaseDatabase.getReference("Connections");
        clubReference = firebaseDatabase.getReference("ClubsInfo");
        roomReference = firebaseDatabase.getReference("RoomsInfo").child(roomId);
        mainReference = firebaseDatabase.getReference("RoomsInfo");
        microphoneOn=false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_home, container, false);

        raiseHand=view.findViewById(R.id.raiseHandandaudio);

        setting=view.findViewById(R.id.setting);
        moderatorText=view.findViewById(R.id.moderatorsText);
        raiseHandText=view.findViewById(R.id.raisedHandsText);
        onStageText=view.findViewById(R.id.onStageText);
        listnerText=view.findViewById(R.id.listenerText);

        listnerList=view.findViewById(R.id.listenerList);
        raiseHandList=view.findViewById(R.id.raisedHandList);
        onStageList=view.findViewById(R.id.onStageList);
        moderatorList=view.findViewById(R.id.moderatorsList);
        rasinghand=view.findViewById(R.id.rasinghand);



        findRoomData();

        myConnectionData();

        if(joinGroup){
            joinedTheGroup();
        }else{
            whoIsInside();
        }

        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO,PERMISSION_REQ_ID_RECORD_AUDIO)) {
           roomReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DataSnapshot> task) {
//                   initAgoraEngineAndJoinChannel();
               }
           });
        }



        raiseHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(microphoneprovided) {
                    if (microphoneOn){
                        microphoneOn=false;
                        rasinghand.setBackgroundResource(R.drawable.ic_muteoff);
                        roomReference.child("Audio On").child(user.getUid()).removeValue();
                        VoiceService.getInstance().mutePresed(true);
                    }else {
                        microphoneOn=true;
                        rasinghand.setBackgroundResource(R.drawable.ic_mute);
                        roomReference.child("Audio On").child(user.getUid()).setValue("On");
                        VoiceService.getInstance().mutePresed(false);
                    }
                }else {
                    roomReference.child("Peoples").child(user.getUid()).setValue("Raise Hand");
                }
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingNavigation();
            }
        });


        return view;

    }

    private boolean checkSelfPermission(String recordAudio, int permission_req_id_record_audio) {
        if(ContextCompat.checkSelfPermission(getActivity(),recordAudio)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{recordAudio},permission_req_id_record_audio);
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
//                    initAgoraEngineAndJoinChannel();
                }else{
//                    messageReadListner.onMessage();
//                    getFragmentManager().popBackStack();
                }
                break;
            }
        }
    }

    private void myConnectionData() {
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
    }

    protected void settingNavigation(){
        final Dialog nav = new Dialog(getActivity());
        nav.setContentView(R.layout.room_setting_nav);
        nav.setCancelable(true);
        nav.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nav.setCanceledOnTouchOutside(true);
        nav.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        nav.getWindow().setGravity(Gravity.BOTTOM);


        nav.show();

        //initialization
        LinearLayout pin=nav.findViewById(R.id.pin);
        LinearLayout invite=nav.findViewById(R.id.invite);
        LinearLayout speakerormic=nav.findViewById(R.id.speakerormic);
        LinearLayout reportGroup=nav.findViewById(R.id.reportGroup);
        LinearLayout help=nav.findViewById(R.id.help);
        LinearLayout rules=nav.findViewById(R.id.rules);
        TextView speakerormicText=nav.findViewById(R.id.speakerormicText);
        ImageView back=nav.findViewById(R.id.back);
        ImageView speakerormicimage=nav.findViewById(R.id.speakerormicimage);

        //Onclick
        if (speakerenabled){
            speakerormicimage.setBackgroundResource(R.drawable.ic_speaker);
            speakerormicText.setText("Speaker enabled");
        }else {
            speakerormicimage.setBackgroundResource(R.drawable.ic_baseline_headset_24);
            speakerormicText.setText("Mic enabled");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.dismiss();
            }
        });

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,Constants.PlayStoreLink);
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                startActivity(Intent.createChooser(intent,"Share Using"));

            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                invite("Listener");
                nav.dismiss();
            }
        });

        reportGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent club=new Intent(getActivity(), Report.class);
                club.putExtra("Id",roomId);
                club.putExtra("which","Room");
                startActivity(club);
                nav.dismiss();
            }
        });

        speakerormic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent
                if (speakerenabled){
                    speakerormicimage.setBackgroundResource(R.drawable.ic_baseline_headset_24);
                    speakerormicText.setText("Mic enabled");
                    VoiceService.getInstance().setspeaker(false);
                    speakerenabled=false;
                }else {
                    speakerormicimage.setBackgroundResource(R.drawable.ic_speaker);
                    speakerormicText.setText("Speaker enabled");
                    speakerenabled=true;
                    VoiceService.getInstance().setspeaker(true);
                }
                VoiceService.getInstance().setspeaker(speakerenabled);
            }
        });

        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent
                Intent club=new Intent(getActivity(), Help.class);
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
                Intent club=new Intent(getActivity(), Help.class);
                club.putExtra("purpose","Help");
                club.putExtra("which","Room");
                startActivity(club);
                nav.dismiss();
            }
        });


    }

    private void findRoomData() {
        roomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.child("Assets").child("room name").exists()) {
                    roomname=snapshot.child("Assets").child("room name").getValue().toString();
                    }
                    //checking for my position in the room
                    if (joinGroup){
                        if (snapshot.child("Peoples").child(user.getUid()).exists()) {
                            positionroom=snapshot.child("Peoples").child(user.getUid()).getValue().toString();

                            if (positionroom.equals("Owner")) {
                                microphoneprovided=true;

                                if ( microphoneOn){
                                    rasinghand.setBackgroundResource(R.drawable.ic_muteoff);
                                }else {
                                    rasinghand.setBackgroundResource(R.drawable.ic_mute);
                                }


                            }
                            else if (positionroom.equals("Onstage")) {
                                microphoneprovided=true;

                                if ( microphoneOn){
                                    rasinghand.setBackgroundResource(R.drawable.ic_muteoff);
                                }else {
                                    rasinghand.setBackgroundResource(R.drawable.ic_mute);
                                }
                            }
                            else if (positionroom.equals("Listener")) {
                                microphoneprovided=false;
                                rasinghand.setBackgroundResource(R.drawable.ic_handraise);

                            }
                            else if (positionroom.equals("Raise Hand")) {
                                microphoneprovided=false;
                                rasinghand.setBackgroundResource(R.drawable.ic_handraise);
                            }
                        }
                        else {

                            //
                        }
                    }
                    else {
                        if (snapshot.child("Peoples").child(user.getUid()).exists()) {
                            positionroom = snapshot.child("Peoples").child(user.getUid()).getValue().toString();
                        }
                    }
                }



                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        roomReference.child("Peoples").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {




                    ///Displaying up all the users

                    owner.clear();
                    onstage.clear();
                    listner.clear();
                    raisehand.clear();
                    ownerNo = 0;
                    onstageNo = 0;
                    listenerNo = 0;
                    raiseHandNo = 0;
                    if (getActivity()!=null) {
                        String ownerss = "", raisehandss = "", onstagess = "", listss = "";
                        for (DataSnapshot s : snapshot.getChildren()) {
                            if (s.getValue().toString().equals("Owner")) {
                                ownerss += s.getKey() + "#";
                                if (ownerNo == 0) {
                                    ownerss="";
                                    ownerss += s.getKey() + "#";
                                    owner.add((int) ownerNo / 4, ownerss);
                                } else {
                                    owner.set((int) ownerNo / 4, ownerss);
                                }
                                ownerNo++;
                            } else if (s.getValue().toString().equals("Listener")) {
                                listss += s.getKey() + "#";
                                if (listenerNo == 0) {
                                    listss="";
                                    listss += s.getKey() + "#";
                                    listner.add((int) listenerNo / 4, listss);
                                } else {
                                    listner.set((int) listenerNo / 4, listss);
                                }
                                listenerNo++;
                            } else if (s.getValue().toString().equals("Onstage")) {
                                onstagess += s.getKey() + "#";
                                if (onstageNo == 0) {

                                    onstagess="";
                                    onstagess += s.getKey() + "#";
                                    onstage.add((int) onstageNo / 4, onstagess);
                                } else {
                                    onstage.set((int) onstageNo / 4, onstagess);
                                }
                                onstageNo++;
                            } else if (s.getValue().toString().equals("Raise Hand")) {
                                raisehandss += s.getKey() + "#";
                                if (raiseHandNo == 0) {

                                    raisehandss="";
                                    raisehandss += s.getKey() + "#";
                                    raisehand.add((int) raiseHandNo / 4, raisehandss);
                                } else {
                                    raisehand.set((int) raiseHandNo / 4, raisehandss);
                                }
                                raiseHandNo++;
                            }
                        }

                        moderatorText.setText("Moderators  (" + ownerNo + ")");
                        listnerText.setText("Listener  (" + listenerNo + ")");
                        onStageText.setText("On Stage  (" + onstageNo + ")");
                        raiseHandText.setText("Raised hand  (" + raiseHandNo + ")");

                        if (getActivity()!=null){
                            roomUserAdapter ownerAdapter = new roomUserAdapter(getActivity(), owner, joinGroup, roomId, "Owner", positionroom);
                            ownerAdapter.notifyDataSetChanged();
                            moderatorList.setAdapter(ownerAdapter);
                        }


                        if (getActivity()!=null){
                            onStageAdapter = new roomUserAdapter(getActivity(), onstage, joinGroup, roomId, "Onstage", positionroom);
                            onStageAdapter.notifyDataSetChanged();
                            onStageList.setAdapter(onStageAdapter);
                        }

                        if (getActivity()!=null){
                            listenerAdapter = new roomUserAdapter(getActivity(), listner, joinGroup, roomId, "Listener", positionroom);
                            listenerAdapter.notifyDataSetChanged();
                            listnerList.setAdapter(listenerAdapter);
                        }

                        if (getActivity()!=null){
                            raiseHandAdapter = new roomUserAdapter(getActivity(), raisehand, joinGroup, roomId, "Raise Hand", positionroom);
                            raiseHandAdapter.notifyDataSetChanged();
                            raiseHandList.setAdapter(raiseHandAdapter);


                        }

                        if (onstage.isEmpty()) {
                            onStageText.setVisibility(View.GONE);
                            onStageList.setVisibility(View.GONE);
                        } else {
                            onStageText.setVisibility(View.VISIBLE);
                            onStageList.setVisibility(View.VISIBLE);
                        }

                        if (listner.isEmpty()) {
                            listnerText.setVisibility(View.GONE);
                            listnerList.setVisibility(View.GONE);
                        } else {
                            listnerText.setVisibility(View.VISIBLE);
                            listnerList.setVisibility(View.VISIBLE);
                        }

                        if (raisehand.isEmpty()) {
                            raiseHandText.setVisibility(View.GONE);
                            raiseHandList.setVisibility(View.GONE);
                        } else {
                            raiseHandText.setVisibility(View.VISIBLE);
                            raiseHandList.setVisibility(View.VISIBLE);
                        }
                    }


                }
                else{
//                    //Room has ended
//                    leaveChannel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void joinedTheGroup(){
        raiseHand.setVisibility(View.VISIBLE);
        setting.setVisibility(View.VISIBLE);

    }

    private void whoIsInside(){
        raiseHand.setVisibility(View.INVISIBLE);
        setting.setVisibility(View.INVISIBLE);
    }

    private void invite(String whomToInvite){
        final Dialog inviting = new Dialog(getActivity());
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

        inviteRoomlist inviteAdapter = new inviteRoomlist(getActivity(),myConnection,whomToInvite,roomId,roomname);

        followers.setAdapter(inviteAdapter);
        followers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.getAdapter().getView(position,view,parent).setBackgroundResource(R.color.colorCream);
            }
        });

        title.setText("Invite "+whomToInvite+" to Room");

        body.setText("Welcome Your Friends to Listen to the Room");



        //onClick
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviting.dismiss();
            }
        });


    }

}