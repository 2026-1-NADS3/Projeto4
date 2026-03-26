package com.example.codigo_pi;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // BOTÃO PRIMEIRO ACESSO
        Button btnPrimeiroAcesso = findViewById(R.id.bottom_primeiroacesso);
        btnPrimeiroAcesso.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, PrimeiroAcesso.class);
            startActivity(intent);
        });

        // BOTÃO ACESSAR abrindo a tela de login (MainActivity)
        Button acessar = findViewById(R.id.btn_acessar);
        acessar.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, MainActivity.class);
            startActivity(intent);
        });
    }
}