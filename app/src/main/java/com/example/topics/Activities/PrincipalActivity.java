package com.example.topics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.topics.Activities.Chats.LobbyActivity;
import com.example.topics.Activities.Top.TopActivity;
import com.example.topics.Activities.Upload.UploadImage;
import com.example.topics.Adaptadores.AdapterPrincipal;
import com.example.topics.Modelo.Post;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.PreferenceManager;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PrincipalActivity extends AppCompatActivity {
    private static final String TAG = "Lectura";

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    private ArrayList<Post> posts;

    private View home, search, profile, top;

    private RecyclerView recyclerView;

    private Utilitarios utilitarios;

    private GestureDetector gesture;

    private PreferenceManager preferenceManager;

    private FloatingActionButton add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_principal);
        init();
        setListeners();
        String id = preferenceManager.getString(Constants.KEY_ID_USER);
        foundAllSeguidos(id);
        readData(id);
    }

    private void setListeners(){
        search.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchActivity.class));
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

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        posts = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        utilitarios = new Utilitarios();
        FirebaseUser user = mAuth.getCurrentUser();
        mAuth.updateCurrentUser(user);
        add = findViewById(R.id.add);
        home = findViewById(R.id.menuHome);
        search = findViewById(R.id.menuSearch);
        top = findViewById(R.id.menuStats);
        profile = findViewById(R.id.menuPerson);
        recyclerView = findViewById(R.id.RecyclerViewPrincipal);
        gesture = new GestureDetector(this, new EscuchaTextos());
    }

    class EscuchaTextos extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e2.getX()<e1.getX()){

                Intent intent = new Intent(PrincipalActivity.this, LobbyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
            return super.onFling(e1, e2, velocityX, velocityY);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void readData(String id){
        db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    User user = completeUser(documentSnapshot);
                    guardarDatos(user);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void guardarDatos(User user){
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.putString(Constants.KEY_ID_USER, user.getId());
        preferenceManager.putString(Constants.KEY_EMAIL, user.getEmail());
        preferenceManager.putString(Constants.KEY_PHOTO_PERFIL, user.getUrlPerfil());
        preferenceManager.putString(Constants.KEY_NAME, user.getNombre());
        preferenceManager.putString(Constants.KEY_NAME_USER, user.getNombreCuenta());
    }





    public User completeUser(DocumentSnapshot document) {
        User user = new User();
        user.setId(document.getString(Constants.KEY_ID_USER));
        user.setUrlPerfil(document.getString(Constants.KEY_PHOTO_PERFIL));
        user.setNombreCuenta(document.getString(Constants.KEY_NAME_USER));
        user.setNombre(document.getString(Constants.KEY_NAME));
        user.setEmail(document.getString(Constants.KEY_EMAIL));
        return user;
    }

    public void foundAllSeguidos(String emailUser){
        db = FirebaseFirestore.getInstance();
        db.collection("Social")
                .document(emailUser).collection("Seguidos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()){
                                ArrayList<User> users = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    User u = new User();
                                    u.setEmail(document.get("email").toString());
                                    users.add(u);
                                }
                                foundAllPostsbyUser(users);
                            }else {
                                Toast.makeText(PrincipalActivity.this,"NO Hay contenido",Toast.LENGTH_LONG);
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void inicializarElementosOrdenCronologico(ArrayList<Post> posts){
        recyclerView = findViewById(R.id.RecyclerViewPrincipal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterPrincipal postAdapter= new AdapterPrincipal(posts,this);
        recyclerView.setAdapter(postAdapter);
    }

    public void foundAllPostsbyUser(ArrayList<User> users){
        ArrayList<Post> posts = new ArrayList<>();
        for (User u : users){
            db.collection("users").document(u.getEmail())
                    .collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Post post = createPost(document);
                        posts.add(post);
                    }
                    inicializarElementosOrdenCronologico(posts);
                }
            });
        }
    }

    public Post createPost(QueryDocumentSnapshot documentSnapshot){
        Post post = new Post();
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        String rutaImagen = "/"+email + documentSnapshot.get("RutaImagen").toString();
        post.setCodigoImagen(rutaImagen);
        String url = documentSnapshot.get("Url").toString();
        String descripcion = documentSnapshot.get("Descripcion").toString();
        String nameUser = documentSnapshot.get("nameCount").toString();
        post.setDescripcion(descripcion);
        post.setEmailuser(email);
        post.setUrlImagen(url);
        post.setUser(nameUser);
        return post;
    }



}