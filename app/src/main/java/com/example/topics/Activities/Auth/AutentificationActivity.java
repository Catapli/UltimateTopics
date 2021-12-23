package com.example.topics.Activities.Auth;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.topics.Adaptadores.AdapterCardViewAuth;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AutentificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private FirebaseFirestore db;

    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        users = new ArrayList<>();
        setContentView(R.layout.activity_autentification);
        recyclerView = findViewById(R.id.recyclerViewAuth);
        foundAllInhabilitados();
    }

    public void inicializarElementosOrdenCronologico(){
        recyclerView = findViewById(R.id.recyclerViewAuth);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterCardViewAuth authAdapter= new AdapterCardViewAuth(users,this);
        recyclerView.setAdapter(authAdapter);
    }

    public void foundAllInhabilitados(){
        db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_INHABILITADOS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User u = queryToUser(document);
                            Log.d("VALIDATION",u.toString());
                            users.add(u);
                        }
                        Log.d("VALIDATION","PRUEBA");
                        inicializarElementosOrdenCronologico();

                }
            }
        });
    }

    public User queryToUser(DocumentSnapshot doc){
        User usuario = new User();
        String url = doc.get(Constants.KEY_PHOTO_PERFIL).toString();
        usuario.setUrlPerfil(url);
        String email = doc.get(Constants.KEY_EMAIL).toString();
        usuario.setEmail(email);
        String fecha = doc.get(Constants.KEY_DATE).toString();
        usuario.setFecha(fecha);
        String urlDni = doc.get(Constants.KEY_DNI).toString();
        usuario.setUrlDni(urlDni);
        String urlRostro = doc.get(Constants.KEY_ROSTRO).toString();
        usuario.setUrlRostro(urlRostro);
        boolean habilitado = doc.getBoolean(Constants.KEY_HABILITADO);
        usuario.setHabilitado(habilitado);
        String id = doc.get(Constants.KEY_ID_USER).toString();
        usuario.setId(id);
        String name = doc.get(Constants.KEY_NAME).toString();
        usuario.setNombre(name);
        String nameCount = doc.get(Constants.KEY_NAME_USER).toString();
        usuario.setNombreCuenta(nameCount);
        String password = doc.get(Constants.KEY_PASSWORD).toString();
        usuario.setPassword(password);
        String token = doc.get(Constants.KEY_TOKEN).toString();
        usuario.setToken(token);
        return usuario;
    }


}