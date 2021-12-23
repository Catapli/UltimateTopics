package com.example.topics.Activities.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Activities.Auth.AutentificationActivity;
import com.example.topics.Activities.Dialogos.LogOutDialogFragment;
import com.example.topics.R;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingActivity extends AppCompatActivity {

    private TextView cuenta,seguridad,notificaciones,suscripciones,metodoPago,cerrarSesion, authPeople;

    private FirebaseFirestore db;

    private Utilitarios utilitarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_setting);
        utilitarios = new Utilitarios();
        cuenta = findViewById(R.id.Cuenta);
        cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, AjustesCuentaSettingsActivity.class);
                startActivity(intent);
            }
        });
        seguridad = findViewById(R.id.Seguridad);
        seguridad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,SecuritySettingsActivity.class);
                startActivity(intent);
            }
        });
        notificaciones = findViewById(R.id.Notificaciones);
        notificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,NotificationsActivity.class);
                startActivity(intent);
            }
        });
        suscripciones = findViewById(R.id.SuscripcionesAjustes);
        suscripciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,SuscriptionSettingsActivity.class);
                startActivity(intent);
            }
        });
        metodoPago = findViewById(R.id.metodosPago);
        metodoPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,PashmentSettingsActivity.class);
                startActivity(intent);
            }
        });
        cerrarSesion = findViewById(R.id.cerrarSesión);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOutDialogFragment logOutDialogFragment = new LogOutDialogFragment();
                logOutDialogFragment.show(getSupportFragmentManager(),"logOut");
            }
        });
        authPeople = findViewById(R.id.AutentificarSettings);
        isAdmin();
        authPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, AutentificationActivity.class);
                startActivity(intent);
            }
        });

    }

    public void isAdmin(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    try {
                        boolean admin = doc.getBoolean("admin");
                        if (admin){
                            authPeople.setVisibility(View.VISIBLE);
                        }
                    }catch (NullPointerException e){
                        e.getMessage();
                    }
                }
            }
        });


    }
}