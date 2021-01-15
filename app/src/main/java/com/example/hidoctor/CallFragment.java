package com.example.hidoctor;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//VIDEO CALL TO THE DOCTOR
public class CallFragment extends Fragment {
    //DECLARATION
    Button call;
    TextView doctor;
    FirebaseDatabase database;
    DatabaseReference reference;
    static Boolean MEDFOUND =true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call, container, false);
        database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
        doctor= (TextView) rootView.findViewById(R.id.docLabel);
        HTTPost httpDoc= new HTTPost();

        if(MEDFOUND) {
                String doctorName=(httpDoc.doctorHTTP(User.currentUser.getId()));
                doctor.setText(doctorName);
                reference=database.getReference().child("User").child(User.currentUser.getId()).child("Dottore");
                reference.setValue(doctorName);
                MEDFOUND =false;
                User.currentUser.setNomeMedico(doctorName);
        }else{
                    doctor.setText(User.currentUser.getNomeMedico());
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        call= (Button) getView().findViewById(R.id.callButton);
        call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                HTTPost http = new HTTPost();
                String urlString ="https://appr.tc/r/"+http.callHTTP(User.currentUser.getId());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.browser");
                try {
                    getContext().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    getContext().startActivity(intent);
                }
            }
        });
    }
}
