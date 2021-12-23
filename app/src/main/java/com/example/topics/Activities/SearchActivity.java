package com.example.topics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.topics.Activities.Top.TopActivity;
import com.example.topics.Activities.Upload.UploadImage;
import com.example.topics.Adaptadores.SearchAdapterPosts;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.PreferenceManager;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText buscar;

    private FirebaseFirestore db;

    private RecyclerView recyclerView;

    private Utilitarios utilitarios;

    private PreferenceManager preferenceManager;

    private View home, search, profile, top;

    private FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_search);
        init();
        setListeners();
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    int contador= editable.length();
                    if (contador > 0){
                        foundAllUsuarios(buscar.getText().toString());
                    }
            }
        });

    }

    private void init(){
        utilitarios = new Utilitarios();
        preferenceManager = new PreferenceManager(getApplicationContext());
        buscar = findViewById(R.id.EditTextSearch);
        add = findViewById(R.id.add);
        home = findViewById(R.id.menuHome);
        search = findViewById(R.id.menuSearch);
        top = findViewById(R.id.menuStats);
        profile = findViewById(R.id.menuPerson);
    }

    private void setListeners(){
        home.setOnClickListener(view -> {
            startActivity(new Intent(this, PrincipalActivity.class));
        });
        top.setOnClickListener(view -> {
            startActivity(new Intent(this, TopActivity.class));
        });
        profile.setOnClickListener(view -> {
            startActivity(new Intent(this, Perfil.class));
        });
        add.setOnClickListener(view -> {
            startActivity(new Intent(this, UploadImage.class));
        });
    }


    public void foundAllUsuarios(String text){
        ArrayList<User> usuarios = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).whereGreaterThanOrEqualTo(Constants.KEY_NAME_USER,text)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    String nameCount = preferenceManager.getString(Constants.KEY_NAME_USER);
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String name = documentSnapshot.get("nameCount").toString();
                        if (!name.equals(nameCount) && name.contains(text)) {
                            User user = createUser(documentSnapshot);
                            usuarios.add(user);
                        }
                    }
                    Log.d("Search","numUsers = " + usuarios.size());
                    mostrarPosiblesResultados(usuarios);
                }
            }
        });
    }



    public User createUser(QueryDocumentSnapshot doc){
        User usuario = new User();
        usuario.setNombreCuenta(doc.getString(Constants.KEY_NAME_USER));
        usuario.setNombre(doc.getString(Constants.KEY_NAME));
        usuario.setUrlPerfil(doc.getString(Constants.KEY_PHOTO_PERFIL));
        usuario.setEmail(doc.getString(Constants.KEY_EMAIL));
        usuario.setId(doc.getString(Constants.KEY_ID_USER));
        return usuario;
    }

    public void mostrarPosiblesResultados(ArrayList<User> usuarios){
        recyclerView = findViewById(R.id.RecyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SearchAdapterPosts postAdapter= new SearchAdapterPosts(usuarios,this);
        recyclerView.setAdapter(postAdapter);
    }
}