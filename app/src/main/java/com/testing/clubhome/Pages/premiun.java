package com.testing.clubhome.Pages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testing.clubhome.Constant.Constants;
import com.testing.clubhome.R;

import java.util.List;

public class premiun extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    BillingProcessor bp;
    Button join;
    TransactionDetails subscriptionTransactionDetails;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premiun);
        join=findViewById(R.id.join);
        bp=new BillingProcessor(this, Constants.GOOGLE_PLAY_LICENSE, this);
        bp.initialize();
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase=firebaseDatabase.getReference("UsersInfo").child(user.getUid());
    }

    public boolean hassubsription(){
        if (subscriptionTransactionDetails!=null){
            return subscriptionTransactionDetails.purchaseInfo!=null;
        }else{
            return false;
        }
    }

    public void backPressed(View view) {
        finish();
    }

    @Override
    public void onBillingInitialized() {

        subscriptionTransactionDetails=bp.getPurchaseTransactionDetails(Constants.PRODUCT_ID);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bp.isOneTimePurchaseSupported()){
                    if (!hassubsription()){
                        bp.purchase(premiun.this,Constants.PRODUCT_ID);
                    }
                }
            }
        });
        if (hassubsription()){
            mDatabase.child("pro").setValue("pro");
            Toast.makeText(premiun.this,"You have purchased, Just restart the app.",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        mDatabase.child("pro").setValue("pro");
        mDatabase.child("roomremaining").removeValue();
        Toast.makeText(premiun.this,"You have purchased, Just restart the app.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

}