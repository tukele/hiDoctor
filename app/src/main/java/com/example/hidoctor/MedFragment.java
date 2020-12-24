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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_med, container, false);
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


        reference= database.getReference().child("User").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Symptoms").exists()){
                    febbre.setChecked(Boolean.valueOf(String.valueOf(snapshot.child("febbre").getValue())));
                    febbre.setActivated(febbre.isChecked());
                    tosse.setChecked(Boolean.valueOf(String.valueOf(snapshot.child("tosse").getValue())));
                    tosse.setActivated(tosse.isChecked());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        send = (Button) getView().findViewById(R.id.sendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HL7SEND message = new HL7SEND();
                Symptoms symptoms= new Symptoms();
                //TOSSE

                    System.out.println(message.HL7Message(id, "tosse", Boolean.toString(tosse.isChecked())));

                //FEBBRE

                    System.out.println(message.HL7Message(id, "tosse", Boolean.toString(febbre.isChecked())));

                symptoms.setTosse(Boolean.toString(tosse.isChecked()));
                symptoms.setFebbre(Boolean.toString(febbre.isChecked()));

                reference= database.getReference().child("User").child(id).child("Symptoms");
                System.out.println(symptoms.getFebbre()+symptoms.getTosse());
                reference.setValue(symptoms.getFebbre()+symptoms.getTosse());

            }
        });
    }

}
