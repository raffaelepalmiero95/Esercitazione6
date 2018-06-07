package com.example.antonio.esercitazione6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

public class CreaSegnalazioneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_segnalazione);

        Button fotocamera = findViewById(R.id.button_scatta_foto);
        Button allegato = findViewById(R.id.button_Inserisci_allegato);
        Button annulla = findViewById(R.id.button_annulla);
        Button invio = findViewById(R.id.button_invia);

        fotocamera.setOnClickListener(null);
        allegato.setOnClickListener(null);
        invio.setOnClickListener(null);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent torna_alla_home = new Intent(CreaSegnalazioneActivity.this,MainActivity.class);
                startActivity(torna_alla_home);
            }});
        invio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Segnalazione Inviata", Toast.LENGTH_LONG).show();
                Intent ricarica_pagina_segnalazione = new Intent (CreaSegnalazioneActivity.this,CreaSegnalazioneActivity.class);
                startActivity(ricarica_pagina_segnalazione);
            }});


    }
}
