package com.example.topics.Activities.Settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Activities.Perfil;
import com.example.topics.Modelo.Post;
import com.example.topics.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PerfilOptionsActivity extends AppCompatActivity {

    private EditText changeNameCount,changeName,changeDescripcion;

    private TextView changeFoto;

    private ImageView changePhoto;

    private ImageButton submit;

    private FirebaseFirestore db;

    private static final int PICK_IMAGE_REQUEST = 71;

    private Uri filePath;

    private String idImagen;

    private StorageReference storageReference;

    private FirebaseStorage storage;

    private FirebaseAuth mAuth;

    private Post post;

    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        db = FirebaseFirestore.getInstance();
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        setContentView(R.layout.activity_ajustes_perfil);
        changeFoto = findViewById(R.id.textViewChangeFoto);
        changeName = findViewById(R.id.ChangeName);
        storage  = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        changeNameCount = findViewById(R.id.ChangeNameCount);
        changeDescripcion = findViewById(R.id.ChangeDescription);
        submit = findViewById(R.id.imageButtonSubmit);
        changePhoto = findViewById(R.id.changePhotoPerfil);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarDatos(email);
            }
        });
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        changeFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            Toast.makeText(PerfilOptionsActivity.this,filePath.getPath(),Toast.LENGTH_SHORT).show();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                changePhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void insertarPost(){
        mAuth = FirebaseAuth.getInstance();
        String rutaImagen ="/perfil/perfil";;
        db = FirebaseFirestore.getInstance();
        post = new Post(rutaImagen);
        Map<String,Object> map = new HashMap<>();
        map.put("UrlPerfil","gs://topics-2021.appspot.com/"+email+rutaImagen);
        db.collection("users").
                document(email).set(map,SetOptions.merge());
    }

    public void subirImage() {
        if (filePath != null){
            borrarImagenPerfil();
            final ProgressDialog progressDialog = new ProgressDialog (this);
            progressDialog.show ();
            StorageReference ref = storageReference.child (email+"/perfil/perfil");
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss ();
                    insertarPost();
                    Toast.makeText (PerfilOptionsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show ();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss ();
                    Toast.makeText(PerfilOptionsActivity.this,"Error",Toast.LENGTH_SHORT).show();
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

    public void borrarImagenPerfil(){
        StorageReference ref = storageReference.child (email+"/perfil/perfil");
        ref.delete();
    }

    public void chooseImage() {
        Intent intent = new Intent ();
        intent.setType ("image/*");
        intent.setAction (Intent.ACTION_GET_CONTENT);
        startActivityForResult (Intent.createChooser (intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }



    public void comprobarDatos(String email){
        Map<String, Object> user = new HashMap<>();
        if (!changeName.getText().toString().isEmpty()){
            String newName = changeName.getText().toString();
            user.put("name", newName);
        }
        if (!changeNameCount.getText().toString().isEmpty()){
            String newCountName = changeNameCount.getText().toString();
            user.put("nameCount", newCountName);
        }
        if (!changeDescripcion.getText().toString().isEmpty()){
            String description = changeDescripcion.getText().toString();
            user.put("descripcion", description);
        }
        if (filePath != null){
            subirImage();
        }
        changeSettingsProfile(user,email);
    }

    public void changeSettingsProfile(Map<String,Object> user, String email){
        db.collection("users")
                .document(email).set(user, SetOptions.merge());
        Intent returnPerfil = new Intent(this, Perfil.class);
        startActivity(returnPerfil);
    }
}