package com.testing.clubhome.supporting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testing.clubhome.Profiles.ProfileActivity;
import com.testing.clubhome.R;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class roomUserAdapter extends ArrayAdapter<String> {
    private List<String> userList=new ArrayList();
    private final Activity context;
    //Firebase initialization
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase,roomReference,connectionData;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private boolean insideTheRoom;
    private String roomId,partPosition,userPosition,whatFor;


    public roomUserAdapter(@NonNull Activity context, List<String> userList,boolean insideTheRoom,String roomId,String partPosition,String userPosition) {
        super(context,R.layout.rooms_people_layout, userList);
        this.userList=userList;
        this.context = context;
        this.insideTheRoom=insideTheRoom;
        this.roomId=roomId;
        this.partPosition=partPosition;
        this.userPosition=userPosition;
    }

    public View getView(final int position , View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        final View rowView=inflater.inflate(R.layout.rooms_people_layout, null,true);



        TextView firstName=rowView.findViewById(R.id.userNameOne);
        TextView secondName=rowView.findViewById(R.id.userNameTwo);
        TextView thirdName=rowView.findViewById(R.id.userNameThree);
        TextView fourthName=rowView.findViewById(R.id.userNameFour);

        LinearLayout firstUser=rowView.findViewById(R.id.userOne);
        LinearLayout secondUser=rowView.findViewById(R.id.userTwo);
        LinearLayout thirdUser=rowView.findViewById(R.id.userThree);
        LinearLayout fourthUser=rowView.findViewById(R.id.userFour);

        ShapeableImageView firstImage=rowView.findViewById(R.id.profilePhotoOne);
        ShapeableImageView secondImage=rowView.findViewById(R.id.profilePhotoTwo);
        ShapeableImageView thirdUImage=rowView.findViewById(R.id.profilePhotoThree);
        ShapeableImageView fourthImage=rowView.findViewById(R.id.profilePhotoFour);

        RelativeLayout speaking1= rowView.findViewById(R.id.speaking1);
        RelativeLayout speaking2= rowView.findViewById(R.id.speaking2);
        RelativeLayout speaking3= rowView.findViewById(R.id.speaking3);
        RelativeLayout speaking4= rowView.findViewById(R.id.speaking4);

        ImageView audio1= rowView.findViewById(R.id.audioOne);
        ImageView audio2= rowView.findViewById(R.id.audioTwo);
        ImageView audio3= rowView.findViewById(R.id.audioThree);
        ImageView audio4= rowView.findViewById(R.id.audioFour);



        String []parts=userList.get(position).split("#");


        //getting FireBase Values
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase=firebaseDatabase.getReference("UsersInfo");
        connectionData=firebaseDatabase.getReference("Connections");
        roomReference=firebaseDatabase.getReference("RoomsInfo").child(roomId);


        roomReference.child("Audio On").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(insideTheRoom) {

                        if (snapshot.child(parts[0]).exists()) {
                            speaking1.setBackgroundResource(R.drawable.speaking);
                            audio1.setVisibility(View.INVISIBLE);
                        } else {
                            speaking1.setBackgroundResource(R.color.colorWhite);
                            audio1.setVisibility(View.VISIBLE);
                        }
                        if (parts.length > 1) {
                            if (snapshot.child(parts[1]).exists()) {
                                speaking2.setBackgroundResource(R.drawable.speaking);
                                audio2.setVisibility(View.INVISIBLE);
                            } else {
                                speaking2.setBackgroundResource(R.color.colorWhite);
                                audio2.setVisibility(View.VISIBLE);
                            }
                        }
                        if (parts.length > 2) {
                            if (snapshot.child(parts[2]).exists()) {
                                speaking3.setBackgroundResource(R.drawable.speaking);
                                audio3.setVisibility(View.INVISIBLE);
                            } else {
                                speaking3.setBackgroundResource(R.color.colorWhite);
                                audio3.setVisibility(View.VISIBLE);
                            }
                        }
                        if (parts.length > 3) {
                            if (snapshot.child(parts[3]).exists()) {
                                speaking4.setBackgroundResource(R.drawable.speaking);
                                audio4.setVisibility(View.INVISIBLE);
                            } else {
                                speaking4.setBackgroundResource(R.color.colorWhite);
                                audio4.setVisibility(View.VISIBLE);
                            }
                        }


                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {
                    String image1="",image2 = "",image3="",image4="";
                    //first Name
                    firstName.setText(task.getResult().child(parts[0]).child("name").getValue().toString());
                    image1=""+task.getResult().child( parts[0]).child("profilePhoto").getValue();
                    try {
                        Picasso.get().load(image1).into(firstImage);
                     } catch (Exception e) {
                        Picasso.get().load(R.drawable.user).into(firstImage);
                     }

                    //2 Name

                    if(parts.length>1) {
                        secondUser.setVisibility(View.VISIBLE);
                        secondName.setText(task.getResult().child(parts[1]).child("name").getValue().toString());
                        image2 = "" + task.getResult().child(parts[1]).child("profilePhoto").getValue();

                        try {
                            Picasso.get().load(image2).into(secondImage);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.user).into(secondImage);
                        }
                    }else{
                        secondUser.setVisibility(View.INVISIBLE);
                    }
                    //3 Name
                    if(parts.length>2) {
                        thirdUser.setVisibility(View.VISIBLE);
                        thirdName.setText(task.getResult().child(parts[2]).child("name").getValue().toString());
                        image3 = "" + task.getResult().child(parts[2]).child("profilePhoto").getValue();

                        try {
                            Picasso.get().load(image3).into(thirdUImage);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.user).into(thirdUImage);
                        }
                    }
                    else {
                        thirdUser.setVisibility(View.INVISIBLE);
                    }
                    //fourth Name
                    if(parts.length>3) {
                        fourthUser.setVisibility(View.VISIBLE);
                        fourthName.setText(task.getResult().child(parts[3]).child("name").getValue().toString());
                        image4 = "" + task.getResult().child(parts[3]).child("profilePhoto").getValue();

                        try {
                            Picasso.get().load(image4).into(fourthImage);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.user).into(fourthImage);
                        }
                    }else {
                        fourthUser.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });

        whatFor=userPosition+"To"+partPosition;

        firstUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String []userName=userList.get(position).split("#");

                longClickDiolog(whatFor,userName[0]);
            }
        });

        secondUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String []userName=userList.get(position).split("#");
                longClickDiolog(whatFor,userName[1]);
            }
        });

        thirdUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String []userName=userList.get(position).split("#");
                longClickDiolog(whatFor,userName[2]);
            }
        });

        fourthUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String []userName=userList.get(position).split("#");
                longClickDiolog(whatFor,userName[3]);
            }
        });

//
//        firstUser.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                String []userName=userList.get(position).split("#");
//                if(userPosition.equals("Owner")){
//                    longClickDiolog(whatFor,userName[0]);
//                }
//
//                return false;
//            }
//        });
//        secondUser.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                String []userName=userList.get(position).split("#");
//
//                if(userPosition.equals("Owner")){
//                    longClickDiolog(whatFor,userName[1]);
//                }
//                return false;
//            }
//        });
//        thirdUser.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                String []userName=userList.get(position).split("#");
//
//                if(userPosition.equals("Owner")){
//                    longClickDiolog(whatFor,userName[2]);
//                }
//                return false;
//            }
//        });
//        fourthUser.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                String []userName=userList.get(position).split("#");
//
//                if(userPosition.equals("Owner")){
//                    longClickDiolog(whatFor,userName[3]);
//                }
//                return false;
//            }
//        });

        return rowView;
    }

    private void userClicked(String user) {
        Intent intent=new Intent(context, ProfileActivity.class);
        intent.putExtra("user",user);
        context.startActivity(intent);
    }

    private void longClickDiolog(String whatFor,String userClickedOn) {
        if((!user.getUid().equals(userClickedOn))&&userPosition.equals("Owner")) {

            final Dialog nav = new Dialog(getContext());
            nav.setContentView(R.layout.extra_list_dialog);
            nav.setCancelable(true);
            nav.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            nav.setCanceledOnTouchOutside(true);
            nav.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            nav.getWindow().setGravity(Gravity.BOTTOM);
            nav.show();

            ShapeableImageView profile=nav.findViewById(R.id.profilePhoto);

            TextView userName=nav.findViewById(R.id.userName);
            TextView connections=nav.findViewById(R.id.connections);
            TextView club=nav.findViewById(R.id.club);
            TextView userjob=nav.findViewById(R.id.userjob);
            TextView shortDiscription=nav.findViewById(R.id.shortDiscription);
            TextView remove=nav.findViewById(R.id.remove);
            TextView owner=nav.findViewById(R.id.owner);
            TextView onStageormoderator=nav.findViewById(R.id.onStageormoderator);
            TextView viewprofile=nav.findViewById(R.id.viewprofile);
            ImageView back=nav.findViewById(R.id.back);

            //getting data part
            mDatabase.child(userClickedOn).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.getResult().exists()) {
                        userName.setText(task.getResult().child("name").getValue().toString());
                        userjob.setText(task.getResult().child("job").getValue().toString());

                        shortDiscription.setText(task.getResult().child("shortDis").getValue().toString());
                        String image = task.getResult().child("profilePhoto").getValue().toString();
                        try {
                            Picasso.get().load(image).into(profile);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.user).into(profile);
                        }

                        if(task.getResult().child("Club").exists()) {

                            int clubs=0;
                            for (DataSnapshot s : task.getResult().child("Club").getChildren()){

                                clubs++;
                            }
                            club.setText(""+clubs);
                        }
                    }
                }
            });

            connectionData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.getResult().child(userClickedOn).exists()){
                        int connected=0;
                        for(DataSnapshot ds:task.getResult().child(userClickedOn).getChildren()){
                            if(ds.getValue().toString().equals("C")){
                                connected++;
                            }
                        }
                        connections.setText(String.valueOf(connected));
                    }
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nav.dismiss();
                }
            });
            viewprofile.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
            remove.setText("Remove from Room");
            if (whatFor.equals("OwnerToOwner")) {
                onStageormoderator.setVisibility(View.VISIBLE);
                onStageormoderator.setText("Change to Listener");
                owner.setVisibility(View.GONE);

            } else if (whatFor.equals("OwnerToOnstage")) {
                onStageormoderator.setVisibility(View.VISIBLE);
                onStageormoderator.setText("Change to Listener");
                owner.setVisibility(View.VISIBLE);
                owner.setText("Change to Owner");


            } else if (whatFor.equals("OwnerToListener")) {
                onStageormoderator.setVisibility(View.VISIBLE);
                onStageormoderator.setText("Change to On Stage");
                owner.setVisibility(View.VISIBLE);
                owner.setText("Change to Owner");

            } else if (whatFor.equals("OwnerToRaise Hand")) {
                onStageormoderator.setVisibility(View.VISIBLE);
                onStageormoderator.setText("Change to On Stage");
                owner.setVisibility(View.VISIBLE);
                owner.setText("Change to Owner");
            }


            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roomReference.child("Peoples").child(userClickedOn).removeValue();
                    nav.dismiss();
                }
            });
            viewprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   userClicked(userClickedOn);
                   nav.dismiss();
                }
            });
            owner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roomReference.child("Peoples").child(userClickedOn).setValue("Owner");
                    nav.dismiss();
                }
            });
            onStageormoderator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (whatFor.equals("OwnerToOwner")) {
                        roomReference.child("Peoples").child(userClickedOn).setValue("Listener");

                    } else if (whatFor.equals("OwnerToOnstage")) {
                        roomReference.child("Peoples").child(userClickedOn).setValue("Listener");


                    } else if (whatFor.equals("OwnerToListener")) {
                        roomReference.child("Peoples").child(userClickedOn).setValue("Onstage");

                    } else if (whatFor.equals("OwnerToRaise Hand")) {
                        roomReference.child("Peoples").child(userClickedOn).setValue("Onstage");
                    }
                    nav.dismiss();
                }
            });


        }
        else{
            userClicked(userClickedOn);
        }

    }



}
