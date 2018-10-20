package com.example.antonio.esercitazione6;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;



public class MapActivity extends SignupActivity implements OnMapReadyCallback {
   //dichiarazione variabili
    private Button ok;
    public double Posizione[];
    Intent resultIntent = new Intent();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "La mappa è pronta");
        mMap = googleMap;
        //richiesta permessi di posizione
        if (mLocationPermissionsGranted)
        {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
           mMap.setMyLocationEnabled(true);

            //questo pulsante mi riporta alla posizione attuale anche se cerco altri luoghi sulla mappa
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            init();
        }
    }


    //dichiarazioni variabili
    private static final String TAG = "Mappa";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 18f;
    private EditText mSearchText;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = (EditText) findViewById(R.id.input_search);
        ok = (Button) findViewById(R.id.btn_ok);
        Intent vai_alla_mappa = getIntent();
        final String descrizione_problema = vai_alla_mappa.getStringExtra("Descrizione problema");
       ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String a = descrizione_problema;
                resultIntent.putExtra("Descrizione problema", a);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        getLocationPermission();
    }



    //inizializzo la mappa
    private void init(){
        Log.d(TAG, "inizializza");
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });
    }

    //geolocalizzazione
    private void geoLocate(){
        Log.d(TAG, "Geolocalizzazione");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geolocalizzazione: " + e.getMessage() );
        }
        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "trovata una posizione: " + address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    //prendi la posizione attuale del dispositivo
    private void getDeviceLocation(){
        Log.d(TAG, "Posizione Attuale");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Posizione Trovata");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "Posizione Attuale");
                        }else{
                            Log.d(TAG, "la posizione attuale è nulla");
                            Toast.makeText(MapActivity.this, "impossibile trovare la posizione attuale", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "SecurityException: " + e.getMessage() );
        }
    }

    //salvataggio della posizione ricercata, con zoom verso la zona ricercata
    private double[] moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "Questa è la tua posizione: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
        Posizione = new double[]{latLng.latitude, latLng.longitude};
        resultIntent.putExtra("Posizione", Posizione);
        setResult(RESULT_OK, resultIntent);
        return Posizione;
    }


    private void initMap(){
        Log.d(TAG, "inizializzo mappa");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }


    //permessi di localizzazione
    private void getLocationPermission(){
        Log.d(TAG, "Ho i permessi di localizzazione");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "Permessi");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "permessi falliti");
                            return;
                        }
                    }
                    Log.d(TAG, "permessi accordati");
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }
}





