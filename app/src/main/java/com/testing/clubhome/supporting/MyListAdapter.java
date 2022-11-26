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

public class MyListAdapter extends ArrayAdapter<String> {
    private List<String> userList=new ArrayList();
    private final Activity context;
    //Firebase initialization
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase,connectionData;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    public MyListAdapter(@NonNull Context context, List<String> userId) {
        super(context, R.layout.user_list, userId);
        userList=userId;
        this.context= (Activity) context;
    }

    public View getView(final int position , View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder")

        final View rowView=inflater.inflate(R.layout.user_list, null,true);
        TextView userName=rowView.findViewById(R.id.name);
        TextView designation=rowView.findViewById(R.id.designation);
        ShapeableImageView userProfile=rowView.findViewById(R.id.profilePhoto);
        Button connect=rowView.findViewById(R.id.connect);
        RelativeLayout relativeLayout= rowView.findViewById(R.id.relative);

        //getting FireBase Values
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase=firebaseDatabase.getReference("UsersInfo");
        connectionData=firebaseDatabase.getReference("Connections");
        mDatabase.child(userList.get(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {

                    Log.i("position", String.valueOf(position));
                    userName.setText(task.getResult().child("name").getValue().toString());
                    designation.setText(task.getResult().child("job").getValue().toString());
                    String image =""+task.getResult().child("profilePhoto").getValue();
                    try {
                        Picasso.get().load(image).into(userProfile);
                   } catch (Exception e) {

                        Picasso.get().load(R.drawable.user).into(userProfile);
                  }
                }
            }
        });
        connectionData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().child(user.getUid()).child(userList.get(position)).exists()){
                    //remove form connection

                    if(task.getResult().child(userList.get(position)).child(user.getUid()).exists()){

                        //present in my connect list and in the user
                        connectionData.child(user.getUid()).child(userList.get(position)).setValue("C");
                        connectionData.child(userList.get(position)).child(user.getUid()).setValue("C");
                        connect.setText("Connected");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            connect.setTextColor(Color.parseColor("#ffffff"));
                        }
                        connect.setBackgroundResource(R.drawable.selected_button);
                    }
                    else{
                        //present in my connect list but not in the user
                        connect.setText("Requested");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            connect.setTextColor(Color.parseColor("#000000"));
                        }
                        connect.setBackgroundResource(R.drawable.not_selected_search_button);
                    }


                }else{
                    // niether in my connection list nor in the user connection list
                    connect.setText("Connect");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        connect.setTextColor(Color.parseColor("#000000"));
                    }
                    connect.setBackgroundResource(R.drawable.not_selected_search_button);

                }
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectionData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.getResult().child(user.getUid()).child(userList.get(position)).exists()){
                            String c=task.getResult().child(user.getUid()).child(userList.get(position)).getValue().toString();
                            //remove form connection
                            // present in my connection list
                            if(task.getResult().child(userList.get(position)).child(user.getUid()).exists()) {
                                connectionData.child(userList.get(position)).child(user.getUid()).setValue("R");
                            }
                            connectionData.child(user.getUid()).child(userList.get(position)).removeValue();
                            connect.setText("Connect");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                connect.setTextColor(Color.parseColor("#000000"));
                            }
                            v.setBackgroundResource(R.drawable.not_selected_search_button);

                        }else{
                             if(task.getResult().child(userList.get(position)).child(user.getUid()).exists()){
                                 // not in my list but present in their list directly connect
                                 connectionData.child(user.getUid()).child(userList.get(position)).setValue("C");
                                 connectionData.child(userList.get(position)).child(user.getUid()).setValue("C");
                                 connect.setText("Connected");
                                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                     connect.setTextColor(Color.parseColor("#ffffff"));
                                 }
                                 v.setBackgroundResource(R.drawable.selected_button);
                             }
                             else {
                                 // not in my list and the userlist connectection request
                                 connectionData.child(user.getUid()).child(userList.get(position)).setValue("R");
                                 connect.setText("Requested");
                                 String title= "User"+" - "+user.getUid();
                                 String body= user.getDisplayName()+" has sent you connection Request.";

                                 AppNotification appNotification=new AppNotification(userList.get(position),title,body,context,context);
                                 appNotification.storeValue();


                                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                     connect.setTextColor(Color.parseColor("#000000"));
                                 }
                                 v.setBackgroundResource(R.drawable.not_selected_search_button);
                             }

                        }
                    }
                });
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedOnItem(userList.get(position));
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedOnItem(userList.get(position));
            }
        });

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedOnItem(userList.get(position));
            }
        });

        designation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedOnItem(userList.get(position));
            }
        });

        return rowView;
    }

    private void clickedOnItem(String s) {
        Intent intent=new Intent(context, ProfileActivity.class);
        intent.putExtra("user",s);
        context.startActivity(intent);
    }

}
