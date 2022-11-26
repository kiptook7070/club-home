package com.testing.clubhome.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.R;

public class Report extends AppCompatActivity {
    EditText issue,email,description;
    TextView title;
    String Id,which;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        title=findViewById(R.id.title);
        issue=(EditText)findViewById(R.id.issue);
//        email=(EditText) findViewById(R.id.emailAdress);
        description=(EditText) findViewById(R.id.Description);
        Intent intent= getIntent();
        Id =intent.getStringExtra("Id");
        which=intent.getStringExtra("which");
        title.setText("Reporting the "+which);

    }

    public void backPressed(View view) {
        finish();
    }

    public void Send(View view) {
        String i=issue.getText().toString();
        String e=email.getText().toString();
        String d=description.getText().toString();
        if(i.isEmpty()||e.isEmpty()){
            Toast.makeText(Report.this,"Enter name and issue",Toast.LENGTH_LONG).show();
        }
        else{
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL,Constants.EMAIL_ADRESS);
            intent.putExtra(Intent.EXTRA_SUBJECT,i);
            intent.putExtra(Intent.EXTRA_TEXT,e+" "+Id+" "+which+" "+d);
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent,"Choose Email client"));
        }
    }
}