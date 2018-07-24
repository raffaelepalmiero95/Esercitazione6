package com.example.antonio.esercitazione6;

import android.accounts.Account;
import android.app.Dialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private Button login;
    private Button bacheca;
    private Button segnala;
    private FirebaseAuth auth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

    }


}
