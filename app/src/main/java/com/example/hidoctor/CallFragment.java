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
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;


public class CallFragment extends Fragment {

    Button call;
    EditText URL;
    String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call, container, false);
        id = getArguments().getString("id");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        URL=(EditText) getView().findViewById(R.id.URL);
        call= (Button) getView().findViewById(R.id.callButton);
        call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                CallPost callPost= new CallPost("http://hidoctor.shardslab.com/Api/getCallCode",id);
                System.out.println(callPost.getJSON());
                String urlString ="https://appr.tc/r/"+callPost.getJSON();
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
