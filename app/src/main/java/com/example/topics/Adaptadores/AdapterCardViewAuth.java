package com.example.topics.Adaptadores;

import android.content.Context;
import android.content.Intent;
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
import com.example.topics.Activities.Auth.DetallesAuthActivity;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;

import java.util.ArrayList;


public class AdapterCardViewAuth extends  RecyclerView.Adapter<AdapterCardViewAuth.ViewHolderAuth>{

    public ArrayList<User> users;

    public Context context;

    public AdapterCardViewAuth(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderAuth onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewauth,parent,false);
        return new ViewHolderAuth(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAuth holder, int position) {
        Glide.with(context)
                .load(users.get(position).getUrlPerfil())
                .into(holder.imagenPerfil);
        holder.cardView.setOnClickListener(onClickListener);
        holder.cardView.setTag(holder);
        holder.fecha.setText(users.get(position).getFecha());
        holder.email.setText(users.get(position).getEmail());
    }

    View.OnClickListener onClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolderAuth sh = (ViewHolderAuth) view.getTag();
            int posicion = sh.getAdapterPosition();
            Intent intent = new Intent(context, DetallesAuthActivity.class);
            intent.putExtra(Constants.KEY_EMAIL,users.get(posicion).getEmail());
            intent.putExtra(Constants.KEY_ROSTRO,users.get(posicion).getUrlRostro());
            intent.putExtra(Constants.KEY_DNI,users.get(posicion).getUrlDni());
            intent.putExtra(Constants.KEY_PHOTO_PERFIL,users.get(posicion).getUrlPerfil());
            intent.putExtra(Constants.KEY_DATE,users.get(posicion).getFecha());
            intent.putExtra(Constants.KEY_ID_USER,users.get(posicion).getId());
            intent.putExtra(Constants.KEY_NAME,users.get(posicion).getNombre());
            intent.putExtra(Constants.KEY_NAME_USER,users.get(posicion).getNombreCuenta());
            intent.putExtra(Constants.KEY_PASSWORD,users.get(posicion).getPassword());
            intent.putExtra(Constants.KEY_TOKEN,users.get(posicion).getToken());
            context.startActivity(intent);
        }
    });

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolderAuth extends RecyclerView.ViewHolder{

        private ImageView imagenPerfil;

        private ImageButton botonAcces;

        private TextView email, fecha;


        private CardView cardView;

        public ViewHolderAuth(@NonNull View itemView) {
            super(itemView);
            imagenPerfil = itemView.findViewById(R.id.imageViewAuth);
            email = itemView.findViewById(R.id.TextviewEmail);
            fecha = itemView.findViewById(R.id.NacimientoAuth);
            botonAcces = itemView.findViewById(R.id.buttonVerDetalles);
            cardView = itemView.findViewById(R.id.cardViewAuth);
        }
    }
}
