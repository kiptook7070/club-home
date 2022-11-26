package com.testing.clubhome.supporting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

public class roomSearchAdapter extends ArrayAdapter<String> {
    private List<String> clubList=new ArrayList();
    private final Activity context;
    //Firebase initialization
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference clubReference,personReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    int count=0;

    public roomSearchAdapter(@NonNull Activity context, List<String> clubList) {
        super(context,R.layout.room_layout, clubList);
        this.clubList=clubList;
        this.context = context;
    }

    public View getView(final int position , View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder")

        final View rowView=inflater.inflate(R.layout.room_layout, null,true);

            TextView clubName=rowView.findViewById(R.id.clubName);
            TextView roomName=rowView.findViewById(R.id.roomName);
            LinearLayout clubLayout=rowView.findViewById(R.id.clubLayout);

            ShapeableImageView profilePhotoOne=rowView.findViewById(R.id.profilePhotoOne);
            ShapeableImageView profilePhotoTwo=rowView.findViewById(R.id.profilePhotoTwo);
            ShapeableImageView profilePhotoThree=rowView.findViewById(R.id.profilePhotoThree);
            ShapeableImageView profilePhotoFour=rowView.findViewById(R.id.profilePhotoFour);
            ShapeableImageView profilePhotoFive=rowView.findViewById(R.id.profilePhotoFive);

            TextView name=rowView.findViewById(R.id.person);
            clubLayout.setVisibility(View.GONE);
            count=0;
            //getting FireBase Values
            firebaseAuth=FirebaseAuth.getInstance();
            user=firebaseAuth.getCurrentUser();
            firebaseDatabase=FirebaseDatabase.getInstance();
            clubReference=firebaseDatabase.getReference("RoomsInfo");
            DatabaseReference club=firebaseDatabase.getReference("ClubsInfo");
            personReference=firebaseDatabase.getReference("UsersInfo");

            clubReference.child( clubList.get(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (clubList.size()==0){
                    }else {
                        if (task.getResult().exists()) {
                            //people count


                            roomName.setText(task.getResult().child("Assets").child("room name").getValue().toString());
                            List<String> userToStore=new ArrayList<>();

                            int i=0;
                            count=0;
                            for(DataSnapshot d:task.getResult().child("Peoples").getChildren()){
                                String userposi=d.getValue().toString();
                                if(i<=4&&(userposi.equals("Owner")||userposi.equals("Onstage"))){
                                    userToStore.add(d.getKey());
                                    i++;
                                }
                                count++;
                            }

                            if(task.getResult().child("Assets").child("hosted by").exists()){
                                clubLayout.setVisibility(View.VISIBLE);
                                String ClubId = task.getResult().child("Assets").child("hosted by").getValue().toString();

                                club.child(ClubId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task1) {
                                        String ClubName = task1.getResult().child("Assets").child("club name").getValue().toString();
                                        clubName.setText(ClubName);
                                    }
                                });

                            }
                            else {
                                clubLayout.setVisibility(View.GONE);
                            }

                            personReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task1) {
                                    String namefor[]={"",""};
                                    if(userToStore.size()>=1) {
                                        namefor[0]=task1.getResult().child(userToStore.get(0)).child("name").getValue().toString();
                                        String image1 =""+task1.getResult().child( userToStore.get(0)).child("profilePhoto").getValue();
                                        try {
                                            Picasso.get().load(image1).into(profilePhotoOne);
                                        } catch (Exception e) {
                                            Picasso.get().load(R.drawable.user).into(profilePhotoOne);
                                        }

                                    if(userToStore.size()>=2) {
                                        //second
                                        namefor[1]=task1.getResult().child(userToStore.get(1)).child("name").getValue().toString();
                                        String image2 =""+task1.getResult().child( userToStore.get(1)).child("profilePhoto").getValue();
                                        try {
                                            Picasso.get().load(image2).into(profilePhotoTwo);
                                        } catch (Exception e) {

                                            Picasso.get().load(R.drawable.user).into(profilePhotoTwo);
                                        }

                                        //third
                                        if(userToStore.size()>=3) {
                                            String image3 =""+task1.getResult().child( userToStore.get(2)).child("profilePhoto").getValue();
                                            try {
                                                Picasso.get().load(image3).into(profilePhotoThree);
                                            } catch (Exception e) {

                                                Picasso.get().load(R.drawable.user).into(profilePhotoThree);
                                            }
                                            //Four
                                            if(userToStore.size()>=4) {
                                                String image4 =""+task1.getResult().child( userToStore.get(3)).child("profilePhoto").getValue();
                                                try {
                                                    Picasso.get().load(image4).into(profilePhotoFour);
                                                } catch (Exception e) {

                                                    Picasso.get().load(R.drawable.user).into(profilePhotoFour);
                                                }
                                                //Five
                                                if(userToStore.size()>=5) {
                                                    String image5 =""+task1.getResult().child( userToStore.get(4)).child("profilePhoto").getValue();
                                                    try {
                                                        Picasso.get().load(image4).into(profilePhotoFive);
                                                    } catch (Exception e) {

                                                        Picasso.get().load(R.drawable.user).into(profilePhotoFive);
                                                    }

                                                }else {
                                                    profilePhotoFive.setVisibility(View.GONE);

                                                }

                                            }else {
                                                profilePhotoThree.setVisibility(View.GONE);

                                            }



                                        }else{
                                            profilePhotoTwo.setVisibility(View.GONE);

                                        }

                                    }
                                    }
                                    name.setText(namefor[0]+" "+namefor[1]+" and "+count+" here");

                                }
                            });

                        }
                    }
                }
            });



        return rowView;
    }

}
