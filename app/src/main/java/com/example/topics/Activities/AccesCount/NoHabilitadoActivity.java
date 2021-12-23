package com.example.topics.Activities.AccesCount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.Utilitarios;
import com.google.firebase.auth.FirebaseAuth;

public class NoHabilitadoActivity extends AppCompatActivity {

    private TextView volverLogin,mensaje;

    private Utilitarios utilitarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_no_habilitado);
        utilitarios = new Utilitarios();
        volverLogin = findViewById(R.id.ReturnLogin);
        Intent intent = getIntent();
        String email = intent.getExtras().getString(Constants.KEY_EMAIL);
        String text = "La cuenta de "+email+" esta inhabilitada por el momento, espere por favor a que se la habilitamos en un plazo máximo de 48 horas.";
        mensaje = findViewById(R.id.MensajeHabilitado);
        mensaje.setText(text);
        volverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnLogin();
            }
        });
    }




    public void returnLogin(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this,AccesCount.class);
        startActivity(intent);
    }





}