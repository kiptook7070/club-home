package com.testing.clubhome.supporting;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.testing.clubhome.Club.ClubHome;
import com.testing.clubhome.Pages.Calender;
import com.testing.clubhome.Pages.MainActivity;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

public class CalenderAdapter extends ArrayAdapter<String> {
    private List<String> clubList;
    private final Activity context;
    //Firebase initialization
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference clubReference,personReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;



    public CalenderAdapter(@NonNull Activity context,List<String> ids) {
        super(context,R.layout.calender_adapter, ids);
        this.clubList=ids;
        this.context = context;
    }

    public View getView(final int position , View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        final View rowView=inflater.inflate(R.layout.calender_adapter, null,true);

        TextView clubName=rowView.findViewById(R.id.clubName);
        TextView roomName=rowView.findViewById(R.id.roomName);
        TextView person=rowView.findViewById(R.id.person);
        TextView time =rowView.findViewById(R.id.time);
        TextView description =rowView.findViewById(R.id.description);

        LinearLayout clubLayout=rowView.findViewById(R.id.clubLayout);
        Button notify=rowView.findViewById(R.id.notify);

        //getting FireBase Values
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        clubReference=firebaseDatabase.getReference("RoomsInfo");
        DatabaseReference club=firebaseDatabase.getReference("ClubsInfo");
        personReference=firebaseDatabase.getReference("UsersInfo");
        if (clubList.size()>position){
            clubReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.getResult().exists()) {
                        if (task.getResult().child( clubList.get(position)).exists()) {

                            roomName.setText(task.getResult().child( clubList.get(position)).child("Assets").child("room name").getValue().toString());
                            List<String> userToStore=new ArrayList<>();
                            description.setText(task.getResult().child( clubList.get(position)).child("Assets").child("short Description").getValue().toString());

                            String dateInString=task.getResult().child("upComing").child(clubList.get(position)).getValue().toString();

                            DateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm");
                            Calendar cal = Calendar.getInstance();
                            Date todaysDate = cal.getTime();

                            Date date=new Date();
                            int tda=todaysDate.getDate();
                            int tmon=todaysDate.getMonth()+1;
                            int tear=todaysDate.getYear()+1900;
                            Log.e("agdknf",tear+"");

                            try {
                                date=format.parse(dateInString);
                                int da=date.getDate();
                                int mon=date.getMonth()+2;
                                int ear=date.getYear()+3800;

                                if ((da==tda)&& (mon==tmon)&&(ear==tear)){
                                    time.setText("Today"+"  .  "+date.getHours()+":"+date.getHours());

                                }else if ((da==(tda+1))&& (mon==tmon)&&(ear==tear)){
                                    time.setText("Tomorrow"+"  .  "+date.getHours()+":"+date.getHours());
                                }else {
                                    time.setText(da+" / "+mon+" / "+ear+" . "+date.getHours()+":"+date.getHours());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            for(DataSnapshot d:task.getResult().child( clubList.get(position)).child("Peoples").getChildren()){

                                if(d.getValue().toString().equals("Owner")){
                                    userToStore.add(d.getKey());
                                    if (user.getUid().equals(d.getKey())){
                                        notify.setText("Start Room");
                                    }
                                }

                            }

                            if(task.getResult().child(clubList.get(position)).child("Assets").child("hosted by").exists()){
                                clubLayout.setVisibility(View.VISIBLE);
                                String ClubId = task.getResult().child(clubList.get(position)).child("Assets").child("hosted by").getValue().toString();

                                club.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task1) {
                                        String ClubName = task1.getResult().child(ClubId).child("Assets").child("club name").getValue().toString();
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

                                    String name=task1.getResult().child(userToStore.get(0)).child("name").getValue().toString();
                                    person.setText(name);

                                }
                            });

                        }
                    }
                }
            });
        }

        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notify.getText().toString().equals("Start Room")) {
                    roomOpen(clubList.get(position));
                }else {
                    Toast.makeText(getContext(),"you will be notified",Toast.LENGTH_LONG).show();
                }


            }
        });

        return rowView;
    }

    private void roomOpen(String roomId){
        Calendar cal = Calendar.getInstance();
        final Date[] date1 = {cal.getTime()};
        String time=date1[0].getYear()+ "/" + date1[0].getMonth()+ "/" +date1[0].getDate()+ " " +date1[0].getHours()+ ":"+date1[0].getMinutes() ;

        personReference.child(user.getUid()).child("Room Participated").setValue(roomId);
        clubReference.child(roomId).child("Assets").child("Started").setValue("Started");
        clubReference.child("upComing").child(roomId).removeValue();
        clubReference.child("onGoing").child(roomId).setValue("Public");
        clubReference.child(roomId).child("Assets").child("time").setValue(time);
        clubReference.child(roomId).child("Peoples").child(user.getUid()).setValue("Owner").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent club = new Intent(getContext(), com.testing.clubhome.Room.room.class);
                club.putExtra("roomid", roomId);
                club.putExtra("join", true);
                getContext().startActivity(club);

            }
        });

    }

}
