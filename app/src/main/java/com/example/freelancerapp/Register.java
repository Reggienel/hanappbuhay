package com.example.freelancerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private Button button;
    TextView textView;
    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    EditText etFname, etEmail, etphonenum, etPassword, etConfPassword;
    String fname, email, cnumber, password, confpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etFname = findViewById(R.id.fname);
        etEmail = findViewById(R.id.email);
        etphonenum = findViewById(R.id.phoneNumber);
        etPassword = findViewById(R.id.password);
        etConfPassword = findViewById(R.id.confPassword);


        textView=(TextView)findViewById(R.id.reg_butt);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname = etFname.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                cnumber = etphonenum.getText().toString();
                password = etPassword.getText().toString();
                confpassword = etConfPassword.getText().toString();


                if (fname.matches("")||email.matches("")||password.matches("")||confpassword.matches("")||
                        cnumber.matches("")){
                    Toast.makeText(getApplicationContext(), "Please Fill Out", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(confpassword.matches(password)){
                        createAccount(email, password);
                        //showProgressBar();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Password did not Match", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });}

    public void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private static final String TAG = "registered";

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
                                                writeNewUser(user.getUid(), fname, email, cnumber, password);
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

    public void writeNewUser(String userId, String name, String email, String phonenum, String password) {
        User user = new User(userId,name, email, phonenum, password);

        mDatabase.child("users").child(userId).setValue(user);
        String TAG = "write";
        Log.d(TAG, " write:success");

    }

    public void LogIn() {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }
}