package com.example.freelancerapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Dashboard extends AppCompatActivity implements OnNoteListenerdashboard{
    TextView textView;
    //initialize variable
    DrawerLayout drawerLayout;
    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ArrayList<UserAppointment> userArrayList2;
    private RecyclerView recyclerView;
    String  aId, aName, aService, aTime, aDate;
    String userID, username; // info current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        setContentView(R.layout.dashboard);
        // globally
        TextView myAwesomeTextView = (TextView)findViewById(R.id.NavTitle);

//in your OnCreate() method
        myAwesomeTextView.setText("Dashboard");

        //assign variable
        drawerLayout = findViewById(R.id.drawer_layout);
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


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        userID = fUser.getUid();
        mDatabase = mFirebaseDatabase.getReference();

        recyclerView = findViewById(R.id.recycleviewdb);
        userArrayList2 = new ArrayList<UserAppointment>();
        setAdapter();
    }

    private void setAdapter() {
        recyclerAdapterDashboard adapter = new recyclerAdapterDashboard(userArrayList2, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

       mDatabase.child("bookings").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
//                        User user = new User();
//                        user.setUsername(ds.child(userID).getValue(User.class).getUsername());
//                        user.setEmail(ds.child(userID).getValue(User.class).getEmail());
//                        user.setPhonenum(ds.child(userID).getValue(User.class).getPhonenum());
//                        user.setServicetype(ds.child(userID).getValue(User.class).getServicetype());
//                        user.setAvailabity(ds.child(userID).getValue(User.class).getAvailability());

                        UserAppointment userAppointment = new UserAppointment();
                        userAppointment = ds.getValue(UserAppointment.class);
                        aId = ds.getValue(UserAppointment.class).getId();
                        aName = ds.getValue(UserAppointment.class).getName();
                        aService = ds.getValue(UserAppointment.class).getService();
                        aTime = ds.getValue(UserAppointment.class).getTime();
                        aDate= ds.getValue(UserAppointment.class).getDate();
                        userArrayList2.add(userAppointment);
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
    public void ClickAbout(View view){
        //recreate activity
        NavDrawer.redirectActivity(this, About.class);
    }
    public void ClickDashboard(View view){
        //redirect activity to settings
        recreate();
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

    @Override
    public void onItemClicked(UserAppointment userAppointment) {
        Intent intentCheckOut = new Intent(this, CheckoutActivityJava.class);
        intentCheckOut.putExtra("aId",userAppointment.getId());
        finish();
        startActivity(intentCheckOut);
    }



    @Override
    public void onItemClickedCancel(UserAppointment userAppointment) {
        mDatabase.child("bookings").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AlertDialog.Builder ad1 = new AlertDialog.Builder(Dashboard.this);
                ad1.setTitle("Confirmation:");
                ad1.setIcon(android.R.drawable.ic_dialog_info);
                ad1.setMessage("Are you sure you want to cancel your appointment with " + userAppointment.getName() + " on " + userAppointment.getDate() + " at " + userAppointment.getTime() +" ?");
                ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                            dataSnapshot.getRef().child(String.valueOf(userAppointment.getId())).removeValue();
                            Toast.makeText(getApplicationContext(),  "Appointment Cancelled",
                            Toast.LENGTH_SHORT).show();
                            mDatabase.child("bookings").child(userAppointment.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().child(userID).removeValue();
                                    Toast.makeText(getApplicationContext(),  "Employee Notified",
                                            Toast.LENGTH_SHORT).show();
                                            recreate();
                                }
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(),"Employee Not Notified",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("User", databaseError.getMessage());
                                }
                            });

                    }
                });

                ad1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {

                    }
                });
                ad1.show();// Show dialog

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }

    @Override
    public void onItemClickedMessage(UserAppointment userAppointment) {
        Uri uri = Uri.parse("smsto:"+userAppointment.getPhonenum());
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        finish();
        startActivity(intent);
    }

}

