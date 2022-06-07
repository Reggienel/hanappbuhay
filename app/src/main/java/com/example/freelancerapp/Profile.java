package com.example.freelancerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class Profile extends AppCompatActivity {
    Spinner dropdown;
    DrawerLayout drawerLayout;
    public DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    StorageReference storageReference;
    TextView sName, sEmail, sPhoneNumber,sService, sAvailability;
    EditText editTextNewName,editTextNewEmail, editTextNewPassword, editTextEmail, editTextPassword ;
    String userID;
    String name;
    String email;
    String password;
    String service;
    String strAvailability;
    ArrayList<String> Availability = new ArrayList<String>();
    String[] itemsService, itemsDays;
    private Button button;
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
        storageReference = FirebaseStorage.getInstance().getReference("UsersProfile/");

        userID = user.getUid();
        sName = findViewById(R.id.full_name);
        sEmail = findViewById(R.id.email_add);
        sPhoneNumber = findViewById(R.id.phone_num);
        sService = findViewById(R.id.service);
        sAvailability = findViewById(R.id.availability);
        //imgProfilePic = findViewById(R.id.profilePic);

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
                showData(snapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        imgProfilePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(openGallery, 1000);
//                showProgressBar();
//            }
//        });

        button = (Button) findViewById(R.id.edit_butt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings();
            }}
        );

    }
    private void showData(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            User user = new User();
            user.setUsername(ds.child(userID).getValue(User.class).getUsername());
            user.setEmail(ds.child(userID).getValue(User.class).getEmail());
            user.setPhonenum(ds.child(userID).getValue(User.class).getPhonenum());
            user.setServicetype(ds.child(userID).getValue(User.class).getServicetype());
            user.setAvailabity(ds.child(userID).getValue(User.class).getAvailability());

            sName.setText(user.getUsername());
            sEmail.setText(user.getEmail());
            sPhoneNumber.setText(user.getPhonenum());
            sService.setText(user.getServicetype());
           sAvailability.setText(user.getAvailability());

//            StorageReference filepath = storageReference.child("profile_picture_"+userID);
//            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Picasso.get().load(uri).into(imgProfilePic);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Log.d("View Profile", "No Upload Profile");
//                }
//            });
        }
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
                    service = dropdown.getSelectedItem().toString();dataSnapshot.getRef().child("service_type").setValue(service);}
                else if( i == 4){
                    StringBuffer sb = new StringBuffer();

                    for (String s : Availability) {
                        sb.append(s);
                        sb.append(", ");
                    }
                    strAvailability = sb.toString();
                    Log.d("User", strAvailability);
                    dataSnapshot.getRef().child("availability").setValue(strAvailability);}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }

    public void dialogAvailability(){
        LayoutInflater factory = LayoutInflater.from(Profile.this);
        final View textEntryView = factory.inflate(R.layout.dialog_availability, null);

        itemsDays = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday","N/A"};
        boolean[] checkedItems = {false, false, false, false, false,false};

        editTextEmail = (EditText)textEntryView.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)textEntryView.findViewById(R.id.editTextPassword);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(Profile.this);
        ad1.setTitle("Availability:");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setMultiChoiceItems(itemsDays, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                switch (which) {
                    case 0:
                        if(isChecked)
                            Toast.makeText(getApplicationContext(), "Clicked on 0", Toast.LENGTH_LONG).show();
                        Availability.add("Monday");
                        break;
                    case 1:
                        if(isChecked)
                            Toast.makeText(getApplicationContext(), "Clicked on 1", Toast.LENGTH_LONG).show();
                        Availability.add("Tuesday");
                        break;
                    case 2:
                        if(isChecked)
                            Toast.makeText(getApplicationContext(), "Clicked on 2", Toast.LENGTH_LONG).show();
                        Availability.add("Wednesday");
                        break;
                    case 3:
                        if(isChecked)
                            Toast.makeText(getApplicationContext(), "Clicked on 3", Toast.LENGTH_LONG).show();
                        Availability.add("Thursday");
                        break;
                    case 4:
                        if(isChecked)
                            Toast.makeText(getApplicationContext(), "Clicked on 4", Toast.LENGTH_LONG).show();
                        Availability.add("Friday");
                        break;
                    case 5:
                        if(isChecked)
                            Toast.makeText(getApplicationContext(), "Clicked on 5", Toast.LENGTH_LONG).show();
                        Availability.add("Not Available");
                        break;
                }
            }
        });
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                Log.i("111111", editTextEmail.getText().toString());
                Log.i("111111", editTextPassword.getText().toString());
                if (!editTextEmail.getText().toString().matches("") || !editTextPassword.getText().toString().matches("")) {
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
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                Log.i("111111", editTextEmail.getText().toString());
                Log.i("111111", editTextPassword.getText().toString());
                if (!editTextEmail.getText().toString().matches("") || !editTextPassword.getText().toString().matches("")) {
                        servicetype(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
                else{

                    //showProgressBar();
                    Toast.makeText(getApplicationContext(), "no Service Type",
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
        ad1.setIcon(android.R.drawable.ic_dialog_info);
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
        ad1.setIcon(android.R.drawable.ic_dialog_info);
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
        ad1.setIcon(android.R.drawable.ic_dialog_info);
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
        String[] settings_array = new String[]{"Change Name", "Change Email", "Change Password","Service Type", "Availability"};
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
                    }
                });
        return builder.show();
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

