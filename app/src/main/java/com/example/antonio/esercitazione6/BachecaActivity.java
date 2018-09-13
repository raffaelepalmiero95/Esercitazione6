package com.example.antonio.esercitazione6;

        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;

        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

        import java.util.ArrayList;

public class BachecaActivity extends AppCompatActivity {
    private ListView lista;
    private ArrayList <String> segnalazioni = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacheca);
        lista = findViewById(R.id.lista_segnalazioni);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, segnalazioni);
        lista.setAdapter(arrayAdapter);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mRef = database.getReference("Users/" + user.getUid() + "/Segnalazioni");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String value = dataSnapshot.getValue(String.class);
                segnalazioni.add(value);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Button TornaHome = findViewById(R.id.button_torna_alla_home2);
        TornaHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent button_torna_alla_home2 = new Intent(BachecaActivity.this,MainActivity.class);
                startActivity(button_torna_alla_home2);
            }});
    }
}
