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
import com.example.topics.Utilidades.Constants;

import java.util.ArrayList;

public class PostsAdapterCronologico extends RecyclerView.Adapter<PostsAdapterCronologico.ViewHolderCronologico> {

    public ArrayList<Post> posts;

    public Context context;

    public String nombreCuenta;

    public PostsAdapterCronologico(ArrayList<Post> posts, Context context,String nombreCuenta) {
        this.posts = posts;
        this.context = context;
        this.nombreCuenta = nombreCuenta;
    }

    @NonNull
    @Override
    public ViewHolderCronologico onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_cronologicos,parent,false);
        return new ViewHolderCronologico(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCronologico holder, int position) {
        Glide.with(context)
                .load(posts.get(position).getUrlImagen())
                .into(holder.imageView);
        holder.cardView.setOnClickListener(onClickListener);
        holder.cardView.setTag(holder);
       holder.descripcion.setText(posts.get(position).getDescripcion());
       holder.nombre.setText(nombreCuenta);

    }

    View.OnClickListener onClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolderCronologico sh = (ViewHolderCronologico) view.getTag();
            int posicion = sh.getAdapterPosition();
            Intent intent = new Intent(context, DetallesImagen.class);
            intent.putExtra(Constants.KEY_ID_USER,posts.get(posicion).getIdUser());
            intent.putExtra(Constants.KEY_DESCRIPCION,posts.get(posicion).getDescripcion());
            intent.putExtra(Constants.KEY_RUTA_POST,posts.get(posicion).getUrlImagen());
            context.startActivity(intent);
        }
    });



    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolderCronologico extends RecyclerView.ViewHolder{

        private ImageView imageView;

        private TextView descripcion, nombre;

        private CardView cardView;

        public ViewHolderCronologico(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.PostImagenView);
            descripcion = itemView.findViewById(R.id.PostDescripcion);
            nombre = itemView.findViewById(R.id.PostNombrePerfil);
            cardView = itemView.findViewById(R.id.PostsCronologico);
        }
    }


}
