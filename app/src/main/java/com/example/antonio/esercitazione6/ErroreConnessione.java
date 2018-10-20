package com.example.antonio.esercitazione6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ErroreConnessione extends AppCompatActivity {

    private Button btn_riprova;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errore_connessione);

        btn_riprova = findViewById(R.id.riprova);

        btn_riprova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetActivity();
            }
        });
    }

    private void checkInternetActivity(){
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int[] type = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE};
                if (CheckInternetBroadcast.isNetworkAvailable(context, type)==true){
                    Intent main = new Intent(ErroreConnessione.this, MainActivity.class);
                    startActivity(main);
                    finish();
                }else{
                    return;
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
