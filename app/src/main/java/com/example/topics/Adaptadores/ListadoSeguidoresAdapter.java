package com.example.topics.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import java.util.ArrayList;

public class ListadoSeguidoresAdapter extends RecyclerView.Adapter<ListadoSeguidoresAdapter.ViewHolderListadoUsers> {

    public ArrayList<User> usuarios;

    public Context context;

    public ListadoSeguidoresAdapter( ArrayList<User> usuarios, Context context) {
        this.usuarios = usuarios;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderListadoUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_perfiles,parent,false);
        return new ViewHolderListadoUsers(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListadoUsers holder, int position) {
        Glide.with(context)
                .load(usuarios.get(position).getUrlPerfil())
                .into(holder.imageView);
        Log.d("Listado",usuarios.get(position).toString());
        holder.nombreCuenta.setText(usuarios.get(position).getNombreCuenta());
        holder.nombre.setText(usuarios.get(position).getNombre());

    }

    View.OnClickListener onClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolderListadoUsers sh = (ViewHolderListadoUsers) view.getTag();
            int posicion = sh.getAdapterPosition();
            Intent intent = new Intent(context, PerfilAjeno.class);
            intent.putExtra("email",usuarios.get(posicion).getEmail());
            context.startActivity(intent);
        }
    });

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class ViewHolderListadoUsers extends RecyclerView.ViewHolder{

        private ImageView imageView;

        private TextView nombre, nombreCuenta;

        private CardView cardView;


        public ViewHolderListadoUsers(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageProfileList);
            nombre = itemView.findViewById(R.id.nombreRealSearch);
            nombreCuenta = itemView.findViewById(R.id.nombreCuentaSearch);
            cardView = itemView.findViewById(R.id.SearchCardView);
        }
    }
}
