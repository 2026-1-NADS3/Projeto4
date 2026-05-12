package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.codigo_pi.databinding.ActivityTelaExerciciosBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tela de detalhes do exercício, onde o usuário visualiza as instruções antes de começar.
 */
public class TelaExercicios extends AppCompatActivity {

    private static final String TAG = "TelaExercicios";
    private ActivityTelaExerciciosBinding binding;
    private FirebaseFirestore db;
    
    private Exercicio exercicioAtual;
    private String treinoId;
    private String exercicioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityTelaExerciciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        
        // Recupera IDs passados por Intent (pode vir da biblioteca ou de um treino)
        treinoId = getIntent().getStringExtra("TREINO_ID");
        exercicioId = getIntent().getStringExtra("EXERCICIO_ID");

        setupUI();
        setupListeners();
        setupNavigation();
        
        carregarDados();
    }

    private void setupUI() {
        // Ajuste de preenchimento para as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        // Inicia a execução do exercício em tela cheia
        binding.btnComecar.setOnClickListener(v -> {
            if (exercicioAtual != null) {
                iniciarExecucao();
            } else {
                Toast.makeText(this, "Carregando exercício...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_progress);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
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

    /**
     * Define qual exercício carregar baseado nos IDs recebidos.
     */
    private void carregarDados() {
        if (exercicioId != null) {
            buscarExercicioPorId(exercicioId);
        } else if (treinoId != null) {
            buscarExercicioDoTreino();
        } else {
            buscarPrimeiroExercicioDisponivel();
        }
    }

    /**
     * Busca dados de um exercício específico no Firestore.
     */
    private void buscarExercicioPorId(String id) {
        db.collection("planos_exercicios").document(id).get()
                .addOnSuccessListener(doc -> {
                    exercicioAtual = doc.toObject(Exercicio.class);
                    if (exercicioAtual != null) {
                        exercicioAtual.setId(doc.getId());
                        vincularDadosExercicio();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Erro ao buscar exercício", e));
    }

    /**
     * Busca o próximo exercício pendente de um treino específico.
     */
    private void buscarExercicioDoTreino() {
        db.collection("treinos_pacientes").document(treinoId).get()
                .addOnSuccessListener(doc -> {
                    Treino treino = doc.toObject(Treino.class);
                    if (treino != null && treino.getExercicios() != null) {
                        String targetId = treino.getExercicios().stream()
                                .filter(te -> !te.isConcluido())
                                .map(TreinoExercicio::getExercicioId)
                                .findFirst()
                                .orElse(!treino.getExercicios().isEmpty() ? 
                                        treino.getExercicios().get(0).getExercicioId() : null);

                        if (targetId != null) {
                            buscarExercicioPorId(targetId);
                        }
                    }
                });
    }

    /**
     * Fallback: busca qualquer exercício disponível caso nenhum ID tenha sido passado.
     */
    private void buscarPrimeiroExercicioDisponivel() {
        db.collection("planos_exercicios").limit(1).get()
                .addOnSuccessListener(qs -> {
                    if (!qs.isEmpty()) {
                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) qs.getDocuments().get(0);
                        exercicioAtual = doc.toObject(Exercicio.class);
                        exercicioAtual.setId(doc.getId());
                        vincularDadosExercicio();
                    }
                });
    }

    /**
     * Atualiza a interface com as informações do exercício carregado.
     */
    private void vincularDadosExercicio() {
        binding.txtNomeExercicio.setText(exercicioAtual.getNome());
        binding.txtOrientacoesCorpo.setText(exercicioAtual.getOrientacoes());
        
        if (exercicioAtual.getRepeticoes() > 0) {
            binding.txtSeries.setText(String.format(Locale.getDefault(), "%d repetições", exercicioAtual.getRepeticoes()));
        }

        String imageUrl = (exercicioAtual.getImagens() != null && !exercicioAtual.getImagens().isEmpty())
                ? exercicioAtual.getImagens().get(0) : null;

        // Carrega a imagem principal usando Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.dor_lombar)
                .error(R.drawable.dor_lombar)
                .centerCrop()
                .into(binding.imgCapaExercicio);
    }

    /**
     * Abre a tela de execução com as imagens em tela cheia.
     */
    private void iniciarExecucao() {
        Intent intent = new Intent(this, FullscreenImageActivity.class);
        intent.putStringArrayListExtra("IMAGENS", new ArrayList<>(exercicioAtual.getImagens()));
        intent.putExtra("EXERCICIO_ID", exercicioAtual.getId());
        intent.putExtra("EXERCICIO_NOME", exercicioAtual.getNome());
        if (treinoId != null) {
            intent.putExtra("TREINO_ID", treinoId);
        }
        startActivity(intent);
    }
}
