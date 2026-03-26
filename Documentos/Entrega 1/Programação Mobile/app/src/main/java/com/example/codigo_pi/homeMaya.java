package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * Activity principal do paciente (Dashboard).
 * Centraliza o acesso às funcionalidades de exercícios, chat e agendamento,
 * servindo como ponto de partida após o login na plataforma Clínica Maya.
 */
public class homeMaya extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_maya);

        // Aplicação de Insets para compatibilidade com telas de borda a borda (Edge-to-Edge)
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Navegação direta para a visualização do plano de exercícios RPG
        MaterialCardView cardExercicios = findViewById(R.id.card_exercicios);
        if (cardExercicios != null) {
            cardExercicios.setOnClickListener(v -> {
                Intent intent = new Intent(homeMaya.this, TelaExercicios.class);
                startActivity(intent);
            });
        }

        // Navegação para o sistema de agendamento de consultas presenciais/remotas
        MaterialButton btnAgendar = findViewById(R.id.btn_agendar_consulta);
        if (btnAgendar != null) {
            btnAgendar.setOnClickListener(v -> {
                Intent intent = new Intent(homeMaya.this, Agenda_Consulta.class);
                startActivity(intent);
            });
        }

        // Configuração da barra de navegação inferior e gerenciamento de estados
        bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);

            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_agenda) {
                    startActivity(new Intent(this, Agenda_Consulta.class));
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    startActivity(new Intent(this, MeuProgresso.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(this, Perfil.class));
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Sincroniza o estado visual da Bottom Navigation ao retornar para esta Activity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }
}
