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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends CreaSegnalazioneActivity { //messo CreaSegnalazione al posto di appcompatactivity
    public EditText inputEmail, inputPassword,inputNome,inputCognome,inputResidenza;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        //aggiunto da anto
        inputNome = (EditText) findViewById(R.id.registra_nome);
        inputCognome = (EditText) findViewById(R.id.registra_cognome);
        inputResidenza = (EditText) findViewById(R.id.registra_residenza);
        //fine

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



                //aggiunta anto
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String nome = inputNome.getText().toString().trim();
                String cognome = inputCognome.getText().toString().trim();
                String residenza = inputResidenza.getText().toString().trim();
                //fine


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Inserisci l'indirizzo Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Inserisci la password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password troppo corta, inserisci almeno 6 caratteri", Toast.LENGTH_SHORT).show();
                    return;
                }


                //aggiunto da me

                //al posto di users dobbiamo cercare di mettere una mail di chi è loggato


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                myRef.child("Users").child("Nome").setValue(nome);
                myRef.child("Users").child("Cognome").setValue(cognome);
                myRef.child("Users").child("Residenza").setValue(residenza);
                //fine

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                Toast.makeText(SignupActivity.this, "Account creato con successo " , Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {

                                    Toast.makeText(SignupActivity.this, "Email non valida ",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    //finish();
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


