package com.example.topics.Activities.Top;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.topics.Adaptadores.AdaptadorTop;
import com.example.topics.Modelo.User;
import com.example.topics.R;
import com.example.topics.Utilidades.Constants;
import com.example.topics.Utilidades.Utilitarios;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopMensualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopMensualFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;

    private ArrayList<User> users;

    private ProgressBar progressBar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TopMensualFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopMensualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopMensualFragment newInstance(String param1, String param2) {
        TopMensualFragment fragment = new TopMensualFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_top_mensual, container, false);
        recyclerView = vista.findViewById(R.id.recyclerViewTopMensual);
        progressBar = vista.findViewById(R.id.progresBar);
        isLoading(true);
        users = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getUsers();
        return vista;
    }

    private void isLoading(boolean loading){
        if (loading){
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void getUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_TOP).orderBy(Constants.KEY_TOP_MENSUAL, Query.Direction.DESCENDING).limit(51).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("DOCUMENTS", task.getResult().getDocuments().size()+"");
                for (DocumentSnapshot doc : task.getResult()){
                    User user = docToUser(doc);
                    users.add(user);
                }
                isLoading(false);
                AdaptadorTop adaptadorTop = new AdaptadorTop(users, getContext());
                recyclerView.setAdapter(adaptadorTop);
            }
        });
    }

    private User docToUser(DocumentSnapshot doc){
        User user = new User();
        user.setSeguidores(String.valueOf(doc.get(Constants.KEY_TOP_MENSUAL)));
        user.setNombreCuenta(doc.getString(Constants.KEY_NAME_USER));
        user.setNuevosSubsTop(String.valueOf(doc.get(Constants.KEY_SEGUIDORES_TOTALES)));
        user.setUrlPerfil(doc.getString(Constants.KEY_PHOTO_PERFIL));
        return user;
    }
}