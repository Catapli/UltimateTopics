package com.example.topics.Utilidades;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Locale;

public class Api {

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    private FirebaseUser firebaseUser;

    public Api() {
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
    }

    private void notificationToken (String id){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        Log.d("TOKEN","LLEGO");
                        // Get new FCM registration token
                        String token = task.getResult();
                        HashMap<String,Object> tokenData = new HashMap<>();
                        tokenData.put(Constants.KEY_TOKEN,token);
                        db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(id).set(tokenData, SetOptions.merge());
                    }
                });
    }

    public void hasToken(String id){
        db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("TOKEN", "busqueda completa");
                    DocumentSnapshot doc = task.getResult();
                    try {
                        String token = doc.getString("token");
                        Log.d("TOKEN", "campo token =" + token);
                        if (token == null || token.isEmpty()){
                            notificationToken(id);
                        }
                    }catch (NullPointerException e){
                        e.getMessage();
                    }
                }
            }
        });
    }

    public void insertUserInhabilitados(HashMap<String,Object> user){
        db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(mAuth.getCurrentUser().getUid())
                .set(user,SetOptions.merge());

    }


    public void ifExistsEmailInInhabilitadosRegister(EditText editText, TextView textView){
        textView.setText("");
        String texto = editText.getText().toString().toLowerCase(Locale.ROOT);
        if (texto.length()>2){
            db.collection("inhabilitados").document(texto).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){
                            textView.setText("El correo ya esta asignado a otra cuenta");
                            textView.setTextColor(Color.RED);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }else {
            textView.setText("El formato para el email es incorrecto");
            textView.setTextColor(Color.RED);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void ifExistsEmailInUsersRegister(EditText editText, TextView textView){
        textView.setText("");
        String texto = editText.getText().toString().toLowerCase(Locale.ROOT);
        if (texto.length()>2){
            db.collection("users").document(texto).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (!task.getResult().exists()){
                            ifExistsEmailInInhabilitadosRegister(editText,textView);
                        }else {
                            textView.setText("El correo ya esta asignado a otra cuenta");
                            textView.setTextColor(Color.RED);
                            textView.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                }
            });
        }else {
            textView.setText("El formato para el email es incorrecto");
            textView.setTextColor(Color.RED);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void ifExistsEmailInInhabilitadosLogin(EditText editText, TextView textView){
        textView.setText("");
        String texto = editText.getText().toString().toLowerCase(Locale.ROOT);
        if (texto.length()>2){
            db.collection(Constants.KEY_COLLECTION_INHABILITADOS).whereEqualTo(Constants.KEY_EMAIL, texto).get().addOnCompleteListener( task ->  {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        textView.setText("El correo no existe");
                        textView.setTextColor(Color.RED);
                        textView.setVisibility(View.VISIBLE);
                    }else {
                        Log.d("REGISTER","Existe en inhabilitados");
                        return;
                    }
                }
            });
        }else {
            textView.setText("El formato para el email es incorrecto");
            textView.setTextColor(Color.RED);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void ifExistsEmailInUsersLogin(EditText editText, TextView textView){
        textView.setText("");
        String texto = editText.getText().toString().toLowerCase(Locale.ROOT);
        if (texto.length()>2){
            db.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_EMAIL, texto).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        ifExistsEmailInInhabilitadosLogin(editText,textView);
                    }else {
                        Log.d("REGISTER","Existe en users");
                        return;
                    }
                }
            });
        }else {
            textView.setText("El formato para el email es incorrecto");
            textView.setTextColor(Color.RED);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void changeEmailVerifiedInhabilitados(String id){
        HashMap<String,Object> check = new HashMap<>();
        check.put(Constants.KEY_EMAIL_VERIFIED,true);
        db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(id).set(check,SetOptions.merge());
    }

    public void countSeguidores(String id, TextView textView){
        db.collection(Constants.KEY_COLLECTION_SEGUIDORES)
                .whereEqualTo(Constants.KEY_ID_CUENTA, id)
                .get().addOnCompleteListener(task -> {
            int count = 0;
            if (task.isSuccessful()){
                if (!task.getResult().isEmpty()){
                    count = task.getResult().getDocuments().size();
                    textView.setText(String.valueOf(count));
                }else {
                    textView.setText(String.valueOf(count));
                }
            }
        });
    }
    public void countSeguidos(String id, TextView textView){
        db.collection(Constants.KEY_COLLECTION_SEGUIDORES)
                .whereEqualTo(Constants.KEY_ID_SEGUIDOR, id)
                .get().addOnCompleteListener(task -> {
            int count = 0;
            if (task.isSuccessful()){
                if (!task.getResult().isEmpty()){
                    count = task.getResult().getDocuments().size();
                    textView.setText(String.valueOf(count));
                }else {
                    textView.setText(String.valueOf(count));
                }
            }
        });
    }





}
