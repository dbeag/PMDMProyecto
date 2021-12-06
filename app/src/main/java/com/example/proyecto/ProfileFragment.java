package com.example.proyecto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    
    Button btnLogout;
    TextView tvWelcome;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        btnLogout = profileView.findViewById(R.id.btnLogout);
        tvWelcome = profileView.findViewById(R.id.tvUser);
        tvWelcome.setText(tvWelcome.getText().toString() + " " + MainActivity.email);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sesion = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sesion.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                getActivity().onBackPressed();
            }
        });
        return profileView;
    }
}