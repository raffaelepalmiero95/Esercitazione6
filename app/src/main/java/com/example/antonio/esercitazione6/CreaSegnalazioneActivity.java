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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class CreaSegnalazioneActivity extends MapActivity implements View.OnClickListener{ //map al posto di appcomp 14 settembre

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

    //21 settembre
    private Uri filePath;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    //




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_crea_segnalazione);
            setUI();
            setUITEXT();
        } catch (Exception e){
            e.printStackTrace();
        }

         fotocamera = findViewById(R.id.imageButton2);
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
                click=false;
                Intent vai_alla_mappa = new Intent(CreaSegnalazioneActivity.this,MapActivity.class);
                vai_alla_mappa.putExtra("Descrizione problema", problema.getText().toString());
                startActivityForResult(vai_alla_mappa,1);
            }});

           invio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                //prova 21 settembre
                myRef.child("Users").child(user.getUid()).child("Segnalazioni").child(UUID.randomUUID().toString()).setValue(problema.getText().toString());
                //


                //myRef.child("Users").child(user.getUid()).child("Segnalazioni").push().setValue(problema.getText().toString());
                //invece del push provare con UUID

                //questo if è funzionante per salvare le posizioni sia da mappa che senza mappa
                /*
               if (click){
                   myRef.child("Users").child(user.getUid()).child("Segnalazioni").child("Posizione").child("Latitudine e Longitudine").setValue(Posizione[0] + " e " + Posizione[1]);
               }
               else  {
                   myRef.child("Users").child(user.getUid()).child("Segnalazioni").child("Posizione").child("Latitudine e Longitudine").setValue(posizione[0] + " e " + posizione[1]);
               }
               */

               //non funzionante
                //myRef.child("Users").child(user.getUid()).child("Segnalazioni").child(UUID.fromString(problema.getText().toString()).toString()).child("Posizione").child("Latitudine e Longitudine").setValue(Posizione[0] + " e " + Posizione[1]);
               // myRef.child("Users").child(user.getUid()).child("Segnalazioni").child("Posizione").child("Latitudine e Longitudine").setValue(posizione[0] + " e " + posizione[1]);
                //

                //21 settembre
                uploadFile();




                //


                Intent fine_segnalazione = new Intent (CreaSegnalazioneActivity.this,MainActivity.class);
                startActivity(fine_segnalazione);
            }});

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            fotocamera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }


    //21 settembre
    private void uploadFile() {

        if (filePath != null) {


            storageReference = FirebaseStorage.getInstance().getReference();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Caricamento");
            progressDialog.show();

            StorageReference riversRef = storageReference.child("Immagini" + UUID.randomUUID().toString());
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();


                            Toast.makeText(getApplicationContext(), "File caricato con successo ", Toast.LENGTH_LONG).show();
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
    //




    private void setUITEXT() {
    }

    private void setUI() {
        anteprima = (ImageView)findViewById(R.id.image_anteprima);
        fotocamera = (ImageView)findViewById(R.id.imageButton2);
        fotocamera.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageButton2:
                selectImage();
                break;
        }
    }


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


    private void selectImage() {
        final CharSequence[] items = { "Scatta foto", "Scegli dalla galleria",
                "Indietro" };
        AlertDialog.Builder builder = new AlertDialog.Builder(CreaSegnalazioneActivity.this);
        builder.setTitle("Foto aggiunta");
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


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleziona file"),SELECT_FILE);
    }



    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }



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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String descrizione_problema = data.getStringExtra("Descrizione problema");
                problema.setText(descrizione_problema);
                posizione = data.getDoubleArrayExtra("Posizione");
            }
        }

        /* semi funzionante
// non mette l'immagine dalla galleria alla view anteprima
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
            }
        }
*/


        //funzionante sembra
        //21 settembre
        if (requestCode == 0) {
                if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
        }
/*
        if (requestCode == 3) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
        }
        //
*/

        //21 settembre
        if (requestCode == SELECT_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                anteprima.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //

    }



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

/*
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        anteprima.setImageBitmap(bm);
    }
*/

}
