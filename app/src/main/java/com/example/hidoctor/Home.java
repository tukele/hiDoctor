package com.example.hidoctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    private User user;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent= getIntent();
        String ktm=intent.getStringExtra("id");
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bundle = new Bundle();
        bundle.putString("id", ktm);
        ProfileFragment prof= new ProfileFragment();
        prof.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, prof).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment=null;
                    switch (item.getItemId()){
                        case R.id.profile:
                            selectedFragment = new ProfileFragment();
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.med:
                            selectedFragment = new MedFragment();
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.call:
                            selectedFragment = new CallFragment();
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.settings:
                            selectedFragment = new SettingsFragment();
                            selectedFragment.setArguments(bundle);
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}