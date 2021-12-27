package com.example.topics.Activities.AccesCount;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Activities.PrincipalActivity;
import com.example.topics.R;
import com.example.topics.Utilidades.Api;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.Utilitarios;
import com.example.topics.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "Login";

    private EditText email;

    private TextView errorName, errorPassword;

    private EditText password;

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    private ActivityLoginBinding binding;

    private CheckBox checkBox;

    private FirebaseUser firebaseUser;

    private Utilitarios utilitarios;

    private Api api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utilitarios = new Utilitarios(getApplicationContext());
        api = new Api();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        errorName = findViewById(R.id.errorNameCountLogin);
        errorPassword = findViewById(R.id.errorPasswordLogin);
        checkBox = findViewById(R.id.checkboxLogin);
        email = findViewById(R.id.EditTextCorreo);
        password = findViewById(R.id.EditTextContraseña);
        password = findViewById(R.id.EditTextContraseña);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox.isChecked()) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (email.getText().toString().length() > 0) {
                    api.ifExistsEmailInUsersLogin(email, errorName);
                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (utilitarios.isVacioEditableEmail(editable, email.getHint().toString(), errorName)) {
                    api.ifExistsEmailInUsersLogin(email, errorName);
                }
            }
        });

    }

    private void init(){

    }

    private void updateUI(FirebaseUser user) {
        firebaseUser = user;
    }

    private void accederPrincipal() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }

    private void loading (boolean isLoading){
        if (isLoading){
            binding.progresBar.setVisibility(View.VISIBLE);
            binding.imageButton2.setVisibility(View.INVISIBLE);
        }else {
            binding.progresBar.setVisibility(View.INVISIBLE);
            binding.imageButton2.setVisibility(View.VISIBLE);
        }
    }


    public void validarDatos() {
        loading(true);
        if (utilitarios.comprobarDatos(email, password, errorPassword, errorName)) {
            String e = email.getText().toString().toLowerCase(Locale.ROOT);
            String p = password.getText().toString();
            mAuth.signInWithEmailAndPassword(e, p)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user.isEmailVerified()){
                                    api.changeEmailVerifiedInhabilitados(mAuth.getCurrentUser().getUid());
                                }
                                Log.d("Login", String.valueOf(user.isEmailVerified()));
                                loginUser(e, errorName);
                            } else {
                                loading(false);
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                errorPassword.setText(task.getException().getMessage());
                                errorPassword.setVisibility(View.VISIBLE);
                                errorPassword.setTextColor(Color.RED);
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("FALLO", "HA FALLADO");
                }
            });
        }else {
            loading(false);
        }
    }



    public void loginUser(String text, TextView textView) {
        if (text.length() > 3) {
            db.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_EMAIL, text).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            loading(false);
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            if (doc.get(Constants.KEY_HABILITADO).toString().equals("false")) {
                                Intent intent = new Intent(LoginActivity.this, NoHabilitadoActivity.class);
                                intent.putExtra(Constants.KEY_EMAIL, text);
                                startActivity(intent);
                            } else if (doc.get(Constants.KEY_EMAIL_VERIFIED).toString().equals("false")) {
                                Intent intent = new Intent(LoginActivity.this, EmailVerificationActivity.class);
                                intent.putExtra(Constants.KEY_EMAIL, text);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                                intent.putExtra(Constants.KEY_EMAIL, text);
                                startActivity(intent);
                            }
                        } else {
                            loading(false);
                            loginUserInhabilitado(text,textView);
                        }
                    }
                }
            });
        } else {
            textView.setText("El formato es incorrecto");
            textView.setTextColor(Color.RED);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void loginUserInhabilitado(String email, TextView textView) {
        db.collection(Constants.KEY_COLLECTION_INHABILITADOS).whereEqualTo(Constants.KEY_EMAIL, email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (!task.getResult().isEmpty()){
                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    if (doc.get(Constants.KEY_EMAIL_VERIFIED).toString().equals("false")) {
                        Intent intent = new Intent(LoginActivity.this, EmailVerificationActivity.class);
                        intent.putExtra(Constants.KEY_EMAIL, email);
                        startActivity(intent);
                    } else if (!doc.contains(Constants.KEY_PHOTO_PERFIL)) {
                        Intent intent = new Intent(LoginActivity.this, EscogerFotoPerfilActivity.class);
                        intent.putExtra(Constants.KEY_EMAIL, email);
                        startActivity(intent);
                    }else if (!doc.contains(Constants.KEY_DNI)){
                        Intent intent = new Intent(LoginActivity.this, SubirValidacion.class);
                        intent.putExtra(Constants.KEY_EMAIL, email);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(LoginActivity.this, NoHabilitadoActivity.class);
                        intent.putExtra(Constants.KEY_EMAIL, email);
                        startActivity(intent);
                    }
                }

            }else {
                textView.setText("Este correo no existe");
                textView.setTextColor(Color.RED);
                textView.setVisibility(View.VISIBLE);
            }
        });
    }


}