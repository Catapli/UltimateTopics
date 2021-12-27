package com.example.topics.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.topics.Activities.PerfilAjeno;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchAdapterPosts extends RecyclerView.Adapter<SearchAdapterPosts.ViewHolderSearchPosts> {

    public ArrayList<User> usuarios;

    public Context context;

    private FirebaseStorage storage;

    private StorageReference userReference;

    public SearchAdapterPosts(ArrayList<User> usuarios, Context context) {
        this.usuarios = usuarios;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.userReference = storage.getReference();
    }

    public static class ViewHolderSearchPosts extends RecyclerView.ViewHolder{

        private ImageView imageView;

        private TextView nombre, nombreCuenta;

        private CardView cardView;

        public ViewHolderSearchPosts(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageProfileList);
            nombre = itemView.findViewById(R.id.nombreRealSearch);
            nombreCuenta = itemView.findViewById(R.id.nombreCuentaSearch);
            cardView = itemView.findViewById(R.id.SearchCardView);
        }
    }

    @NonNull
    @Override
    public ViewHolderSearchPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_perfiles,parent,false);
        return new ViewHolderSearchPosts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSearchPosts holder, int position) {
        if (usuarios.get(position).getUrlPerfil().equals("no")){
            StorageReference perfilDefault = userReference.child("default.jpg");
            Glide.with(context).load(perfilDefault).into(holder.imageView);
        }else{
            Glide.with(context).load(usuarios.get(position).getUrlPerfil()).into(holder.imageView);
        }
        holder.nombreCuenta.setText(usuarios.get(position).getNombreCuenta());
        holder.nombre.setText(usuarios.get(position).getNombre());
        holder.cardView.setOnClickListener(onClickListener);
        holder.cardView.setTag(holder);
    }

    View.OnClickListener onClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolderSearchPosts sh = (ViewHolderSearchPosts) view.getTag();
            int posicion = sh.getAdapterPosition();
            Intent intent = new Intent(context, PerfilAjeno.class);
            intent.putExtra(Constants.KEY_ID_USER,usuarios.get(posicion).getId());
            context.startActivity(intent);
        }
    });

    @Override
    public int getItemCount() {
        return usuarios.size();
    }
}
