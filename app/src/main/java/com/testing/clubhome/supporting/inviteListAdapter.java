package com.testing.clubhome.supporting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testing.clubhome.R;
import com.testing.clubhome.SendNoificaion.AppNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class inviteListAdapter extends ArrayAdapter<String> {
    private List<String> userList=new ArrayList();
    private final Activity context;
    //Firebase initialization
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase,club;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String forThePosition,clubId,clubName;



    public inviteListAdapter(@NonNull Activity context, List<String> userList,String forThePosition,String clubId,String clubName) {
        super(context, R.layout.invite_user_list, userList);
        this.userList=userList;
        this.context = context;
        this.forThePosition=forThePosition;
        this.clubId=clubId;
        this.clubName=clubName;
    }

    public View getView(final int position , View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        final View rowView=inflater.inflate(R.layout.invite_user_list, null,true);
        TextView userName=rowView.findViewById(R.id.name);
        TextView designation=rowView.findViewById(R.id.designation);
        ShapeableImageView userProfile=rowView.findViewById(R.id.profilePhoto);

        RelativeLayout relativeLayout= rowView.findViewById(R.id.relative);
        Button invite=rowView.findViewById(R.id.add);



        //getting FireBase Values
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase=firebaseDatabase.getReference("UsersInfo");
        club = firebaseDatabase.getReference("ClubsInfo").child(clubId);

        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {

                    Log.i("position", String.valueOf(position));
                    userName.setText(task.getResult().child( userList.get(position)).child("name").getValue().toString());
                    designation.setText(task.getResult().child( userList.get(position)).child("job").getValue().toString());
                    String image =""+task.getResult().child( userList.get(position)).child("profilePhoto").getValue();
                    try {
                        Picasso.get().load(image).into(userProfile);
                    } catch (Exception e) {

                        Picasso.get().load(R.drawable.user).into(userProfile);
                    }
                }
            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(invite.getText().toString().equals("Invite")){
                    invite.setText("Invited");
                    //send Invitation

                    addInvitation(userList.get(position));

                }else{
                    invite.setText("Invite");
                    //remove invitation
                    removeInvitation(userList.get(position));
                }

            }
        });


        return rowView;
    }

    private void addInvitation(String s) {

        //Noifying
        String title= "Club"+" - "+clubId;
        String body= user.getDisplayName()+" has invited you to join the club " +clubName;
        AppNotification appNotification=new AppNotification(s,title,body,context,context);
        appNotification.storeValue();

    }

    private void removeInvitation(String s) {
    }

}
