package com.example.topics.Activities.AccesCount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Modelo.Post;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubirValidacion extends AppCompatActivity {

    private ImageView dni, rostro;

    private Uri filePath, filePathDNI,filePathrostro;

    private FirebaseFirestore db;

    private static final int PICK_IMAGE_REQUEST = 71;

    private String idImagen;

    private ImageButton imageButton;

    private StorageReference storageReference;

    private FirebaseStorage firebaseStorage;

    private FirebaseAuth mAuth;

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_subir_validacion);
        init();
        setListeners();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();
                if (providerId.equals("google.com")){
                    // Name, email address, and profile photo Url
                    String name = profile.getDisplayName();
                    String email = profile.getEmail();
                    Uri photoUrl = profile.getPhotoUrl();
                    Log.d("Prueba", photoUrl.toString());
                    User usuario = new User();
                    usuario.setId(mAuth.getCurrentUser().getUid());
                    usuario.setNombre(name);
                    usuario.setEmail(email);
                    usuario.setUrlPerfil(photoUrl.toString());
                    insertUserGoogle(usuario);
                }
            }
        }
        }


    public void insertUserGoogle(User usuario){
        Utilitarios utilitarios = new Utilitarios();
        HashMap<String, Object> user = utilitarios.userToHashMapUsers(usuario);
        db.collection(Constants.KEY_COLLECTION_USERS).document(usuario.getId()).set(user, SetOptions.merge());
    }

    private void setListeners(){
        imageButton.setOnClickListener(view -> {
            String email = mAuth.getCurrentUser().getEmail();
            comprobarDatos(email);
            subirImage(filePathDNI,"Dni");
            subirImage(filePathrostro,"Rostro");
            goToLogin();
        });
        rostro.setOnClickListener(view -> {
            chooseImage("rostro");
        });
        dni.setOnClickListener(view -> {
            chooseImage("dni");
        });
    }

    private void init(){
        UserInfo userInfo = mAuth.getCurrentUser();
        rostro = findViewById(R.id.imageViewescogerRostro);
        imageButton = findViewById(R.id.checkAuth);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        dni = findViewById(R.id.imageViewEscogerDNI);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }


    public void subirImage(Uri uri, String text) {
        if (uri != null){
            mAuth = FirebaseAuth.getInstance();
            String email = mAuth.getCurrentUser().getEmail();
            final ProgressDialog progressDialog = new ProgressDialog (this);
            progressDialog.show ();
            idImagen = UUID.randomUUID ().toString ();
            StorageReference ref = storageReference.child(email+"/auth/"+ idImagen);
            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss ();
                    insertarPost(idImagen,text);
                    Toast.makeText (SubirValidacion.this, "Uploaded", Toast.LENGTH_SHORT).show ();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss ();
                    Toast.makeText(SubirValidacion.this,"Error",Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred () / snapshot.getTotalByteCount ());
                    progressDialog.setMessage ("Uploaded" + (int) progress + "%");
                }
            });
        }
    }

    public void goToLogin(){
        Intent intent = new Intent(this,NoHabilitadoActivity.class);
        startActivity(intent);
    }

    public void chooseImage(String filePath) {
        Intent intent = new Intent ();
        intent.putExtra("foto", filePath);
        intent.setType ("image/*");
        intent.setAction (Intent.ACTION_GET_CONTENT);
        startActivityForResult (Intent.createChooser (intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }

    public void comprobarDatos(String email){
        Map<String, Object> user = new HashMap<>();
        Log.d("URL DNI",filePathDNI.toString());
        if (filePathDNI != null){
            String url = filePathDNI.toString();
            user.put(Constants.KEY_DNI,url);
        }
        Log.d("URL ROSTRO",filePathrostro.toString());
        if (filePathrostro != null){
            String url = filePathrostro.toString();
            user.put(Constants.KEY_ROSTRO,url);
        }
        db.collection("inhabilitados")
                .document(email).set(user, SetOptions.merge());
    }

    public void obtenerURL(StorageReference gs, String text){
        String email = mAuth.getCurrentUser().getEmail();
        gs.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Map<String, Object> dataUsuario = new HashMap<>();
                dataUsuario.put("Url" + text, uri.toString());
                db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(email).set(dataUsuario,SetOptions.merge());
            }
        });
    }

    public void insertarPost(String idImagen, String text){
        mAuth = FirebaseAuth.getInstance();
        String rutaImagen ="/auth/" + idImagen;;
        db = FirebaseFirestore.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        FirebaseStorage storage  = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://topics-2021.appspot.com/"+email+rutaImagen);
        obtenerURL(gsReference, text);
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            Log.d("URL", data.toString());
            Toast.makeText(SubirValidacion.this,filePath.getPath(),Toast.LENGTH_SHORT).show();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Log.d("URL",filePath.toString());
                if (hasImage(dni)){
                    filePathrostro = filePath;
                    rostro.setImageBitmap(bitmap);
                }else {
                    filePathDNI = filePath;
                    dni.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}