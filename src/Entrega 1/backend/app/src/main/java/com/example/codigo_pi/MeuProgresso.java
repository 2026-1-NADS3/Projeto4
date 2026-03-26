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
import com.google.android.material.button.MaterialButton;

/**
 * Activity que centraliza o monitoramento do progresso do paciente.
 * Exibe métricas de conclusão semanal (Análise numérica) e lista de atividades RPG.
 * Permite o início imediato do treino prescrito via Intent explícita.
 */
public class MeuProgresso extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meu_progresso);

        // Ajuste de layout para barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) ;
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Encerra a visualização atual para retorno de navegação
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }

        // Navegação para a visualização detalhada de exercícios RPG
        MaterialButton btnIniciar = findViewById(R.id.btn_iniciar);
        if (btnIniciar != null) {
            btnIniciar.setOnClickListener(v -> {
                Intent intent = new Intent(MeuProgresso.this, TelaExercicios.class);
                startActivity(intent);
            });
        }

        // Configuração do BottomNavigationView e gerenciamento de transição entre telas
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_progress);

            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, homeMaya.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_agenda) {
                    startActivity(new Intent(this, Agenda_Consulta.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(this, Perfil.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}
