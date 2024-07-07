package com.example.el_fahim_sana_projet_dynamic_fit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoriqueActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DBHelper myDB;
    ArrayList<String> nom_activity, date_debut, date_fin;
    ActivitiesAdapter activitiesAdapter;
    Button homeButton, clearHistoryButton; // Ajout du bouton pour vider l'historique

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histo);

        // Initialiser les vues
        recyclerView = findViewById(R.id.recyclerView);
        homeButton = findViewById(R.id.homeButton);
        clearHistoryButton = findViewById(R.id.clearHistoryButton); // Bouton pour vider l'historique

        myDB = new DBHelper(this);
        nom_activity = new ArrayList<>();
        date_debut = new ArrayList<>();
        date_fin = new ArrayList<>();

        storeDataInArrays();

        activitiesAdapter = new ActivitiesAdapter(this, nom_activity, date_debut, date_fin);
        recyclerView.setAdapter(activitiesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoriqueActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Appel à une méthode pour vider l'historique
                clearHistory();
            }
        });
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.displayAllActivity(Variables.id);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Aucune donnée.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                nom_activity.add(cursor.getString(0));
                date_debut.add(cursor.getString(1));
                date_fin.add(cursor.getString(2));
            }
        }
    }

    // Méthode pour vider l'historique
    void clearHistory() {
        myDB.deleteAllActivities(); // Supprimer toutes les activités de la base de données
        // Actualiser l'affichage après suppression
        nom_activity.clear();
        date_debut.clear();
        date_fin.clear();
        activitiesAdapter.notifyDataSetChanged(); // Notifier le RecyclerView des changements
        Toast.makeText(this, "L'historique des activités a été vidé.", Toast.LENGTH_SHORT).show();
    }
}
