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
import androidx.viewpager2.widget.ViewPager2;

import com.example.topics.Activities.Perfil;
import com.example.topics.Activities.PrincipalActivity;
import com.example.topics.Activities.SearchActivity;
import com.example.topics.Activities.Upload.UploadImage;
import com.example.topics.Adaptadores.AdaptadorTop;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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

    private ArrayList<User> TopDiario, TopMensual, TopSemanal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        init();
        setListeners();

        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new TopAdapter(this));
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:{
                        tab.setText("Top Diario");
                        tab.setIcon(R.drawable.ic_add);

                        break;
                    }
                    case 1:{
                        tab.setText("Top Semanal");
                        tab.setIcon(R.drawable.ic_add);
                        break;
                    }
                    case 2:{
                        tab.setText("Top Mensual");
                        tab.setIcon(R.drawable.ic_add);
                        break;
                    }
                }
            }
        }
        );
        tabLayoutMediator.attach();
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

    }

    private void chargeUsers(String text){

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
                .limit(51).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
              //  mostrarPosiblesResultados(usuarios);
            }
        });
    }

//    public void mostrarPosiblesResultados(ArrayList<User> usuarios){
//        recyclerView = findViewById(R.id.recyclerViewTop);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        AdaptadorTop adaptadorTop = new AdaptadorTop(usuarios,this);
//        recyclerView.setAdapter(adaptadorTop);
//    }

}