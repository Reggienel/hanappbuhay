package com.example.freelancerapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
public class Settings extends AppCompatActivity {
    TextView textView;
    //initialize variable
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);
        // globally
        TextView myAwesomeTextView = (TextView)findViewById(R.id.NavTitle);
        //in your OnCreate() method
        myAwesomeTextView.setText("SETTINGS");
        //assign variable
        drawerLayout = findViewById(R.id.drawer_layout);
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
        NavDrawer.redirectActivity(this,NavDrawer.class);
    }
    public void ClickProfile(View view){
        //redirect activity to profile
        NavDrawer.redirectActivity(this,Profile.class);
    }
    public void ClickSettings(View view){
        //recreate activity
        recreate();
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
    protected void onPause() {
        super.onPause();
        NavDrawer.closeDrawer(drawerLayout);
    }
}