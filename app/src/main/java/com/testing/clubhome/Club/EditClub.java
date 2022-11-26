package com.testing.clubhome.Club;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.testing.clubhome.Profiles.ProfileActivity;
import com.testing.clubhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditClub extends AppCompatActivity {
    EditText username,shortDisc,bio;
    TextView save;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    SwitchCompat privacy;
    String clubId;
    boolean privacybool;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club);



        username=findViewById(R.id.userName);
        shortDisc=findViewById(R.id.shortDiscription);
        bio=findViewById(R.id.bio);
        privacy=findViewById(R.id.privacy);
        save=findViewById(R.id.save);

        //getting FireBase Values
        //getData
        Intent intent= getIntent();
        clubId =intent.getStringExtra("clubId");



        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("ClubsInfo").child(clubId).child("Assets");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()){
                    username.setText(task.getResult().child("club name").getValue().toString());
                    bio.setText(task.getResult().child("club Description").getValue().toString());
                    shortDisc.setText(task.getResult().child("club Short Description").getValue().toString());


                    if(task.getResult().child("Privacy").getValue().toString().equals("Public")) {
                        privacybool=false;
                        privacy.setChecked(false);
                    }else {
                        privacybool=true;
                        privacy.setChecked(true);
                    }
                }
            }
        });
//Advertising Initialization
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        mInterstitialAd= new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.Interstial_app_id));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//
//
//        privacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                privacybool =isChecked;
//                if(privacybool){
//                    reference.child("Privacy").setValue("Private");
//                }else {
//                    reference.child("Privacy").setValue("Public");
//                }
//            }
//        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().isEmpty()){
                    Toast.makeText(EditClub.this, "Add Club Name", Toast.LENGTH_SHORT).show();
                }else{
                    updateData();
                }
            }
        });



    }

    private void updateData() {
        reference.child("club name").setValue(username.getText().toString());
        reference.child("club Description").setValue(bio.getText().toString());
        reference.child("club Short Description").setValue(shortDisc.getText().toString());

        Intent intent=new Intent(getApplicationContext(), ClubHome.class);
        intent.putExtra("clubId",clubId);
//        if(mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
        startActivity(intent);
        finish();
    }

    public void backPressed(View view) {
        Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
        intent1.putExtra("user",user.getUid());
        startActivity(intent1);
        finish();
    }
}