package com.testing.clubhome.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.testing.clubhome.Pages.MainActivity;
import com.testing.clubhome.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignupActivity extends AppCompatActivity {

    View signUpbar, signInbar;
    LinearLayout signingIn,signingUp, signUp, signIn;

    EditText signInEmail,signInPassword,signUpEmail,signUpPassword,signUpUsername,signUpcPassword;
    TextView forgotPassword,sIPasswordError,sIEmailError,suPasswordError,sucPasswordError;
    SignInButton signUpGoogle;

    String username,email,password;
    FirebaseAuth firebaseAuth;

    ///Sign in functions
    public void signin(View view){
        if(!internet_connection()) {
            noInternet();
            return;
        }
        sIPasswordError.setVisibility(View.INVISIBLE);
        sIEmailError.setVisibility(View.INVISIBLE);
        email=signInEmail.getText().toString();
        password=signInPassword.getText().toString();

        if(email.isEmpty()||password.isEmpty()){
            String s="Enter All inputs";
            displayToast(s);
        }
        else if(password.length()<=5){
            sIPasswordError.setText("Password must have at least 6 digits");
            sIPasswordError.setVisibility(View.VISIBLE);
        }
        else{
            sIPasswordError.setVisibility(View.INVISIBLE);
            sIEmailError.setVisibility(View.INVISIBLE);
            signInEmail.setText("");
            signInPassword.setText("");
            //Calling User profile activity and getting data from firebase
            checkingFirebase();

        }


    }

    public void checkingFirebase(){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    String s="Welcome "+user.getDisplayName();
                    displayToast(s);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else{
                    String s="Error! "+task.getException();
                    displayToast(s);
                }
            }
        });
    }

    ///Sign up functions
    public void signup(View view){
        suPasswordError.setVisibility(View.GONE);
        sucPasswordError.setVisibility(View.GONE);
        if(!internet_connection()) {
            noInternet();
            return;
        }
        username=signUpUsername.getText().toString();
        email=signUpEmail.getText().toString();
        password=signUpPassword.getText().toString();
        if(username.isEmpty()||email.isEmpty()||password.isEmpty()||signUpcPassword.getText().toString().isEmpty()) {
            String s = "Enter All Inputs";
            displayToast(s);
        }
        else if(password.length()<=5){
            suPasswordError.setText("Password must have at least 6 digits");
            suPasswordError.setVisibility(View.VISIBLE);
        }
        else if(!signUpcPassword.getText().toString().equals(password)){
            sucPasswordError.setText("Enter same password as above");
            sucPasswordError.setVisibility(View.VISIBLE);
        }
        else{

            suPasswordError.setVisibility(View.GONE);
            sucPasswordError.setVisibility(View.GONE);
            addToFirebase();
            signUpEmail.setText("");
            signUpUsername.setText("");
            signUpcPassword.setText("");
            signUpPassword.setText("");
            //Adding data to firebase

        }
    }

    public void addToFirebase(){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdate =new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                    user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String s="Successfully Signed Up";
                            displayToast(s);
                            storingToFirebase(user);

                            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                }else{
                    String s="Error! "+task.getException();
                    displayToast(s);
                }
            }
        });
    }

    private void storingToFirebase(FirebaseUser user) {
        //storing data to firebase real time database
        HashMap<Object,String> hashMap=new HashMap<>();

        hashMap.put("name",user.getDisplayName());
        hashMap.put("bio","");
        hashMap.put("job","");
        hashMap.put("web","");
        hashMap.put("shortDis","");

        try {
            hashMap.put("profilePhoto", user.getPhotoUrl().toString());
        }catch (Exception e){
            hashMap.put("profilePhoto","");
        }

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        //user data is stored in "UsersInfo"

        DatabaseReference reference=database.getReference("UsersInfo");

        reference.child(user.getUid()).setValue(hashMap);
        reference.child(user.getUid()).child("roomremaining").setValue(20);

    }

    boolean internet_connection() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signUp=findViewById(R.id.signUp);
        signIn=findViewById(R.id.signIn);
        signingIn=findViewById(R.id.signingIn);
        signingUp=findViewById(R.id.signingUp);

        signUpbar=findViewById(R.id.signUpBar);
        signInbar=findViewById(R.id.signInBar);

        signInEmail=findViewById(R.id.signInEmail);
        signInPassword=findViewById(R.id.signInPassword);
        forgotPassword=findViewById(R.id.forgotPassword);
        sIPasswordError=findViewById(R.id.passwordError);
        sIEmailError=findViewById(R.id.emailError);
        forgotPassword=findViewById(R.id.forgotPassword);

        signUpUsername=findViewById(R.id.userName);
        signUpcPassword=findViewById(R.id.confirmPassword);
        signUpEmail=findViewById(R.id.signUpEmail);
        signUpPassword=findViewById(R.id.firstPassword);
        suPasswordError=findViewById(R.id.suPasswordError);
        sucPasswordError=findViewById(R.id.sucPasswordError);
        signUpGoogle=findViewById(R.id.googleSign);

        GoogleSignInClient googleSignInClint;

        firebaseAuth=FirebaseAuth.getInstance();

        signingIn.setVisibility(View.INVISIBLE);
        signingUp.setVisibility(View.VISIBLE);
        signUpbar.setVisibility(View.VISIBLE);
        signInbar.setVisibility(View.INVISIBLE);

        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //navigating
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUpbar.setVisibility(View.VISIBLE);
                signInbar.setVisibility(View.INVISIBLE);
                signingIn.setVisibility(View.INVISIBLE);
                signingUp.setVisibility(View.VISIBLE);
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpbar.setVisibility(View.INVISIBLE);
                signInbar.setVisibility(View.VISIBLE);
                signingUp.setVisibility(View.INVISIBLE);
                signingIn.setVisibility(View.VISIBLE);
            }
        });

        //If password forgot
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), ForgotPassward.class);
                startActivity(intent);

            }
        });




        googleSignInClint=GoogleSignIn.getClient(this,gso);

        signUpGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =googleSignInClint.getSignInIntent();
                startActivityForResult(intent,100);
            }
        });

        //if user has logged
        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            if(signInAccountTask.isSuccessful()){
                String s="Google sign in successful";
                displayToast(s);
                try {
                    GoogleSignInAccount account = signInAccountTask.getResult(ApiException.class);
                    //if(account!=null){
                        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user=firebaseAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdate =new UserProfileChangeRequest.Builder().setDisplayName(account.getDisplayName()).build();
                                    UserProfileChangeRequest profileUpdate2=new UserProfileChangeRequest.Builder().setPhotoUri(account.getPhotoUrl()).build();
                                    user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            String s="Successfully Signed Up";
                                            displayToast(s);
                                        }
                                    });

                                    if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                                        storingToFirebase(user);
                                    }
                                    Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("user",user.getUid());
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    displayToast("Failed!!!");
                                }
                            }
                        });

                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //display toast
    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}