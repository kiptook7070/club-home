package com.testing.clubhome.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.testing.clubhome.Authentication.SignupActivity;
import com.testing.clubhome.Club.ClubHome;
import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.Pages.CreateClub;
import com.testing.clubhome.Pages.premiun;
import com.testing.clubhome.Profiles.ProfileActivity;
import com.testing.clubhome.Profiles.ProfileSetting;
import com.testing.clubhome.R;
import com.testing.clubhome.SendNoificaion.AppNotification;
import com.testing.clubhome.supporting.MyListAdapter;
import com.testing.clubhome.supporting.clubSearchAdapter;

import java.util.ArrayList;
import java.util.List;


public class Profile extends Fragment {

    ShapeableImageView profilePhoto;
    ImageButton setting;
    TextView userName,userJob,bio,website,shortDis,clubNum,club,connectionsNum,connect,pro;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase,connectionData;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userdisplay;
    Button connectOrEdit;
    ImageView webIcon,p,insta,twit;
    LinearLayout connectionGroup,CreateClub,linking;
    List<String> clubList=new ArrayList<>();
    List<String> Connections=new ArrayList<>();
    MyListAdapter myListAdapter;
    boolean prouser=false;
    String instagram,twitter;

    int connected;
    com.testing.clubhome.supporting.clubSearchAdapter clubSearchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        connectOrEdit=view.findViewById(R.id.connectOrEdit);
        profilePhoto=view.findViewById(R.id.profilePhoto);
        userName=view.findViewById(R.id.userName);
        bio=view.findViewById(R.id.bio);
        linking=view.findViewById(R.id.CreateClub);
        clubNum=view.findViewById(R.id.club);
        club=view.findViewById(R.id.clubsText);
        p=view.findViewById(R.id.p);
        pro=view.findViewById(R.id.pro);
        connectionsNum=view.findViewById(R.id.connections);
        connect=view.findViewById(R.id.connectionText);
        insta=view.findViewById(R.id.icinstagram);
        twit=view.findViewById(R.id.ictwitter);

        setting=view.findViewById(R.id.setting);
        website=view.findViewById(R.id.website);
        connectionGroup=view.findViewById(R.id.connectionGroup);
        userJob=view.findViewById(R.id.userjob);
        shortDis=view.findViewById(R.id.shortDiscription);
        webIcon=view.findViewById(R.id.linkIcon);

        webIcon.setVisibility(View.VISIBLE);

        p.setVisibility(View.INVISIBLE);
        pro.setVisibility(View.INVISIBLE);
        linking.setVisibility(View.INVISIBLE);



        webIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://"+website.getText().toString()));
                startActivity(intent);
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://"+website.getText().toString()));
                startActivity(intent);
            }
        });

        //getting FireBase Values
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();

        connectionData=firebaseDatabase.getReference("Connections");
        userdisplay=user.getUid();
        mDatabase=firebaseDatabase.getReference("UsersInfo").child(userdisplay);
        //checking Internet Connection
        if(!internet_connection()){
            noInternet();
        }
        //getting data part
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {
                    userName.setText(task.getResult().child("name").getValue().toString());
                    userJob.setText(task.getResult().child("job").getValue().toString());
                    bio.setText(task.getResult().child("bio").getValue().toString());
                    website.setText(task.getResult().child("web").getValue().toString());
                    if (website.getText().toString().isEmpty()) {
                        webIcon.setVisibility(View.INVISIBLE);
                    }
                    shortDis.setText(task.getResult().child("shortDis").getValue().toString());
                    String image = task.getResult().child("profilePhoto").getValue().toString();
                    try {
                        Picasso.get().load(image).into(profilePhoto);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.user).into(profilePhoto);
                    }
                    if(task.getResult().child("instagram").exists()&&!(task.getResult().child("instagram").getValue().toString().trim().isEmpty())){
                        insta.setVisibility(View.VISIBLE);
                        instagram=task.getResult().child("instagram").getValue().toString().trim();
                    }else {
                        insta.setVisibility(View.GONE);
                    }
                    if(task.getResult().child("twitter").exists()&&!(task.getResult().child("twitter").getValue().toString().trim().isEmpty())){
                        twit.setVisibility(View.VISIBLE);
                        twitter=task.getResult().child("twitter").getValue().toString().trim();
                    }else {
                        twit.setVisibility(View.GONE);
                    }


                    if(task.getResult().child("pro").exists()) {
                        p.setVisibility(View.VISIBLE);
                        prouser=true;
                        pro.setVisibility(View.VISIBLE);
                        linking.setVisibility(View.VISIBLE);
                    }else{
                        p.setVisibility(View.INVISIBLE);
                        prouser=false;
                        pro.setVisibility(View.INVISIBLE);
                        linking.setVisibility(View.INVISIBLE);
                    }
                    if(task.getResult().child("Club").exists()) {
                        clubList.clear();
                        int clubs=0;
                        for (DataSnapshot s : task.getResult().child("Club").getChildren()){
                            clubList.add(s.getKey());
                            clubs++;
                        }
                        clubNum.setText(""+clubs);
                    }
                }
            }
        });

        howManyConnections();


        //when connection is clicked
        connectionsNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConnecton("Connection",Connections);
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConnecton("Connection",Connections);
            }
        });

        //Connecting or editing
        connectOrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Editing User Data
                //checking Internet Connection
                if(!internet_connection()){
                    noInternet();
                }else {
                    Intent intent1 = new Intent(getActivity(), ProfileSetting.class);
                    startActivity(intent1);

                }


            }
        });

        //Club is Clicked
        club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConnecton("Clubs",clubList);
            }
        });
        clubNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConnecton("Clubs",clubList);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingNavigation();
            }
        });

        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.instagram.com/"+instagram));
                startActivity(intent);
            }
        });
        twit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.twitter.com/"+twitter));
                startActivity(intent);
            }
        });

        linking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!internet_connection()){
                    noInternet();
                }else{
                    Intent createClub = new Intent(getActivity(), CreateClub.class);
                    startActivity(createClub);
                }
            }
        });
        return view;
    }

    private void showConnecton(String connection, List<String> bothSameConnectionName){
        final Dialog connectionsDialog = new Dialog(getActivity());
        connectionsDialog.setContentView(R.layout.users_connection_club_list);
        connectionsDialog.setCancelable(true);
        connectionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        connectionsDialog.setCanceledOnTouchOutside(true);
        connectionsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        connectionsDialog.getWindow().setGravity(Gravity.BOTTOM);

        connectionsDialog.show();

        TextView title=connectionsDialog.findViewById(R.id.title);
        ImageButton back=connectionsDialog.findViewById(R.id.back);
        LinearLayout empty=connectionsDialog.findViewById(R.id.empty);
        ListView listView=connectionsDialog.findViewById(R.id.list);

        title.setText(connection);
        if(bothSameConnectionName.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else {
            empty.setVisibility(View.INVISIBLE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionsDialog.dismiss();
            }
        });

        if(connection.equals("Connection")){
            myListAdapter=new MyListAdapter(getActivity(),bothSameConnectionName);
            listView.setAdapter(myListAdapter);
        }else {
            clubSearchAdapter=new clubSearchAdapter(getActivity(),bothSameConnectionName);
            listView.setAdapter(clubSearchAdapter);
        }

    }

    protected void settingNavigation(){
        final Dialog nav = new Dialog(getActivity());
        nav.setContentView(R.layout.setting_nav);
        nav.setCancelable(true);
        nav.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nav.setCanceledOnTouchOutside(true);
        nav.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        nav.getWindow().setGravity(Gravity.BOTTOM);
        nav.getWindow().getAttributes().windowAnimations = android.R.anim.slide_in_left;

        nav.show();

        //initialization
        LinearLayout rate=nav.findViewById(R.id.rate);
        LinearLayout invite=nav.findViewById(R.id.invite);
        LinearLayout privacy=nav.findViewById(R.id.privacy);
        LinearLayout inapp=nav.findViewById(R.id.purchase);
        LinearLayout theme=nav.findViewById(R.id.theme);
        LinearLayout password=nav.findViewById(R.id.passwordChange);
        LinearLayout help=nav.findViewById(R.id.help);
        LinearLayout logOut=nav.findViewById(R.id.logOut);

        //Onclick

        if (prouser){
            inapp.setVisibility(View.GONE);
        }
        inapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createClub = new Intent(getActivity(), premiun.class);
                startActivity(createClub);
            }
        });

        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nav.dismiss();
                themedialog();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.PlayStoreLink));
                startActivity(intent);
                nav.dismiss();
            }
        });
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Constants.PlayStoreLink);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Clubhouse");
                startActivity(Intent.createChooser(intent,"Share Using"));
                nav.dismiss();
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!internet_connection()){
                    noInternet();
                }
                firebaseAuth.sendPasswordResetEmail(user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>(){

                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),"Link has ben sent on Email",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"An Error ! "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                nav.dismiss();
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent
                nav.dismiss();
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent
                nav.dismiss();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SignupActivity.class));
                nav.dismiss();
                //end mainacivi
            }
        });


    }

    private void themedialog() {
        final Dialog nav = new Dialog(getActivity());
        nav.setContentView(R.layout.theme);
        nav.setCancelable(true);
        nav.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nav.setCanceledOnTouchOutside(true);
        nav.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        nav.getWindow().setGravity(Gravity.CENTER);
        nav.getWindow().getAttributes().windowAnimations = android.R.anim.slide_in_left;

        nav.show();
        SharedPreferences ui=getContext().getSharedPreferences( "com.testing.clubhome", Context.MODE_PRIVATE);


        TextView black=nav.findViewById(R.id.black);
        TextView White=nav.findViewById(R.id.White);

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui.edit().putBoolean("nightmode",true).commit();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                nav.dismiss();
            }
        });

        White.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui.edit().putBoolean("nightmode",false).commit();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                nav.dismiss();
            }
        });
    }

    public boolean internet_connection(){
        //Check if connected to internet, output accordingly
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected()&& activeNetwork.isAvailable();
        return isConnected;
    }

    public void noInternet(){

        final Dialog dialog2 = new Dialog(getActivity());
        dialog2.setContentView(R.layout.nointernet);
        dialog2.setCancelable(true);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCanceledOnTouchOutside(true);
        dialog2.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog2.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog2.show();
        Button button = dialog2.findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
    }

    private void howManyConnections(){
        connectionData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().child(userdisplay).exists()){
                    connected=0;
                    Connections.clear();
                    for(DataSnapshot ds:task.getResult().child(userdisplay).getChildren()){
                        if(ds.getValue().toString().equals("C")){
                            connected++;
                            Connections.add(ds.getKey());
                        }
                    }
                    connectionsNum.setText(String.valueOf(connected));
                }
            }
        });
    }
}