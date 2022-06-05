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

public class LogIn extends AppCompatActivity {
    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button button;
    TextView textView;
    EditText etEmail, etPassword;
    String  email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etEmail = findViewById(R.id.uname);
        etPassword = findViewById(R.id.password);

        button = (Button) findViewById(R.id.log_butt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if(!email.matches("") && !password.matches("")){
                    logincond(email,password);
                    //showProgressBar();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Log In failed.",
                            Toast.LENGTH_SHORT).show();
                    recreate();;
                }
            }}
        );
        textView=(TextView)findViewById(R.id.register);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterPage();
            }
        });
    }
    public void logincond(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private static final String TAG ="Loginpage" ;
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()){
                                Log.w(TAG, "signInWithEmail:success", task.getException());
                                Toast.makeText(getApplicationContext(), "Log In success",
                                        Toast.LENGTH_SHORT).show();
                                //hideProgressBar();
                                finish();
                                NavDrawer();
                            }
                            else if(!user.isEmailVerified()){
                                Toast.makeText(getApplicationContext(), "Log In failed. Please verify Email",
                                        Toast.LENGTH_LONG).show();
                                //hideProgressBar();
                                recreate();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Log In failed." + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                                //hideProgressBar();

                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Log In failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //hideProgressBar();
                            recreate();
                        }
                    }
                });
    }
    public void NavDrawer () {
        Intent intentN = new Intent(this, NavDrawer.class);
        startActivity(intentN);
    }

    public void RegisterPage() {
        Intent intentR = new Intent(this, Register.class);
        startActivity(intentR);
    }}