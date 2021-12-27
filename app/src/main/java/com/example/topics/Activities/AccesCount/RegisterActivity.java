package com.example.topics.Activities.AccesCount;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Adaptadores.DatePickerFragment;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Api;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "Autentificacion";


    private Button button;

    private EditText nombre,password,email,passwordRepeat,nameUser,fecha;

    private TextView errorRepeatPassword, errorName, errorEmail, errorNameUser, errorDate, errorPassword;

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    private FirebaseUser firebaseUser;

    private CheckBox checkBox;

    private Utilitarios utilitarios;

    private Api api;

    private User usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_register);
        init();
        setOnFocusChangedListener();
        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (utilitarios.isVacioEditable(editable,"Name",errorName)){
                    utilitarios.validarCampo(nombre,errorName,password.getText().toString());
                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (utilitarios.isVacioEditableEmail(editable, email.getHint().toString(),errorEmail)){
                    utilitarios.validarCampo(email,errorEmail,password.getText().toString());
                }

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (utilitarios.isVacioEditable(editable,"Password",errorPassword)){
                    utilitarios.validarCampo(password,errorPassword,password.getText().toString());
                }
            }
        });

        passwordRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (utilitarios.isVacioEditable(editable,"Repeat Password",errorRepeatPassword)){
                    utilitarios.validarCampo(passwordRepeat,errorRepeatPassword,password.getText().toString());
                }

            }
        });
        nameUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (utilitarios.isVacioEditable(editable,"Name User",errorNameUser)){
                    utilitarios.validarCampo(nameUser,errorNameUser,password.getText().toString());
                }
            }
        });
    }


    private void setOnFocusChangedListener(){
        nameUser.setOnFocusChangeListener((view, b) -> {
            if (utilitarios.isVacioEditText(nameUser,errorNameUser)){
                utilitarios.validarCampo(nameUser,errorNameUser,password.getText().toString());
            }
        });
        passwordRepeat.setOnFocusChangeListener((view, b) -> {
            if (utilitarios.isVacioEditText(passwordRepeat,errorRepeatPassword)){
                utilitarios.validarCampo(passwordRepeat,errorRepeatPassword,password.getText().toString());
            }
        });
        password.setOnFocusChangeListener((view, b) -> {
            if (utilitarios.isVacioEditText(password,errorPassword)){
                utilitarios.validarCampo(password,errorPassword,password.getText().toString());
            }
        });
        email.setOnFocusChangeListener((view, b) -> {
            if (utilitarios.isVacioEditText(email,errorEmail)){
                utilitarios.validarCampo(email,errorEmail,password.getText().toString());
            }
        });
        nombre.setOnFocusChangeListener((view, b) -> {
            if (utilitarios.isVacioEditText(nombre,errorName)){
                utilitarios.validarCampo(nombre,errorName,password.getText().toString());
            }
        });
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (checkBox.isChecked()) {
                // show password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordRepeat.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // hide password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordRepeat.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        fecha.setOnClickListener(view -> {
            showDatePickerDialog();
        });
    }


    private void init(){
        utilitarios = new Utilitarios(getApplicationContext());
        api = new Api();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        nombre = findViewById(R.id.EditTextName);
        email = findViewById(R.id.EditTextEmail);
        nameUser = findViewById(R.id.EditTextNameUser);
        checkBox = findViewById(R.id.checkBoxRegistro);
        password = findViewById(R.id.EditTextPassword);
        passwordRepeat = findViewById(R.id.EditTextRepeatPassword);
        fecha = findViewById(R.id.EditTextDate);
        errorRepeatPassword = findViewById(R.id.TextViewErrorRepPassword);
        errorPassword = findViewById(R.id.TextViewErrorPassword);
        errorEmail = findViewById(R.id.TextViewErrorEmail);
        errorName = findViewById(R.id.TextViewErrorName);
        errorNameUser = findViewById(R.id.TextViewErrorNameCount);
        errorDate = findViewById(R.id.errorDate);

    }



    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                fecha.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    private void updateUI(FirebaseUser user) {
        firebaseUser = user;
    }

    public void registerComplete(){
        createUser();
        HashMap<String,Object> user = utilitarios.conversionUsuarioToHashMapInhabilitados(usuario);
        api.insertUserInhabilitados(user);
        Log.d("TOKEN","Go to token");
        api.hasToken(usuario.getId());
        Intent intent = new Intent(this,EmailVerificationActivity.class);
        intent.putExtra(Constants.KEY_EMAIL,email.getText().toString());
        intent.putExtra(Constants.KEY_PASSWORD,password.getText().toString());
        startActivity(intent);
    }


    private void createUser(){
        usuario = new User();
        usuario.setNombreCuenta(nameUser.getText().toString());
        usuario.setNombre(nombre.getText().toString());
        usuario.setEmail(email.getText().toString().toLowerCase(Locale.ROOT));
        usuario.setPassword(password.getText().toString());
        usuario.setFecha(fecha.getText().toString());
        usuario.setId(mAuth.getCurrentUser().getUid());
    }

    public void login(View view) {
        if (comprobarTodos()){
                String e = email.getText().toString();
                String p = password.getText().toString();
                mAuth.createUserWithEmailAndPassword(e, p)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                    Toast.makeText(RegisterActivity.this, "Authentication Correctly.",
                                            Toast.LENGTH_LONG).show();
                                    registerComplete();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            }
    }

    private boolean comprobarTodos(){
        boolean nameUserE = utilitarios.validarCampo(nameUser,errorNameUser, password.getText().toString());
        utilitarios.validarCampo(email, errorEmail,password.getText().toString());
        boolean emailE = utilitarios.isVisible(email);
        boolean passwordE = utilitarios.validarCampo(password, errorPassword,password.getText().toString());
        boolean nombreE = utilitarios.validarCampo(nombre, errorName,password.getText().toString());
        boolean passwordRepeatE = utilitarios.validarCampo(passwordRepeat, errorRepeatPassword,password.getText().toString());
        if (nameUserE && emailE && passwordE && nombreE && passwordRepeatE){
            return true;
        }
        return false;
    }
}
