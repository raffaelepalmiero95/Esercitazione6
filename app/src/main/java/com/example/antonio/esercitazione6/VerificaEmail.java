package com.example.antonio.esercitazione6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificaEmail extends AppCompatActivity
{

    private Button verifica;
    private FirebaseAuth auth;

    private Button rimanda;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifica_email);
        verifica = findViewById(R.id.button);
        rimanda = findViewById(R.id.button2);
        verifica.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user.isEmailVerified())
                {
                    startActivity(new Intent(VerificaEmail.this, CreaSegnalazioneActivity.class));
                    finish();
                }
                else {
                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(VerificaEmail.this, "E' necessario verificare la tua email!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        rimanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(VerificaEmail.this, "Email di verifica inviata nuovamente", Toast.LENGTH_SHORT).show();
                        }
                        else{ Toast.makeText(VerificaEmail.this, "Email di verifica non inviata", Toast.LENGTH_SHORT).show();}
                    }
                });
            }
        });
    }
}
