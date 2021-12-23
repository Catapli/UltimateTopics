package com.example.topics.Activities.Chats;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Adaptadores.ChatAdapter;
import com.example.topics.Modelo.Mensaje;
import com.example.topics.Modelo.MensajeTexto;
import com.example.topics.Modelo.User;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.PreferenceManager;
import com.example.topics.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    private User receivedUser;
    private ArrayList<Mensaje> mensajes;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;
    private String conversionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        mensajes = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                mensajes,
                receivedUser.getUrlPerfil(),
                preferenceManager.getString(Constants.KEY_ID_USER)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        db = FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_ID_EMISOR, preferenceManager.getString(Constants.KEY_ID_USER));
        message.put(Constants.KEY_ID_RECEPTOR, receivedUser.getId());
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        db.collection(Constants.KEY_COLLECTION_CHATS).add(message);
        if (conversionId != null){
            updateConversion(binding.inputMessage.getText().toString());
        }else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_ID_EMISOR, preferenceManager.getString(Constants.KEY_ID_USER));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME_USER));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_PHOTO_PERFIL));
            conversion.put(Constants.KEY_ID_RECEPTOR, receivedUser.getId());
            conversion.put(Constants.KEY_RECEIVER_NAME, receivedUser.getNombreCuenta());
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receivedUser.getUrlPerfil());
            conversion.put(Constants.KEY_LAST_MESSAGE,binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        binding.inputMessage.setText(null);
    }

    private void listenMessages(){
        db.collection(Constants.KEY_COLLECTION_CHATS)
                .whereEqualTo(Constants.KEY_ID_EMISOR, preferenceManager.getString(Constants.KEY_ID_USER))
                .whereEqualTo(Constants.KEY_ID_RECEPTOR, receivedUser.getId())
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CHATS)
                .whereEqualTo(Constants.KEY_ID_EMISOR, receivedUser.getId() )
                .whereEqualTo(Constants.KEY_ID_RECEPTOR, preferenceManager.getString(Constants.KEY_ID_USER))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value,error) ->{
        if (error != null){
            return;
        }
        if (value != null){
            int count = mensajes.size();
            for (DocumentChange doc : value.getDocumentChanges()){
                if (doc.getType() == DocumentChange.Type.ADDED){
                    Mensaje mensaje = new MensajeTexto();
                    mensaje.setIdEmisor(doc.getDocument().getString(Constants.KEY_ID_EMISOR));
                    mensaje.setIdReceptor(doc.getDocument().getString(Constants.KEY_ID_RECEPTOR));
                    mensaje.setMensaje(doc.getDocument().getString(Constants.KEY_MESSAGE));
                    mensaje.setDateTime(getReadableDateTime(doc.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                    mensaje.setDateObject(doc.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    mensajes.add(mensaje);
                }
            }
            Collections.sort(mensajes, (obj1, obj2) -> obj1.getDateObject().compareTo(obj2.getDateObject()));
            if (count == 0){
                chatAdapter.notifyDataSetChanged();
            }else {
                chatAdapter.notifyItemRangeInserted(mensajes.size(), mensajes.size());
                binding.chatRecyclerView.smoothScrollToPosition(mensajes.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBarChat.setVisibility(View.GONE);
        if (conversionId == null){
            checkForConversion();
        }
    };

    private void loadReceiverDetails(){
        receivedUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receivedUser.getNombreCuenta());
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    private void  updateConversion(String message){
        DocumentReference documentReference =
                db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, new Date()
        );
    }

    private void checkForConversion(){
        if (mensajes.size() != 0){
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_ID_USER),
                    receivedUser.getId()
            );
            checkForConversionRemotely(
                    receivedUser.getId(),
                    preferenceManager.getString(Constants.KEY_ID_USER)

            );
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverId){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_ID_EMISOR, senderId)
                .whereEqualTo(Constants.KEY_ID_RECEPTOR, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
            conversionId = doc.getId();
        }
    };
}