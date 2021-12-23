package com.example.topics.Activities.Chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Adaptadores.UsersAdapter;
import com.example.topics.Modelo.User;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.PreferenceManager;
import com.example.topics.databinding.ActivityUsersChatBinding;
import com.example.topics.listeners.UserListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UsersChatActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersChatBinding binding;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getUsers();
        setListeners();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
    }

    public void getUsers(){
        db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).get()
                .addOnCompleteListener( task -> {
                    loading(false);
                    PreferenceManager preferenceManager = new PreferenceManager(this);
                    String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
                    if (task.isSuccessful() && task.getResult() != null){
                        ArrayList<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            if (currentUserEmail.equals(doc.get(Constants.KEY_EMAIL))){
                                continue;
                            }
                            User usuario = new User();
                            usuario.setEmail(doc.getString(Constants.KEY_EMAIL));
                            usuario.setUrlPerfil(doc.getString(Constants.KEY_PHOTO_PERFIL));
                            usuario.setId(doc.getString(Constants.KEY_ID_USER));
                            usuario.setNombreCuenta(doc.getString(Constants.KEY_NAME_USER));
                            usuario.setToken(doc.getString(Constants.KEY_TOKEN));
                            users.add(usuario);
                        }
                        if (users.size() > 0){
                            UsersAdapter usersAdapter = new UsersAdapter(users,this);
                            binding.usersChatsRecyclerView.setAdapter(usersAdapter);
                            binding.usersChatsRecyclerView.setVisibility(View.VISIBLE);
                        }else {
                            showError();
                        }
                    }else {
                        showError();
                    }
                });
    }

    private void showError(){
        binding.textErrorMessage.setText("No user available");
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading (boolean isLoading){
        if (isLoading){
            binding.progresBar.setVisibility(View.VISIBLE);
        }else {
            binding.progresBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User usuario) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,  usuario);
        startActivity(intent);
        finish();
    }
}