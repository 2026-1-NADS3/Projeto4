package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

/**
 * Activity responsável pela exibição detalhada do plano de exercícios de RPG.
 * Exibe uma imagem de capa do exercício e permite iniciar o guia passo a passo
 * em tela cheia ao clicar no botão 'Começar'.
 */
public class TelaExercicios extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_exercicios);

        // Aplicação de Insets para compatibilidade com telas modernas (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retorno para a tela de progresso ou dashboard anterior
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Imagem de capa do exercício (Utilizando dor_lombar como capa original)
        ImageView imgCapa = findViewById(R.id.img_capa_exercicio);
        imgCapa.setImageResource(R.drawable.dor_lombar);

        // Lógica do botão "Começar": Inicia o guia passo a passo em tela cheia (Fullscreen Activity)
        MaterialButton btnComecar = findViewById(R.id.btn_comecar);
        btnComecar.setOnClickListener(v -> {
            Intent intent = new Intent(TelaExercicios.this, FullscreenImageActivity.class);
            startActivity(intent);
        });

        // Gerenciamento da navegação inferior para transição entre módulos do app
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.nav_progress);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(TelaExercicios.this, homeMaya.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_progress) {
                Intent intent = new Intent(TelaExercicios.this, MeuProgresso.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(TelaExercicios.this, Perfil.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
