package com.testing.clubhome.supporting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testing.clubhome.Profiles.ProfileActivity;
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

public class  messagingAdapter extends ArrayAdapter<String> {

    private List<String> username=new ArrayList();
    private List<String> messageList=new ArrayList();
    private final Activity context;
    //Firebase initialization
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    public  messagingAdapter(@NonNull Context context,List<String> message) {
        super(context, R.layout.messageadapter, message);

        messageList=message;
        this.context= (Activity) context;

        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference("UsersInfo");

    }

    public View getView(final int position , View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        View rowView=inflater.inflate(R.layout.messageadapter, null,true);
        LinearLayout othermessage=rowView.findViewById(R.id.othermessage);
        LinearLayout mymessage=rowView.findViewById(R.id.mymessage);
        TextView otherwhom=rowView.findViewById(R.id.otherwhom);
        TextView othertextmessage=rowView.findViewById(R.id.othertextmessage);
        TextView metextmessage=rowView.findViewById(R.id.metextmessage);

        String []parts=messageList.get(position).split(" : ");

        if(user.getUid().equals(parts[0])){
            mymessage.setVisibility(View.VISIBLE);
            othermessage.setVisibility(View.GONE);
            metextmessage.setText(parts[1]);
        }else {
            mymessage.setVisibility(View.GONE);
            othermessage.setVisibility(View.VISIBLE);
            othertextmessage.setText(parts[1]);
            mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    if (task.getResult().child(parts[0]).exists()) {

                        otherwhom.setText(task.getResult().child(parts[0]).child("name").getValue().toString());

                    }
                }


            });
        }


        return rowView;
    }



}
