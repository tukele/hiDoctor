package com.example.hidoctor;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if(getIntent().getBooleanExtra("Med",false)){
            MedFragment prof = new MedFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, prof).commit();
        }else{
            ProfileFragment prof = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, prof).commit();
        }

    }
    //NAVIGATION AMONG THE BOTTOM NAVIGATION VIEW
    private BottomNavigationView.OnNavigationItemSelectedListener navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment=null;
                    switch (item.getItemId()){
                        case R.id.profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.med:
                            selectedFragment = new MedFragment();
                            break;
                        case R.id.call:
                            selectedFragment = new CallFragment();
                            break;
                        case R.id.settings:
                            selectedFragment = new SettingsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}