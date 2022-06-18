package com.example.freelancerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class NavDrawer extends AppCompatActivity {
    TextView textView;
    ImageView ivCleaning, ivLaundry, ivPlumbing, ivElectrical;
    //Initialize Variable
    DrawerLayout drawerLayout;
    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nav_drawer);
        //assign variable
        drawerLayout = findViewById(R.id.drawer_layout);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        userID = fUser.getUid();
        mDatabase = mFirebaseDatabase.getReference();

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

        ivCleaning = findViewById(R.id.cleaning);
        ivLaundry= findViewById(R.id.laundry);
        ivPlumbing = findViewById(R.id.plumbing);
        ivElectrical = findViewById(R.id.electrical);

        ActivityCompat.requestPermissions(NavDrawer.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_PHONE_STATE}, PackageManager.PERMISSION_GRANTED);

        ivCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cleaning();
            }
        });
        ivLaundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Laundry();
            }
        });
        ivPlumbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Plumbing();
            }
        });
        ivElectrical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Electrical();
            }
        });
    }

    public void Cleaning() {
        Intent intentR = new Intent(this, Cleaning.class);
        startActivity(intentR);
    }

    public void Laundry() {
        Intent intentR = new Intent(this, Laundry.class);
        startActivity(intentR);
    }

    public void Plumbing() {
        Intent intentR = new Intent(this, Plumbing.class);
        startActivity(intentR);
    }

    public void Electrical() {
        Intent intentR = new Intent(this, Electrical.class);
        startActivity(intentR);
    }

    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public void ClickLogo(View view){
        //close drawer
        closeDrawer(drawerLayout);

    }
    public static void closeDrawer(DrawerLayout drawerLayout) {
        //close drawer layout
        //check condition
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            //when drawer is open
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public void ClickHome(View view){
        //Recreate activity
        recreate();
    }
    public void ClickProfile(View view){
        //Recreate activity to profile
        redirectActivity(this,Profile.class);
    }
    public void ClickDashboard(View view){
        //Recreate activity to settings
        redirectActivity(this, Dashboard.class);
    }
    public void ClickAbout(View view){
        //Recreate activity to settings
        redirectActivity(this,About.class);
    }
    public void ClickLogout(View view){
        //Recreate activity to logout
        logout(this);
    }

    public static void logout(final Activity activity) {
        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Logout");
        //set message
        builder.setMessage("Are you sure you want to logout?");
        //positive yes
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Finish Activity
                activity.finishAffinity();


            }
        });
        //negative no button
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();

            }
        });
        //show dialog
        builder.show();
    }
    public static void redirectActivity(Activity activity, Class aClass) {
        //initialize intent
        Intent intent = new Intent(activity,aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start activity
        activity.startActivity(intent);
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
        //close drawer
        closeDrawer(drawerLayout);
    }

}