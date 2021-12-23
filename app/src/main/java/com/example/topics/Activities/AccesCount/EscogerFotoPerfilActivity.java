package com.example.topics.Activities.AccesCount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Modelo.Post;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
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

public class EscogerFotoPerfilActivity extends AppCompatActivity {

    private ImageView imagenperfil;

    private Uri filePath;

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    private Post post;

    private String idImagen;

    private ImageButton buttonAccept, masTarde;

    private StorageReference storageReference;

    private FirebaseStorage firebaseStorage;

    private static final int PICK_IMAGE_REQUEST = 71;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_escoger_foto_perfil);
        init();
        setListeners();
    }

    private void init(){
        imagenperfil = findViewById(R.id.imageViewFotoPerfil);
        buttonAccept = findViewById(R.id.ButtonFotoPerfilAccept);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        masTarde = findViewById(R.id.ButtonEscogerFotoMasTarde);
        storageReference = firebaseStorage.getReference();
    }

    private void setListeners(){
        imagenperfil.setOnClickListener(view -> chooseImage());
        buttonAccept.setOnClickListener(view -> subirImage());
        masTarde.setOnClickListener(view -> {
            Map<String, Object> dataUsuario = new HashMap<>();
            dataUsuario.put(Constants.KEY_PHOTO_PERFIL, "no");
            String id = mAuth.getCurrentUser().getUid();
            db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(id)
                    .set(dataUsuario, SetOptions.merge());
            goToAuth();
        });
    }


    public void chooseImage() {
        Intent intent = new Intent ();
        intent.setType ("image/*");
        intent.setAction (Intent.ACTION_GET_CONTENT);
        startActivityForResult (Intent.createChooser (intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }
    public void subirImage() {
        if (filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog (this);
            progressDialog.show ();
            String id = mAuth.getCurrentUser().getUid();
            StorageReference ref = storageReference.child(id +"/perfil/perfil");
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss ();
                    insertarPost();
                    Toast.makeText (EscogerFotoPerfilActivity.this, "Uploaded", Toast.LENGTH_SHORT).show ();
                    goToAuth();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss ();
                    Toast.makeText(EscogerFotoPerfilActivity.this,"Error",Toast.LENGTH_SHORT).show();
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



    public void goToAuth(){
        Intent intent = new Intent(this, SubirValidacion.class);
        startActivity(intent);
    }

    public void insertarPost(){
        mAuth = FirebaseAuth.getInstance();
        String rutaImagen ="/perfil/perfil" ;
        String id = mAuth.getCurrentUser().getUid();
        HashMap<String, Object> urlPerfil = new HashMap<>();
        urlPerfil.put(Constants.KEY_PHOTO_PERFIL,"gs://topics-2021.appspot.com/"+id+rutaImagen);
        db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(id).set(urlPerfil,SetOptions.merge());

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            Toast.makeText(EscogerFotoPerfilActivity.this,filePath.getPath(),Toast.LENGTH_SHORT).show();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imagenperfil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}