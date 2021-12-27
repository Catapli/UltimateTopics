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
import com.example.topics.Activities.Dialogos.DesuscribirseDialogFragment;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerfilAjeno extends AppCompatActivity {
    private ImageView imagenPerfil;

    private RecyclerView recyclerView;

    private final String TAG = "PERFIL";

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    private FirebaseStorage storage;

    private TextView nombreCuenta, numPosts, numSeguidores, numSeguidos, nombre, descripcion;

    private ImageButton galeria, postsCronologicos, seguir;

    private User user;

    private Utilitarios utilitarios;

    private SwipeRefreshLayout mySwipeRefreshLayout ;

    private PreferenceManager preferenceManager;

    private View home, search, profile, top;

    private FloatingActionButton add;

    private Api api;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_perfil_ajeno);
        init();
        Intent intent = getIntent();
        String id = intent.getExtras().getString(Constants.KEY_ID_USER);
        setListeners(id);
        readData(id);
        foundAllPosts(id,1);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });
        listenChangesProfile();
    }

    private void init(){
        db = FirebaseFirestore.getInstance();
        imagenPerfil = findViewById(R.id.imagenPerfilAjeno);
        galeria = findViewById(R.id.buttonMostrarGaleriaAjeno);
        preferenceManager = new PreferenceManager(getApplicationContext());
        utilitarios = new Utilitarios(getApplicationContext());
        storage = FirebaseStorage.getInstance();
        user = new User();
        postsCronologicos = findViewById(R.id.buttonMostrarPostsAjeno);
        seguir = findViewById(R.id.ButtonSeguir);
        mySwipeRefreshLayout = findViewById(R.id.SwipèrefreshPerfilAjeno);
        add = findViewById(R.id.add);
        home = findViewById(R.id.menuHome);
        search = findViewById(R.id.menuSearch);
        top = findViewById(R.id.menuStats);
        profile = findViewById(R.id.menuPerson);
        api = new Api();

        // user information
        recyclerView = findViewById(R.id.Recycler_viewAjeno);
        numPosts = findViewById(R.id.NumPostsAjeno);
        nombre = findViewById(R.id.NombrePerfilAjeno);
        nombreCuenta = findViewById(R.id.nameUsuarioAjeno);
        numSeguidos = findViewById(R.id.NumSeguidosAjeno);
        numSeguidores = findViewById(R.id.NumSeguidoresAjeno);

    }

    private void setListeners(String id){
        galeria.setOnClickListener(view -> foundAllPosts(id,1));
        postsCronologicos.setOnClickListener(view -> foundAllPosts(id,2));
        seguir.setOnClickListener(view -> {
            isSeguido(id,preferenceManager.getString(Constants.KEY_ID_USER));
        });
        home.setOnClickListener(view -> {
            startActivity(new Intent(this, PrincipalActivity.class));
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
        profile.setOnClickListener(view -> {
            startActivity(new Intent(this, Perfil.class));
        });
    }
    public void isSeguido(String idCuenta, String idUser){
        db.collection(Constants.KEY_COLLECTION_SEGUIDORES)
                .whereEqualTo(Constants.KEY_ID_CUENTA, idCuenta)
                .whereEqualTo(Constants.KEY_ID_SEGUIDOR, idUser)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (task.getResult().isEmpty()){
                            insertarSeguidor(user);
                        }else {
                            DesuscribirseDialogFragment logOutDialogFragment = new DesuscribirseDialogFragment(idUser,idCuenta,PerfilAjeno.this);
                            logOutDialogFragment.show(getSupportFragmentManager(),"logOut");
                            refresh();
                        }
                    }
        });
    }


    public void sumarSeguidor(){
            sumarSeguidorTop();
    }



    public void sumarSeguidorTop(){
        int seguidores = Integer.parseInt(numSeguidores.getText().toString());
        Map<String, Object> dataUsuario = new HashMap<>();
        dataUsuario.put(Constants.KEY_TOP_SEMANAL, String.valueOf(seguidores+1));
        dataUsuario.put(Constants.KEY_TOP_DIARIO, String.valueOf(seguidores+1));
        dataUsuario.put(Constants.KEY_TOP_MENSUAL, String.valueOf(seguidores+1));
        dataUsuario.put(Constants.KEY_SEGUIDORES_TOTALES, String.valueOf(seguidores+1));
        db.collection(Constants.KEY_COLLECTION_TOP)
                .document(user.getId())
                .set(dataUsuario, SetOptions.merge());
    }

    public User completeUser(DocumentSnapshot document) {
        user.setNombre(document.getString(Constants.KEY_NAME));
        user.setNombreCuenta(document.getString(Constants.KEY_NAME_USER));
        user.setEmail(document.getString(Constants.KEY_EMAIL));
        user.setUrlPerfil(document.getString(Constants.KEY_PHOTO_PERFIL));
        user.setId(document.getString(Constants.KEY_ID_USER));
        if (user.getUrlPerfil().equals("no")){
            StorageReference userReference = storage.getReference();
            StorageReference perfilDefault = userReference.child("default.jpg");
            Glide.with(this).load(perfilDefault).into(imagenPerfil);
        }else{
            Glide.with(this).load(user.getUrlPerfil()).into(imagenPerfil);
        }
        return user;
    }

    public void refresh(){
        readData(user.getId());
    }




    public void insertarSeguidor(User userCount){
            sumarSeguidor();
            Map<String,Object> dataUser = new HashMap<>();
            dataUser.put(Constants.KEY_ID_CUENTA, userCount.getId());
            dataUser.put(Constants.KEY_IMAGE_CUENTA, userCount.getUrlPerfil());
            dataUser.put(Constants.KEY_ID_SEGUIDOR, preferenceManager.getString(Constants.KEY_ID_USER));
            dataUser.put(Constants.KEY_IMAGE_SEGUIDOR, preferenceManager.getString(Constants.KEY_PHOTO_PERFIL));
            dataUser.put(Constants.KEY_NAME_USER_SEGUIDOR, preferenceManager.getString(Constants.KEY_NAME_USER));
            dataUser.put(Constants.KEY_NAME_USER_CUENTA, userCount.getNombreCuenta());
            dataUser.put(Constants.KEY_TIMESTAMP, new Date());
            db.collection(Constants.KEY_COLLECTION_SEGUIDORES).add(dataUser);
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
        recyclerView = findViewById(R.id.Recycler_viewAjeno);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        PostAdapter postAdapter = new PostAdapter(posts,this);
        recyclerView.setAdapter(postAdapter);
    }

    public void foundAllPosts(String id,int num){
        db.collection(Constants.KEY_COLLECTION_POSTS)
                .whereEqualTo(Constants.KEY_ID_CUENTA, id)
                .orderBy(Constants.KEY_DATE, Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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
                    }
                });
    }

    public Post createPost(QueryDocumentSnapshot documentSnapshot){
        Post post = new Post();
        String id = user.getId();
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
                    user = completeUser(documentSnapshot);
                    nombreCuenta.setText(user.getNombreCuenta());
                    nombre.setText(user.getNombre());
                    api.countSeguidos(user.getId(), numSeguidos);
                    api.countSeguidores(user.getId(), numSeguidores);

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void listenChangesProfile(){
        db.collection(Constants.KEY_COLLECTION_SEGUIDORES)
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) ->{
        if (error != null){
            return;
        }
        if (value != null){
            int seguidoresInt;
            int seguidosInt;
            String seguidores =numSeguidores.getText().toString() ;
            String seguidos = numSeguidos.getText().toString();
            for (DocumentChange doc : value.getDocumentChanges()){
                Log.d("PRUEBA", doc.toString());
                switch (doc.getType()) {
                    case ADDED:
                        if (doc.getDocument().get(Constants.KEY_ID_CUENTA).equals(user.getId())){
                            seguidoresInt = Integer.parseInt(seguidores)+1;
                            numSeguidores.setText(String.valueOf(seguidoresInt));
                        }
                        if (doc.getDocument().get(Constants.KEY_ID_SEGUIDOR).equals(user.getId())){
                            seguidosInt = Integer.parseInt(seguidos)+1;
                            numSeguidos.setText(String.valueOf(seguidosInt));
                        }
                        break;
                    case REMOVED:
                        if (doc.getDocument().get(Constants.KEY_ID_CUENTA).equals(user.getId())){
                            seguidoresInt = Integer.parseInt(seguidores)-1;
                            numSeguidores.setText(String.valueOf(seguidoresInt));
                        }
                        if (doc.getDocument().get(Constants.KEY_ID_SEGUIDOR).equals(user.getId())){
                            seguidosInt = Integer.parseInt(seguidos)-1;
                            numSeguidos.setText(String.valueOf(seguidosInt));
                        }
                        break;
                }
            }
        }
    };





}