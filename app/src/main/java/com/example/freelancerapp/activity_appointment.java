package com.example.freelancerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class activity_appointment extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button datebutton, timebutton, appointbutton, chatbutton;
    private TextView tvDate, tvTime, tvname_of_provider, tvprice, tvLocation;
    private ImageView imgProfile;
    int hour, minute;
    public DatabaseReference mDatabase, appointDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser fUser;
    public String newUserName;
    String selectedDate, selectedTime, meetUp, newUserID, newService, newPhoneNum, newPrice, newLocation, newImageUri, newRating;
    String userID, username, userphonenum, userImageUri, cTime, cDate, userRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        tvname_of_provider = findViewById(R.id.provider);
        tvprice = findViewById(R.id.price);
        tvLocation = findViewById(R.id.location);

        initDatePicker();
        datebutton = findViewById(R.id.setdate);
        tvDate = findViewById(R.id.tvDate);
        //tvDate.setText(getTodaysDate());

        timebutton = findViewById(R.id.setTime);
        tvTime = findViewById(R.id.tvTime);
        //tvTime.setText(getTodaysTime());

        appointbutton = findViewById(R.id.btnAppoint);
        imgProfile = findViewById(R.id.dp_appointment);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference();
        appointDatabase = mFirebaseDatabase.getReference();
        fUser = mAuth.getCurrentUser();
        userID = fUser.getUid();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newUserName= null;
                newUserID= null;
                newService= null;
                newPhoneNum= null;
                newPrice = null;
                newImageUri = null;
                newRating = null;
            } else {
                newUserName= extras.getString("username");
                newUserID= extras.getString("userid");
                newService= extras.getString("service");
                newPhoneNum= extras.getString("phonenum");
                newPrice= extras.getString("serviceprice");
                newLocation = extras.getString("location");
                newImageUri = extras.getString("profile_image_uri");
                newRating = extras.getString("rating");

                if(newImageUri != null){Glide.with(activity_appointment.this).load(newImageUri).into(imgProfile);}

                tvname_of_provider.setText(newUserName);
                tvprice.setText(newPrice);
                tvLocation.setText(newLocation);
            }
        } else {
            newUserName = (String) savedInstanceState.getSerializable("username");
            newUserID = (String) savedInstanceState.getSerializable("userid");
            newService = (String) savedInstanceState.getSerializable("service");
            newPhoneNum = (String) savedInstanceState.getSerializable("phonenum");
            newPrice = (String) savedInstanceState.getSerializable("serviceprice");
            newLocation = (String) savedInstanceState.getSerializable("location");
            newImageUri = (String) savedInstanceState.getSerializable("profile_image_uri");
            newRating = (String) savedInstanceState.getSerializable("rating");

            if(newImageUri != null){Glide.with(activity_appointment.this).load(newImageUri).into(imgProfile);}

            tvname_of_provider.setText(newUserName);
            tvprice.setText(newPrice);
            tvLocation.setText(newLocation);
        }

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

        mDatabase.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = new User();
                    user.setUsername(snapshot.getValue(User.class).getUsername());
                    user.setPhonenum(snapshot.getValue(User.class).getPhonenum());
                    user.setProfile_image_uri(snapshot.getValue(User.class).getProfile_image_uri());
                    user.setRating(snapshot.getValue(User.class).getRating());

                    username = user.getUsername();
                    userphonenum = user.getPhonenum();
                    userImageUri = user.getProfile_image_uri();
                    userRating = user.getRating();

                Log.d("user", "checking: "+ username + userRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        appointDatabase.child("bookings").child(newUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    UserAppointment userA = new  UserAppointment();
                    cDate = ds.getValue(UserAppointment.class).getDate();
                    cTime = ds.getValue(UserAppointment.class).getTime();
                    Log.d("TAG", "onDataChange: " + cDate + cTime); }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("User", error.getMessage());
            }
        });

        appointbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appointDatabase.child("bookings").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("TAG", "onDataChangetry: " + cDate + cTime);
                        if (selectedDate != null && selectedTime != null && meetUp != null) {
                            if (cDate == null || cTime == null) {
                                AlertDialog.Builder ad1 = new AlertDialog.Builder(activity_appointment.this);
                                ad1.setTitle("Confirmation:");
                                ad1.setIcon(android.R.drawable.ic_dialog_info);
                                ad1.setMessage("Are you sure you want to set an appointment with " + newUserName + " on " + selectedDate + " at " + selectedTime + " ?");
                                ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int i) {

                                        dataSnapshot.getRef().child(newUserID).child("id").setValue(newUserID);
                                        dataSnapshot.getRef().child(newUserID).child("name").setValue("Employee: " + newUserName);
                                        dataSnapshot.getRef().child(newUserID).child("service").setValue(newService);
                                        dataSnapshot.getRef().child(newUserID).child("date").setValue(selectedDate);
                                        dataSnapshot.getRef().child(newUserID).child("time").setValue(selectedTime);
                                        dataSnapshot.getRef().child(newUserID).child("payment").setValue("Not Paid");
                                        dataSnapshot.getRef().child(newUserID).child("phonenum").setValue(newPhoneNum);
                                        dataSnapshot.getRef().child(newUserID).child("serviceprice").setValue(newPrice);
                                        dataSnapshot.getRef().child(newUserID).child("meetup").setValue(meetUp);
                                        dataSnapshot.getRef().child(newUserID).child("profile_image_uri").setValue(newImageUri);
                                        dataSnapshot.getRef().child(newUserID).child("rating").setValue(newRating);
                                        dataSnapshot.getRef().child(newUserID).child("confirmation").setValue("unconfirmed");

                                        sendSMS("You have an appointment with " + username + " Check Dashboard", newPhoneNum);

                                        notifyEmployee();
                                        Toast.makeText(getApplicationContext(), "Appointment Save",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(activity_appointment.this, Dashboard.class);
                                        finish();
                                        recreate();
                                        startActivity(intent);

                                    }
                                });
                                ad1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int i) {

                                    }
                                });
                                ad1.show();// Show dialog
                            }
                            else {
                                if (cDate.matches(selectedDate) && cTime.matches(selectedTime)) {
                                    Toast.makeText(getApplicationContext(), "Date and Time Not Available",
                                            Toast.LENGTH_SHORT).show();}
                                else {
                                    AlertDialog.Builder ad2 = new AlertDialog.Builder(activity_appointment.this);
                                    ad2.setTitle("Confirmation:");
                                    ad2.setIcon(android.R.drawable.ic_dialog_info);
                                    ad2.setMessage("Are you sure you want to set an appointment with " + newUserName + " on " + selectedDate + " at " + selectedTime + " ?");
                                    ad2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int i) {

                                            dataSnapshot.getRef().child(newUserID).child("id").setValue(newUserID);
                                            dataSnapshot.getRef().child(newUserID).child("name").setValue("Employee: " + newUserName);
                                            dataSnapshot.getRef().child(newUserID).child("service").setValue(newService);
                                            dataSnapshot.getRef().child(newUserID).child("date").setValue(selectedDate);
                                            dataSnapshot.getRef().child(newUserID).child("time").setValue(selectedTime);
                                            dataSnapshot.getRef().child(newUserID).child("payment").setValue("Not Paid");
                                            dataSnapshot.getRef().child(newUserID).child("phonenum").setValue(newPhoneNum);
                                            dataSnapshot.getRef().child(newUserID).child("serviceprice").setValue(newPrice);
                                            dataSnapshot.getRef().child(newUserID).child("meetup").setValue(meetUp);
                                            dataSnapshot.getRef().child(newUserID).child("profile_image_uri").setValue(newImageUri);
                                            dataSnapshot.getRef().child(newUserID).child("rating").setValue(newRating);
                                            dataSnapshot.getRef().child(newUserID).child("confirmation").setValue("unconfirmed");

                                            sendSMS("You have an appointment with " + username + " Check Dashboard", newPhoneNum);

                                            notifyEmployee();
                                            Toast.makeText(getApplicationContext(), "Appointment Save",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(activity_appointment.this, Dashboard.class);
                                            finish();
                                            recreate();
                                            startActivity(intent);
                                        }
                                    });
                                    ad2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int i) {

                                        }
                                    });
                                    ad2.show();}
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),  "Fill the Information",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });
            }

        });


        chatbutton = findViewById(R.id.chat_btn);
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("smsto:"+newPhoneNum);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                finish();
                startActivity(intent);
            }
        });
    }

    public void sendSMS(String messageToSend, String number){
        Log.d("User", "sendSMS: " + messageToSend + number);
        SmsManager mySmsManager = SmsManager.getDefault();
        mySmsManager.sendTextMessage(number, null, messageToSend, null,null);
    }


    private void notifyEmployee() {
        appointDatabase.child("bookings").child(newUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().child(userID).child("id").setValue(userID);
                dataSnapshot.getRef().child(userID).child("name").setValue("Employer: "+username);
                dataSnapshot.getRef().child(userID).child("service").setValue(newService);
                dataSnapshot.getRef().child(userID).child("date").setValue(selectedDate);
                dataSnapshot.getRef().child(userID).child("time").setValue(selectedTime);
                dataSnapshot.getRef().child(userID).child("payment").setValue("Not Paid");
                dataSnapshot.getRef().child(userID).child("phonenum").setValue(userphonenum);
                dataSnapshot.getRef().child(userID).child("meetup").setValue(meetUp);
                dataSnapshot.getRef().child(userID).child("profile_image_uri").setValue(userImageUri);
                dataSnapshot.getRef().child(userID).child("rating").setValue(userRating);
                dataSnapshot.getRef().child(userID).child("confirmation").setValue("unconfirmed");

//                Toast.makeText(getApplicationContext(),  "Employee Notified",
//                        Toast.LENGTH_SHORT).show();
            }
            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(),"Employee Not Notified",
//                        Toast.LENGTH_SHORT).show();
                Log.d("User", databaseError.getMessage());
            }
        });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                selectedDate = makeDateString(day,month,year);
                tvDate.setText(selectedDate);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
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

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private String getTodaysTime() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        return String.format(Locale.getDefault(), "%02d:%02d", hour, min);
    }

    public void openTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                tvTime.setText(selectedTime);
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
    public void openDialog(View view) {
        LayoutInflater factory = LayoutInflater.from(activity_appointment.this);
        final View textEntryView = factory.inflate(R.layout.dialog_meetup, null);

        EditText editTextMeetUp = (EditText) textEntryView.findViewById(R.id.editTextMeetUp);
        TextView txtmeetp = findViewById(R.id.MeetPlace);

        AlertDialog.Builder ad1 = new AlertDialog.Builder(activity_appointment.this);
        ad1.setTitle("Set Meet-Up:");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                if (!editTextMeetUp.getText().toString().matches("") ) {
                    meetUp = editTextMeetUp.getText().toString();
                    txtmeetp.setText(meetUp);
                }
                else{
                    //showProgressBar();
                    Toast.makeText(getApplicationContext(), "Failed to Set Meet-Up",
                            Toast.LENGTH_SHORT).show();
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


}