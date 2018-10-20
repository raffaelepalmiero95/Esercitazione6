package com.example.antonio.esercitazione6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckInternetBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        if (isNetworkAvailable(context,type) == false){
            return;
        }else{
            Toast.makeText(context, "Nessuna connessione a Internet ", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNetworkAvailable(Context context, int[] typeNetworks){
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            for (int typeNetwork: typeNetworks){
                NetworkInfo networkInfo = cm.getNetworkInfo(typeNetwork);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        } catch (Exception e){ return false; }
        return false;
    }
}
