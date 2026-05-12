package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity que exibe o progresso do usuário, histórico e treinos.
 */
public class MeuProgresso extends AppCompatActivity {

    private RecyclerView rvHistorico, rvTreinos;
    private HistoricoAdapter adapterHistorico;
    private TreinoAdapter adapterTreino;
    private List<Historico> historicoList;
    private List<Treino> treinoList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private AppDatabase localDb;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meu_progresso);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        localDb = AppDatabase.getInstance(this);

        // Ajuste de padding para barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botão voltar
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Botão para abrir biblioteca de exercícios
        MaterialButton btnIniciar = findViewById(R.id.btn_iniciar);
        if (btnIniciar != null) {
            btnIniciar.setOnClickListener(v -> startActivity(new Intent(MeuProgresso.this, BibliotecaExerciciosActivity.class)));
        }

        // Configuração do histórico
        rvHistorico = findViewById(R.id.rv_historico);
        historicoList = new ArrayList<>();
        adapterHistorico = new HistoricoAdapter(historicoList);
        rvHistorico.setLayoutManager(new LinearLayoutManager(this));
        rvHistorico.setAdapter(adapterHistorico);

        // Configuração da lista de treinos
        rvTreinos = findViewById(R.id.rv_treinos);
        treinoList = new ArrayList<>();
        adapterTreino = new TreinoAdapter(treinoList, treino -> {
            Intent intent = new Intent(MeuProgresso.this, TelaExercicios.class);
            intent.putExtra("TREINO_ID", treino.getId());
            startActivity(intent);
        });
        rvTreinos.setLayoutManager(new LinearLayoutManager(this));
        rvTreinos.setAdapter(adapterTreino);

        carregarDadosLocais();
        sincronizarComFirestore();
        setupBottomNavigation();
    }

    /**
     * Configura a barra de navegação inferior.
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_progress);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_progress) return true;
                
                Class<?> target = null;
                if (itemId == R.id.nav_home) target = homeMaya.class;
                else if (itemId == R.id.nav_agenda) target = Agenda_Consulta.class;
                else if (itemId == R.id.nav_chat) target = TopicListActivity.class;
                else if (itemId == R.id.nav_profile) target = Perfil.class;

                if (target != null) {
                    startActivity(new Intent(this, target));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Carrega treinos e histórico do banco local (Room).
     */
    private void carregarDadosLocais() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();

        executor.execute(() -> {
            List<Treino> localTreinos = localDb.appDao().getTreinosByUserId(userId);
            List<Historico> localHistorico = localDb.appDao().getHistoricoByUserId(userId);

            runOnUiThread(() -> {
                treinoList.clear();
                treinoList.addAll(localTreinos);
                adapterTreino.notifyDataSetChanged();

                historicoList.clear();
                historicoList.addAll(localHistorico);
                adapterHistorico.notifyDataSetChanged();
                
                atualizarIndicadores(localHistorico);
            });
        });
    }

    /**
     * Atualiza os campos de texto com estatísticas de dor e exercícios.
     */
    private void atualizarIndicadores(List<Historico> historicos) {
        int total = historicos.size();
        double somaDor = 0;
        for (Historico h : historicos) {
            somaDor += h.getNivel_dor();
        }
        double media = total > 0 ? somaDor / total : 0.0;

        TextView txtTotal = findViewById(R.id.txt_total_exercicios);
        TextView txtMedia = findViewById(R.id.txt_media_dor);
        if (txtTotal != null) txtTotal.setText(String.valueOf(total));
        if (txtMedia != null) txtMedia.setText(String.format(Locale.getDefault(), "%.1f", media));
    }

    /**
     * Sincroniza dados do histórico e treinos com o Firestore.
     */
    private void sincronizarComFirestore() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();

        // Sincroniza histórico de execução
        db.collection("historico_execucao")
                .whereEqualTo("usuario_id", userId)
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Historico> novosHistoricos = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Historico h = document.toObject(Historico.class);
                            h.setId(document.getId());
                            novosHistoricos.add(h);
                        }
                        executor.execute(() -> {
                            localDb.appDao().insertHistoricos(novosHistoricos);
                            carregarDadosLocais();
                        });
                    }
                });

        // Sincroniza treinos do paciente
        db.collection("treinos_pacientes")
                .whereEqualTo("usuarioId", userId)
                .orderBy("dataCriacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Treino> novosTreinos = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Treino treino = doc.toObject(Treino.class);
                            treino.setId(doc.getId());
                            novosTreinos.add(treino);
                        }
                        executor.execute(() -> {
                            localDb.appDao().insertTreinos(novosTreinos);
                            carregarDadosLocais();
                        });
                    }
                });
    }
}
