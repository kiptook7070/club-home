package com.testing.clubhome.SendNoificaion;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;

public class AppNotification {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference notificationData;
    String userShowing,title,body;
    Context context;
    Activity activity;

    public AppNotification(String userShowing, String title, String body, Context context, Activity activity){
        firebaseDatabase=FirebaseDatabase.getInstance();
        notificationData=firebaseDatabase.getReference("Notification Data").child(userShowing);
        this.body=body;
        this.activity=activity;
        this.context=context;
        this.title=title;
        this.userShowing=userShowing;
    }

    public void storeValue(){
        String[]parts=title.split(" - ");
        notificationData.child(title).setValue(body);
        FirebaseDatabase.getInstance().getReference("UsersInfo").child(userShowing)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                FcmNoificaionSender noificaionSender=new FcmNoificaionSender(task.getResult().child("token").getValue().toString()
                        ,parts[0],body,context,activity);
                noificaionSender.SendNotifications();
            }
        });

    }




}
