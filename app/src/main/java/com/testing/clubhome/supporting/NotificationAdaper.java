package com.testing.clubhome.supporting;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.testing.clubhome.R;
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

public class NotificationAdaper extends ArrayAdapter<String> {
    private List<String> clubList=new ArrayList();
    private final Activity context;
    //Firebase initialization
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference clubReference,notificationData,userDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    public NotificationAdaper(@NonNull Activity context, List<String> clubList) {
        super(context,R.layout.notification_adapter, clubList);
        this.clubList=clubList;
        this.context = context;
    }

    public View getView(final int position , View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        final View rowView=inflater.inflate(R.layout.notification_adapter, null,true);
        final String[] disp = new String[1];
        TextView text=rowView.findViewById(R.id.text);
        ShapeableImageView image=rowView.findViewById(R.id.image);
        Log.e( "upct ", "ids[0] ");
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        clubReference = firebaseDatabase.getReference("ClubsInfo");
        userDatabase= firebaseDatabase.getReference("UsersInfo");
        notificationData=firebaseDatabase.getReference("Notification Data").child(user.getUid());

        notificationData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                text.setText(task.getResult().child(clubList.get(position)).getValue().toString());
            }
        });

        String []title=clubList.get(position).split(" - ");
        if(title[0].equals("Room")){
            Picasso.get().load(R.drawable.ic_wavinghand).into(image);
        }
        else if(title[0].equals("Club")){
            clubReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    disp[0]= task.getResult().child(title[1]).child("Assets").child("club Photo").getValue().toString();

                }
            });
            try {
                Picasso.get().load(disp[0]).into(image);
            } catch (Exception e) {
                Picasso.get().load(R.drawable.ic_wavinghand).into(image);
            }

        }
        else {
            userDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    disp[0] = task.getResult().child(title[1]).child("profilePhoto").getValue().toString();

                }
            });
            try {
                Picasso.get().load(disp[0]).into(image);
            } catch (Exception e) {
                Picasso.get().load(R.drawable.user).into(image);
            }

        }


        return rowView;
    }
}
