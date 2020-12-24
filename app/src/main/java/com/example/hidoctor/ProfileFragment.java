package com.example.hidoctor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;


public class ProfileFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference reference;
    EditText name;
    String usernome;
    String usercognome;
    String temperatura;
    String pressione;
    CalendarView calendar;
    Button salva;
    String id;
    String date;
    LocalDateTime dateTime;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        id = getArguments().getString("id");
        dateTime = LocalDateTime.now();
        date = dateTime.getYear()+""+dateTime.getMonthValue()+""+dateTime.getDayOfMonth()+"";
        database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = database.getReference().child("User").child(getArguments().getString("id")).child("Parameters").child(date).child("pressione");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EditText temp = (EditText) getView().findViewById(R.id.editTextPressione);
                temp.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference = database.getReference().child("User").child(getArguments().getString("id")).child("Parameters").child(date).child("temperatura");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EditText temp = (EditText) getView().findViewById(R.id.editTextTemperatura);
                temp.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(rootView.getContext(),rootView);
                return false;
            }
        });


        reference= database.getReference().child("User").child(getArguments().getString("id")).child("nome");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usernome = dataSnapshot.getValue(String.class);
                name = (EditText) rootView.findViewById(R.id.layout);
                name.setText(usernome+" "+usercognome);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        reference=database.getReference().child("User").child(getArguments().getString("id")).child("cognome");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                usercognome= snap.getValue(String.class);
                name = (EditText) rootView.findViewById(R.id.layout);
                name.setText(usernome+" "+usercognome);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        }) ;


        calendar = (CalendarView) rootView.findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = String.valueOf(year) + String.valueOf(month+1) + String.valueOf(dayOfMonth);
                reference = database.getReference().child("User").child(getArguments().getString("id")).child("Parameters").child(date).child("pressione");
                reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    EditText temp = (EditText) getView().findViewById(R.id.editTextPressione);
                    temp.setText(snapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                reference = database.getReference().child("User").child(getArguments().getString("id")).child("Parameters").child(date).child("temperatura");
                reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    EditText temp = (EditText) getView().findViewById(R.id.editTextTemperatura);
                    temp.setText(snapshot.getValue(String.class));
                    }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
        });

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStart() {
        super.onStart();
        //calendar = (CalendarView) getView().findViewById(R.id.calendarView);
        salva = (Button) getView().findViewById(R.id.buttonSalva);
        salva.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Parameters param = new Parameters();
                param.setPressione(((EditText) getView().findViewById(R.id.editTextPressione)).getText().toString());
                param.setTemperatura(((EditText) getView().findViewById(R.id.editTextTemperatura)).getText().toString());
                if (!param.getTemperatura().equals("")&&!param.getPressione().equals("")){
                    database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
                    reference= database.getReference().child("User").child(id).child("Parameters").child(date);
                    reference.setValue(param);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        /*
        database = FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = database.getReference("Name");
        name= (EditText)getView().findViewById(R.id.name);
        reference.setValue(name.getText().toString());
        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
