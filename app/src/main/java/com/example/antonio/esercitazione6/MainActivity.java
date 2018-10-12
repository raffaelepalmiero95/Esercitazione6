package com.example.antonio.esercitazione6;

import android.Manifest;
import android.accounts.Account;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    //dichiarazione variabili
    private Button login;
    private Button bacheca;
    private Button segnala;
    private FirebaseAuth auth;
    private TextView mostra_evento;


    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //riferimenti agli id
         auth = FirebaseAuth.getInstance();
         login=findViewById(R.id.button_login);
         bacheca= findViewById(R.id.button_vai_alla_bacheca);
         segnala=findViewById(R.id.button_aggiungi_segnalazione);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passa_al_login = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(passa_al_login);
            }});
        bacheca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, BachecaActivity.class));
                    finish();
                }
                else {
                    Intent passa_alla_bacheca = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(passa_alla_bacheca);
                }
            }});
        segnala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, CreaSegnalazioneActivity.class));
                    finish();
                }
                else {
                    Intent passa_alla_segnalazione = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(passa_alla_segnalazione);
                }
            }});
        //per vedere in real time quello che viene scritto in evento sul database
        mostra_evento = findViewById(R.id.evento);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = database.getReference("Evento");
        mRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                String evento = String.valueOf(dataSnapshot.getValue());
                mostra_evento.setText(evento);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        //permessi della fotocamera
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
       }



    }



}
