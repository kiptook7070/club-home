package com.testing.clubhome.supporting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testing.clubhome.Club.ClubHome;
import com.testing.clubhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class clubSearchAdapter extends ArrayAdapter<String> {
    private List<String> clubList=new ArrayList();
    private final Activity context;
    //Firebase initialization
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference clubReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;



    public clubSearchAdapter(@NonNull Activity context, List<String> clubList) {
        super(context,R.layout.club_list, clubList);
        this.clubList=clubList;
        this.context = context;
    }

    public View getView(final int position , View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        final View rowView=inflater.inflate(R.layout.club_list, null,true);
        TextView clubName=rowView.findViewById(R.id.clubName);
        TextView shortDescription=rowView.findViewById(R.id.shortDiscription);
        TextView numberOfConnection=rowView.findViewById(R.id.numberOfConnection);
        TextView numberOfRooms=rowView.findViewById(R.id.numberOfRooms);
        TextView numberOfLiveRooms=rowView.findViewById(R.id.numberOfLiveRooms);
        LinearLayout layout= rowView.findViewById(R.id.mainLayout);

        //getting FireBase Values
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        clubReference=firebaseDatabase.getReference("ClubsInfo");

        clubReference.child( clubList.get(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {
                    int count=0;
                    String totalRoomsCount = "";
                    int totalRoomsLive=0;
                    clubName.setText(task.getResult().child("Assets").child("club name").getValue().toString());
                    shortDescription.setText(task.getResult().child("Assets").child("club Short Description").getValue().toString());

                    for(DataSnapshot d:task.getResult().child("Peoples").getChildren()){
                        count++;
                    }
                    if (task.getResult().child("Assets").child("totalRoomsCount").exists()) {
                        totalRoomsCount=task.getResult().child("Assets").child("totalRoomsCount").getValue().toString();
                    }
                    numberOfRooms.setText(totalRoomsCount+" rooms created .");
                    for(DataSnapshot d:task.getResult().child("Rooms").getChildren()){
                        totalRoomsLive++;
                    }
                    numberOfLiveRooms.setText(totalRoomsLive+" live now");
                    numberOfConnection.setText(count+" members");
                }
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedOnItem(clubList.get(position));
            }
        });

        return rowView;
    }

    private void clickedOnItem(String s) {
        Intent intent=new Intent(context, ClubHome.class);
        intent.putExtra("clubId",s);
        context.startActivity(intent);
    }
}
