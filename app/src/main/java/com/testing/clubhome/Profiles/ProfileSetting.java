package com.testing.clubhome.Profiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//
import com.testing.clubhome.Backend.AddUserData;
import com.testing.clubhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
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

public class ProfileSetting extends AppCompatActivity  {

    EditText username,shortDisc,designation,bio,website,insta,twit;
    TextView changePhoto;
    ShapeableImageView profilePhoto;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    Button save;

    String cameraPermission[];
    String storagePermission[];
    Uri profilePhotoUri;
    String downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);


        profilePhoto=findViewById(R.id.myImage);
        changePhoto=findViewById(R.id.changePhoto);
        username=findViewById(R.id.userName);
        shortDisc=findViewById(R.id.shortDiscription);
        designation=findViewById(R.id.job);
        bio=findViewById(R.id.bio);
        website=findViewById(R.id.website);
        save=findViewById(R.id.save);
        insta=findViewById(R.id.instagram);
        twit=findViewById(R.id.twiter);


        //getting FireBase Values

        storage=FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("UsersInfo").child(user.getUid());
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()){
                    username.setText(task.getResult().child("name").getValue().toString());
                    designation.setText(task.getResult().child("job").getValue().toString());
                    bio.setText(task.getResult().child("bio").getValue().toString());
                    website.setText(task.getResult().child("web").getValue().toString());
                    shortDisc.setText(task.getResult().child("shortDis").getValue().toString());
                     String image = task.getResult().child("profilePhoto").getValue().toString();
                    if(task.getResult().child("instagram").exists()){
                        insta.setText(task.getResult().child("instagram").getValue().toString());
                    }
                    if(task.getResult().child("twitter").exists()){
                        twit.setText(task.getResult().child("twitter").getValue().toString());
                    }
                    try{
                        Picasso.get().load(image).into(profilePhoto);
                    }catch (Exception e){

                        Picasso.get().load(R.drawable.user).into(profilePhoto);
                    }
                }
            }
        });

//Advertising Initialization


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().trim().isEmpty()){
                    Toast.makeText(ProfileSetting.this, "Add UserName", Toast.LENGTH_SHORT).show();

                }else{
                    updateData();
                }
            }
        });


        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoUpdate();
            }
        });
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoUpdate();
            }
        });


        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }

    private void updateData() {
        AddUserData addUserData=new AddUserData(user.getUid());
        addUserData.name(username.getText().toString());
        addUserData.job(designation.getText().toString());
        addUserData.bio(bio.getText().toString());
        addUserData.shortDis(shortDisc.getText().toString());
        addUserData.web(website.getText().toString());
        if(!twit.getText().toString().trim().isEmpty()) {
            addUserData.twit(twit.getText().toString().trim());
        }

        if(!insta.getText().toString().trim().isEmpty()) {
            addUserData.ista(insta.getText().toString().trim());
        }


        Intent intent=new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("user",user.getUid());

        startActivity(intent);
        finish();
    }

    private void photoUpdate() {
        if(!checkStoragePermission()){
            requestStoragePermission();
        }else{
            pickFromGallery();
        }
    }

    public void backPressed(View view) {
        Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
        intent1.putExtra("user",user.getUid());
        startActivity(intent1);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0){
            boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
            if(cameraAccepted){
                pickFromGallery();
            }else {
                Toast.makeText(ProfileSetting.this, "Please Enable Camera and Storage Permission", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void pickFromGallery() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(ProfileSetting.this);

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

           if(data!=null) {
               if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                   CropImage.ActivityResult result = CropImage.getActivityResult(data);
                   profilePhotoUri = result.getUri();
                   StorageReference profRef = storageReference.child("Profile Photos/" + user.getUid() + ".jpg");
                   UploadTask uploadTask = profRef.putFile(profilePhotoUri);
                   // Register observers to listen for when the download is done or if it fails
                   uploadTask.addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception exception) {
                           // Handle unsuccessful uploads
                           Toast.makeText(ProfileSetting.this, "Failed", Toast.LENGTH_SHORT).show();
                       }
                   }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc
                           Toast.makeText(ProfileSetting.this, "Success", Toast.LENGTH_SHORT).show();
                           storageReference.child("Profile Photos/" + user.getUid() + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                               @Override
                               public void onComplete(@NonNull Task<Uri> task) {
                                   downloadUri = task.getResult().toString();
                                   Toast.makeText(ProfileSetting.this, "SuccessFully Uploaded", Toast.LENGTH_SHORT).show();
                                   reference.child("profilePhoto").setValue(downloadUri);
                                   try {
                                       Picasso.get().load(downloadUri).into(profilePhoto);
                                   } catch (Exception e) {
                                       Toast.makeText(ProfileSetting.this, e.toString(), Toast.LENGTH_LONG).show();
                                       Picasso.get().load(R.drawable.user).into(profilePhoto);
                                   }

                               }
                           });
                       }
                   });


               }
           }
            super.onActivityResult(requestCode, resultCode, data);


    }
}

