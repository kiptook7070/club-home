package com.testing.clubhome.Room;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testing.clubhome.R;
import com.testing.clubhome.supporting.messagingAdapter;

import java.util.ArrayList;
import java.util.List;


public class textChannel extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase,roomReference;
    private FirebaseUser user;
    String roomId;
    ListView message;
    EditText value;
    RelativeLayout send;
    long position=0;
    List<String> messagerid=new ArrayList<>();
    List <String> messagingText=new ArrayList<>();
    com.testing.clubhome.supporting.messagingAdapter messagingAdapter;


    public textChannel(String roomId){
       this.roomId= roomId;
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference("UsersInfo");
        roomReference = firebaseDatabase.getReference("RoomsInfo").child(roomId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_text_channel, container, false);
        message=view.findViewById(R.id.message);
        value=view.findViewById(R.id.value);
        send=view.findViewById(R.id.send);

        roomReference.child("messaging").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagerid.clear();
                messagingText.clear();
                position=snapshot.getChildrenCount();
                if (snapshot.exists()){
                    for (DataSnapshot snap:snapshot.getChildren()){
                        messagerid.add(snap.getKey());
                        messagingText.add(snap.getValue().toString());
                    }
                }
                for (int i=0;i<messagerid.size();i++ ){
                    for (int j=i+1;j<messagerid.size();j++ ){
                        int one= Integer.parseInt(messagerid.get(i));
                        int sec= Integer.parseInt(messagerid.get(j));
                        if (one>sec){
                            messagerid.set(i, String.valueOf(sec));
                            messagerid.set(j, String.valueOf(one));
                            String emp=messagingText.get(i);
                            messagingText.set(i,messagingText.get(j));
                            messagingText.set(j,emp);
                        }
                    }
                }
                if (getActivity()!=null){
                    messagingAdapter=new messagingAdapter(getActivity(),messagingText);
                    message.setAdapter(messagingAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (value.getText().toString().isEmpty()){

                }else{

                    roomReference.child("messaging").child(String.valueOf(position)).setValue(user.getUid()+ " : "+ value.getText().toString());
                    value.setText("");
                }

            }
        });
        return view;
    }
}