package com.example.topics.Activities.Upload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Activities.PrincipalActivity;
import com.example.topics.Modelo.Post;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.PreferenceManager;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

public class UploadImage extends AppCompatActivity {

    private ImageButton cargarImagen;

    private String idImagen;

    private ImageView imagen;

    private Uri filePath;

    private StorageReference storageReference;

    private FirebaseStorage firebaseStorage;

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;


    private EditText editTextDescripcion;

    private Post post;

    private Utilitarios utilitarios;


    private static final int SELECT_PICTURE = 1;

    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_upload_image);
        init();
        setListeners();
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[] { takePhotoIntent }
                );

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    private void init(){

        cargarImagen = findViewById(R.id.buttonSubirImage);
        imagen = findViewById(R.id.imageView);
        firebaseStorage = FirebaseStorage.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        storageReference = firebaseStorage.getReference();
        mAuth = FirebaseAuth.getInstance();
         utilitarios= new Utilitarios(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference().child(id);
    }

    private void setListeners(){
        cargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImage();
            }
        });

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && data != null ){
            if (data.getData() != null){
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imagen.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imagen.setImageBitmap(imageBitmap);
            }




        }
    }


    public void subirImage() {
        if (filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog (this);
            progressDialog.show ();
            idImagen = UUID.randomUUID ().toString ();
            StorageReference ref = storageReference.child ("images/"+ idImagen);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss ();
                    insertarPost();
                    Toast.makeText (UploadImage.this, "Uploaded", Toast.LENGTH_SHORT).show ();
                    returnPrincipal();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss ();
                    Toast.makeText(UploadImage.this,"Error",Toast.LENGTH_SHORT).show();
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

    public void insertarPost(){
        mAuth = FirebaseAuth.getInstance();
        String rutaImagen ="/images/" + idImagen;
        editTextDescripcion = findViewById(R.id.EditDescripcion);
        String  descripcion = editTextDescripcion.getText().toString();
        String id = preferenceManager.getString(Constants.KEY_ID_USER);
        FirebaseStorage storage  = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl(Constants.KEY_URL_ROOT_FIREBASE+id+rutaImagen);
        post = new Post();
        post.setDescripcion(descripcion);
        HashMap<String,Object> map = utilitarios.createMapfromPost(post);
        db.collection(Constants.KEY_COLLECTION_POSTS).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                obtenerURL(gsReference, documentReference.getId());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("prueba","no funcionó");
            }
        });
    }



    public void obtenerURL(StorageReference gs, String id){
        gs.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Map<String, Object> data = new HashMap<>();
                data.put(Constants.KEY_RUTA_POST, uri.toString());
                db.collection(Constants.KEY_COLLECTION_POSTS).document(id)
                        .set(data, SetOptions.merge());
            }
        });

    }

    public void returnPrincipal(){
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }






}