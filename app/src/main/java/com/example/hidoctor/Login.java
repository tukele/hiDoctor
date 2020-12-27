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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class Login extends AppCompatActivity {
   //Widget Declaration
   private Button loginButton;
   private EditText email;
   private EditText password;
   private View view;
   private String URL="http://hidoctor.shardslab.com/auth";
   private FirebaseDatabase database;
   private Task<Void> reference;

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Widget Inizialization
        view = (View) findViewById(R.id.view);
        loginButton = (Button) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                SendPost post=new SendPost(URL,email.getText().toString(), password.getText().toString());

                try {
                    post.execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (post.getFlag()) {
                        try {
                            JSONArray json=new JSONArray(post.getJSON());

                                if(json.getJSONObject(0).getString("flag_medico").equals("0")) {

                                    User user=new User();
                                    user.setId((String) json.getJSONObject(0).getString("id"));
                                    user.setNome((String) json.getJSONObject(0).getString("nome"));
                                    user.setCognome((String) json.getJSONObject(0).getString("cognome"));

                                    database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
                                    Query query = database.getReference().child("User").orderByChild("id").equalTo(user.getId());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int i = 1;
                                            for (DataSnapshot data : snapshot.getChildren()) {
                                                i++;
                                            }
                                            if (i<=1){
                                                System.out.println(i+"");
                                                database.getReference().child("User").child(user.getId()).setValue(user);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    Intent intent = new Intent(Login.this, Home.class);
                                    intent.putExtra("id",user.getId());
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }


            }
        });
    }

}