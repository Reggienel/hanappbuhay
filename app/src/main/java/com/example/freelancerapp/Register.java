package com.example.freelancerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Register extends AppCompatActivity {
    private Button button, button2;

    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    StorageReference storageReference;

    EditText etFname, etEmail, etphonenum, etPassword, etConfPassword;
    TextView haveAccountTxt;
    CheckBox checkBox;
    String fname, email, cnumber, password, confpassword;
    public Uri imageURI;
    private static final String TAG = "registered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        etFname = findViewById(R.id.fname);
        etEmail = findViewById(R.id.email);
        etphonenum = findViewById(R.id.phoneNumber);
        etPassword = findViewById(R.id.password);
        etConfPassword = findViewById(R.id.confPassword);
        checkBox = findViewById(R.id.checkBox);


        button = findViewById(R.id.reg_butt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname = etFname.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                cnumber = etphonenum.getText().toString();
                password = etPassword.getText().toString();
                confpassword = etConfPassword.getText().toString();


                if (fname.matches("")||email.matches("")||password.matches("")||confpassword.matches("")||
                        cnumber.matches("")||imageURI == null || !checkBox.isChecked()){
                    Toast.makeText(getApplicationContext(), "Please Fill Out", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(confpassword.matches(password)){
                        //showProgressBar();
                        if(cnumber.length() < 12) {
                            if (cnumber.startsWith("09")) {
                                createAccount(email, password);

                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Phone Number must start to 09", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Phone Number must be 11 Digits", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Password did not Match", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        button2 = findViewById(R.id.verify_butt);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfilePic();
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad1 = new AlertDialog.Builder(Register.this);
                ad1.setTitle("Terms and Conditions");
                ad1.setIcon(android.R.drawable.ic_dialog_info);
                ad1.setMessage(getResources().getString(R.string.paragraph));
                ad1.setPositiveButton("Ok", null);
                ad1.show();// Show dialog
            }
        });

        haveAccountTxt = findViewById(R.id.txtHaveAccount);
        haveAccountTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogIn();
            }
        });
    }

    private void chooseProfilePic() {
        Intent openGallery = new Intent();
        openGallery.setType("image/*");
        openGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(openGallery, 1);
        //showProgressBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!= null){
            imageURI=data.getData();
        }
    }

    public void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(),"Sign Up Success. Please Verify Email",
                                                        Toast.LENGTH_SHORT).show();
                                                        final ProgressDialog pd = new ProgressDialog(Register.this);
                                                        pd.setTitle("Uploading Image...");
                                                        pd.show();

                                                        StorageReference strRef = storageReference.child("id/" + user.getUid());
                                                        strRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                pd.dismiss();
                                                                Toast.makeText(getApplicationContext(), "Image Uploaded Successfully",
                                                                        Toast.LENGTH_SHORT).show();
                                                                strRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        mDatabase.child("users").child(user.getUid()).child("id_image_uri").setValue(uri.toString());
                                                                        Log.w(TAG, "get Uri:success");
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "get Uri:failure", e);
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                pd.dismiss();
                                                                Toast.makeText(getApplicationContext(), "Image Upload Failed",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                double progPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                                                pd.setMessage("Percentage: " + (int) progPercent + "%");
                                                            }
                                                        });
                                                writeNewUser(user.getUid(), fname, email, cnumber, getMd5(password));
                                                //hideProgressBar();
                                                LogIn();
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(),"Sign Up Failed",
                                                        Toast.LENGTH_SHORT).show();
                                                //hideProgressBar();
                                                recreate();
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"Sign Up Failed "+task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            recreate();
                        }
                    }
                });
    }

    public static String getMd5(String input)
    {
        try
        {
// invoking the static getInstance() method of the MessageDigest class
// Notice it has MD5 in its parameter.
            MessageDigest msgDst = MessageDigest.getInstance("MD5");

// the digest() method is invoked to compute the message digest
// from an input digest() and it returns an array of byte
            byte[] msgArr = msgDst.digest(input.getBytes());

// getting signum representation from byte array msgArr
            BigInteger bi = new BigInteger(1, msgArr);

// Converting into hex value
            String hshtxt = bi.toString(16);

            while (hshtxt.length() < 32)
            {
                hshtxt = "0" + hshtxt;
            }
            return hshtxt;
        }
// for handling the exception
        catch (NoSuchAlgorithmException abc)
        {
            throw new RuntimeException(abc);
        }
    }

    public void writeNewUser(String userId, String name, String email, String phonenum, String password) {
        User user = new User(userId,name, email, phonenum, password);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void LogIn() {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }
}