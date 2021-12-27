package com.example.topics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetallesImagen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView imageView;

    private TextView textViewDescripcion,textViewNombre;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_detalles_imagen);
        init();
        Intent intent = getIntent();
        String url = intent.getExtras().getString(Constants.KEY_RUTA_POST);
        String descripcion = intent.getExtras().getString(Constants.KEY_DESCRIPCION);
        String idUser = intent.getExtras().getString(Constants.KEY_ID_USER);
        String user = intent.getExtras().getString(Constants.KEY_NAME_USER);
        Glide.with(this).load(url).into(imageView);
        textViewDescripcion.setText(descripcion);
        textViewNombre.setText(user);
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        textViewDescripcion = findViewById(R.id.DetallesImagenDescripcion);
        imageView = findViewById(R.id.DetallesPostImagen);
        textViewNombre = findViewById(R.id.DetallesImagenNombreCuenta);

    }



}