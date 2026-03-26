package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activity de perfil do paciente.
 * Exibe dados pessoais, métricas históricas de sessões e pontos de fidelidade.
 * Gerencia o logout do sistema e acesso às configurações da conta.
 */
public class Perfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        
        // Ajuste de layout para as janelas do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retorno para a Activity anterior
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }

        // Navegação inferior com gerenciamento de Flags para otimizar o consumo de memória
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_profile);

            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(Perfil.this, homeMaya.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_agenda) {
                    Intent intent = new Intent(Perfil.this, Agenda_Consulta.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    Intent intent = new Intent(Perfil.this, MeuProgresso.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    return true;
                }
                return false;
            });
        }

        // Lógica para encerramento de sessão e retorno à tela inicial (Login/Welcome)
        if (findViewById(R.id.card_logout) != null) {
            findViewById(R.id.card_logout).setOnClickListener(v -> {
                finish();
            });
        }
    }
}
