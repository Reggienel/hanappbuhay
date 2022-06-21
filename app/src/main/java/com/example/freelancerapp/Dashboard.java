package com.example.freelancerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.stream.IntStream;


public class Dashboard extends AppCompatActivity implements OnNoteListenerdashboard{
    TextView textView;
    //initialize variable
    DrawerLayout drawerLayout;
    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public ArrayList<UserAppointment> userArrayList2;
    public RecyclerView recyclerView;
    recyclerAdapterDashboard adapter;
    int recyclerItems;
    String  aId, aName, aService, aTime, aDate, aPrice, aPhonenum;
    String userID, userName, rating, userPhone; // info current user
    List<Double> arr = new ArrayList<>();
//    double[] arr;
    double ratingValue;
    RatingBar ratingBar;

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
        db = FirebaseFirestore.getInstance();

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

                        userName = user.getUsername();
                        userPhone = user.getPhonenum();

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

        recyclerView = findViewById(R.id.recycleviewdb);
        userArrayList2 = new ArrayList<UserAppointment>();
        setAdapter();
    }

    private void setAdapter() {
        adapter = new recyclerAdapterDashboard(userArrayList2, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

       mDatabase.child("bookings").child(userID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        UserAppointment userAppointment = new UserAppointment();
                        userAppointment = ds.getValue(UserAppointment.class);
                        aId = ds.getValue(UserAppointment.class).getId();
                        aName = ds.getValue(UserAppointment.class).getName();
                        aService = ds.getValue(UserAppointment.class).getService();
                        aTime = ds.getValue(UserAppointment.class).getTime();
                        aDate= ds.getValue(UserAppointment.class).getDate();
                        aPrice = ds.getValue(UserAppointment.class).getServiceprice();
                        aPhonenum = ds.getValue(UserAppointment.class).getPhonenum();
                        userArrayList2.add(userAppointment);
                        Log.d("TAG", "onDataChange: "+aName);
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
     public void sendSMS(String messageToSend, String number){
         Log.d("User", "sendSMS: " + messageToSend + number);
        SmsManager mySmsManager = SmsManager.getDefault();
        mySmsManager.sendTextMessage(number, null, messageToSend, null,null);
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
        if(userAppointment.getName().startsWith("Employee")){
            LayoutInflater factory = LayoutInflater.from(Dashboard.this);
            final View textEntryView = factory.inflate(R.layout.dialog_rate_user, null);
            String TAG = "Firestore";

            ratingBar = textEntryView.findViewById(R.id.dialogRatingBar);

            AlertDialog.Builder ad1 = new AlertDialog.Builder(Dashboard.this);
            ad1.setTitle("Rate the User?:");
            ad1.setIcon(android.R.drawable.ic_dialog_info);
            ad1.setView(textEntryView);
            ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {
                    ratingValue = ratingBar.getRating();
                    try {
                        Map<String, Object> review = new HashMap<>();
                        review.put("reviewerName", userName);

                        review.put("reviewId", userAppointment.getId());
                        review.put("reviewName", userAppointment.getName());
                        review.put("reviewRating", ratingValue);

                        // Add a new document with a generated ID
                        db.collection("users_review")
                                .add(review)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                        db.collection("users_review")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.get("reviewId").equals(userAppointment.getId())) {
                                                    Log.d(TAG, document.getId() + " => " + document.get("reviewRating"));
                                                    arr.add((Double) document.get("reviewRating"));
                                                }
                                            }
                                            double totalarr = arr.stream().mapToDouble(Double::doubleValue).sum();
                                            double finalRate = totalarr / arr.size();

                                            mDatabase.child("users").child(userAppointment.getId()).child("rating").setValue(String.valueOf(finalRate));
                                            mDatabase.child("users").child(userAppointment.getId()).child("userratingcount").setValue(String.valueOf(arr.size()));

                                            checkOut(userAppointment.getId(), userAppointment.getServiceprice());
                                            Log.d(TAG, Arrays.toString(new List[]{arr}));
                                            Log.d(TAG, "Total " + finalRate);
                                            Log.d(TAG, "Total " + arr.size());
                                        } else {
                                            Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                    }
                                });
                        Log.d(TAG, "onClick: " + ratingValue);
                    } catch (NullPointerException ignored) {

                    }
                }
            });
            ad1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {

                }
            });
            ad1.show();// Show dialog
        }
        else{
            Log.d("User", "onItemClicked: " + userAppointment.getPayment());
                if(!userAppointment.getPayment().equals("Not Paid") || userAppointment.getPayment().equals("Cash on Meet")){
                    AlertDialog.Builder ad1 = new AlertDialog.Builder(Dashboard.this);
                    ad1.setTitle("Confirmation:");
                    ad1.setIcon(android.R.drawable.ic_dialog_info);
                    ad1.setMessage("Are you sure the appointment with " + userAppointment.getName() + " on " + userAppointment.getDate() + " at " + userAppointment.getTime() + " is Done");
                    ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            mDatabase.child("bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    LayoutInflater factory = LayoutInflater.from(Dashboard.this);
                                    final View textEntryView = factory.inflate(R.layout.dialog_rate_user, null);
                                    String TAG = "Firestore";

                                    ratingBar = textEntryView.findViewById(R.id.dialogRatingBar);

                                    AlertDialog.Builder ad1 = new AlertDialog.Builder(Dashboard.this);
                                    ad1.setTitle("Rate the User?:");
                                    ad1.setIcon(android.R.drawable.ic_dialog_info);
                                    ad1.setView(textEntryView);
                                    ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int i) {
                                            ratingValue = ratingBar.getRating();
                                            try {
                                                Map<String, Object> review = new HashMap<>();
                                                review.put("reviewerName", userName);

                                                review.put("reviewId", userAppointment.getId());
                                                review.put("reviewName", userAppointment.getName());
                                                review.put("reviewRating", ratingValue);

                                                // Add a new document with a generated ID
                                                db.collection("users_review")
                                                        .add(review)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error adding document", e);
                                                            }
                                                        });
                                                db.collection("users_review")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        if (document.get("reviewId").equals(userAppointment.getId())) {
                                                                            Log.d(TAG, document.getId() + " => " + document.get("reviewRating"));
                                                                            arr.add((Double) document.get("reviewRating"));
                                                                        }
                                                                    }
                                                                    double totalarr = arr.stream().mapToDouble(Double::doubleValue).sum();
                                                                    double finalRate = totalarr / arr.size();

                                                                    mDatabase.child("users").child(userAppointment.getId()).child("rating").setValue(String.valueOf(finalRate));
                                                                    mDatabase.child("users").child(userAppointment.getId()).child("userratingcount").setValue(String.valueOf(arr.size()));

                                                                    dataSnapshot.getRef().child(userID).child(userAppointment.getId()).removeValue();
                                                                    dataSnapshot.getRef().child(userAppointment.getId()).child(userID).removeValue();

                                                                    Toast.makeText(getApplicationContext(), "Appointment Finished",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    recreate();
                                                                    Log.d(TAG, Arrays.toString(new List[]{arr}));
                                                                    Log.d(TAG, "Total " + finalRate);
                                                                    Log.d(TAG, "Total " + arr.size());
                                                                } else {
                                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                                }
                                                            }
                                                        });
                                                Log.d(TAG, "onClick: " + ratingValue);
                                            } catch (NullPointerException ignored) {

                                            }
                                        }
                                    });
                                    ad1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int i) {

                                        }
                                    });
                                    ad1.show();// Show dialog

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

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
                else{
                        Toast.makeText(getApplicationContext(), "Appointment Not Paid",
                                Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void checkOut(String id , String price) {
        String[] settings_array = new String[]{"Cash on Meet", "Credit & Debit Card"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
        builder.setTitle("Choose Payment Method")
                .setItems(settings_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            mDatabase.child("bookings").child(userID).child(aId).child("payment").setValue("Cash on Meet");
                            mDatabase.child("bookings").child(aId).child(userID).child("payment").setValue("Cash on Meet");
                        }
                        else if(which == 1){
                            Intent intentCheckOut = new Intent(getApplicationContext(), CheckoutActivityJava.class);
                            intentCheckOut.putExtra("aId",id);
                            intentCheckOut.putExtra("serviceprice",price);
                            startActivity(intentCheckOut);
                            finish();

                        }
                    }
                });
                builder.show();
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }

    @Override
    public void onItemClickedCancel(UserAppointment userAppointment) {
        Log.d("User", "userAppointment.getDate()" + userAppointment.getName());
        if(!userAppointment.getDate().equals(getTodaysDate())){
                mDatabase.child("bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AlertDialog.Builder ad1 = new AlertDialog.Builder(Dashboard.this);
                        ad1.setTitle("Confirmation:");
                        ad1.setIcon(android.R.drawable.ic_dialog_info);
                        ad1.setMessage("Are you sure you want to cancel your appointment with " + userAppointment.getName() + " on " + userAppointment.getDate() + " at " + userAppointment.getTime() + " ?");
                        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {

                                dataSnapshot.getRef().child(userID).child(userAppointment.getId()).removeValue();
                                dataSnapshot.getRef().child(userAppointment.getId()).child(userID).removeValue();

                                Toast.makeText(getApplicationContext(), "Appointment Cancelled",
                                        Toast.LENGTH_SHORT).show();

                                Log.d("User", "userAppointment.getId()" + userAppointment.getId());


                                sendSMS("Your appointment with " + userName + " on " + aDate+ " at " + aTime + " is Cancelled", userAppointment.getPhonenum());
                                recreate();
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
        else{
            Toast.makeText(getApplicationContext(),  "Cannot Cancel on Appointment Day",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClickedMessage(UserAppointment userAppointment) {
        Uri uri = Uri.parse("smsto:"+userAppointment.getPhonenum());
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(intent);
    }

    @Override
    public void onItemClickedConfirm(UserAppointment userAppointment) {
        mDatabase.child("bookings").child(userID).child(userAppointment.getId()).child("confirmation").setValue("confirmed");
        mDatabase.child("bookings").child(userAppointment.getId()).child(userID).child("confirmation").setValue("confirmed");
        recreate();
    }

}

