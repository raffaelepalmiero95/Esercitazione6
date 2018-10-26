package com.example.antonio.esercitazione6;

import android.Manifest;
import android.accounts.Account;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Permissions;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity { //map invece di app compat 20 ottobre
    //dichiarazione variabili
    private Button login;
    private Button bacheca;
    private Button segnala;
    private FirebaseAuth auth;
    private TextView mostra_evento;

    //check connessione internet
    private BroadcastReceiver broadcastReceiver;

    //check per il gps
    LocationManager locationManager ;
    boolean GpsStatus ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //chiede i 3 permessi, gps, camera e storage
        int Permission_All = 1;
        String [] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.MEDIA_CONTENT_CONTROL, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermission(this, Permissions)){
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }


        //check connessione internet
        checkInternetActivity();


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
                }
                else {
                    Intent passa_alla_bacheca = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(passa_alla_bacheca);
                }
                finish(); //21 ottobre
            }});
        segnala.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                    //se non è attivo il gps, ma l'utente è loggato chiede di attivare il gps altrimenti chiede il login
                    if (auth.getCurrentUser() != null)
                    {
                        CheckGpsStatus() ;
                        if(GpsStatus == true)
                        {
                            startActivity(new Intent(MainActivity.this, CreaSegnalazioneActivity.class));
                        }else
                            {
                                Toast.makeText(MainActivity.this, "Attiva il GPS per poter inviare una segnalazione", Toast.LENGTH_SHORT).show();
                                return;
                            }
                    }else
                        {
                            Intent passa_alla_segnalazione = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(passa_alla_segnalazione);
                        }
                finish(); //21 ottobre
            }
        });
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

    }

    //check di connessione internet
    private void checkInternetActivity(){
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
         broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int[] type = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE};
                if (CheckInternetBroadcast.isNetworkAvailable(context, type)==true){
                    return;
                }else{
                    Intent check = new Intent(MainActivity.this, ErroreConnessione.class);
                    startActivity(check);
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    //check per la posizione
    public void CheckGpsStatus(){

        locationManager = (LocationManager)getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    //richiesta multipla di permessi
    public static boolean hasPermission(Context context, String...permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context!=null && permissions != null) {
            for(String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context, permission ) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }





}
