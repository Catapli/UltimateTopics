package com.example.topics.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdaptadorTop extends RecyclerView.Adapter<AdaptadorTop.ViewHolderTop> {

    public ArrayList<User> users;

    public Context context;

    public FirebaseStorage storage;

    private StorageReference principal, perfilDefault, first, second, third;



    public AdaptadorTop(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.principal = storage.getReference();
        this.perfilDefault = principal.child("default.jpg");
        this.first = principal.child("1.png");
        this.second = principal.child("2.jpg");
        this.third = principal.child("3.jpg");
    }

    @NonNull
    @Override
    public ViewHolderTop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_top,parent,false);
        return new ViewHolderTop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTop holder, int position) {
        holder.nameCount.setText(users.get(position).getNombreCuenta());
        holder.suscriptores.setText("Total de Seguidores: " + users.get(position).getTop());
        holder.nuevosSuscriptores.setText("Seguidores Nuevos" + users.get(position).getNuevosSubsTop());
        Glide.with(context).load(perfilDefault).into(holder.fotoPerfil);

        switch (position){
            case 0:{
                holder.clasificacionImagen.setVisibility(View.VISIBLE);
                holder.clasificacion.setVisibility(View.INVISIBLE);
                Glide.with(context).load(first).into(holder.clasificacionImagen);
                break;
            }
            case 1:{
                holder.clasificacionImagen.setVisibility(View.VISIBLE);
                holder.clasificacion.setVisibility(View.INVISIBLE);
                Glide.with(context).load(second).into(holder.clasificacionImagen);
                break;
            }
            case 2:{
                holder.clasificacionImagen.setVisibility(View.VISIBLE);
                holder.clasificacion.setVisibility(View.INVISIBLE);
                Glide.with(context).load(third).into(holder.clasificacionImagen);
                break;
            }
            default:{
                holder.clasificacionImagen.setVisibility(View.INVISIBLE);
                holder.clasificacion.setVisibility(View.VISIBLE);
                holder.clasificacion.setText(String.valueOf(position+1)+" º");
            }

        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolderTop extends RecyclerView.ViewHolder{

        private ImageView clasificacionImagen, fotoPerfil;

        private ImageButton botonSuscribirse;

        private TextView nameCount, suscriptores, nuevosSuscriptores, clasificacion;


        private CardView cardView;

        public ViewHolderTop(@NonNull View itemView) {
            super(itemView);
            clasificacionImagen = itemView.findViewById(R.id.imageViewClasificacionTop);
            fotoPerfil = itemView.findViewById(R.id.imageViewFotoPerfilTop);
            nameCount = itemView.findViewById(R.id.textViewNameCountTop);
            suscriptores = itemView.findViewById(R.id.textViewSuscriptoresTop);
            nuevosSuscriptores = itemView.findViewById(R.id.textViewSuscrpitoresGanadosTop);
            botonSuscribirse = itemView.findViewById(R.id.ImageButtonSuscribirse);
            clasificacion = itemView.findViewById(R.id.TextViewCalificacion);
            cardView = itemView.findViewById(R.id.CardViewTop);
        }
    }
}
