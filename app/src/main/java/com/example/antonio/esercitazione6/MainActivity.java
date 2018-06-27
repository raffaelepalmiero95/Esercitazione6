package com.example.antonio.esercitazione6;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private Button login;
    private Button bacheca;
    private Button segnala;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




         login=findViewById(R.id.button_login);
         bacheca= findViewById(R.id.button_vai_alla_bacheca);
         segnala=findViewById(R.id.button_aggiungi_segnalazione);

//in questo login bisogna inserire un if dove se il login è stato effettuato non apre loginactivity ma apre accountactivity
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                      Intent passa_al_login = new Intent(MainActivity.this, LoginActivity.class);
                      startActivity(passa_al_login);

            }});


        bacheca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passa_alla_bacheca = new Intent(MainActivity.this,BachecaActivity.class);
                startActivity(passa_alla_bacheca);
            }});


        segnala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passa_alla_segnalazione = new Intent(MainActivity.this,CreaSegnalazioneActivity.class);
                startActivity(passa_alla_segnalazione);
            }});



    }



    public boolean isServicesOK()
        {
        Log.d(TAG, "Il servizio è funzionante la versione di google è ");
        int avaible = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (avaible == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Il servizio è funzionante e sta lavorando");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(avaible)) {
            Log.d(TAG, "Il servizio non è funzionante ma possiamo correggerlo");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, avaible, ERROR_DIALOG_REQUEST);
            dialog.show();
            ;
        } else {
            Toast.makeText(this, "Non puoi richiedere la mappa", Toast.LENGTH_SHORT).show();
        }
        return false;
        }


}
