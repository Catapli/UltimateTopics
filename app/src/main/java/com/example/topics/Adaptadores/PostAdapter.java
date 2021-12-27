package com.example.topics.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.topics.Activities.DetallesImagen;
import com.example.topics.Modelo.Post;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

     public ArrayList<Post> posts;

     public Context context;

    public PostAdapter(ArrayList<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.perfil_images,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(posts.get(position).getUrlImagen())
                .into(holder.imageView);
        holder.cardView.setOnClickListener(onClickListener);
        holder.cardView.setTag(holder);
        Log.d("POST", posts.get(position).toString());

    }

    View.OnClickListener onClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolder sh = (ViewHolder) view.getTag();
            int posicion = sh.getAdapterPosition();
            Log.d("POST", posts.get(posicion).toString());
            Intent intent = new Intent(context, DetallesImagen.class);
            intent.putExtra(Constants.KEY_ID_USER,posts.get(posicion).getIdUser());
            intent.putExtra(Constants.KEY_NAME_USER, posts.get(posicion).getUser());
            intent.putExtra(Constants.KEY_DESCRIPCION,posts.get(posicion).getDescripcion());
            intent.putExtra(Constants.KEY_RUTA_POST,posts.get(posicion).getUrlImagen());
            context.startActivity(intent);
        }
    });



    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagen_post);
            cardView = itemView.findViewById(R.id.CardViewGridLayout);

        }
    }

}
