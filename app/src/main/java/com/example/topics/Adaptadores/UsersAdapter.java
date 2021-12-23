package com.example.topics.Adaptadores;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.topics.Modelo.User;
import com.example.topics.databinding.ItemContainerUserBinding;
import com.example.topics.listeners.UserListener;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final ArrayList<User> users;

    private final UserListener userListener;


    public UsersAdapter(ArrayList<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User usuario){
            binding.textName.setText(usuario.getNombreCuenta());
            binding.textEmail.setText(usuario.getEmail());
            Glide.with(binding.getRoot()).load(usuario.getUrlPerfil()).into(binding.imageProfile);
            binding.getRoot().setOnClickListener(view -> userListener.onUserClicked(usuario));
        }


    }


}
