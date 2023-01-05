package com.example.patientappointmentassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class Login extends AppCompatActivity {

    private TextView Register;
    private TextInputEditText RegisterName, RegisterNumber, RegisterPhoneNumber, LoginEmailhere, LoginPassword;
    private Button ButtonLoginID;
    private CircleImageView PFP;

    //User Interface PFP Uri:
    private Uri UIResult;

    //Firebase Database:
    private FirebaseAuth FBAuth;
    private DatabaseReference DBRef;
    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Register = findViewById(R.id.Register);
        LoginEmailhere = findViewById(R.id.LoginEmailID);
        LoginPassword = findViewById(R.id.LoginPasswordID);

        PD = new ProgressDialog(this);
        FBAuth = FirebaseAuth.getInstance();
        ButtonLoginID = findViewById(R.id.ButtonLoginID);


        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RegisterAction = new Intent(Login.this, Registration.class);
                startActivity(RegisterAction);
            }
        });

        ButtonLoginID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginMethod();
            }
        });
    }

    private void LoginMethod() {
        final String Email = LoginEmailhere.getText().toString().trim();
        final String Password = LoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(Email)) {
            LoginEmailhere.setError("Email is required!");
        }
        if (TextUtils.isEmpty(Password)) {
            LoginPassword.setError("Password is required!");
        } else {
            PD.setMessage("Please wait..");
            PD.setCanceledOnTouchOutside(false);
            PD.show();

            FBAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        String ErrorsDisplay = task.getException().toString();
                        Toast.makeText(Login.this, "There was an error:" + ErrorsDisplay, Toast.LENGTH_LONG).show();
                    } else {
                        String CUID = FBAuth.getCurrentUser().getUid();
                        DBRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CUID);
                        sendUserToNextActivity();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {

    Intent intent = new Intent(Login.this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    }
}