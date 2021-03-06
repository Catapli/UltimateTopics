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
import com.example.topics.Activities.DetallesImagen;
import com.example.topics.Modelo.Post;
import com.example.topics.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterPrincipal extends RecyclerView.Adapter<AdapterPrincipal.ViewHolderPrincipal> {

    public ArrayList<Post> posts;

    public Context context;

    private StorageReference defaul;

    public FirebaseStorage storage;




    public AdapterPrincipal(ArrayList<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.defaul = storage.getReference().child("default.jpg");
    }

    @NonNull
    @Override
    public ViewHolderPrincipal onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewprincipal,parent,false);
        return new ViewHolderPrincipal(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPrincipal holder, int position) {
        Glide.with(context)
                .load(posts.get(position).getUrlImagen())
                .into(holder.principal);
        holder.cardView.setOnClickListener(onClickListener);
        holder.cardView.setTag(holder);
        holder.descripcion.setText(posts.get(position).getDescripcion());
        holder.nombre.setText(posts.get(position).getUser());
        if (posts.get(position).getUserFoto().equals("no") || posts.get(position).getUserFoto().equals(null)){
            Glide.with(context).load(defaul).into(holder.perfil);
        }else {
            Glide.with(context)
                    .load(posts.get(position).getUserFoto())
                    .into(holder.perfil);
        }


    }

    View.OnClickListener onClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolderPrincipal sh = (ViewHolderPrincipal) view.getTag();
            int posicion = sh.getAdapterPosition();
            Intent intent = new Intent(context, DetallesImagen.class);
            intent.putExtra("email",posts.get(posicion).getIdUser());
            intent.putExtra("descripcion",posts.get(posicion).getDescripcion());
            intent.putExtra("urlImage",posts.get(posicion).getUrlImagen());
            intent.putExtra("user",posts.get(posicion).getUser());
            context.startActivity(intent);
        }
    });



    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolderPrincipal extends RecyclerView.ViewHolder{

        private ImageView principal;

        private TextView descripcion, nombre;

        private CardView cardView;

        private ImageView perfil;

        public ViewHolderPrincipal(@NonNull View itemView) {
            super(itemView);
            principal = itemView.findViewById(R.id.imageViewAdapterPrincipal);
            descripcion = itemView.findViewById(R.id.textViewAdapterDescripcion);
            nombre = itemView.findViewById(R.id.textViewAdapterNombrePerfil);
            cardView = itemView.findViewById(R.id.CardViewAdapterPrincipal);
            perfil = itemView.findViewById(R.id.profile);
        }
    }


}
