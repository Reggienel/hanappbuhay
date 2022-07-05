package com.example.freelancerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;


public class Profile extends AppCompatActivity {
    Spinner dropdown;
    DrawerLayout drawerLayout;
    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    StorageReference storageReference;
    ImageView imgProfilePic;
    TextView sName, sEmail, sPhoneNumber,sService, sAvailability, sServicePrice, sLocation, sBalance, textView;
    EditText editTextNewName,editTextNewEmail, editTextNewPassword, editTextEmail, editTextPassword,editTextServicePrice, editTextLocation;
    String userID;
    String name;
    String email;
    String password;
    String service;
    String serviceprice;
    String strAvailability;
    String strLocation;
    ArrayList<String> Availability = new ArrayList<String>();
    String[] itemsService, itemsDays;
    private Button button;
    public Uri imageURI;
    RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);
        // globally
        TextView myAwesomeTextView = (TextView)findViewById(R.id.NavTitle);

//in your OnCreate() method
        myAwesomeTextView.setText("PROFILE");

        //assign variable
        drawerLayout = findViewById(R.id.drawer_layout);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        userID = user.getUid();
        sName = findViewById(R.id.full_name);
        sEmail = findViewById(R.id.email_add);
        sPhoneNumber = findViewById(R.id.phone_num);
        sService = findViewById(R.id.service);
        sAvailability = findViewById(R.id.availability);
        sServicePrice = findViewById(R.id.tvServicePrice);
        sLocation = findViewById(R.id.txtvLocation);
        ratingBar = findViewById(R.id.ratingBarProfile);
        sBalance = findViewById(R.id.balance);

        imgProfilePic = findViewById(R.id.imgProfilePic);

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

                        sEmail.setText(user.getEmail());
                        sPhoneNumber.setText(user.getPhonenum());
                        sName.setText(user.getUsername());
                        if(user.getBalance() != null){
                            sBalance.setText(user.getBalance().toString());}
                        if(user.getRating() != null){
                            ratingBar.setRating(Float.parseFloat(user.getRating()));}
                        if (user.getServicetype() != null) {
                            sService.setText(user.getServicetype()); }
                        if (user.getServiceprice() != null) {
                            sServicePrice.setText(user.getServiceprice()); }
                        if (user.getAvailability() != null) {
                            sAvailability.setText(user.getAvailability()); }
                        if(user.getLocation() !=null){
                            sLocation.setText(user.getLocation()); }
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

        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfilePic();
            }
        });

        button = (Button) findViewById(R.id.edit_butt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings();
            }
        }
        );

        storageReference.child("images/"+userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(imgProfilePic);
                mDatabase.child("users").child(userID).child("profile_image_uri").setValue(uri.toString());
                Log.d("User", "onNull: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
            uploadPicture();
        }

    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        StorageReference strRef = storageReference.child("images/" + userID);
        strRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Image Uploaded Successfully",
                        Toast.LENGTH_SHORT).show();
                recreate();
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
    }

    private void changename(String email, final String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        if(user != null) {
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("value", "User re-authenticated.");
                        //hideProgressBar();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        updateRealData(0);
                        Toast.makeText(getApplicationContext(), "Change Name Success",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //hideProgressBar();
                        Toast.makeText(getApplicationContext(), "Change Name Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //hideProgressBar();
            Toast.makeText(getApplicationContext(), "Change Name Failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void changeemail(String email, final String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        if(user != null) {
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Log.d("value", "User re-authenticated.");

                    // Now change your email address \\
                    //----------------Code for Changing Email Address----------\\
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null) {
                        user.updateEmail(editTextNewEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    updateRealData(1);
                                    user.sendEmailVerification();
                                    if (task.isSuccessful()) {
                                        //hideProgressBar();
                                        Toast.makeText(Profile.this, "Email Changed" + " Current Email is " +
                                                editTextNewEmail.getText().toString(), Toast.LENGTH_LONG).show();
                                        Toast.makeText(Profile.this, "Please Verify Email", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        //hideProgressBar();
                                        Toast.makeText(getApplicationContext(),"Change Email Failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    else{
                        //hideProgressBar();
                        Toast.makeText(getApplicationContext(), "Change Email Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //hideProgressBar();
            Toast.makeText(getApplicationContext(), "Change Email Failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void changepass(String email, final String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        if(user != null) {
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("value", "User re-authenticated.");
                    if (task.isSuccessful()) {
                        // Now change your passowrd \\
                        //----------------Code for Changing password----------\\
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.updatePassword(editTextNewPassword.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //hideProgressBar();
                                                updateRealData(2);
                                                Toast.makeText(Profile.this, "Password Changed", Toast.LENGTH_LONG).show();
                                            } else {
                                                //hideProgressBar();
                                                Toast.makeText(getApplicationContext(), "Change Password Failed",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            //hideProgressBar();
                            Toast.makeText(getApplicationContext(), "Change Password Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        //hideProgressBar();
                        Toast.makeText(getApplicationContext(), "Change Password Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //hideProgressBar();
            Toast.makeText(getApplicationContext(), "Change Password Failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void servicetype (String email, final String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        if(user != null) {
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("value", "User re-authenticated.");
                        //hideProgressBar();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        updateRealData(3);
                        Toast.makeText(getApplicationContext(), "Service Type" + dropdown.getSelectedItem(),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //hideProgressBar();
                        Toast.makeText(getApplicationContext(), "Change Service type Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //hideProgressBar();
            Toast.makeText(getApplicationContext(), "Change Service type Failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void availability (String email, final String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        if(user != null) {
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("value", "User re-authenticated.");
                        //hideProgressBar();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        updateRealData(4);
                        Toast.makeText(getApplicationContext(), "Availability",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //hideProgressBar();
                        Toast.makeText(getApplicationContext(), "Change Availability Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //hideProgressBar();
            Toast.makeText(getApplicationContext(), "Change Availability Failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void serviceprice(String email, final String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        if(user != null) {
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("value", "User re-authenticated.");
                        //hideProgressBar();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        updateRealData(5);
                        Toast.makeText(getApplicationContext(), "Service Price Set",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //hideProgressBar();
                        Toast.makeText(getApplicationContext(), "Set Service Price Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //hideProgressBar();
            Toast.makeText(getApplicationContext(), "Set Service Price Failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocation(String email, final String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        if (user != null) {
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("value", "User re-authenticated.");
                        //hideProgressBar();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        updateRealData(6);
                        Toast.makeText(getApplicationContext(), "Location Set Success",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //hideProgressBar();
                        Toast.makeText(getApplicationContext(), "Failed to Set Location",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateRealData(int i){
        mDatabase.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(i == 0){
                    name = editTextNewName.getText().toString().trim();dataSnapshot.getRef().child("username").setValue(name);}
                else if(i == 1){
                    email = editTextNewEmail.getText().toString().trim();dataSnapshot.getRef().child("email").setValue(email);}
                else if( i == 2){
                    password = editTextNewPassword.getText().toString();dataSnapshot.getRef().child("password").setValue(password);}
                else if( i == 3){
                    service = dropdown.getSelectedItem().toString();dataSnapshot.getRef().child("service_type").setValue(service);
                    dataSnapshot.getRef().child("date_posted").setValue(getTodaysDate());
                    }
                else if( i == 4){
                    StringBuffer sb = new StringBuffer();

                    for (String s : Availability) {
                        sb.append(s);
                        sb.append(", ");
                    }
                    strAvailability = sb.toString();
                    Log.d("User", "Str "+strAvailability);
                    Log.d("User", "Avail "+Availability.toString());
                    dataSnapshot.getRef().child("availability").removeValue();
                    dataSnapshot.getRef().child("availability").setValue(strAvailability);}
                else if( i == 5){
                      serviceprice = editTextServicePrice.getText().toString();dataSnapshot.getRef().child("serviceprice").setValue(serviceprice+"PHP");
                    }
                else if( i == 6){
                    strLocation = editTextLocation.getText().toString();dataSnapshot.getRef().child("location").setValue(strLocation);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }

    public void dialogLocation(){
        LayoutInflater factory = LayoutInflater.from(Profile.this);
        final View textEntryView = factory.inflate(R.layout.dialog_location, null);

        editTextLocation = (EditText) textEntryView.findViewById(R.id.editTextLocation);

        editTextEmail = (EditText)textEntryView.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)textEntryView.findViewById(R.id.editTextPassword);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(Profile.this);
        ad1.setTitle("Set Location:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                Log.i("111111", editTextEmail.getText().toString());
                Log.i("111111", editTextPassword.getText().toString());
                if (!editTextLocation.getText().toString().matches("") || !editTextEmail.getText().toString().matches("") || !editTextPassword.getText().toString().matches("")) {
                    setLocation(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
                else{
                    //showProgressBar();
                    Toast.makeText(getApplicationContext(), "Failed to Set Location",
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

    public void dialogAvailability(){
        LayoutInflater factory = LayoutInflater.from(Profile.this);
        final View textEntryView = factory.inflate(R.layout.dialog_availability, null);

        itemsDays = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", " N/A"};
        boolean[] checkedItems = {false, false, false, false, false,false,false,false};

        editTextEmail = (EditText)textEntryView.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)textEntryView.findViewById(R.id.editTextPassword);
        Availability.clear();
        AlertDialog.Builder ad1 = new AlertDialog.Builder(Profile.this);
        ad1.setTitle("Availability:");
        ad1.setView(textEntryView);
        ad1.setMultiChoiceItems(itemsDays, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if(isChecked){
                                    Availability.add(itemsDays[which]);
                                }
                                else{
                                    Availability.remove(itemsDays[which]);
                                }
//                switch (which) {
//                    case 0:
//                        if(isChecked)
//                            Toast.makeText(getApplicationContext(), "Monday", Toast.LENGTH_LONG).show();
//                            Availability.add("Monday");
//                        break;
//                    case 1:
//                        if(isChecked)
//                            Toast.makeText(getApplicationContext(), "Tuesday", Toast.LENGTH_LONG).show();
//                            Availability.add("Tuesday");
//                        break;
//                    case 2:
//                        if(isChecked)
//                            Toast.makeText(getApplicationContext(), "Wednesday", Toast.LENGTH_LONG).show();
//                            Availability.add("Wednesday");
//                        break;
//                    case 3:
//                        if(isChecked)
//                            Toast.makeText(getApplicationContext(), "Thursday", Toast.LENGTH_LONG).show();
//                            Availability.add("Thursday");
//                        break;
//                    case 4:
//                        if(isChecked)
//                            Toast.makeText(getApplicationContext(), "Friday", Toast.LENGTH_LONG).show();
//                            Availability.add("Friday");
//                        break;
//                    case 5:
//                        if(isChecked)
//                            Toast.makeText(getApplicationContext(), "Not Available", Toast.LENGTH_LONG).show();
//                            Availability.add("Not Available");
//                        break;
//                }
            }
        });
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                Log.i("111111", editTextEmail.getText().toString());
                Log.i("111111", editTextPassword.getText().toString());
                if (!editTextEmail.getText().toString().matches("") || !editTextPassword.getText().toString().matches("")) {
                    Comparator<String> dateComparator = new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            try{
                                SimpleDateFormat format = new SimpleDateFormat("EEE");
                                Date d1 = format.parse(s1);
                                Date d2 = format.parse(s2);
                                if(d1.equals(d2)){
                                    return s1.substring(s1.indexOf(" ") + 1).compareTo(s2.substring(s2.indexOf(" ") + 1));
                                }else{
                                    Calendar cal1 = Calendar.getInstance();
                                    Calendar cal2 = Calendar.getInstance();
                                    cal1.setTime(d1);
                                    cal2.setTime(d2);
                                    return cal1.get(Calendar.DAY_OF_WEEK) - cal2.get(Calendar.DAY_OF_WEEK);
                                }
                            }catch(ParseException pe){
                                throw new RuntimeException(pe);
                            }
                        }
                    };
                    Collections.sort(Availability, dateComparator);
                    Set<String> set = new LinkedHashSet<>();
                    set.addAll(Availability);
                    Availability.clear();
                    Availability.addAll(set);
                    Log.d("User", "Set "+set.toString());
                    availability(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
                else{

                    //showProgressBar();
                    Toast.makeText(getApplicationContext(), "no Availability",
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

    public void dialogServiceType(){
        LayoutInflater factory = LayoutInflater.from(Profile.this);
        final View textEntryView = factory.inflate(R.layout.dialog_service_type, null);

        editTextServicePrice = (EditText) textEntryView.findViewById(R.id.editTextServicePrice);

        dropdown = textEntryView.findViewById(R.id.spAvailability);
//create a list of items for the spinner.
        itemsService = new String[]{"Cleaning", "Laundry", "Plumbing", "Electrical", "Employer"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsService);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        editTextEmail = (EditText)textEntryView.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)textEntryView.findViewById(R.id.editTextPassword);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(Profile.this);
        ad1.setTitle("Service Type:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                Log.i("111111", editTextEmail.getText().toString());
                Log.i("111111", editTextPassword.getText().toString());
                if (!editTextEmail.getText().toString().matches("") || !editTextPassword.getText().toString().matches("")) {
                        if(editTextServicePrice.getText().toString().matches("") && dropdown.getSelectedItem().toString().matches("Employer")){
                            servicetype(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                            serviceprice(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                        }
                        else if (!editTextServicePrice.getText().toString().matches("")){
                            servicetype(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                            serviceprice(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "No Price",
                                    Toast.LENGTH_SHORT).show();
                        }
                }
                else{

                    //showProgressBar();
                    Toast.makeText(getApplicationContext(), "No Service Type",
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

    public void dialogChangeName(){
        LayoutInflater factory = LayoutInflater.from(Profile.this);
        final View textEntryView = factory.inflate(R.layout.dialog_new_name, null);

        editTextNewName = (EditText) textEntryView.findViewById(R.id.editTextNewName);

        editTextEmail = (EditText)textEntryView.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)textEntryView.findViewById(R.id.editTextPassword);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(Profile.this);
        ad1.setTitle("Change Name:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                Log.i("111111", editTextEmail.getText().toString());
                Log.i("111111", editTextPassword.getText().toString());
                if (!editTextNewName.getText().toString().matches("") || !editTextEmail.getText().toString().matches("") || !editTextPassword.getText().toString().matches("")) {
                    changename(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
                else{
                    //showProgressBar();
                    Toast.makeText(getApplicationContext(), "Failed Change Name",
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

    public void dialogChangeEmail(){
        LayoutInflater factory = LayoutInflater.from(Profile.this);
        final View textEntryView = factory.inflate(R.layout.dialog_new_email, null);

        editTextNewEmail = (EditText)textEntryView.findViewById(R.id.editTextNewEmail);
        editTextEmail = (EditText)textEntryView.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)textEntryView.findViewById(R.id.editTextPassword);

        AlertDialog.Builder ad1 = new AlertDialog.Builder(Profile.this);
        ad1.setTitle("Change Email:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                Log.i("111111", editTextEmail.getText().toString());
                Log.i("111111", editTextPassword.getText().toString());
                if (!editTextNewEmail.getText().toString().matches("") || !editTextEmail.getText().toString().matches("") || !editTextPassword.getText().toString().matches("")){
                    changeemail(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
                else{
                    //showProgressBar();
                    Toast.makeText(getApplicationContext(), "Failed Change Email",
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

    public void dialogChangePass(){
        LayoutInflater factory = LayoutInflater.from(Profile.this);
        final View textEntryView = factory.inflate(R.layout.dialog_new_pass, null);

        editTextNewPassword = (EditText)textEntryView.findViewById(R.id.editTextNewPassword);
        editTextEmail = (EditText)textEntryView.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)textEntryView.findViewById(R.id.editTextPassword);

        AlertDialog.Builder ad1 = new AlertDialog.Builder(Profile.this);
        ad1.setTitle("Change Password:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                Log.i("111111", editTextEmail.getText().toString());
                Log.i("111111", editTextPassword.getText().toString());
                if (!editTextNewPassword.getText().toString().matches("") || !editTextEmail.getText().toString().matches("") || !editTextPassword.getText().toString().matches("")) {
                    //showProgressBar();
                    changepass(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Failed Change Password",
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

    public AlertDialog Settings(){
        String[] settings_array = new String[]{"Change Name", "Change Email", "Change Password","Service Type", "Availability","Set Location", "Payout"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle("Account Settings")
                .setItems(settings_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            dialogChangeName();
                        }
                        else if(which == 1){
                            dialogChangeEmail();
                        }
                        else if (which == 2){
                            dialogChangePass();
                        }
                        else if (which == 3){
                            dialogServiceType();
                        }
                        else if (which == 4){
                            dialogAvailability();
                        }
                        else if (which == 5){
                            dialogLocation();
                        }
                        else if (which == 6){
                            Intent intent = new Intent(getApplicationContext(), Payout.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
        return builder.show();
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
        return day + "/" + month + "/" + year;
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
        recreate();
    }
    public void ClickDashboard(View view){
        //Recreate activity to settings
        NavDrawer.redirectActivity(this, Dashboard.class);
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


}

