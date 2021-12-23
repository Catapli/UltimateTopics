package com.example.topics.Adaptadores;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.topics.Modelo.Mensaje;
import com.example.topics.databinding.ItemContainerReceivedMessageBinding;
import com.example.topics.databinding.ItemContainerSentMessageBinding;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final ArrayList<Mensaje> mensajes;

    private final String receiverImageProfile;

    private final String senderId;

    public  static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;


    public ChatAdapter(ArrayList<Mensaje> mensajes, String receiverImageProfile, String senderId) {
        this.mensajes = mensajes;
        this.receiverImageProfile = receiverImageProfile;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new ReceeivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(mensajes.get(position));
        }else {
            ((ReceeivedMessageViewHolder) holder).setData(mensajes.get(position), receiverImageProfile);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mensajes.get(position).getIdEmisor().equals(senderId)){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class  SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(Mensaje mensaje){
            binding.textMessage.setText(mensaje.getMensaje());
            binding.textDateTime.setText(mensaje.getDateTime());

        }
    }

    static class ReceeivedMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerReceivedMessageBinding binding;

        ReceeivedMessageViewHolder(ItemContainerReceivedMessageBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(Mensaje mensaje, String Url){
            binding.textMessage.setText(mensaje.getMensaje());
            binding.textDateTime.setText(mensaje.getDateTime());
            Glide.with(binding.getRoot()).load(Url).into(binding.imageProfile);
        }
    }
}
