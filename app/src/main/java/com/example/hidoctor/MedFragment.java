package com.example.hidoctor;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MedFragment extends Fragment {

    ListView list;
    private ArrayAdapter<String> adapter;
    ArrayList<Symptom> symptoms;
    public static Symptom currentSymptom;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    //SET UP MED FRAGMENT
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_med, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        symptoms = new ArrayList<>();
        parseXML(symptoms);
        list= (ListView) rootView.findViewById(R.id.listView);
        ArrayList<String> arrayList= new ArrayList<>();
        for(Symptom symptom: symptoms){
            arrayList.add(symptom.name.trim());
        }
        adapter=new ArrayAdapter<String>(getContext(), R.layout.list_layout,arrayList);
        list.setAdapter(adapter);

        return rootView;
    }
    //SEND HL7 POST
    @Override
    public void onStart() {
        super.onStart();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MedFragment.currentSymptom=symptoms.get(position);
                Intent intent = new Intent(getActivity(), SymptomActivity.class);
                startActivity(intent);
            }
        });
    }
    //SAVE TO DATABASE IF USER SWITCHES FRAGMENT
    //DATA WONT BE SEND TO WEB SERVER UNTIL USERS DOES NOT PRESS SEND BUTTON
    @Override
    public void onPause() {
        super.onPause();
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
       // printSymptoms(symptoms);
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
