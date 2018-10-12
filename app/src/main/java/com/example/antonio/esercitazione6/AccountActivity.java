package com.example.antonio.esercitazione6;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

//dichiarazione variabili
    private ImageView profile_img;
    private ImageView camera;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button log;
    private FirebaseAuth auth;
    private TextView prendi_nome;
    private TextView prendi_cognome;
    private TextView prendi_email;
    private TextView prendi_residenza;


    //12 ottobre
    private Uri filePath; //per caricare la foto dalla galleria sullo storage
    Bitmap photo; //per caricare la foto dalla fotocamera
    //reference di storage e database
    private StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //codice random per distinguere le segnalazioni
    public String uuid = UUID.randomUUID().toString();
    //



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //riferimenti agli id dei layout
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_account);
        profile_img = (ImageView) findViewById(R.id.imageView);
        camera = (ImageView)findViewById(R.id.imageButton);
        camera.setOnClickListener(this);

        //12 ottobre
        prendi_nome = findViewById(R.id.prendiNome);
        prendi_cognome = findViewById(R.id.prendiCognome);
        prendi_email = findViewById(R.id.prendiEmail);
        prendi_residenza = findViewById(R.id.prendiResidenza);
        //



//permessi di uso della fotocamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            camera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }



//pulsante di login
        log = findViewById(R.id.btn_log);
         log.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent log_account = new Intent(AccountActivity.this, LoginActivity.class);
                 startActivity(log_account);
             }
         });

         //se l'utente è loggato il pulsante si chiama gestione account e mette nell'array list i dati dell'utente da firebase
        if (auth.getCurrentUser() != null) {
            log.setText("Gestione Account");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            //12 ottobre
            storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference Ref = storageReference.child("Immagini/" + user.getUid() + "/Immagine_Profilo/" + "Profilo" );
            Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //per salvare l'url della foto e inviarlo anche al database collegato alla segnalazione
                    Uri URL_profilo = uri;
                    Glide.with(getApplicationContext()).load(URL_profilo).into(profile_img);
                }
            });

            //

            final DatabaseReference mRef = database.getReference("Users/" + user.getUid() + "/Dati_Utente");
            mRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    DettagliAccount dettagli = dataSnapshot.getValue(DettagliAccount.class);
                    prendi_nome.setText(dettagli.getNome().toString());
                    prendi_cognome.setText(dettagli.getCognome().toString());
                    prendi_email.setText(dettagli.getEmail().toString());
                    prendi_residenza.setText(dettagli.getResidenza().toString());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        //se l'utente non è loggato il pulsante si chiama login e non gestisci account
        else {
            log.setText("Login");
            camera.setVisibility(View.INVISIBLE);
        }

        //per tornare alla main
        Button home = findViewById(R.id.torna_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent button_torna_alla_home2 = new Intent(AccountActivity.this,MainActivity.class);
                startActivity(button_torna_alla_home2);
            }});
    }


    //metodo per caricare foto dalla fotocamera
    public void uploadFotocamera(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] b = stream.toByteArray();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Caricamento");
        progressDialog.show();
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference riversRef = storageReference.child("Immagini/" + user.getUid() + "/Immagine_Profilo/" + "Profilo");
        riversRef.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Immagine caricata con successo ", Toast.LENGTH_LONG).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Caricamento " + ((int) progress) + "%...");
                    }
                });
    }



    //metodo per caricare la foto dalla galleria
    private void uploadFile() {
        if (filePath != null) {

            storageReference = FirebaseStorage.getInstance().getReference();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Caricamento");
            progressDialog.show();
            final StorageReference riversRef = storageReference.child("Immagini/" + user.getUid() + "/Immagine_Profilo/" + "Profilo");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Immagine caricata con successo ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Caricamento " + ((int) progress) + "%...");
                        }
                    });
        }
    }


//click per selezionare la foto nell'account
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageButton:
                selectImage();
                break;
        }
    }

    //ciclo di vita dell'app
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //layout e uso di scelta immagine, da fotocamera, galleria o annulla
    private void selectImage() {
        final CharSequence[] items = { "Scatta foto", "Scegli dalla galleria",
                "Indietro" };
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle("Scegli da dove caricare l'immagine");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(AccountActivity.this);
                if (items[item].equals("Scatta foto")) {
                    userChoosenTask ="Scatta foto";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Scegli dalla galleria")) {
                    userChoosenTask ="Scegli dalla galleria";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Indietro")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //seleziona il file dalla galleria
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Seleziona file"),SELECT_FILE);
    }

    //apri fotocamera
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    //permessi per le foto
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Scatta foto"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Scegli dalla galleria"))
                        galleryIntent();
                } else {
                }
                break;
        }
    }

    //activity result per fotocamera o galleria
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


//salvataggio della stringa problema e posizione anche quando poi vado in mappa e torno

        if (requestCode == REQUEST_CAMERA && resultCode ==RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            profile_img.setImageBitmap(photo);
            uploadFotocamera();
        }

//prende le foto dalla galleria
        if (requestCode == SELECT_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_img.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadFile();
        }
    }
    }


