package com.testing.clubhome.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.R;

public class Help extends AppCompatActivity {
    String which,purpose;
    TextView title,des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Intent intent= getIntent();
        which=intent.getStringExtra("which");
        purpose=intent.getStringExtra("purpose");
        title=findViewById(R.id.title);
        des=findViewById(R.id.Description);
        title.setText(purpose);
        if(which.equals("Club")){
            if(purpose.equals("Rules")){
                des.setText(Constants.RULES_FOR_CLUB);
            }else if(purpose.equals("Help")){
                des.setText(Constants.HELP_FOR_CLUB);
            }else if(purpose.equals("Privacy")){
                des.setText(Constants.Privacy_FOR_CLUB);
            }
        }else {
            if(purpose.equals("Rules")){
                des.setText(Constants.RULES_FOR_ROOM);
            }else if(purpose.equals("Help")){
                des.setText(Constants.HELP_FOR_ROOM);
            }else if(purpose.equals("Privacy")){
                des.setText(Constants.Privacy_FOR_ROOM);
            }
        }

    }

    public void backPressed(View view) {
       finish();
    }
}