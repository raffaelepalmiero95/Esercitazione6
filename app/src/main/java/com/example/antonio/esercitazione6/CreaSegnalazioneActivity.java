package com.example.antonio.esercitazione6;

import android.Manifest;
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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class CreaSegnalazioneActivity extends MapActivity implements View.OnClickListener{

    //dichiarazione variabili
    private ImageView anteprima;
    private ImageView fotocamera;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 3;
    private Button annulla;
    private  Button invio;
    public EditText problema;
    private ImageView mappa;
    public double posizione[];
    private boolean click = true;
    private Uri filePath; //per caricare la foto dalla galleria sullo storage

    //reference di storage e database
    private StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //codice random per distinguere le segnalazioni
    public String uuid = UUID.randomUUID().toString();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_segnalazione);
        //riferimenti agli id nel layout
        anteprima = (ImageView)findViewById(R.id.image_anteprima);
        fotocamera = (ImageView)findViewById(R.id.imageButton2);
        fotocamera.setOnClickListener(this);
         annulla = findViewById(R.id.button_annulla);
         invio = findViewById(R.id.button_invia);
         problema = findViewById(R.id.text_problema);
         mappa = findViewById(R.id.imageButton3);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent torna_alla_home = new Intent(CreaSegnalazioneActivity.this,MainActivity.class);
                startActivity(torna_alla_home);
            }});

        mappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cliccando su mappa salva la posizione sul db cercata in mappa, se non clicchi su mappa salva la posizione attuale del dispositivo
                click=false;
                Intent vai_alla_mappa = new Intent(CreaSegnalazioneActivity.this,MapActivity.class);
                vai_alla_mappa.putExtra("Descrizione problema", problema.getText().toString());
                startActivityForResult(vai_alla_mappa,1);
            }});

           invio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //scrittura sul database della segnalazione
                myRef.child("Users").child(user.getUid()).child("Segnalazioni").child(uuid).child("Descrizione_Problema").setValue(problema.getText().toString());
                myRef.child("Segnalazioni_Comune").child(user.getUid()).child(uuid).child("Descrizione_Problema").setValue(problema.getText().toString());

                if (click){
                    myRef.child("Users").child(user.getUid()).child("Segnalazioni").child(uuid).child("Latitudine").setValue(Posizione[0]);
                    myRef.child("Users").child(user.getUid()).child("Segnalazioni").child(uuid).child("Longitudine").setValue(Posizione[1]);

                    myRef.child("Segnalazioni_Comune").child(user.getUid()).child(uuid).child("Latitudine").setValue(Posizione[0]);
                    myRef.child("Segnalazioni_Comune").child(user.getUid()).child(uuid).child("Longitudine").setValue(Posizione[1]);
                }
                else {
                    myRef.child("Users").child(user.getUid()).child("Segnalazioni").child(uuid).child("Latitudine").setValue(posizione[0]);
                    myRef.child("Users").child(user.getUid()).child("Segnalazioni").child(uuid).child("Longitudine").setValue(posizione[1]);

                    myRef.child("Segnalazioni_Comune").child(user.getUid()).child(uuid).child("Latitudine").setValue(posizione[0]);
                    myRef.child("Segnalazioni_Comune").child(user.getUid()).child(uuid).child("Longitudine").setValue(posizione[1]);
                }

                //metodo per caricare le foto sul database
                uploadFile();

                Intent fine_segnalazione = new Intent (CreaSegnalazioneActivity.this,MainActivity.class);
                startActivity(fine_segnalazione);
            }});

           //permessi della fotocamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fotocamera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }


//metodo per caricare la foto dalla galleria
    private void uploadFile() {
        if (filePath != null) {

            storageReference = FirebaseStorage.getInstance().getReference();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Caricamento");
            progressDialog.show();
            final StorageReference riversRef = storageReference.child("Immagini/" + user.getUid() + "/" + UUID.randomUUID().toString());
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "File caricato con successo ", Toast.LENGTH_LONG).show();

                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //per salvare l'url della foto e inviarlo anche al database collegato alla segnalazione
                                    Uri downloadUri = uri;
                                    myRef.child("Users").child(user.getUid()).child("Segnalazioni").child(uuid).child("URL").setValue(downloadUri.toString());
                                    myRef.child("Segnalazioni_Comune").child(user.getUid()).child(uuid).child("URL").setValue(downloadUri.toString());

                                }
                            });

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

//click del pulsante fotocamera
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageButton2:
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

//seleziona immagine da fotocamera o galleria
    private void selectImage() {
        final CharSequence[] items = { "Scatta foto", "Scegli dalla galleria",
                "Indietro" };
        AlertDialog.Builder builder = new AlertDialog.Builder(CreaSegnalazioneActivity.this);
        builder.setTitle("Scegli da dove caricare l'immagine");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(CreaSegnalazioneActivity.this);
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

//scegli la foto dalla galleria
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleziona file"),SELECT_FILE);
    }


//scegli la foto dalla fotocamera
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


//permessi per la fotocamera e il salvataggio nella memoria del dispositivo
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


//activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//salvataggio della stringa problema e posizione anche quando poi vado in mappa e torno
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String descrizione_problema = data.getStringExtra("Descrizione problema");
                problema.setText(descrizione_problema);
                posizione = data.getDoubleArrayExtra("Posizione");
            }
        }


        //prende le foto dalla fotocamera
        if (requestCode == 0) {
                if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
        }


//prende le foto dalla galleria
        if (requestCode == SELECT_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                anteprima.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




//metodo per prendere le foto dalla fotocamera e salvarle sul dispositivo
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        anteprima.setImageBitmap(thumbnail);
    }


}
