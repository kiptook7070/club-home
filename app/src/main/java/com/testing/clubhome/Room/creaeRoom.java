package com.testing.clubhome.Room;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testing.clubhome.Pages.Calender;
import com.testing.clubhome.Pages.CreateClub;
import com.testing.clubhome.Pages.premiun;
import com.testing.clubhome.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class creaeRoom extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText roomname,shortDiscription;
    ImageView back;
    TextView date,hour;
    Button create;
    LinearLayout timeLayout;
    SwitchCompat switchCompat;
    String roomId;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase,clubReference,roomReference;
    FirebaseUser user;
    List<String> clubsList=new ArrayList<>();
    List<String> clubsname=new ArrayList<>();
    boolean privacy = false;
    DatePickerDialog.OnDateSetListener setListener;
    String writtenDate,clubselec;
    Spinner spinner;
    TextView purchase;
    boolean prouser=false;
    int roomremaing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creae_room);
        back = findViewById(R.id.back);
        create = findViewById(R.id.createRoom);
        roomname = findViewById(R.id.roomName);
        shortDiscription = findViewById(R.id.shortDiscription);
        date = findViewById(R.id.date);
        timeLayout=findViewById(R.id.timeLayout);
        hour = findViewById(R.id.hour);
        switchCompat = findViewById(R.id.privacy);
        spinner=findViewById(R.id.spinner);
        purchase=findViewById(R.id.purchase);

        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        clubReference = firebaseDatabase.getReference("ClubsInfo");
        mDatabase = firebaseDatabase.getReference("UsersInfo").child(user.getUid());
        roomReference = firebaseDatabase.getReference("RoomsInfo");


        clubsList.add("none");
        clubsname.add("none");
        clubselec="none";
        //getting data part
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().child("pro").exists()){
                    purchase.setVisibility(View.INVISIBLE);
                    prouser=true;
                }else{
                    prouser=false;
                }

                if (task.getResult().child("roomremaining").exists()) {
                    roomremaing= Integer.parseInt(task.getResult().child("roomremaining").getValue().toString());
                    purchase.setText("You have "+roomremaing+" rooms this month purchase pro band for unlimited room");

                }
                if(task.getResult().child("Club").exists()) {
                    clubsList.clear();
                    for (DataSnapshot s : task.getResult().child("Club").getChildren()){
                        clubsList.add(s.getKey());
                    }
                }

            }
        });

        clubReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()) {
                    clubsname.clear();

                    for (String s : clubsList){
                        if(task.getResult().child(s).exists()) {
                            clubsname.add(task.getResult().child(s).child("Assets").child("club name").getValue().toString());
                        }
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(creaeRoom.this, android.R.layout.simple_spinner_item, clubsname);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
                    spinner.setOnItemSelectedListener(creaeRoom.this);
                }
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                privacy = isChecked;
            }
        });


        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        Calendar cal = Calendar.getInstance();
        final Date[] date1 = {cal.getTime()};

        date.setText(( date1[0].getYear()+1900)+ "/"+ date1[0].getMonth()+ "/"+date1[0].getDate());

        hour.setText(date1[0].getHours()+ ":"+date1[0].getMinutes());
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(creaeRoom.this, android.R.style.Theme_Holo_Dialog_MinWidth, setListener,  date1[0].getYear()+1900, date1[0].getMonth(), date1[0].getDate());
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        writtenDate =(date.getText().toString() + " " + hour.getText().toString());



        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                int tomonth = i1 + 1;
                date.setText(i+ "/"+ tomonth+ "/"+i2);

            }
        };

        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(creaeRoom.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour.setText( selectedHour + ":" + selectedMinute);
                    }
                }, date1[0].getHours(), date1[0].getMinutes(), true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        //onClick

        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createClub = new Intent(creaeRoom.this, premiun.class);
                startActivity(createClub);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prouser||(roomremaing>0)){
                    if (roomname.getText().toString().isEmpty()) {
                        Toast.makeText(creaeRoom.this, "Enter Room Name", Toast.LENGTH_SHORT).show();
                    } else {
                        String roomTime = date.getText() + " " + hour.getText() ;

                        if (!prouser){
                           roomremaing =roomremaing-1;
                           mDatabase.child("roomremaining").setValue(roomremaing);
                        }
                        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        Date date2 = new Date();
                        try {
                            date2 = format.parse(roomTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date1[0].compareTo(date2) < 0) {
                            //room is starting after some time

                            String Time=date2.getYear() + "/" +(date2.getMonth()) + "/" +date2.getDate()+ " " +date2.getHours()+ ":" +date2.getDate();

                            Random random = new Random();
                            int random1 = random.nextInt(100);
                            int random2 = random.nextInt(100);
                            int random3 = random.nextInt(100);
                            int random4 = random.nextInt(100);
                            int random5 = random.nextInt(100);
                            roomId = random1 +""+ random2+""+random3+""+random4+""+random5+"";
                            roomReference.child("upComing").child(roomId).setValue(Time);
                            roomReference.child(roomId).child("Assets").child("Started").setValue("Not Started");
                            roomReference.child(roomId).child("Assets").child("room name").setValue(roomname.getText().toString());
                            roomReference.child(roomId).child("Assets").child("short Description").setValue(shortDiscription.getText().toString());
                            if (privacy) {
                                roomReference.child(roomId).child("Assets").child("Privacy").setValue("Private");
                            } else {
                                roomReference.child(roomId).child("Assets").child("Privacy").setValue("Public");
                            }

                            if (clubselec.equals("none")){

                            }
                            else {
                                roomReference.child(roomId).child("Assets").child("hosted by").setValue(clubselec);
                                clubReference.child(clubselec).child("Rooms").child(roomId).setValue("Not Started");
                            }


                            roomReference.child(roomId).child("Peoples").child(user.getUid()).setValue("Owner");


                            finish();
                        } else {
                            Toast.makeText(creaeRoom.this,"check time",Toast.LENGTH_LONG).show();
                        }




                    }
                }else {
                    Toast.makeText(creaeRoom.this, "Rooms for this month has been ended ", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        clubselec=clubsList.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}