package com.testing.clubhome.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.testing.clubhome.Club.ClubHome;
import com.testing.clubhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class CreateClub extends AppCompatActivity {

    EditText clubName,shortDiscription,description;
    SwitchCompat privacy;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference,personReference;
    String clubId;

    boolean privacybool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        clubName = findViewById(R.id.clubName);
        shortDiscription = findViewById(R.id.shortDiscription);
        description = findViewById(R.id.description);
        privacy = findViewById(R.id.privacy);

        privacybool = false;

        privacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                privacybool =isChecked;
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("ClubsInfo");
        personReference=database.getReference("UsersInfo").child(user.getUid());

    }

    public void createClub(View view){
        int random1,random2;
        Random random=new Random();
        random1 =random.nextInt(100);
        random2 =random.nextInt(100);

        clubId=clubName.getText().toString()+random1+random2;
        reference.child(clubId).child("Assets").child("club name").setValue(clubName.getText().toString());
        reference.child(clubId).child("Assets").child("club Description").setValue(description.getText().toString());
        reference.child(clubId).child("Assets").child("club Short Description").setValue(shortDiscription.getText().toString());
        if(privacybool){
            reference.child(clubId).child("Assets").child("Privacy").setValue("Private");
        }
        else{
            reference.child(clubId).child("Assets").child("Privacy").setValue("Public");
        }


        personReference.child("Club").child(clubId).setValue("Owner");

        reference.child(clubId).child("Peoples").child(user.getUid()).setValue("Owner").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent club=new Intent(getApplicationContext(), ClubHome.class);
                club.putExtra("clubId",clubId);
                startActivity(club);
                finish();
            }
        });


    }

    public void back(View view) {
        finish();
    }
}