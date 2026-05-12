package com.example.codigo_pi;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Tela de entrada (Welcome) que decide se o usuário deve logar ou ir para a Home.
 */
public class home extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        // Redireciona automaticamente se o usuário já estiver logado
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, homeMaya.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Ajuste de barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botão para novos usuários
        Button btnPrimeiroAcesso = findViewById(R.id.bottom_primeiroacesso);
        btnPrimeiroAcesso.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, PrimeiroAcesso.class);
            startActivity(intent);
        });

        // Botão para usuários já cadastrados
        Button acessar = findViewById(R.id.btn_acessar);
        acessar.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
