package com.example.freelancerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class About extends AppCompatActivity {
    TextView textView;
    //initialize variable
    DrawerLayout drawerLayout;
    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_about);
        // globally
        TextView myAwesomeTextView = (TextView)findViewById(R.id.NavTitle);

//in your OnCreate() method
        myAwesomeTextView.setText("About");

        //assign variable
        drawerLayout = findViewById(R.id.drawer_layout);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                String TAG ="Authstate" ;
                if(user != null){
                    Log.w(TAG, "onAuthStateChanged:sign_in" + user.getUid());
                }
                else{
                    Log.w(TAG, "onAuthStateChanged:sign_out");

                }
            }
        };

        textView = findViewById(R.id.notification);
        mDatabase.child("bookings").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                textView.setText(Integer.toString(size));
                Log.d("notif", "onDataChange: " +size);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void ClickMenu (View view){
        //open drawer
        NavDrawer.openDrawer(drawerLayout);

    }
    public void ClickLogo(View view){
        //close drawer
        NavDrawer.closeDrawer(drawerLayout);
    }
    public void ClickHome(View view){
        //redirect activity to home
        NavDrawer.redirectActivity(this, NavDrawer.class);
    }
    public void ClickAbout(View view){
        //recreate activity
        recreate();
    }
    public void ClickDashboard(View view){
        //redirect activity to settings
        NavDrawer.redirectActivity(this, Dashboard.class);
    }
    public void ClickProfile(View view){
        //redirect activity to settings
        NavDrawer.redirectActivity(this, Profile.class);
    }
    public void ClickLogout(View view){
        //logout
        NavDrawer.logout(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NavDrawer.closeDrawer(drawerLayout);
    }
}

