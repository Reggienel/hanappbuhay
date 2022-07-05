package com.example.freelancerapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Payout extends AppCompatActivity {
    private Button button, button2;

    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    StorageReference storageReference;

    EditText etFname, etAmount, etAccountnumber;
    String fname, strAmount, strAccountnumber, userID;
    Double Amount, balance;

    private static final String TAG = "payout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        userID = user.getUid();

        etFname = findViewById(R.id.fname);
        etAmount = findViewById(R.id.amountTxt);
        etAccountnumber = findViewById(R.id.accountNumber);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    try {
                        User user = new User();
                        user.setUsername(ds.child(userID).getValue(User.class).getUsername());
                        user.setEmail(ds.child(userID).getValue(User.class).getEmail());
                        user.setPhonenum(ds.child(userID).getValue(User.class).getPhonenum());
                        user.setServicetype(ds.child(userID).getValue(User.class).getServicetype());
                        user.setAvailabity(ds.child(userID).getValue(User.class).getAvailability());
                        user.setServiceprice(ds.child(userID).getValue(User.class).getServiceprice());
                        user.setLocation(ds.child(userID).getValue(User.class).getLocation());
                        user.setRating(ds.child(userID).getValue(User.class).getRating());
                        user.setBalance(ds.child(userID).getValue(User.class).getBalance());

                        balance = user.getBalance();
                    }
                    catch (Exception e){
                        Log.d("Profile", "showData: "+ e.getMessage());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        button = findViewById(R.id.send_butt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname = etFname.getText().toString().trim();
                strAmount = etAmount.getText().toString();
                strAccountnumber = etAccountnumber.getText().toString();
                Amount = Double.parseDouble(strAmount);

                if (fname.matches("")||strAmount.matches("") ||strAccountnumber.matches("")){
                    Toast.makeText(getApplicationContext(), "Please Fill Out", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(Amount >= 100){
                        if(!(Amount > balance)) {
                            AlertDialog.Builder ad2 = new AlertDialog.Builder(Payout.this);
                            ad2.setTitle("Confirmation:");
                            ad2.setIcon(android.R.drawable.ic_dialog_info);
                            ad2.setMessage("Transferring " + Amount + " to " + strAccountnumber);
                            ad2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                    balance = balance - Amount;
                                    mDatabase.child("users").child(userID).child("balance").setValue(balance);
                                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            ad2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            ad2.show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Insufficient Balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Minimum of 100 Pesos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}