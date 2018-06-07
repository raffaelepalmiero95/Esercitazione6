package com.example.antonio.esercitazione6;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

public class BachecaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacheca);

        Button TornaHome=findViewById(R.id.button_torna_alla_home2);

        TornaHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent button_torna_alla_home2 = new Intent(BachecaActivity.this,MainActivity.class);
                startActivity(button_torna_alla_home2);
            }});

    }
}
