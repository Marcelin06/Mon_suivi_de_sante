package com.example.monsuividesante;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class CaloriesActivity extends AppCompatActivity {

    private DatabaseAccess db;
    private DatabaseOpenhelper db_helper;

    private float calories_depense; //en kcal a afficher dans le rectangle vert
    private float calories_depense_reel; //en kcal a afficher dans le rectangle rouge
    private float calories_activite;
    private String duree_activite;
    private ConstraintLayout toolar;
    private ConstraintLayout entrer_calorie_consomme;
    private ConstraintLayout liste_deroulante_choix_activite;
    private HashMap<String, Float> items_choix_activite;
    private AlertDialog pop_up_choix_activite;
    private ConstraintLayout liste_deroulante_duree_activite;
    private ArrayList<String> items_duree_activite;
    private AlertDialog pop_up_duree_activite;
    private LinearLayout pas;
    private LinearLayout mes_info;
    private LinearLayout sommeil;
    private Button bouton_calories_consome_ok;
    private Button bouton_activite_ok;
    private ImageButton bouton_edit_calories_consomme;
    private ImageButton bouton_liste_choix_activite;
    private ImageButton bouton_liste_duree_activite;
    private ImageButton bouton_explication;
    private ImageButton bouton_mes_info;
    private ImageButton bouton_pas;
    private ImageButton bouton_calories;
    private ImageButton bouton_sommeil;

    //private Utilisateur user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calories);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_calories), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = DatabaseAccess.getInstance(this);
        db_helper = new DatabaseOpenhelper(this);

        TextView textViewCalDepReel = findViewById(R.id.calories_depense_reel).findViewById(R.id.val_calories_depense).findViewById(R.id.text_calorie_depense_reel);
        calories_depense_reel = setTextViewCalorieDepenseReel(textViewCalDepReel, 0F);

        ConstraintLayout tmp_CL = findViewById(R.id.calories_depense);

        TextView textViewCalDep = tmp_CL.findViewById(R.id.val_calories_depense).findViewById(R.id.text_calorie_depense_reel);
        calories_depense = setTextViewCalorieDepense(textViewCalDep);

        bouton_explication= tmp_CL.findViewById(R.id.val_calories_depense).findViewById(R.id.bouton_explication);
        bouton_explication.setOnClickListener(this::onClickListenerBoutonExplication);

        toolar = findViewById(R.id.toolbar);

        pas = toolar.findViewById(R.id.pas);
        mes_info = toolar.findViewById(R.id.mes_info);
        sommeil = toolar.findViewById(R.id.sommeil);

        pas.setAlpha(0.4F);
        mes_info.setAlpha(0.4F);
        sommeil.setAlpha(0.4F);

        tmp_CL = findViewById(R.id.demande_calorie_consomme);

        entrer_calorie_consomme = tmp_CL.findViewById(R.id.entrer_calorie_consomme);
        bouton_edit_calories_consomme = entrer_calorie_consomme.findViewById(R.id.bouton_modifications);

        bouton_edit_calories_consomme.setOnClickListener(this::onClickListenerCaloriesConsomme);
        entrer_calorie_consomme.setOnClickListener(this::onClickListenerCaloriesConsomme);

        bouton_calories_consome_ok = tmp_CL.findViewById(R.id.bouton_ok);
        bouton_calories_consome_ok.setOnClickListener(this::onClickListenerBoutonConsommeOK);

        tmp_CL = findViewById(R.id.demande_info_activite);

        bouton_activite_ok = tmp_CL.findViewById(R.id.bouton_ok_activite);
        bouton_activite_ok.setOnClickListener(this::onClickListenerBoutonActiviteOK);

        bouton_pas = pas.findViewById(R.id.bouton_pas);
        bouton_pas.setOnClickListener(this::onClickListenerBoutonPas);

        bouton_mes_info = mes_info.findViewById(R.id.bouton_mes_info);
        bouton_mes_info.setOnClickListener(this::onClickListenerBoutonMesInfo);

        bouton_calories = toolar.findViewById(R.id.calories).findViewById(R.id.bouton_calories);
        bouton_calories.setOnClickListener(this::onClickListenerBoutonCalorie);

        bouton_sommeil = sommeil.findViewById(R.id.bouton_sommeil);
        bouton_sommeil.setOnClickListener(this::onClickListenerBoutonSommeil);

        liste_deroulante_duree_activite = tmp_CL.findViewById(R.id.liste_deroulante_duree);
        bouton_liste_duree_activite = liste_deroulante_duree_activite.findViewById(R.id.bouton_liste_deroulante);

        items_duree_activite = setListeDuree();
        liste_deroulante_duree_activite.setOnClickListener(this::onClickListenerActiviteDuree);
        bouton_liste_duree_activite.setOnClickListener(this::onClickListenerActiviteDuree);

        liste_deroulante_choix_activite = tmp_CL.findViewById(R.id.liste_deroulante_activite);
        bouton_liste_choix_activite = liste_deroulante_choix_activite.findViewById(R.id.bouton_liste_deroulante);

        items_choix_activite = setListeActivite();
        liste_deroulante_choix_activite.setOnClickListener(this::onClickListenerChoixActivite);
        bouton_liste_choix_activite.setOnClickListener(this::onClickListenerChoixActivite);

        db.close();
    }

    public float setTextViewCalorieDepenseReel(TextView textView, float ajout){
        db.open();

        /*Utiliser les getter de Utilisateur pour avoir les données de Utilisateur*/

        float res = db.getCalorieDepense("userTest") + ajout;
        String calorie = res + " kcal";
        textView.setText(calorie);

        db.close();

        return res;
    }

    public float setTextViewCalorieDepense(TextView textView){
        db.open();

        /*formule réél pour "res":
         *
         *homme: mb = 8,362 + (13,397 x poids en kg) + (4,799 x taille en cm) - (5,677 x âge en années)
         *femme: mb = 447,593 + (9,247 x poids en kg) + (3,098 x taille en cm) - (4,330 x âge en années)
         *
         *Si Sédentaire (peu ou pas d'exercice) : MB x 1,2
         *Si Léger (exercice léger/sport 1 à 3 jours/semaine) : MB x 1,375
         *Si Modéré (exercice modéré/sport 3 à 5 jours/semaine) : MB x 1,55
         *Si Actif (exercice intense/sport 6 à 7 jours/semaine) : MB x 1,725
         *Si Très actif (exercice intense quotidien ou activité physique très difficile) : MB x 1,9*/

        float res = db.getCalorieDepense("userTest");
        String calorie = res + " kcal";
        textView.setText(calorie);

        db.close();

        return res;
    }

    public ArrayList<String> setListeDuree(){
        db.open();
        ArrayList<String> res = db.getDuree();
        db.close();

        return res;
    }

    public HashMap<String, Float> setListeActivite(){
        db.open();
        HashMap<String, Float> res = db.getActiviteCalories();
        db.close();

        return res;
    }

    public void onClickListenerBoutonExplication(View view){
        AlertDialog.Builder pop_up_explication = new AlertDialog.Builder(this, R.style.PopUpArrondi);

        pop_up_explication.setView(R.layout.pop_up_explication);

        AlertDialog pop_up = pop_up_explication.create();
        pop_up.show();

        TextView textExplication = pop_up.findViewById(R.id.text_pop_up_explication);

        String explication = "Si vous dépensez plus de " + calories_depense_reel + " vous allez maigrir.\nSi vous dépensez moins de " + calories_depense_reel + " vous allez grossir";
        textExplication.setText(explication);
    }

    public void onClickListenerCaloriesConsomme(View view){
        AlertDialog.Builder pop_up_calorie_consomme = new AlertDialog.Builder(this, R.style.PopUpArrondi);

        pop_up_calorie_consomme.setView(R.layout.pop_up_calorie);

        AlertDialog pop_up = pop_up_calorie_consomme.create();
        pop_up.show();

        Button bouton_ok = pop_up.findViewById(R.id.bouton_ok);
        bouton_ok.setOnClickListener(v -> {
            TextView choix = entrer_calorie_consomme.findViewById(R.id.calorie_consome);
            EditText saisie = pop_up.findViewById(R.id.saisie_user);

            String affichage = saisie.getText() + " kcal";

            choix.setText(affichage);
            choix.setTextColor(Color.BLACK);

            pop_up.dismiss();
        });

        Button bouton_annuler = pop_up.findViewById(R.id.bouton_annuler);
        bouton_annuler.setOnClickListener(v -> { pop_up.dismiss(); });
    }

    public void onClickListenerActiviteDuree(View view){
        AlertDialog.Builder pop_up_activite_dure_builder = new AlertDialog.Builder(this, R.style.PopUpArrondi);

        LayoutInflater inflater = getLayoutInflater();
        View view_pop_up = inflater.inflate(R.layout.pop_up_duree_activite, null);

        RecyclerView liste_deroulante = view_pop_up.findViewById(R.id.liste_duree_activite);

        liste_deroulante.setLayoutManager(new LinearLayoutManager(this));

        ListeDeroulanteAdapter adapter = new ListeDeroulanteAdapter(items_duree_activite, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                duree_activite = getChoixListeDeroulanteDuree(view, liste_deroulante_duree_activite);

                pop_up_duree_activite.dismiss();
            }
        });
        liste_deroulante.setAdapter(adapter);

        pop_up_duree_activite = pop_up_activite_dure_builder.create();

        pop_up_duree_activite.setView(view_pop_up);
        pop_up_duree_activite.show();
    }

    public void onClickListenerChoixActivite(View view){
        AlertDialog.Builder pop_up_activite_choix_builder = new AlertDialog.Builder(this, R.style.PopUpArrondi);

        LayoutInflater inflater = getLayoutInflater();
        View view_pop_up = inflater.inflate(R.layout.pop_up_choix_activite, null);

        RecyclerView liste_deroulante = view_pop_up.findViewById(R.id.liste_choix_activite);

        liste_deroulante.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> liste_items = new ArrayList<String>(items_choix_activite.keySet());

        ListeDeroulanteAdapter adapter = new ListeDeroulanteAdapter(liste_items, view1 -> {
            String choix = getChoixListeDeroulanteActivite(view1, liste_deroulante_choix_activite);

            /*formule reel pour calories_depense:
            *
            * calories_depense = items_choix_activite.get(choix) * poids * heure
            */
            Float tmp = items_choix_activite.get(choix);

            calories_activite = tmp!=null ? tmp : 0;

            pop_up_choix_activite.dismiss();
        });
        liste_deroulante.setAdapter(adapter);

        pop_up_choix_activite = pop_up_activite_choix_builder.create();

        pop_up_choix_activite.setView(view_pop_up);
        pop_up_choix_activite.show();
    }

    public String getChoixListeDeroulanteActivite(View view, ViewGroup liste_deroulante){
        String choix = (String) ((TextView) view.findViewById(R.id.activite)).getText();
        TextView explication = liste_deroulante.findViewById(R.id.explication_liste_deroulante);

        explication.setText(choix);
        explication.setTextColor(Color.BLACK);

        return choix;
    }

    public String getChoixListeDeroulanteDuree(View view, ViewGroup liste_deroulante){
        String choix = (String) ((TextView) view.findViewById(R.id.activite)).getText();
        TextView explication = liste_deroulante.findViewById(R.id.explication_liste_deroulante);

        explication.setText(choix);
        explication.setTextColor(Color.BLACK);

        return choix;
    }

    public void onClickListenerBoutonPas(View view){
        /*Modifier MainActivity.class par la classe java de l'activity Pas)*/
        Intent intent = new Intent(CaloriesActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickListenerBoutonCalorie(View view){
        /*Soit on supprime ce listener soit on le garde*/
        Intent intent = new Intent(CaloriesActivity.this, CaloriesActivity.class);
        startActivity(intent);
    }

    public void onClickListenerBoutonMesInfo(View view){
        /*Modifier MainActivity.class par la classe java de l'activity Mes informations)*/
        Intent intent = new Intent(CaloriesActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickListenerBoutonSommeil(View view){
        /*Modifier MainActivity.class par la classe java de l'activity Sommeil)*/
        Intent intent = new Intent(CaloriesActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickListenerBoutonConsommeOK(View view){
        Toast.makeText(this, "bouton consomme ok clique", Toast.LENGTH_SHORT).show();
    }

    public float dureeStringToFloat(String duree){
        String[] horaire = duree.split(":");
        float res = Float.parseFloat(horaire[0]);

        switch (horaire[1]){
            case "15": res+=0.25F;
            case "30": res+=0.5F;
            case "45": res+=0.75F;
        }

        return res;
    }

    public void onClickListenerBoutonActiviteOK(View view){
        /*formule reel de calories_depense:
         *
         * calories_depense += calories_activite * poids * duree_activite
         */

        calories_depense += calories_activite * dureeStringToFloat(duree_activite);
        Log.println(Log.INFO, "onClickListenerBoutonActiviteOK", "calorie depense: " + calories_depense);
        Log.println(Log.INFO, "onClickListenerBoutonActiviteOK", "calorie activite: " + calories_activite);
        Log.println(Log.INFO, "onClickListenerBoutonActiviteOK", "duree: " + dureeStringToFloat(duree_activite));

        TextView textViewCalDepReel = findViewById(R.id.calories_depense_reel)
                .findViewById(R.id.val_calories_depense)
                .findViewById(R.id.text_calorie_depense_reel);

        setTextViewCalorieDepenseReel(textViewCalDepReel, calories_depense);
    }
}