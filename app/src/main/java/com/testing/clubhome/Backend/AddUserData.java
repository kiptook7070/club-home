package com.testing.clubhome.Backend;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddUserData {
    public String userId;
    FirebaseDatabase database;
    FirebaseStorage storage;
    DatabaseReference reference;
    StorageReference storageReference;


    public AddUserData(String userId){
        this.userId=userId;
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("UsersInfo");
        storage=FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }

    public void name(String userName){
        reference.child(userId).child("name").setValue(userName);
    }

    public void job(String job){
        reference.child(userId).child("job").setValue(job);
    }

    public void bio(String bio){
        reference.child(userId).child("bio").setValue(bio);
    }

    public void shortDis(String shortDis){
        reference.child(userId).child("shortDis").setValue(shortDis);
    }

    public void web(String web){
        reference.child(userId).child("web").setValue(web);
    }

    public void ista(String ista){
        if (!ista.trim().isEmpty()){
            reference.child(userId).child("instagram").setValue(ista);
        }
    }
    public void twit(String twit){
        if (!twit.trim().isEmpty()){
            reference.child(userId).child("twitter").setValue(twit);
        }
    }

}
