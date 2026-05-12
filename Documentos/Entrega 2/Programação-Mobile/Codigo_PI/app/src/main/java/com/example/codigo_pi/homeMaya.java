package com.example.codigo_pi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.codigo_pi.databinding.ActivityHomeMayaBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tela principal do aplicativo (Dashboard).
 */
public class homeMaya extends AppCompatActivity {

    private static final String TAG = "homeMaya";
    private ActivityHomeMayaBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityHomeMayaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupUI();
        setupListeners();
        setupNavigation();
        
        // Inicializa dados e permissões
        seedBiblioteca();
        solicitarPermissaoNotificacao();
        agendarLembreteDiario();
    }

    /**
     * Configura elementos visuais e saudações.
     */
    private void setupUI() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String nome = user.getDisplayName();
            if (nome != null && !nome.isEmpty()) {
                binding.txtBemVindo.setText(String.format("Olá, %s", nome));
            }
        }

        // Ajusta padding para barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Configura cliques nos cards e botões.
     */
    private void setupListeners() {
        // Abre a lista completa de exercícios
        binding.cardExercicios.setOnClickListener(v -> 
            startActivity(new Intent(this, BibliotecaExerciciosActivity.class)));

        // Abre a lista de tópicos de chat
        binding.cardChat.setOnClickListener(v -> 
            startActivity(new Intent(this, TopicListActivity.class)));

        // Abre a tela de agendamento
        binding.btnAgendarConsulta.setOnClickListener(v -> 
            startActivity(new Intent(this, Agenda_Consulta.class)));
    }

    /**
     * Gerencia a navegação da BottomNavigationView.
     */
    private void setupNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;

            Class<?> target = null;
            if (itemId == R.id.nav_agenda) target = Agenda_Consulta.class;
            else if (itemId == R.id.nav_progress) target = MeuProgresso.class;
            else if (itemId == R.id.nav_chat) target = TopicListActivity.class;
            else if (itemId == R.id.nav_profile) target = Perfil.class;

            if (target != null) {
                startActivity(new Intent(this, target));
                return true;
            }
            return false;
        });
    }

    /**
     * Verifica se a biblioteca de exercícios precisa ser populada (apenas se vazia).
     */
    private void seedBiblioteca() {
        db.collection("planos_exercicios").limit(1).get()
                .addOnSuccessListener(qs -> {
                    if (qs.isEmpty()) executarSeed();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Erro ao verificar biblioteca", e));
    }

    /**
     * Popula o Firestore com exercícios padrão caso não existam.
     */
    private void executarSeed() {
        List<Exercicio> lista = Arrays.asList(
            new Exercicio("coluna_01", "Alongamento de Coluna", "Deite-se e puxe os joelhos contra o peito por 30 segundos.", Arrays.asList("https://images.pexels.com/photos/4056535/pexels-photo-4056535.jpeg"), 3),
            new Exercicio("quadril_01", "Ponte de Quadril", "Deitado, eleve o quadril mantendo os pés firmes no chão.", Arrays.asList("https://images.pexels.com/photos/4498553/pexels-photo-4498553.jpeg"), 10),
            new Exercicio("pescoco_01", "Alongamento Cervical", "Incline a cabeça lateralmente até sentir um leve alongamento.", Arrays.asList("https://images.pexels.com/photos/3757376/pexels-photo-3757376.jpeg"), 5),
            new Exercicio("tronco_01", "Rotação de Tronco", "Sentado, gire o corpo suavemente para os lados.", Arrays.asList("https://images.pexels.com/photos/4056529/pexels-photo-4056529.jpeg"), 8),
            new Exercicio("perna_01", "Agachamento Assistido", "Use uma cadeira como apoio e desça o quadril devagar.", Arrays.asList("https://images.pexels.com/photos/4325451/pexels-photo-4325451.jpeg"), 12),
            new Exercicio("punho_01", "Extensão de Punhos", "Estique o braço e puxe a palma da mão para trás.", Arrays.asList("https://images.pexels.com/photos/4325461/pexels-photo-4325461.jpeg"), 10),
            new Exercicio("costas_01", "Gato e Camelo", "Em quatro apoios, arqueie e relaxe a coluna alternadamente.", Arrays.asList("https://images.pexels.com/photos/4056532/pexels-photo-4056532.jpeg"), 10)
        );

        for (Exercicio ex : lista) {
            db.collection("planos_exercicios").document(ex.getId()).set(ex);
        }
    }

    /**
     * Solicita permissão de notificações no Android 13+.
     */
    private void solicitarPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    /**
     * Agenda o Worker para lembrete diário de exercícios.
     */
    private void agendarLembreteDiario() {
        PeriodicWorkRequest reminderRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS)
                .addTag("exercicio_reminder")
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("LembreteExercicio", ExistingPeriodicWorkPolicy.KEEP, reminderRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Garante que o item da Home esteja selecionado na barra inferior
        if (binding != null) binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }
}
