package com.example.antonio.esercitazione6;

        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.Toast;

        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.UUID;

public class BachecaActivity extends AppCompatActivity {

    //in realtà questa è le mie segnalazioni e non più bacheca

    //dichiarazione array list
    private ListView lista;
    private ArrayList <String> segnalazioni = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //ci rifacciamo al percorso su firebase che ci serve per portare le segnalazioni nell'array list
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacheca);
        lista = findViewById(R.id.lista_segnalazioni);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, segnalazioni);
        lista.setAdapter(arrayAdapter);


        //prova 17 ottobre click elementi lista
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id){
                final String riga = (String) adattatore.getItemAtPosition(pos);
                Toast.makeText(getApplicationContext(), "Descrizione problema: " + riga, Toast.LENGTH_LONG).show();
            }
        });
        //


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mRef = database.getReference("Users/" + user.getUid() + "/Segnalazioni"  );
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot,@Nullable String s) {
//ci rifacciamo ai getter e setter in dettaglio segnalazione e prendiamo da Segnalazioni su database solo la descrizione
                    DettaglioSegnalazione dettaglio = dataSnapshot.getValue(DettaglioSegnalazione.class);
                    segnalazioni.add(dettaglio.getDescrizione_Problema());
                    arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        //pulsante per tornare alla main
        Button TornaHome = findViewById(R.id.button_torna_alla_home2);
        TornaHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent button_torna_alla_home2 = new Intent(BachecaActivity.this,MainActivity.class);
                startActivity(button_torna_alla_home2);
                finish(); //aggiunto il 16 ottobre per chiuedere la lista dopo essere tornati in bacheca
            }});
    }
}
