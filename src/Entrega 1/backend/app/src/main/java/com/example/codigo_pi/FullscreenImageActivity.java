package com.example.codigo_pi;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity que exibe o guia passo a passo do exercício em tela cheia.
 * Permite ao paciente deslizar entre as imagens para entender a execução correta.
 */
public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        ViewPager2 viewPager = findViewById(R.id.viewPager_fullscreen);
        ImageButton btnClose = findViewById(R.id.btn_close_fullscreen);
        TextView txtContador = findViewById(R.id.txt_contador);

        // Lista de imagens do passo a passo atualizada com as fotos solicitadas
        List<Integer> imagens = new ArrayList<>();
        imagens.add(R.drawable.exlombar);  // Imagem exlombar.jpg
        imagens.add(R.drawable.exlombar2); // Imagem exlombar2.webp

        // Reutiliza o ExercicioAdapter para o carrossel em tela cheia
        ExercicioAdapter adapter = new ExercicioAdapter(imagens);
        viewPager.setAdapter(adapter);

        // Atualiza o contador de páginas (ex: 1 / 2) conforme o paciente desliza
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                txtContador.setText((position + 1) + " / " + imagens.size());
            }
        });

        // Retorna para a tela de orientações do exercício
        btnClose.setOnClickListener(v -> finish());
    }
}
