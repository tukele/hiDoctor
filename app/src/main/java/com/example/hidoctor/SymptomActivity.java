package com.example.hidoctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SymptomActivity extends AppCompatActivity {

    TextView placeHolder;
    Button exitButton;
    Button saveButton;
    EditText value;
    TextView symptomDescription;
    TextView symptomName;
    Spinner spinner;
    FirebaseDatabase database;
    DatabaseReference referenceDate;
    DatabaseReference referenceValue;
    private ArrayAdapter<String> adapter;
    private View symptomView;
    String lastUpdated="";
    boolean flag=false;
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_selection);
        symptomView=(View) findViewById(R.id.symptomView);
        //HIDE THE KEYBOARD
        symptomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        System.out.println(MedFragment.currentSymptom.getOperation());
        spinner = (Spinner) findViewById(R.id.spinner);
        value=(EditText) findViewById(R.id.value);
        placeHolder= (TextView) findViewById(R.id.symptomPlaceholder);
        placeHolder.setText(MedFragment.currentSymptom.getPlaceholder().trim().replaceAll("\\W", " "));
        symptomName=(TextView) findViewById(R.id.symptomName);
        symptomName.setText(MedFragment.currentSymptom.getName().trim());
        symptomDescription=(TextView) findViewById(R.id.symptomDescription);
        symptomDescription.setText(MedFragment.currentSymptom.getDescription().replaceAll("\\W", " ").trim());
        saveButton=(Button) findViewById(R.id.saveButton);


        database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
        HTTPost post= new HTTPost();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date_D = new Date();
        String date=(String)(formatter.format(date_D));
        //UI CHANGES
        if(MedFragment.currentSymptom.getOperation().trim().equals("Select")) {
            //REMOVE UI NOT USED
            value.setVisibility(View.INVISIBLE);
            placeHolder.setVisibility(View.INVISIBLE);
            //POPULATE SPINNER
            String[] options= MedFragment.currentSymptom.getOptions().split(",");
            ArrayList<String> arrayListOptions = new ArrayList<>();
            for(String s:options){
               arrayListOptions.add(s.trim().replaceAll("\\W", " "));
            }
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_layout, arrayListOptions);
            adapter.setDropDownViewResource(R.layout.list_layout);
            spinner.setAdapter(adapter);
            spinner.setSelection(0);
        }else{
            spinner.setVisibility(View.INVISIBLE);
        }


        //SAVE BUTTON LISTENER
        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                referenceDate= database.getReference().child("User").child(User.currentUser.getId()).child("Symptoms").child(MedFragment.currentSymptom.getName().trim()).child("Date");
                referenceDate.setValue(date);
                referenceValue = database.getReference().child("User").child(User.currentUser.getId()).child("Symptoms").child(MedFragment.currentSymptom.getName().trim()).child("Value");
                if(MedFragment.currentSymptom.getOperation().trim().equals("Select")) {
                    referenceValue.setValue(spinner.getSelectedItem().toString().trim());

                    if( post.HL7_HTTP(User.currentUser.getId(),MedFragment.currentSymptom.getName().trim(), spinner.getSelectedItem().toString().trim())){
                        Intent intent = new Intent(SymptomActivity.this, Home.class);
                        intent.putExtra("Med",true);
                        startActivity(intent);
                        saveButton.setBackgroundColor(Color.GREEN);
                    }else{
                        saveButton.setBackgroundColor(Color.RED);
                    }
                }else {
                    referenceValue.setValue(String.valueOf(value.getText()));
                    if( post.HL7_HTTP(User.currentUser.getId(),MedFragment.currentSymptom.getName().trim(), String.valueOf(value.getText()))){
                        Intent intent = new Intent(SymptomActivity.this, Home.class);
                        intent.putExtra("Med",true);
                        startActivity(intent);
                        saveButton.setBackgroundColor(Color.GREEN);
                    }else{
                        saveButton.setBackgroundColor(Color.RED);
                    }
                }

            }
        });


        exitButton=(Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SymptomActivity.this, Home.class);
                intent.putExtra("Med",true);
                startActivity(intent);
            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
