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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;

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

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    //DECLARATION
    FirebaseDatabase database;
    DatabaseReference reference;
    private EditText name;
    private CalendarView calendar;
    private Button saveButton;
    private String date;
    private LocalDateTime dateTime;
    private Spinner pressSpinner;
    private Spinner tempSpinner;
    private ArrayAdapter<String> adapter;

    //ONCREATE: NIGHT MODE OFF
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }
    //ON CREATE VIEW: SET UP PROFILE VIEW WITH NAME, SURNAME AND PARAMETERS VALUES
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //GET VIEW
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        // GET CURRENT DATE
        dateTime = LocalDateTime.now();
        date = dateTime.getYear()+""+dateTime.getMonthValue()+""+dateTime.getDayOfMonth()+"";
        //SPINNER
        pressSpinner=(Spinner) rootView.findViewById(R.id.pressSpinner);
        tempSpinner=(Spinner) rootView.findViewById(R.id.tempSpinner);
        ArrayList<String> arrayListOptions = new ArrayList<>();
        arrayListOptions.add("09:00");arrayListOptions.add("12:00");arrayListOptions.add("15:00");arrayListOptions.add("18:00");arrayListOptions.add("21:00");arrayListOptions.add("24:00");
        adapter = new ArrayAdapter<String>(getContext(), R.layout.list_layout, arrayListOptions);
        adapter.setDropDownViewResource(R.layout.list_layout);
        tempSpinner.setAdapter(adapter);
        tempSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child("Temperature").child(date+tempSpinner.getSelectedItem());
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        pressSpinner.setAdapter(adapter);
        pressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child("Pressure").child(date+pressSpinner.getSelectedItem());
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //GET DATABASE REFERENCE
        database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
        //SET PRESSION VALUE IF IT'S IN THE DATABASE
        reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child("Pressure").child(date+pressSpinner.getSelectedItem());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EditText temp = (EditText) rootView.findViewById(R.id.editTextPressione);
                temp.setText(snapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //SET TEMPERATURE VALUE IF IT'S IN THE DATABASE
        reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child("Temperature").child(date+tempSpinner.getSelectedItem());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EditText temp = (EditText) rootView.findViewById(R.id.editTextTemperatura);
                temp.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //HIDEKEYBOARD IF USER TOUCHES OUTSIDE WIDGETS
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(rootView.getContext(),rootView);
                return false;
            }
        });
        //SET NAME TEXT BOX
        name= (EditText) rootView.findViewById(R.id.layout);
        name.setText(User.currentUser.getNome()+" "+User.currentUser.getCognome());
        //CALENDAR VIEW
        calendar = (CalendarView) rootView.findViewById(R.id.calendarView);
        //CHECK IF THERE'S A VALUE FOR THE DAY SELECTED
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = String.valueOf(year) + String.valueOf(month+1) + String.valueOf(dayOfMonth);
                reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child("Pressure").child(date+pressSpinner.getSelectedItem());
                reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    EditText temp = (EditText) rootView.findViewById(R.id.editTextPressione);
                    temp.setText(snapshot.getValue(String.class));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child("Temperature").child(date+tempSpinner.getSelectedItem());
                reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    EditText temp = (EditText) rootView.findViewById(R.id.editTextTemperatura);
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
    //HIDE THE KEYBOARD WHEN USER TOUCHES OUTSIDE WIDGETS
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //SAVE NEW VALUES OF PARAMETERS FOR THE DAY SELECTED
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStart() {
        super.onStart();
        //IF BUTTON SAVE IS PRESSED DATA ARE PUSHED TO DATABASE
        saveButton = (Button) getView().findViewById(R.id.buttonSalva);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String p=(((EditText) getView().findViewById(R.id.editTextPressione)).getText().toString());
                String t=((EditText) getView().findViewById(R.id.editTextTemperatura)).getText().toString();
                if (!t.equals("")){
                    database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
                    reference= database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child("Temperature").child(date+tempSpinner.getSelectedItem());
                    reference.setValue(t);
                }
                if(!p.equals("")){
                    database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
                    reference= database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child("Pressure").child(date+pressSpinner.getSelectedItem());
                    reference.setValue(p);
                }
            }
        });
    }

}
