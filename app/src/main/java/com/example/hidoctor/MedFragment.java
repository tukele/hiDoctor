package com.example.hidoctor;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedFragment extends Fragment {

    CheckBox tosse;
    CheckBox febbre;
    TextView name;
    Button send;
    FirebaseDatabase database;
    DatabaseReference reference;
    private static Boolean tosseFlag;
    private static Boolean febbreFlag;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    //SET UP MED FRAGMENT
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_med, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //SET NAME
        name= (TextView) rootView.findViewById(R.id.name);
        name.setText(User.currentUser.getNome()+" "+User.currentUser.getCognome());
        //CHECKBOX DECLARATION
        tosse=(CheckBox) rootView.findViewById(R.id.checkTosse);
        febbre=(CheckBox) rootView.findViewById(R.id.checkFebbre);
        // LOADS VALUES OF CHECKBOX FROM DATABASE
        database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
        reference= database.getReference().child("User").child(User.currentUser.getId()).child("Symptoms").child("febbre");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    febbre.setChecked(Boolean.valueOf((snapshot.getValue(String.class))));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference= database.getReference().child("User").child(User.currentUser.getId()).child("Symptoms").child("tosse");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tosse.setChecked((Boolean)(Boolean.valueOf((snapshot.getValue(String.class)))));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //FLAG
        MedFragment.tosseFlag=tosse.isChecked();
        MedFragment.febbreFlag=febbre.isChecked();



        /*
        ListView list= (ListView) rootView.findViewById(R.id.list);

        Symptom[] symptomsList= new Symptom[2];

        symptomsList[0]=(new Symptom("febbre","0"));
        symptomsList[1]=(new Symptom("tosse","0"));
        @SuppressLint("CutPasteId")

        ArrayAdapter arrayAdapter= new ArrayAdapter(getContext(),R.layout.fragment_med, Arrays.stream(symptomsList).map(p -> p.nome).toArray(size -> new String [symptomsList.length]));
        list.setAdapter(arrayAdapter);
        */
        System.out.println("Vado a cacare loggo su rocket");



        return rootView;
    }
    //SEND HL7 POST
    @Override
    public void onStart() {
        super.onStart();
        //SEND HL7 WITH HTTP TO WEBSERVER IF DATA FROM CHECKBOX HAS CHANGED
        send = (Button) getView().findViewById(R.id.sendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TOSSE
                HTTPost http= new HTTPost();
                if(MedFragment.tosseFlag!=tosse.isChecked()) {
                    if(http.HL7_HTTP(User.currentUser.getId(),"tosse",Boolean.toString(tosse.isChecked()))) {
                        MedFragment.tosseFlag = tosse.isChecked();
                    }
                }
                //FEBBRE
                if(MedFragment.febbreFlag!=febbre.isChecked()) {
                    if(http.HL7_HTTP(User.currentUser.getId(),"febbre",Boolean.toString(febbre.isChecked()))) {
                        MedFragment.febbreFlag = febbre.isChecked();
                    }
                }
            }
        });
        //LOAD FROM DATABASE VALUES OF CHECKBOXES
        febbre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference= database.getReference().child("User").child(User.currentUser.getId()).child("Symptoms").child("febbre");
                reference.setValue(Boolean.toString(febbre.isChecked()));
            }
        });
        tosse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference= database.getReference().child("User").child(User.currentUser.getId()).child("Symptoms").child("tosse");
                reference.setValue(Boolean.toString(tosse.isChecked()));
            }
        });
    }
    //SAVE TO DATABASE IF USER SWITCHES FRAGMENT
    //DATA WONT BE SEND TO WEB SERVER UNTIL USERS DOES NOT PRESS SEND BUTTON
    @Override
    public void onPause() {
        super.onPause();
        Symptoms symptoms= new Symptoms();
        symptoms.setTosse(Boolean.toString(tosse.isChecked()));
        symptoms.setFebbre(Boolean.toString(febbre.isChecked()));
        MedFragment.tosseFlag=tosse.isChecked();
        MedFragment.febbreFlag=febbre.isChecked();
        reference= database.getReference().child("User").child(User.currentUser.getId()).child("Symptoms");
        reference.setValue(symptoms);
    }

}
