package com.example.hidoctor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MedFragment extends Fragment {

    CheckBox tosse;
    CheckBox febbre;
    TextView name;
    Button send;
    FirebaseDatabase database;
    DatabaseReference reference;
    private static Boolean tosseFlag;
    private static Boolean febbreFlag;
    ListView list;
    private ArrayAdapter<String> adapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    //SET UP MED FRAGMENT
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_med, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ArrayList<Symptom> symptoms = new ArrayList<>();
        parseXML(symptoms);
        list= (ListView) rootView.findViewById(R.id.listView);
        ArrayList<String> arrayList= new ArrayList<>();
        for(Symptom symptom: symptoms){
            arrayList.add(symptom.name);
        }
        adapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_selectable_list_item,arrayList);
        list.setAdapter(adapter);
        list.setClickable(false);
        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
    private void parseXML(ArrayList<Symptom> symptoms){
        XmlPullParserFactory parserFactory;
        try{
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser= parserFactory.newPullParser();
            InputStream is = getContext().getAssets().open("symptoms.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(is,null);
            processParsing(parser,symptoms);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private void processParsing(XmlPullParser parser,ArrayList<Symptom> symptoms) throws IOException, XmlPullParserException {
        int eventType = parser.getEventType();
        Symptom symptom=null;
        while(eventType != XmlPullParser.END_DOCUMENT){
            String name=null;
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    if ("Parameter".equals(name)){
                        symptom= new Symptom();
                        symptoms.add(symptom);
                    }
                    else if(symptom !=null ){
                        if("Name".equals(name)){
                            symptom.setName(parser.nextText());
                        }else if("Type".equals(name)){
                            symptom.setType(parser.nextText());
                        }else if("Operation".equals(name)){
                            symptom.setOperation(parser.nextText());
                        }else if("Options".equals(name)){
                            symptom.setOptions(parser.nextText());
                        }else if("Description".equals(name)){
                            symptom.setDescription(parser.nextText());
                        }else if("Placeholder".equals(name)){
                            symptom.setPlaceholder(parser.nextText());
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
        printSymptoms(symptoms);
    }
    private void printSymptoms(ArrayList<Symptom> symptoms){
        StringBuilder builder = new StringBuilder();

        for (Symptom symptom: symptoms){
            builder.append(symptom.name).append("\n").
                    append(symptom.type).append("\n").
                    append(symptom.operation).append("\n").
                    append(symptom.options).append("\n").
                    append(symptom.description).append("\n").
                    append(symptom.placeholder).append("\n\n");
        }
        System.out.println(builder.toString());
    }
}
