package com.example.hidoctor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SymptomActivity extends AppCompatActivity {


    Button exitButton;
    Button saveButton;
    EditText value;
    TextView symptomDescription;
    TextView symptomName;
    Spinner spinner;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_selection);
        System.out.println(MedFragment.currentSymptom.getOperation());
        spinner = (Spinner) findViewById(R.id.spinner);
        value=(EditText) findViewById(R.id.value);
        symptomName=(TextView) findViewById(R.id.symptomName);
        symptomName.setText(MedFragment.currentSymptom.getName().trim());
        symptomDescription=(TextView) findViewById(R.id.symptomDescription);
        symptomDescription.setText(MedFragment.currentSymptom.getDescription().replaceAll("\\W", " ").trim());
        saveButton=(Button) findViewById(R.id.saveButton);

        if(MedFragment.currentSymptom.getOperation().trim().equals("Select")) {
            value.setVisibility(View.INVISIBLE);

            String[] options= MedFragment.currentSymptom.getOptions().split(",");
            ArrayList<String> arrayListOptions = new ArrayList<>();
            for(String s:options){
               arrayListOptions.add(s.trim().replaceAll("\\W", " "));
            }
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_layout, arrayListOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(0);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HTTPost post= new HTTPost();
                    if( post.HL7_HTTP(User.currentUser.getId(),MedFragment.currentSymptom.getName(), spinner.getSelectedItem().toString())){
                        saveButton.setBackgroundColor(Color.GREEN);
                    }else{
                        saveButton.setBackgroundColor(Color.RED);
                    }
                }
            });
        }else{
            spinner.setVisibility(View.INVISIBLE);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HTTPost post= new HTTPost();
                    if( post.HL7_HTTP(User.currentUser.getId(),MedFragment.currentSymptom.getName(), String.valueOf(value.getText()))){
                        saveButton.setBackgroundColor(Color.GREEN);
                    }else{
                        saveButton.setBackgroundColor(Color.RED);
                    }
                }
            });
        }
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
