package com.example.topics.Activities.Dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.topics.Modelo.User;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.Utilitarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class DesuscribirseDialogFragment extends DialogFragment {

    private String idUser;

    private TextView seguidores;

    private User userToDesuscribe;

    private FirebaseFirestore db;

    private Utilitarios utilitarios;

    public DesuscribirseDialogFragment(String idUser, String idToDesuscribe, Context context) {
        User userToDesuscribe = new User();
        userToDesuscribe.setId(idToDesuscribe);
        utilitarios = new Utilitarios();
        this.userToDesuscribe = userToDesuscribe;
        this.idUser = idUser;
        readData(idToDesuscribe);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Estas seguro que quieres desuscribirte?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            borrarSeguido(idUser, userToDesuscribe.getId());
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void borrarSeguido(String idUser, String idCuenta){
        db.collection(Constants.KEY_COLLECTION_SEGUIDORES)
                .whereEqualTo(Constants.KEY_ID_CUENTA, idCuenta)
                .whereEqualTo(Constants.KEY_ID_SEGUIDOR, idUser).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (!task.getResult().isEmpty()){
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            DocumentReference documentReference = doc.getReference();
                            documentReference.delete();
                            restarSeguidorTop();
                            Log.d("BORRADO", documentReference.getId());
                        }
                    }
        });
    }


    public void readData(String idUser){
        db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).document(idUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    userToDesuscribe = completeUser(documentSnapshot);
                }
            }
        });
    }

    public User completeUser(DocumentSnapshot document) {
        userToDesuscribe.setSeguidores(document.get(Constants.KEY_SEGUIDORES).toString());
        userToDesuscribe.setSeguidos(document.get(Constants.KEY_SEGUIDOS).toString());
        userToDesuscribe.setUrlPerfil(document.getString(Constants.KEY_PHOTO_PERFIL));
        userToDesuscribe.setNombre(document.getString(Constants.KEY_NAME));
        userToDesuscribe.setNombreCuenta(document.getString(Constants.KEY_NAME_USER));
        userToDesuscribe.setEmail(document.getString(Constants.KEY_EMAIL));
        return userToDesuscribe;
    }

    public void restarSeguidorTop(){
        int seguidores = Integer.parseInt(userToDesuscribe.getSeguidores());
        Map<String, Object> dataUsuario = new HashMap<>();
        dataUsuario.put(Constants.KEY_TOP_SEMANAL, String.valueOf(seguidores - 1));
        dataUsuario.put(Constants.KEY_TOP_MENSUAL, String.valueOf(seguidores - 1));
        dataUsuario.put(Constants.KEY_TOP_DIARIO, String.valueOf(seguidores - 1));
        dataUsuario.put(Constants.KEY_SEGUIDORES_TOTALES, String.valueOf(seguidores - 1));
        db.collection(Constants.KEY_COLLECTION_TOP)
                .document(userToDesuscribe.getId())
                .set(dataUsuario, SetOptions.merge());
    }

}
