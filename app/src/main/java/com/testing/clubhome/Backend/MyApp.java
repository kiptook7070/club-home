package com.testing.clubhome.Backend;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApp extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        //offline activation
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
