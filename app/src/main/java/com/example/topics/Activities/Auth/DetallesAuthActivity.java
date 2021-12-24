package com.example.topics.Activities.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DetallesAuthActivity extends AppCompatActivity {

    private  final String TAG = "Autentificacion";

    private ImageView rostro, dni;

    private ImageButton yes, no;

    private FirebaseFirestore db;

    private User user;

    private Utilitarios utilitarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        user=new User();
        utilitarios = new Utilitarios();
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_detalles_auth);
        rostro = findViewById(R.id.imageViewRostrovalidate);
        dni = findViewById(R.id.imageViewDniValidate);
        yes = findViewById(R.id.imageButtonAUTHOk);
        no = findViewById(R.id.imageButtonAuthNot);
        completeUser();
        Glide.with(this).load(user.getUrlDni()).into(dni);
        Glide.with(this).load(user.getUrlRostro()).into(rostro);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> data = utilitarios.userToHashMapUsers(user);
                Log.d("AUTH", data.toString());
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(user.getId())
                        .set(data);
                db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(user.getEmail()).delete();
                insertInTops();
                insertUserInformation();
                deleteDocument();
                returnListado();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(user.getEmail()).delete();
                returnListado();
            }
        });
    }

    public void insertInTops(){
        String id = user.getId();
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(Constants.KEY_PHOTO_PERFIL, user.getUrlPerfil());
        userMap.put(Constants.KEY_TOP_DIARIO,0);
        userMap.put(Constants.KEY_TOP_MENSUAL,0);
        userMap.put(Constants.KEY_TOP_SEMANAL,0);
        userMap.put(Constants.KEY_ID_USER,user.getId());
        userMap.put(Constants.KEY_NAME_USER, user.getNombreCuenta());
        userMap.put(Constants.KEY_SEGUIDORES_TOTALES, 0);
        db.collection(Constants.KEY_COLLECTION_TOP).document(id)
                .set(userMap);
    }

    private void deleteDocument(){
        db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(user.getId()).delete();
    }


    public void insertUserInformation(){
        db = FirebaseFirestore.getInstance();
        String id = user.getId();
        HashMap<String,Object> userMap = utilitarios.userToHashMapUsers(user);
        db.collection(Constants.KEY_COLLECTION_USERS).document(id)
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }



    public void completeUser(){
        Intent intent = getIntent();
        String email = intent.getExtras().getString(Constants.KEY_EMAIL);
        String urlrostro = intent.getExtras().getString(Constants.KEY_ROSTRO);
        String urlDni = intent.getExtras().getString(Constants.KEY_DNI);
        String urlPerfil = intent.getExtras().getString(Constants.KEY_PHOTO_PERFIL);
        String fecha = intent.getExtras().getString(Constants.KEY_DATE);
        String id = intent.getExtras().getString(Constants.KEY_ID_USER);
        String name = intent.getExtras().getString(Constants.KEY_NAME);
        String nameCount = intent.getExtras().getString(Constants.KEY_NAME_USER);
        String password = intent.getExtras().getString(Constants.KEY_PASSWORD);
        String token = intent.getExtras().getString(Constants.KEY_TOKEN);
        user.setId(id);
        user.setEmail(email);
        user.setUrlRostro(urlrostro);
        user.setUrlDni(urlDni);
        user.setPassword(password);
        user.setNombre(name);
        user.setNombreCuenta(nameCount);
        user.setUrlPerfil(urlPerfil);
        user.setFecha(fecha);
        user.setToken(token);
    }



    public void returnListado(){
        Intent intent = new Intent(this,AutentificationActivity.class);
        startActivity(intent);
    }
}