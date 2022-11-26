package com.testing.clubhome.supporting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
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

public class UserListAdapter extends ArrayAdapter<String> {
    private List<String> userList=new ArrayList();
    private final Activity context;
    //Firebase initialization
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;



    public UserListAdapter(@NonNull Activity context, List<String> userList) {
        super(context,R.layout.small_user_list, userList);
        this.userList=userList;
        this.context = context;
    }

    public View getView(final int position , View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        final View rowView=inflater.inflate(R.layout.small_user_list, null,true);
        TextView userName=rowView.findViewById(R.id.name);
        TextView designation=rowView.findViewById(R.id.designation);

        ShapeableImageView userProfile=rowView.findViewById(R.id.profilePhoto);
        RelativeLayout relativeLayout= rowView.findViewById(R.id.relative);

        //getting FireBase Values
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase=firebaseDatabase.getReference("UsersInfo");
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


        return rowView;
    }


}
