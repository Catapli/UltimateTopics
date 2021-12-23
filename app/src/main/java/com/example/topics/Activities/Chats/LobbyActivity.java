package com.example.topics.Activities.Chats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.topics.Activities.PrincipalActivity;
import com.example.topics.Adaptadores.RecentConversationsAdapter;
import com.example.topics.Modelo.Mensaje;
import com.example.topics.Modelo.MensajeTexto;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.PreferenceManager;
import com.example.topics.listeners.ConversionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class LobbyActivity extends AppCompatActivity implements ConversionListener {


    private PreferenceManager preferenceManager;

    private GestureDetector gesture;

    private ArrayList<Mensaje> listadoMensajes;

    private RecyclerView recyclerView;

    private RecentConversationsAdapter recentConversationsAdapter;

    private FirebaseFirestore db;

    private ProgressBar progressBar;

    private FloatingActionButton add;

    private FrameLayout frameLayout;

    private AppCompatImageView back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        init();
        setListeners();
        gesture = new GestureDetector(frameLayout.getContext(), new EscuchaTextos());
        listenConversations();

    }

    private void init(){
        frameLayout = findViewById(R.id.frameLayout);
        listadoMensajes = new ArrayList<>();
        progressBar = findViewById(R.id.progresBar);
        recyclerView = findViewById(R.id.recycler_viewConversations);
        recentConversationsAdapter = new RecentConversationsAdapter(listadoMensajes, this);
        recyclerView.setAdapter(recentConversationsAdapter);
        add= findViewById(R.id.fabNewChat);
        db = FirebaseFirestore.getInstance();
        back = findViewById(R.id.imageBack);
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    class EscuchaTextos extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("PRUEBA", "LE DIO");
            if (e2.getX()>e1.getX()){


            }
            return super.onFling(e1, e2, velocityX, velocityY);

        }
    }

    private void listenConversations(){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_ID_EMISOR, preferenceManager.getString(Constants.KEY_ID_USER))
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_ID_RECEPTOR, preferenceManager.getString(Constants.KEY_ID_USER))
                .addSnapshotListener(eventListener);
    }

    private void setListeners(){
        add.setOnClickListener( view ->
                startActivity(new Intent(getApplicationContext(),UsersChatActivity.class)));
        back.setOnClickListener(v-> {
            Intent intent = new Intent(LobbyActivity.this, PrincipalActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        });
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null){
            return;
        }
        if (value != null){
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    String emisorId = documentChange.getDocument().getString(Constants.KEY_ID_EMISOR);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_ID_RECEPTOR);
                    Mensaje mensaje = new MensajeTexto();
                    mensaje.setIdEmisor(emisorId);
                    mensaje.setIdReceptor(receiverId);
                    if (preferenceManager.getString(Constants.KEY_ID_USER).equals(emisorId)){
                        mensaje.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        mensaje.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        mensaje.conversionID = documentChange.getDocument().getString(Constants.KEY_ID_RECEPTOR);
                    }else {
                        mensaje.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        mensaje.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        mensaje.conversionID = documentChange.getDocument().getString(Constants.KEY_ID_EMISOR);
                    }
                    mensaje.setMensaje(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    mensaje.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    listadoMensajes.add(mensaje);
                }else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for (int i = 0; i < listadoMensajes.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_ID_EMISOR);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_ID_RECEPTOR);
                        if (listadoMensajes.get(i).getIdEmisor().equals(senderId) && listadoMensajes.get(i).getIdReceptor().equals(receiverId)){
                            listadoMensajes.get(i).setMensaje(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                            listadoMensajes.get(i).setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                            break;
                        }
                    }
                }
            }
            Collections.sort(listadoMensajes, (obj1, obj2) -> obj2.getDateObject().compareTo(obj1.getDateObject()));
            recentConversationsAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onConversionClicked(User user) {
        startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(Constants.KEY_USER, user));

    }
}