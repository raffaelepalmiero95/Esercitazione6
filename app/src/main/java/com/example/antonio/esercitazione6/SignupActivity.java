package com.example.antonio.esercitazione6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    //dichiarazione variabili
    public EditText inputEmail, inputPassword,inputNome,inputCognome,inputResidenza;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //riferimento agli id
        auth = FirebaseAuth.getInstance();
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        inputNome = (EditText) findViewById(R.id.registra_nome);
        inputCognome = (EditText) findViewById(R.id.registra_cognome);
        inputResidenza = (EditText) findViewById(R.id.registra_residenza);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se il campo email è vuoto richiede dati
                if (TextUtils.isEmpty(inputEmail.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Inserisci l'indirizzo Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                //se il campo password è vuoto richiede dati
                if (TextUtils.isEmpty(inputPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Inserisci la password", Toast.LENGTH_SHORT).show();
                    return;
                }
                //se la password è minore di 6 caratteri da errore
                if (inputPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password troppo corta, inserisci almeno 6 caratteri", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                // crea l'account con successo
                auth.createUserWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "Account creato con successo " , Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                //al momento della registrazione chiede una email valida
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Email non valida ",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    //se la mail è valida salva questi dati sul database
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    myRef.child("Users").child(user.getUid()).child("Dati_Utente").child("Dato1").setValue("Nome : " + inputNome.getText().toString());
                                    myRef.child("Users").child(user.getUid()).child("Dati_Utente").child("Dato2").setValue("Cognome : " + inputCognome.getText().toString());
                                    myRef.child("Users").child(user.getUid()).child("Dati_Utente").child("Dato3").setValue("Email : " + inputEmail.getText().toString());
                                    myRef.child("Users").child(user.getUid()).child("Dati_Utente").child("Dato4").setValue("Residenza : " + inputResidenza.getText().toString());
                                    //10 ottobre
                                    myRef.child("Segnalazioni_Comune").child(user.getUid()).child("Dati_Utente").child("Dato1").setValue("Nome : " + inputNome.getText().toString());
                                    myRef.child("Segnalazioni_Comune").child(user.getUid()).child("Dati_Utente").child("Dato2").setValue("Cognome : " + inputCognome.getText().toString());
                                    myRef.child("Segnalazioni_Comune").child(user.getUid()).child("Dati_Utente").child("Dato3").setValue("Email : " + inputEmail.getText().toString());
                                    myRef.child("Segnalazioni_Comune").child(user.getUid()).child("Dati_Utente").child("Dato4").setValue("Residenza : " + inputResidenza.getText().toString());
                                    //
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}

