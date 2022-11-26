package com.testing.clubhome.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testing.clubhome.Authentication.SignupActivity;
import com.testing.clubhome.R;

import java.util.Calendar;
import java.util.Date;

public class Splash extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase;
    FirebaseUser user;

    SharedPreferences ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ui=getApplicationContext().getSharedPreferences( "com.testing.clubhome", Context.MODE_PRIVATE);

        boolean nightmode=ui.getBoolean("nightmode",false);
        if(nightmode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null) {
            //the user is null
            Intent intent = new Intent(Splash.this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            firebaseAuth=FirebaseAuth.getInstance();
            user=firebaseAuth.getCurrentUser();
            firebaseDatabase=FirebaseDatabase.getInstance();
            mDatabase=firebaseDatabase.getReference("UsersInfo").child(user.getUid());
            Calendar cal = Calendar.getInstance();
            final Date[] date1 = {cal.getTime()};
            if (date1[0].getDate()==1){
                mDatabase.child("roomremaining").setValue(20);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }, 800);
        }
    }

}