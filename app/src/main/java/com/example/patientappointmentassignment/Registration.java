package com.example.patientappointmentassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Registration extends AppCompatActivity {

    private TextView BackToLogin;
    private Button PatientRegistration, DoctorRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        BackToLogin = findViewById(R.id.BackToLogin);
        BackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RegisterAction = new Intent(Registration.this,Login.class);
                startActivity(RegisterAction);
            }
        });

        PatientRegistration = findViewById(R.id.PatientRegistration);
        PatientRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent PatientRegButton = new Intent(Registration.this, PatientRegistration.class);
                startActivity(PatientRegButton);
            }
        });
        DoctorRegistration = findViewById(R.id.DoctorRegistration);
        DoctorRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DoctorRegButton = new Intent(Registration.this, DoctorRegistration.class);
                startActivity(DoctorRegButton);
            }
        });

    }
}