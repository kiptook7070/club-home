package com.testing.clubhome.Profiles;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.testing.clubhome.Authentication.SignupActivity;
import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.Fragment.Profile;
import com.testing.clubhome.Pages.CreateClub;
import com.testing.clubhome.R;
import com.testing.clubhome.SendNoificaion.AppNotification;
import com.testing.clubhome.supporting.MyListAdapter;
import com.testing.clubhome.supporting.clubSearchAdapter;
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

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class ProfileActivity extends AppCompatActivity{
    ShapeableImageView profilePhoto;
    ImageButton setting;
    TextView userName,userJob,bio,website,shortDis,otherConnection,clubNum,club,connectionsNum,connect,pro;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase,connectionData;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userdisplay;
    boolean loginedUser;
    Button connectOrEdit;
    ImageView webIcon,p,insta,twit;
    LinearLayout connectionGroup,connectionsGroup,linking;
    List<String> bothSameConnection=new ArrayList<>();
    List<String> clubList=new ArrayList<>();
    List<String> Connections=new ArrayList<>();
    MyListAdapter myListAdapter;
    String instagram,twitter;

    int connected;
    clubSearchAdapter clubSearchAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        connectOrEdit=findViewById(R.id.connectOrEdit);
        profilePhoto=findViewById(R.id.profilePhoto);
        userName=findViewById(R.id.userName);
        bio=findViewById(R.id.bio);
        otherConnection=findViewById(R.id.otherConnection);
        clubNum=findViewById(R.id.club);
        club=findViewById(R.id.clubsText);
        connectionsNum=findViewById(R.id.connections);
        connect=findViewById(R.id.connectionText);
        p=findViewById(R.id.p);
        linking=findViewById(R.id.CreateClub);
        pro=findViewById(R.id.pro);
        setting=findViewById(R.id.setting);
        website=findViewById(R.id.website);
        connectionGroup=findViewById(R.id.connectionGroup);
        connectionsGroup=findViewById(R.id.connectionsGroup);
        otherConnection=findViewById(R.id.otherConnection);
        userJob=findViewById(R.id.userjob);
        shortDis=findViewById(R.id.shortDiscription);
        webIcon=findViewById(R.id.linkIcon);

        insta=findViewById(R.id.icinstagram);
        twit=findViewById(R.id.ictwitter);


        webIcon.setVisibility(View.VISIBLE);

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

        Intent intent= getIntent();
        userdisplay =intent.getStringExtra("user");

        //getting FireBase Values
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        loginedUser=userdisplay.equals(user.getUid());
        connectionData=firebaseDatabase.getReference("Connections");
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
                            pro.setVisibility(View.VISIBLE);
                            if (loginedUser){
                                linking.setVisibility(View.VISIBLE);
                            }else{
                                linking.setVisibility(View.INVISIBLE);
                            }

                        }else{
                            p.setVisibility(View.INVISIBLE);
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


        //Structuring the page layout
        if(loginedUser){
            //this for the logined user
            connectOrEdit.setBackground(getDrawable(R.drawable.not_selected_button));
            connectOrEdit.setText("Edit");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectOrEdit.setTextColor(getColor(R.color.colorDarkBlue));
            }
            connectionsGroup.setVisibility(View.GONE);

            howManyConnections();

        }
        else {
            //the user is not logined
            connectionsGroup.setVisibility(View.VISIBLE);
            connectOrEdit.setBackground(getDrawable(R.drawable.not_selected_button));
            setting.setVisibility(View.GONE);
            connectOrEdit.setText("Connect");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectOrEdit.setTextColor(getColor(R.color.colorDarkBlue));
            }
            checkForConnection();
        }
        allConnections();

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

        linking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!internet_connection()){
                    noInternet();
                }else{
                    Intent createClub = new Intent(getApplicationContext(), CreateClub.class);
                    startActivity(createClub);
                }
            }
        });
        //Connecting or editing
        connectOrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginedUser){
                    //Editing User Data
                    //checking Internet Connection
                    if(!internet_connection()){
                        noInternet();
                    }else {
                        Intent intent1 = new Intent(getApplicationContext(), ProfileSetting.class);
                        startActivity(intent1);
                        finish();
                    }
                }
                else{
                    //connection request
                    changingConnection();

                }

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



    }

    private void showConnecton(String connection, List<String> bothSameConnectionName){
        final Dialog connectionsDialog = new Dialog(this);
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
            myListAdapter=new MyListAdapter(this,bothSameConnectionName);
            listView.setAdapter(myListAdapter);
        }else {
            clubSearchAdapter=new clubSearchAdapter(this,bothSameConnectionName);
            listView.setAdapter(clubSearchAdapter);
        }

    }

    private void changingConnection(){
        connectionData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().child(user.getUid()).child(userdisplay).exists()){
                    String c=task.getResult().child(user.getUid()).child(userdisplay).getValue().toString();
                    //remove form connection
                    // present in my connection list
                    if(task.getResult().child(userdisplay).child(user.getUid()).exists()) {
                        connectionData.child(userdisplay).child(user.getUid()).setValue("R");
                    }
                    connectionData.child(user.getUid()).child(userdisplay).removeValue();
                    connectOrEdit.setText("Connect");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        connectOrEdit.setTextColor(Color.parseColor("#687CC5"));
                    }
                    connectOrEdit.setBackgroundResource(R.drawable.not_selected_button);

                }else{
                    if(task.getResult().child(userdisplay).child(user.getUid()).exists()){
                        // not in my list but present in their list directly connect

                        connectionData.child(user.getUid()).child(userdisplay).setValue("C");
                        connectionData.child(userdisplay).child(user.getUid()).setValue("C");
                        connectOrEdit.setText("Connected");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            connectOrEdit.setTextColor(Color.parseColor("#ffffff"));
                        }
                        connectOrEdit.setBackgroundResource(R.drawable.selected_button);
                    }
                    else {
                        // not in my list and the userlist connectection request
                        connectionData.child(user.getUid()).child(userdisplay).setValue("R");
                        connectOrEdit.setText("Requested");

                        //Noifying
                        String title= "User"+" - "+user.getUid();
                        String body= user.getUid()+" has sent you connection Request.";

                        AppNotification appNotification=new AppNotification(userdisplay,title,body,getBaseContext(),ProfileActivity.this);
                        appNotification.storeValue();



                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            connectOrEdit.setTextColor(Color.parseColor("#687CC5"));
                        }
                        connectOrEdit.setBackgroundResource(R.drawable.not_selected_button);
                    }

                }
            }
        });

        allConnections();
    }

    private void checkForConnection(){
        connectionData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().child(user.getUid()).child(userdisplay).exists()){
                    //remove form connection

                    if(task.getResult().child(userdisplay).child(user.getUid()).exists()){

                        //present in my connect list and in the user
                        connectionData.child(user.getUid()).child(userdisplay).setValue("C");
                        connectionData.child(userdisplay).child(user.getUid()).setValue("C");
                        connectOrEdit.setText("Connected");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            connectOrEdit.setTextColor(Color.parseColor("#ffffff"));
                        }
                        connectOrEdit.setBackgroundResource(R.drawable.selected_button);
                    }
                    else{
                        //present in my connect list but not in the user
                        connectOrEdit.setText("Requested");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            connectOrEdit.setTextColor(Color.parseColor("#687CC5"));
                        }
                        connectOrEdit.setBackgroundResource(R.drawable.not_selected_button);
                    }


                }else{
                    // niether in my connection list nor in the user connection list
                    connectOrEdit.setText("Connect");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        connectOrEdit.setTextColor(Color.parseColor("#687CC5"));
                    }
                    connectOrEdit.setBackgroundResource(R.drawable.not_selected_button);

                }
            }
        });
        allConnections();

    }

    protected void settingNavigation(){
        final Dialog nav = new Dialog(ProfileActivity.this);
        nav.setContentView(R.layout.setting_nav);
        nav.setCancelable(true);
        nav.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nav.setCanceledOnTouchOutside(true);
        nav.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        nav.getWindow().setGravity(Gravity.RIGHT);
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
                        Toast.makeText(ProfileActivity.this,"Link has ben sent on Email",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this,"An Error ! "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(ProfileActivity.this, SignupActivity.class));
                nav.dismiss();
                //end mainacivi
            }
        });


    }

    private void themedialog() {
        final Dialog nav = new Dialog(ProfileActivity.this);
        nav.setContentView(R.layout.theme);
        nav.setCancelable(true);
        nav.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nav.setCanceledOnTouchOutside(true);
        nav.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        nav.getWindow().setGravity(Gravity.RIGHT);
        nav.getWindow().getAttributes().windowAnimations = android.R.anim.slide_in_left;

        nav.show();
        SharedPreferences ui=getApplicationContext().getSharedPreferences( "com.testing.clubhome", Context.MODE_PRIVATE);


        TextView black=nav.findViewById(R.id.black);
        TextView White=nav.findViewById(R.id.White);

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui.edit().putBoolean("nightmode",true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                nav.dismiss();
            }
        });

        White.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui.edit().putBoolean("nightmode",false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                nav.dismiss();
            }
        });
    }

    public boolean internet_connection(){
        //Check if connected to internet, output accordingly
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected()&& activeNetwork.isAvailable();
        return isConnected;
    }

    public void noInternet(){

        final Dialog dialog2 = new Dialog(this);
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

    public void backPressed(View view){
//        if(mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
        finish();
    }

    @Override
    public void onBackPressed(){
//        if(mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
        super.onBackPressed();
    }

    public void setting(View view){
        settingNavigation();
    }

    private void allConnections(){
        connectionData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            List<String> usersConnection=new ArrayList<>();
            List<String> bothSameConnectionName=new ArrayList<>();

            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().child(userdisplay).exists()){
                    Connections.clear();
                    connected=0;
                    for(DataSnapshot ds:task.getResult().child(userdisplay).getChildren()){
                        if(ds.getValue().equals("C")){
                            connected++;
                            usersConnection.add(ds.getKey());
                            Connections.add(ds.getKey());
                        }
                    }
                    //Show total connections
                    connectionsNum.setText(String.valueOf(connected));
                }
                if(task.getResult().child(user.getUid()).exists()){
                    bothSameConnectionName.clear();
                    bothSameConnection.clear();
                    for(DataSnapshot ds:task.getResult().child(user.getUid()).getChildren()){
                        if(ds.getValue().equals("C") && usersConnection.contains(ds.getKey())){
                            bothSameConnection.add(ds.getKey());
                        }
                    }
                    //get the name of the comman connections
                    DatabaseReference database=firebaseDatabase.getReference("UsersInfo");
                    database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            for(int i=0;i<bothSameConnection.size();i++) {
                                bothSameConnectionName.add(task.getResult().child(bothSameConnection.get(i)).child("name").getValue().toString());
                            }
                            //Show the connection comman in both
                            if(bothSameConnectionName.size()>3){
                                otherConnection.setText(bothSameConnectionName.get(0)+", "
                                        +bothSameConnectionName.get(1)+", "+bothSameConnectionName.get(2)+", +"
                                        +(bothSameConnectionName.size()-3)+"more");

                            }
                            else if(bothSameConnectionName.size()<=3 && bothSameConnectionName.size()>0) {
                                int i=1;
                                String totalText="";
                                for(String name:bothSameConnectionName){
                                    if(i!=bothSameConnectionName.size()) {
                                        totalText = totalText + name + ", ";
                                    }
                                    i++;
                                    Log.i("name",name);
                                }
                                otherConnection.setText(totalText);
                            }
                            else {
                                connectionsGroup.setVisibility(View.GONE);
                            }
                        }
                    });



                }


            }
        });
    }

    private void howManyConnections(){
        connectionData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().child(userdisplay).exists()){
                    connected=0;
                    for(DataSnapshot ds:task.getResult().child(userdisplay).getChildren()){
                        if(ds.getValue().toString().equals("C")){
                            connected++;
                        }
                    }
                    connectionsNum.setText(String.valueOf(connected));
                }
            }
        });
    }
}