package com.example.topics.Adaptadores;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.topics.Modelo.Post;
import com.example.topics.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class AdaptadorExplore extends RecyclerView.Adapter<AdaptadorExplore.ExploreViewHolder> {

    private ArrayList<Post> posts;

    public AdaptadorExplore(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExploreViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.post_item_container,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
            holder.setPostImageView(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ExploreViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView postImageView;


        public ExploreViewHolder(View itemView){
            super(itemView);
            postImageView = itemView.findViewById(R.id.imagePost);
        }

        void setPostImageView(Post post){
            Glide.with(itemView).load(post.getUrlImagen()).into(postImageView);
        }
    }
}
