package com.example.topics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.topics.Adaptadores.ListadoSeguidoresAdapter;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.PreferenceManager;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListaActivityUsers extends AppCompatActivity {

    private EditText editText;

    private TextView textView;

    private RecyclerView recyclerView;

    private FirebaseFirestore db;

    private Utilitarios utilitarios;

    private PreferenceManager preferenceManager;

    private ProgressBar progressBar;

    private ArrayList<User> users = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_lista_users);
        init();
        Intent intent = getIntent();
        String cadena = intent.getExtras().getString("modo");

        if (cadena.equals(Constants.KEY_SEGUIDORES)){
            foundAllSeguidores();
        }else {
            foundAllSeguidos();
        }
    }

    private void init(){
        utilitarios = new Utilitarios(getApplicationContext());
        editText = findViewById(R.id.editTextListado);
        textView = findViewById(R.id.TextViewErrorListado);
        recyclerView = findViewById(R.id.recylerViewListado);
        progressBar = findViewById(R.id.progresBar);
        isLoading(true);
        db = FirebaseFirestore.getInstance();
        users = new ArrayList<>();
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    public void foundAllSeguidores(){
        db.collection(Constants.KEY_COLLECTION_SEGUIDORES)
                .whereEqualTo(Constants.KEY_ID_CUENTA, preferenceManager.getString(Constants.KEY_ID_USER))
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (!task.getResult().isEmpty()){
                    for (DocumentSnapshot doc : task.getResult()){
                        User u = new User();
                        u.setId(doc.getString(Constants.KEY_ID_CUENTA));
                        u.setNombreCuenta(doc.getString(Constants.KEY_NAME_USER_CUENTA));
                        u.setUrlPerfil(doc.getString(Constants.KEY_IMAGE_CUENTA));
                        users.add(u);
                    }
                    mostrarPosiblesResultados(users);
                }else {
                    progressBar.setVisibility(View.GONE);
                    textView.setText("No followers found");
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void isLoading(boolean loading){
        if (loading){
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }




    public void foundAllSeguidos(){
        db.collection(Constants.KEY_SEGUIDORES)
                .whereEqualTo(Constants.KEY_ID_SEGUIDOR, preferenceManager.getString(Constants.KEY_ID_USER))
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (!task.getResult().isEmpty()){
                            for (DocumentSnapshot doc : task.getResult()){
                                User u = new User();
                                u.setId(doc.getString(Constants.KEY_ID_CUENTA));
                                u.setNombreCuenta(doc.getString(Constants.KEY_NAME_USER_CUENTA));
                                u.setUrlPerfil(doc.getString(Constants.KEY_IMAGE_CUENTA));
                                users.add(u);
                            }
                            mostrarPosiblesResultados(users);
                        }else {
                            textView.setText("No followers found");
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
        });
    }


    public void mostrarPosiblesResultados(ArrayList<User> usuarios){

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ListadoSeguidoresAdapter postAdapter= new ListadoSeguidoresAdapter(usuarios,this);
        recyclerView.setAdapter(postAdapter);
        isLoading(false);
    }


}