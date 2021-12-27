package com.example.topics.Activities.AccesCount;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Activities.PrincipalActivity;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccesCount extends AppCompatActivity {

    private static final String TAG = "Lectura";

    private Button button;

    private EditText nombre;

    private EditText password;

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    private ImageButton google;

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private GestureDetector gesture;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_accescount);
        SignInButton signInButton = findViewById(R.id.botonGoogle);
        gesture = new GestureDetector(this, new EscuchaTextos());
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        Log.d("Prueba","Entró");
    }


    class EscuchaTextos extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e2.getX()>e1.getX()){
                Toast.makeText(AccesCount.this, "Se ha desplazado a derecha",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(AccesCount.this, "Se ha desplazado a izquierda",Toast.LENGTH_LONG).show();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void accederLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void accederRegistro(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mAuth.updateCurrentUser(user);
                            startActivity(new Intent(getApplicationContext(), PrincipalActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            mAuth.updateCurrentUser(null);
                        }
                    }
                });
    }

    private void existeUsuario(){
        String uid = mAuth.getCurrentUser().getUid();
        db.collection(Constants.KEY_COLLECTION_USERS).document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (!task.getResult().exists()){
                                Log.d("Usuarios no existe", "No existe el usuario");
                            }else {
                                Intent intent = new Intent(AccesCount.this, PrincipalActivity.class);
                                startActivity(intent);
                                Log.d("Uuario existe", "Si que existe el usuario");
                            }
                        }
                    }
                });
    }

    private void existeInhabilitado(){
        String uid = mAuth.getCurrentUser().getUid();
        Log.d("existe", uid);
        db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_INHABILITADOS).document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()){
                    Log.d("Inhabilitado Existe", "No existe el usuario");
                }else {
                    Log.d("Inhabilitado Existe","existe el usuario");
                    Intent intent = new Intent(AccesCount.this, NoHabilitadoActivity.class);
                    startActivity(intent);
                }
            }
        });
    }






    private void signWithGoogle(){
        existeInhabilitado();
        existeUsuario();
        Intent intent = new Intent(this, SubirValidacion.class);
        intent.putExtra(Constants.KEY_EMAIL, mAuth.getCurrentUser().getEmail());
        intent.putExtra(Constants.KEY_NAME_USER,mAuth.getCurrentUser().getDisplayName());
        intent.putExtra(Constants.KEY_PHOTO_PERFIL, mAuth.getCurrentUser().getPhotoUrl());
        startActivity(intent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(acct.getIdToken());
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


}





