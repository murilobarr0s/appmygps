package com.example.mygps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ResultadoActivity extends AppCompatActivity {
    private TextView tvresultado;
    private Button btvoltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        tvresultado = findViewById(R.id.tvResultado);
        btvoltar = findViewById(R.id.btVoltar);

        Intent intent = getIntent();
        String metros = ""+intent.getStringExtra("metros");
        tvresultado.setText(metros+" m");

        btvoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ResultadoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
