package com.example.el_fahim_sana_projet_dynamic_fit;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivitiesAdapter extends RecyclerView.Adapter<com.example.el_fahim_sana_projet_dynamic_fit.ActivitiesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList nom_activity, date_debut, date_fin;

    ActivitiesAdapter(Context context, ArrayList nom_activity, ArrayList date_debut, ArrayList date_fin) {
        this.context = context;
        this.nom_activity = nom_activity;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nom_activity.setText(String.valueOf(nom_activity.get(position)));
        holder.date_debut.setText(String.valueOf(date_debut.get(position)));
        holder.date_fin.setText(String.valueOf(date_fin.get(position)));
    }

    @Override
    public int getItemCount() {
        return nom_activity.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView nom_activity, date_debut, date_fin;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nom_activity = itemView.findViewById(R.id.nom_activity_txt);
            date_debut = itemView.findViewById(R.id.date_debut_txt);
            date_fin = itemView.findViewById(R.id.date_fin_txt);
        }
    }
}



