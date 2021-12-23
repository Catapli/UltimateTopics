package com.example.topics.Adaptadores;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.topics.Modelo.Mensaje;
import com.example.topics.Modelo.User;
import com.example.topics.listeners.ConversionListener;
import com.example.topics.databinding.ItemContainerRecentConversionBinding;

import java.util.ArrayList;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    private final ArrayList<Mensaje> listadoMensajes;

    private final ConversionListener conversionListener;

    public RecentConversationsAdapter(ArrayList<Mensaje> listadoMensajes, ConversionListener conversionListener) {
        this.listadoMensajes = listadoMensajes;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(listadoMensajes.get(position));
    }

    @Override
    public int getItemCount() {
        return listadoMensajes.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{

        ItemContainerRecentConversionBinding binding;

        public ConversionViewHolder( ItemContainerRecentConversionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(Mensaje mensaje){
            Glide.with(binding.getRoot()).load(mensaje.conversionImage).into(binding.imageProfile);
            binding.textName.setText(mensaje.conversionName);
            binding.textRecentMessage.setText(mensaje.getMensaje());
            binding.getRoot().setOnClickListener(view -> {
                User user = new User();
                user.setId(mensaje.conversionID);
                user.setNombreCuenta(mensaje.conversionName);
                user.setUrlPerfil(mensaje.conversionImage);
                conversionListener.onConversionClicked(user);

            });
        }
    }
}
