package com.example.topics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.topics.Activities.Settings.PerfilOptionsActivity;
import com.example.topics.Activities.Settings.SettingActivity;
import com.example.topics.Activities.Top.TopActivity;
import com.example.topics.Activities.Upload.UploadImage;
import com.example.topics.Adaptadores.PostAdapter;
import com.example.topics.Adaptadores.PostsAdapterCronologico;
import com.example.topics.Modelo.Post;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Api;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Perfil extends AppCompatActivity {

    private ImageView imagenPerfil;

    private RecyclerView recyclerView;

    private final String TAG = "PERFIL";

    private FirebaseUser firebaseUser;


    private FirebaseStorage storage;

    private StorageReference storageReference;

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    private TextView nombreCuenta, numPosts, numSeguidores, numSeguidos, nombre, descripcion;

    private ImageButton galeria, postsCronologicos, settings,changeProfile;

    private SwipeRefreshLayout mySwipeRefreshLayout ;

    private User user;

    private Utilitarios utilitarios;

    private PreferenceManager preferenceManager;

    private View home, search, profile, top;

    private FloatingActionButton add;

    private Api api;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_perfil);
        init();
        setListeners();
        String id = preferenceManager.getString(Constants.KEY_ID_USER);
        readData(id);
        foundAllPosts(1);
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        utilitarios = new Utilitarios(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        imagenPerfil = findViewById(R.id.imageProfile);
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = new User();
        changeProfile = findViewById(R.id.buttonEditPerfil);
        mySwipeRefreshLayout = findViewById(R.id.refreshPage);
        api = new Api();

        galeria = findViewById(R.id.buttonMostrarGaleria);
        postsCronologicos = findViewById(R.id.buttonMostrarPosts);
        settings = findViewById(R.id.imageSettingsPerfil);
        add = findViewById(R.id.add);
        home = findViewById(R.id.menuHome);
        search = findViewById(R.id.menuSearch);
        top = findViewById(R.id.menuStats);
        profile = findViewById(R.id.menuPerson);

        //user information
        numPosts = findViewById(R.id.NumPosts);
        numSeguidores = findViewById(R.id.NumSeguidores);
        numSeguidos = findViewById(R.id.NumSeguidos);
        recyclerView = findViewById(R.id.Recycler_view);
        nombre = findViewById(R.id.NombrePerfil);
        nombreCuenta = findViewById(R.id.nameUsuario);
        descripcion = findViewById(R.id.DescripcionPerfil);

    }


    private void setListeners(){
        settings.setOnClickListener( view -> {
            startActivity(new Intent(getApplicationContext(), SettingActivity.class));
        });
        numSeguidores.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ListaActivityUsers.class).putExtra("modo", Constants.KEY_SEGUIDORES));
        });
        numSeguidos.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ListaActivityUsers.class).putExtra("modo", Constants.KEY_SEGUIDOS));
        });
        postsCronologicos.setOnClickListener( view -> {
            foundAllPosts(2);
        });
        galeria.setOnClickListener(view -> {
            foundAllPosts(1);
        });
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readData(preferenceManager.getString(Constants.KEY_ID_USER));
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });
        changeProfile.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), PerfilOptionsActivity.class));
        });
        home.setOnClickListener(view -> {
            startActivity(new Intent(this,PrincipalActivity.class));
        });
        top.setOnClickListener(view -> {
            startActivity(new Intent(this, TopActivity.class));
        });
        search.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchActivity.class));
        });
        add.setOnClickListener(view -> {
            startActivity(new Intent(this, UploadImage.class));
        });
    }






    public void inicializarElementosOrdenCronologico(ArrayList<Post> posts){
        String nombreUsuario = nombreCuenta.getText().toString();
        numPosts.setText(String.valueOf(posts.size()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PostsAdapterCronologico postAdapter= new PostsAdapterCronologico(posts,this,nombreUsuario);
        recyclerView.setAdapter(postAdapter);
    }


    public void inicializarElementosGridLayout(ArrayList<Post> posts){
        numPosts.setText(String.valueOf(posts.size()));
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        PostAdapter postAdapter = new PostAdapter(posts,this);
        recyclerView.setAdapter(postAdapter);
    }

    public void foundAllPosts(int num){
        db.collection(Constants.KEY_COLLECTION_POSTS)
                .whereEqualTo(Constants.KEY_ID_CUENTA, preferenceManager.getString(Constants.KEY_ID_USER))
                .orderBy(Constants.KEY_DATE, Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DOCUMENTS", task.getResult().getDocuments().size()+"");
                ArrayList<Post> posts = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Post post = createPost(document);
                    posts.add(post);
                }
                if (num == 1){
                    inicializarElementosGridLayout(posts);
                }else if (num == 2){
                    inicializarElementosOrdenCronologico(posts);
                }

            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }

        });
    }

    public Post createPost(QueryDocumentSnapshot documentSnapshot){
        Post post = new Post();
        String id = preferenceManager.getString(Constants.KEY_ID_USER);
        String url = documentSnapshot.getString(Constants.KEY_RUTA_POST);
        String descripcion = documentSnapshot.getString(Constants.KEY_DESCRIPCION);
        post.setDescripcion(descripcion);
        post.setIdUser(id);
        post.setUrlImagen(url);
        return post;
    }









    public void readData(String id){
        db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        completeUser(documentSnapshot);
                        nombreCuenta.setText(user.getNombreCuenta());
                        nombre.setText(user.getNombre());
                        descripcion.setText(user.getDescripcion());
                        api.countSeguidores(id, numSeguidores);
                        api.countSeguidos(id, numSeguidos);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
    public void completeUser(DocumentSnapshot document) {
        user.setUrlPerfil(document.get(Constants.KEY_PHOTO_PERFIL).toString());
        user.setNombreCuenta(document.get(Constants.KEY_NAME_USER).toString());
        user.setEmail(document.get(Constants.KEY_EMAIL).toString());
        user.setNombre(document.get(Constants.KEY_NAME).toString());
        user.setDescripcion(document.get(Constants.KEY_DESCRIPCION).toString());
        if (user.getUrlPerfil().equals("no")){
            StorageReference userReference = storage.getReference();
            StorageReference perfilDefault = userReference.child("default.jpg");
            Glide.with(this).load(perfilDefault).into(imagenPerfil);
         }else{
            Glide.with(this).load(user.getUrlPerfil()).into(imagenPerfil);
        }


    }



}