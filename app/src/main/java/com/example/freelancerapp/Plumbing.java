package com.example.freelancerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import java.util.ArrayList;


public class Plumbing extends AppCompatActivity implements OnNoteListener{
    TextView textView;
    //initialize variable
    DrawerLayout drawerLayout;
    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ArrayList<User> userArrayList;
    private RecyclerView recyclerView;
    String userID, serType, userid, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_plumbing);
        // globally
        TextView myAwesomeTextView = (TextView)findViewById(R.id.NavTitle);

//in your OnCreate() method
        myAwesomeTextView.setText("PLUMBING");

        //assign variable
        drawerLayout = findViewById(R.id.drawer_layout);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference("users");
        FirebaseUser fUser = mAuth.getCurrentUser();
        userID = fUser.getUid();

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

        recyclerView = findViewById(R.id.recycleview);
        userArrayList = new ArrayList<>();
        setAdapter();
    }

    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(userArrayList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    serType = ds.getValue(User.class).getServicetype();


                    try {
                        if (serType.matches("Plumbing")) {
                            userArrayList.add(user);
                            username = ds.getValue(User.class).getUsername();
                            userid = ds.getValue(User.class).getUserid();
                        }
                    }
                    catch (NullPointerException ignored){
                    }


                }
                adapter.notifyDataSetChanged();

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
    public void ClickProfile(View view){
        //recreate activity
        NavDrawer.redirectActivity(this, Profile.class);
    }
    public void ClickSettings(View view){
        //redirect activity to settings
        NavDrawer.redirectActivity(this, Settings.class);
    }
    public void ClickAbout(View view){
        //redirect activity to settings
        NavDrawer.redirectActivity(this, About.class);
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

    @Override
    public void onItemClicked(User user) {
        Intent intentP = new Intent(this, activity_appointment.class);
        intentP.putExtra("userid",userid);
        intentP.putExtra("username",username);
        finish();
        startActivity(intentP);
    }
}

