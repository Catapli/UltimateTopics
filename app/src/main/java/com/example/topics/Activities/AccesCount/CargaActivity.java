package com.example.topics.Activities.AccesCount;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Activities.PrincipalActivity;
import com.example.topics.Utilidades.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CargaActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (!user.isEmailVerified()){
                Intent intent = new Intent(this, EmailVerificationActivity.class);
                intent.putExtra(Constants.KEY_EMAIL, user.getEmail());
                startActivity(intent);
            }
            validarHabilitado(user.getUid(), user.getEmail());
        } else {
            Intent intent = new Intent(this, AccesCount.class);
            startActivity(intent);
        }
    }


    public void validarHabilitado(String id, String email){
         FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.get(Constants.KEY_HABILITADO).toString().equals("false")) {
                        Intent intent = new Intent(CargaActivity.this, NoHabilitadoActivity.class);
                        intent.putExtra(Constants.KEY_EMAIL, email);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(CargaActivity.this, PrincipalActivity.class);
                        startActivity(intent);
                    }
                }else {
                        Intent intent = new Intent(CargaActivity.this, AccesCount.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }



}