package com.example.topics.Activities.Top;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.topics.Activities.Perfil;
import com.example.topics.Activities.PrincipalActivity;
import com.example.topics.Activities.SearchActivity;
import com.example.topics.Activities.Upload.UploadImage;
import com.example.topics.Adaptadores.AdaptadorTop;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class TopActivity extends AppCompatActivity {

    private TextView diario, mensual, semanal;

    private RecyclerView recyclerView;

    private FirebaseStorage storage;

    private StorageReference storageReference;

    private View home, search, profile, top;

    private FloatingActionButton add;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        init();
        setListeners();
        diario.setTextColor(Color.BLUE);
        diario.setBackgroundColor(Color.parseColor("#FFE0B2"));
        semanal.setBackgroundColor(Color.parseColor("#FFE0B2"));
        mensual.setBackgroundColor(Color.parseColor("#FFE0B2"));

        diario.setClickable(false);
        diario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diario.setTextColor(Color.BLUE);
                diario.setClickable(false);
                semanal.setClickable(true);
                mensual.setClickable(true);
                mensual.setTextColor(Color.BLACK);
                semanal.setTextColor(Color.BLACK);
                inicializarTarifas("TopDiario");
            }
        });
        mensual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mensual.setTextColor(Color.BLUE);
                mensual.setClickable(false);
                semanal.setClickable(true);
                diario.setClickable(true);
                diario.setTextColor(Color.BLACK);
                semanal.setTextColor(Color.BLACK);
                inicializarTarifas("TopMensual");


            }
        });
        semanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                semanal.setTextColor(Color.BLUE);
                semanal.setClickable(false);
                mensual.setClickable(true);
                diario.setClickable(true);
                mensual.setTextColor(Color.BLACK);
                diario.setTextColor(Color.BLACK);
                inicializarTarifas("TopSemanal");
            }
        });
    }

    private void init(){
        add = findViewById(R.id.add);
        home = findViewById(R.id.menuHome);
        search = findViewById(R.id.menuSearch);
        top = findViewById(R.id.menuStats);
        profile = findViewById(R.id.menuPerson);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = storage.getReference();
        recyclerView = findViewById(R.id.recyclerViewTop);
        diario = findViewById(R.id.diario);
        mensual = findViewById(R.id.mensual);
        semanal = findViewById(R.id.semanal);
    }

    private void setListeners(){
        home.setOnClickListener(view -> {
            startActivity(new Intent(this, PrincipalActivity.class));
        });
        profile.setOnClickListener(view -> {
            startActivity(new Intent(this, Perfil.class));
        });
        search.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchActivity.class));
        });
        add.setOnClickListener(view -> {
            startActivity(new Intent(this, UploadImage.class));
        });
    }

    public void insertPreciosDiario() {
        HashMap<String, Object> precios = new HashMap<>();
        double inicial = 0;
        String pattern = "#.##";
        db = FirebaseFirestore.getInstance();
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        for (int i = 0; i < 50; i++) {
            if (i == 0) {
                precios.put(String.valueOf(i + 1), 0);
                Log.d("Precios", "ID => " + String.valueOf(i + 1) + ", precio =>" + inicial);
            } else if (i == 1) {
                inicial = inicial +5.5;
                precios.put(String.valueOf(i + 1), inicial);
                Log.d("Precios", "ID => " + String.valueOf(i + 1) + ", precio =>" + inicial);
            } else if (i == 2) {
                inicial = inicial + 0.5;
                precios.put(String.valueOf(i + 1), inicial);
                Log.d("Precios", "ID => " + String.valueOf(i + 1) + ", precio =>" + inicial);
                inicial = 12;

            } else if (i >= 3 && i < 10) {
                String precio = decimalFormat.format(inicial);
                precios.put(String.valueOf(i + 1), precio);
                Log.d("Precios", "ID => " + String.valueOf(i + 1) + ", precio =>" + precio);
                inicial = inicial + 0.2;

            } else if (i == 10) {
                inicial = 13.4;
                precios.put(String.valueOf(i + 1), inicial);
                Log.d("Precios", "ID => " + String.valueOf(i + 1) + ", precio =>" + inicial);
            } else if (i > 10) {
                inicial = inicial + 0.165;
                String precio = decimalFormat.format(inicial);
                precios.put(String.valueOf(i + 1), precio);
                Log.d("Precios", "ID => " + String.valueOf(i + 1) + ", precio =>" + precio);
            }
            db.collection("TopSemanal").document("precios").set(precios);
        }



}

    private void inicializarTarifas(String texto){
        HashMap<String,String > tarifa = new HashMap<>();
        db.collection(texto).document("precios").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    for (int i = 0; i < 50; i++) {
                        String precio = doc.get(String.valueOf(i+1)).toString();
                        tarifa.put(String.valueOf(i+1), precio);
                    }
                    foundAllUsers(texto, tarifa);
                }
            }
        });
    }

    private void foundAllUsers(String text, HashMap<String,String> tarifa){
        ArrayList<User> usuarios = new ArrayList<>();
        db.collection(text)
                .orderBy("seguidores", Query.Direction.DESCENDING)
                .limit(50).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc : task.getResult()){
                    if (!doc.getId().equals("precios")){
                        User user = new User();
                        user.setNuevosSubsTop(doc.get("seguidores").toString());
                        user.setSeguidores(doc.get("Seguidores totales").toString());
                        user.setNombreCuenta(doc.get("nameCount").toString());
                        int id = usuarios.size();
                            String idTarifa = String.valueOf(id+1);
                            String precio = String.valueOf(tarifa.get(idTarifa));
                            user.setTop(precio);

                        usuarios.add(user);
                    }
                }
                mostrarPosiblesResultados(usuarios);
            }
        });
    }

    public void mostrarPosiblesResultados(ArrayList<User> usuarios){
        recyclerView = findViewById(R.id.recyclerViewTop);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdaptadorTop adaptadorTop = new AdaptadorTop(usuarios,this);
        recyclerView.setAdapter(adaptadorTop);
    }

}