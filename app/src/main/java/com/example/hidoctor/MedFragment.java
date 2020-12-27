package com.example.hidoctor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MedFragment extends Fragment {
    private String nome;
    private String cognome;
    private String text;
    CheckBox tosse;
    CheckBox febbre;
    TextView name;
    Button send;
    FirebaseDatabase database;
    DatabaseReference reference;
    String id;
    private static Boolean tosseFlag;
    private static Boolean febbreFlag;

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_med, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        id=getArguments().getString("id");
        database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
        reference= database.getReference().child("User").child(getArguments().getString("id")).child("nome");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nome = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        reference=database.getReference().child("User").child(getArguments().getString("id")).child("cognome");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongViewCast")
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                cognome= snap.getValue(String.class);
                name = (TextView) rootView.findViewById(R.id.name);
                name.setText(nome+" "+cognome);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;

        tosse=(CheckBox) rootView.findViewById(R.id.checkTosse);
        febbre=(CheckBox) rootView.findViewById(R.id.checkFebbre);

        reference= database.getReference().child("User").child(id).child("Symptoms").child("febbre");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    febbre.setChecked(Boolean.valueOf((snapshot.getValue(String.class))));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference= database.getReference().child("User").child(id).child("Symptoms").child("tosse");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tosse.setChecked((Boolean)(Boolean.valueOf((snapshot.getValue(String.class)))));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        
        MedFragment.tosseFlag=tosse.isChecked();
        MedFragment.febbreFlag=febbre.isChecked();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        send = (Button) getView().findViewById(R.id.sendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HL7SEND post = new HL7SEND("");
                //TOSSE
                if(MedFragment.tosseFlag!=tosse.isChecked()) {
                    post.setMessage(id,"tosse",Boolean.toString(tosse.isChecked()));
                    System.out.println("INVIATO TOSEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    /*
                    try {
                        post.execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    MedFragment.tosseFlag = tosse.isChecked();
                }
                //FEBBRE
                if(MedFragment.febbreFlag!=febbre.isChecked()) {
                    post.setMessage(id,"tosse",Boolean.toString(tosse.isChecked()));
                    System.out.println("INVIATO FEBREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    /*
                    try {
                        post.execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    MedFragment.febbreFlag = febbre.isChecked();
                }

            }
        });
        febbre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference= database.getReference().child("User").child(id).child("Symptoms").child("febbre");
                reference.setValue(Boolean.toString(febbre.isChecked()));
            }
        });
        tosse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference= database.getReference().child("User").child(id).child("Symptoms").child("tosse");
                reference.setValue(Boolean.toString(tosse.isChecked()));
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        Symptoms symptoms= new Symptoms();
        symptoms.setTosse(Boolean.toString(tosse.isChecked()));
        symptoms.setFebbre(Boolean.toString(febbre.isChecked()));
        MedFragment.tosseFlag=tosse.isChecked();
        MedFragment.febbreFlag=febbre.isChecked();
        reference= database.getReference().child("User").child(id).child("Symptoms");
        reference.setValue(symptoms);
    }

}
