package com.example.topics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.topics.Adaptadores.ListadoSeguidoresAdapter;
import com.example.topics.Modelo.User;
import com.example.topics.R;
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

    private RecyclerView recyclerView;

    private FirebaseFirestore db;

    private Utilitarios utilitarios;

    private final ArrayList<User> USUARIOS = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_lista_users);
        utilitarios = new Utilitarios();
        editText = findViewById(R.id.editTextListado);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Intent intent = getIntent();
        String cadena = intent.getExtras().getString("modo");
        db = FirebaseFirestore.getInstance();
        if (cadena.equals("Seguidores")){
        }else {
            foundAllSeguidos(email);
        }
    }



    public void foundAllSeguidos(String email){
        db.collection("Social").document(email)
                .collection("Seguidos").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){

                                User u = new User();
                                u.setEmail(documentSnapshot.getId());
                                completeInformation(u);

                            }
                            if (USUARIOS.size() == 0){
                                Toast.makeText(ListaActivityUsers.this,"NO TIENES SEGUIDOS",Toast.LENGTH_LONG).show();
                            }else {
                                Log.d("Listado Total",USUARIOS.get(0).toString());
                                mostrarPosiblesResultados(USUARIOS);
                            }
                        }
                    }
                });
    }

    public void completeInformation(User u){
            db.collection("users").document(u.getEmail())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null) {
                                User u = createUser(documentSnapshot);
                                USUARIOS.add(u);
                                Log.d("Listado",USUARIOS.size()+"");
                            }else  {
                                Log.d("SEGUIDOS","ES NULL");
                            }
                        }
                    }else {
                        Log.d("SEGUIDOS","ERROR");
                    }
                }
            });
    }

    public void mostrarPosiblesResultados(ArrayList<User> usuarios){
        recyclerView = findViewById(R.id.recylerViewListado);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ListadoSeguidoresAdapter postAdapter= new ListadoSeguidoresAdapter(usuarios,this);
        recyclerView.setAdapter(postAdapter);
    }

    public User createUser(DocumentSnapshot documentSnapshot){
        User usuario = new User();
        String nameCount = documentSnapshot.getString("nameCount");
        String name = documentSnapshot.getString("name");
        String urlImagen = documentSnapshot.getString("UrlPerfil");
        String email = documentSnapshot.get("email").toString();
        usuario.setNombreCuenta(nameCount);
        usuario.setNombre(name);
        usuario.setUrlPerfil(urlImagen);
        usuario.setEmail(email);
        return usuario;
    }


}