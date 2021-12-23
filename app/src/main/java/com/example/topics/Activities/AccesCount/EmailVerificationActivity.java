package com.example.topics.Activities.AccesCount;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmailVerificationActivity extends AppCompatActivity {


    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    Button resendEmail;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_email_verification);
        init();
        setListeners();
        sendEmail();
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        resendEmail = findViewById(R.id.buttonresend);
    }

    private void setListeners(){
        resendEmail.setOnClickListener(view -> {
            sendEmail();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null){
            Intent intent = new Intent(EmailVerificationActivity.this,AccesCount.class);
            startActivity(intent);
        }
    }

    private void sendEmail(){
        try {
            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(EmailVerificationActivity.this, "El email ha sido enviado.", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EmailVerificationActivity.this, "El correo electrónico no existe", Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}