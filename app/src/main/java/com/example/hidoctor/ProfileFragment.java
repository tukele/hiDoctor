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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;

public class ProfileFragment extends Fragment {
    //DECLARATION
    FirebaseDatabase database;
    DatabaseReference reference;
    EditText name;
    CalendarView calendar;
    Button salva;
    String date;
    LocalDateTime dateTime;

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
        //GET DATABASE REFERENCE
        database= FirebaseDatabase.getInstance("https://hidoctor-dha-default-rtdb.europe-west1.firebasedatabase.app/");
        //SET PRESSION VALUE IF IT'S IN THE DATABASE
        reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child(date).child("pressione");
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
        //SET TEMPERATURE VALUE IF IT'S IN THE DATABASE
        reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child(date).child("temperatura");
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
        name= (EditText) rootView.findViewById(R.id.userName);
        name.setText(User.currentUser.getNome()+" "+User.currentUser.getCognome());
        //CALENDAR VIEW
        calendar = (CalendarView) rootView.findViewById(R.id.calendarView);
        //CHECK IF THERE'S A VALUE FOR THE DAY SELECTED
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = String.valueOf(year) + String.valueOf(month+1) + String.valueOf(dayOfMonth);
                reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child(date).child("pressione");
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

                reference = database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child(date).child("temperatura");
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
                    reference= database.getReference().child("User").child(User.currentUser.getId()).child("Parameters").child(date);
                    reference.setValue(param);
                }
            }
        });
    }
    //DEFAULT FUNCTION DECLARATION
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onPause() {
        super.onPause();
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
