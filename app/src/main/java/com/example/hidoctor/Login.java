package com.example.hidoctor;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
   //Widget Declaration
   private Button loginButton;
   private EditText email;
   private EditText password;
   private View view;
   //Database Declaration
   private String URL="http://hidoctor.shardslab.com/auth";
   private FirebaseDatabase database;
   private TextView errorMessage;

    //HIDE THE KEYBOARD WHEN THE USER TOUCHES THE SCREEN OUTSIDE TEXT BOX
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //LOGIN OF THE USER
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //NIGHT MODE OFF
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Widget Inizialization
        view = (View) findViewById(R.id.view);
        loginButton = (Button) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        errorMessage = (TextView) findViewById(R.id.error);
        errorMessage.setVisibility(View.INVISIBLE);

        //HIDE THE KEYBOARD
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        //HTTP POST ON CLICK OF LOGIN BUTTON
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                HTTPost http=new HTTPost();
                hideKeyboard();
                //JSON FROM SERVER RETURNS A VARIABLE FLAG THAT INDICATES IF THE LOGIN CREDENTIALS ARE CORRECT
                if (http.loginHTTP(email.getText().toString(), password.getText().toString())) {
                        try {
                                    System.out.println(User.currentUser);
                                    database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
                                    Query query = database.getReference().child("User").orderByChild("id").equalTo(User.currentUser.getId());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int i = 1;
                                            for (DataSnapshot data : snapshot.getChildren()) {
                                                i++;
                                            }
                                            if (i<=1){
                                                System.out.println(i+"");
                                                database.getReference().child("User").child(User.currentUser.getId()).setValue(User.currentUser);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                    Intent intent = new Intent(Login.this, Home.class);
                                    startActivity(intent);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }else
                    errorMessage.setVisibility(View.VISIBLE);
            }
        });
    }

}