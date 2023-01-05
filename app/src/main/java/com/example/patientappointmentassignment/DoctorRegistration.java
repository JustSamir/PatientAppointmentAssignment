package com.example.patientappointmentassignment;

import static com.example.patientappointmentassignment.R.id.DoctorLoginQS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorRegistration extends AppCompatActivity {

    private TextView DoctorLoginQS;

    //Buttons,Views, and Inputs:
    private TextInputEditText RegisterName, RegisterNumber, RegisterPhoneNumber, LoginEmailhere, LoginPassword;
    private Button RegisterButtonID;
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
        setContentView(R.layout.activity_doctor_registration);

        DoctorLoginQS = findViewById(R.id.DoctorLoginQS);
        DoctorLoginQS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DocRegAction = new Intent(DoctorRegistration.this, Login.class);
                startActivity(DocRegAction);

                DoctorLoginQS = findViewById(R.id.DoctorLoginQS);
                DoctorLoginQS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent PatientRegAction = new Intent(DoctorRegistration.this, Login.class);
                        startActivity(PatientRegAction);
                    }
                });

                RegisterName = findViewById(R.id.RegisterName);
                RegisterNumber = findViewById(R.id.RegisterName);
                RegisterPhoneNumber = findViewById(R.id.RegisterPhoneNumber);
                LoginEmailhere = findViewById(R.id.LoginEmailID);
                LoginPassword = findViewById(R.id.LoginPasswordID);

                PD = new ProgressDialog(DoctorRegistration.this);
                FBAuth = FirebaseAuth.getInstance();

                RegisterButtonID = findViewById(R.id.RegisterButtonID);
                RegisterButtonID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String Name1 = RegisterName.getText().toString().trim();
                        final String Number1 = RegisterNumber.getText().toString().trim();
                        final String PhoneNumber1 = RegisterPhoneNumber.getText().toString().trim();
                        final String Email1 = LoginEmailhere.getText().toString().trim();
                        final String Password1 = LoginPassword.getText().toString().trim();

                        if (TextUtils.isEmpty(Name1)) {
                            RegisterName.setError("Please put in your name!");
                            return;
                        }
                        if (TextUtils.isEmpty(Number1)) {
                            RegisterNumber.setError("Please type in your ID number!");
                        }
                        if (TextUtils.isEmpty(PhoneNumber1)) {
                            RegisterPhoneNumber.setError("Please type in your phone number!");
                        }
                        if (TextUtils.isEmpty(Email1)) {
                            LoginEmailhere.setError("Email is required!");
                        }
                        if (TextUtils.isEmpty(Password1)) {
                            LoginPassword.setError("Please make a password!");
                        }
                        if (UIResult == null) {
                            Toast.makeText(DoctorRegistration.this, "Please choose a Profile Picture!", Toast.LENGTH_LONG).show();
                        } else {
                            PD.setMessage("Please wait..");
                            PD.setCanceledOnTouchOutside(false);
                            PD.show();

                            FBAuth.createUserWithEmailAndPassword(Email1, Password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        String ErrorsDisplay = task.getException().toString();
                                        Toast.makeText(DoctorRegistration.this, "There was an error:" + ErrorsDisplay, Toast.LENGTH_LONG).show();
                                    } else {
                                        String CUID = FBAuth.getCurrentUser().getUid();
                                        DBRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CUID);

                                        //Hash Map to capture all details
                                        HashMap UInfo = new HashMap();
                                        UInfo.put("Name2", Name1);
                                        UInfo.put("Email2", Email1);
                                        UInfo.put("Numbe2r", Number1);
                                        UInfo.put("Phone Number2", PhoneNumber1);

                                        UInfo.put("Type", "Patient");
                                        DBRef.updateChildren(UInfo).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(DoctorRegistration.this, "Details are Set Successfully!", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(DoctorRegistration.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                                }
                                                finish();
                                                PD.dismiss();
                                            }
                                        });
                                        //Add and Save Profile Picture to Database:
                                        if (UIResult != null) {
                                            final StorageReference FilePath = FirebaseStorage.getInstance().getReference().child("PFP").child(CUID);
                                            Bitmap BitMap2 = null;
                                            try {
                                                BitMap2 = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), UIResult);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
                                            BitMap2.compress(Bitmap.CompressFormat.JPEG, 20, BAOS);
                                            byte[] data = BAOS.toByteArray();

                                            UploadTask UT = FilePath.putBytes(data);
                                            UT.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    finish();
                                                    return;
                                                }
                                            });
                                            UT.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    if (taskSnapshot.getMetadata() != null) {
                                                        Task<Uri> URIResult = taskSnapshot.getStorage().getDownloadUrl();
                                                        URIResult.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String IMGURL2 = uri.toString();
                                                                Map IMGMap2 = new HashMap<>();
                                                                IMGMap2.put("PFPUrl", IMGURL2);

                                                                DBRef.updateChildren(IMGMap2).addOnCompleteListener(new OnCompleteListener() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(DoctorRegistration.this, "Registered Successfully!", Toast.LENGTH_LONG).show();
                                                                        } else {
                                                                            Toast.makeText(DoctorRegistration.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                                finish();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                            Intent MainActivityAction = new Intent(DoctorRegistration.this, MainActivity.class);
                                            startActivity(MainActivityAction);
                                            finish();
                                            PD.dismiss();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });

                PFP = findViewById(R.id.PFP);
                PFP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent PFPAction = new Intent(Intent.ACTION_PICK);
                        PFPAction.setType("image/*");
                        startActivityForResult(PFPAction, 1);
                    }
                });
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data!=null){
            UIResult = data.getData();
            PFP.setImageURI(UIResult);
        }
    }
}