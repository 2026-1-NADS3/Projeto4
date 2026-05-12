package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity para exibição de exercícios em tela cheia com registro de conclusão (check-in).
 */
public class FullscreenImageActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<String> imagens;
    private String exercicioId;
    private String exercicioNome;
    private String treinoId;
    private AppDatabase localDb;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        localDb = AppDatabase.getInstance(this);
        viewPager = findViewById(R.id.viewPager_fullscreen);
        ImageButton btnClose = findViewById(R.id.btn_close_fullscreen);
        TextView txtContador = findViewById(R.id.txt_contador);
        View btnFinalizar = findViewById(R.id.btn_finalizar_exercicio);

        // Recupera dados do exercício vindos da Intent
        Intent intent = getIntent();
        imagens = intent.getStringArrayListExtra("IMAGENS");
        exercicioId = intent.getStringExtra("EXERCICIO_ID");
        exercicioNome = intent.getStringExtra("EXERCICIO_NOME");
        treinoId = intent.getStringExtra("TREINO_ID");

        if (imagens == null) {
            imagens = new ArrayList<>();
        }

        ExercicioAdapter adapter = new ExercicioAdapter(imagens);
        viewPager.setAdapter(adapter);

        // Atualiza o contador de páginas (ex: 1 / 3)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                txtContador.setText((position + 1) + " / " + imagens.size());
            }
        });

        btnClose.setOnClickListener(v -> finish());
        btnFinalizar.setOnClickListener(v -> mostrarDialogoCheckin());
    }

    /**
     * Exibe o diálogo para o usuário informar o nível de dor e observações.
     */
    private void mostrarDialogoCheckin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_checkin, null);
        builder.setView(dialogView);

        SeekBar seekBarDor = dialogView.findViewById(R.id.seekBar_dor);
        TextView txtValorDor = dialogView.findViewById(R.id.txt_valor_dor);
        EditText edtObservacao = dialogView.findViewById(R.id.edt_observacao);

        seekBarDor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtValorDor.setText("Nível de dor: " + progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        builder.setTitle("Finalizar Exercício")
                .setPositiveButton("Salvar", (dialog, id) -> {
                    int nivelDor = seekBarDor.getProgress();
                    String observacao = edtObservacao.getText().toString();
                    salvarCheckin(nivelDor, observacao);
                })
                .setNegativeButton("Cancelar", null);

        builder.create().show();
    }

    /**
     * Salva os dados da execução no banco local (Room) e no Firestore.
     */
    private void salvarCheckin(int nivelDor, String observacao) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        Timestamp timestamp = Timestamp.now();
        String uniqueId = UUID.randomUUID().toString();

        Historico historico = new Historico(userId, exercicioId, exercicioNome, nivelDor, observacao, timestamp);
        historico.setId(uniqueId);

        // Gravação assíncrona no banco local
        executor.execute(() -> {
            localDb.appDao().insertHistorico(historico);
            runOnUiThread(() -> {
                Map<String, Object> checkin = new HashMap<>();
                checkin.put("usuario_id", userId);
                checkin.put("exercicio_id", exercicioId);
                checkin.put("exercicio_nome", exercicioNome);
                checkin.put("data", timestamp);
                checkin.put("nivel_dor", nivelDor);
                checkin.put("observacao", observacao);

                // Sincronização com Firestore
                db.collection("historico_execucao").document(uniqueId).set(checkin)
                        .addOnSuccessListener(aVoid -> {
                            db.collection("users").document(userId).update("pontos_exercicios", FieldValue.increment(1));
                            if (treinoId != null) {
                                atualizarStatusNoTreino(db);
                            } else {
                                Toast.makeText(this, "Concluído com sucesso!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Salvo offline", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            });
        });
    }

    /**
     * Atualiza o status do exercício para 'concluído' dentro de um treino específico.
     */
    private void atualizarStatusNoTreino(FirebaseFirestore db) {
        db.collection("treinos_pacientes").document(treinoId).get().addOnSuccessListener(doc -> {
            Treino treino = doc.toObject(Treino.class);
            if (treino != null && treino.getExercicios() != null) {
                List<TreinoExercicio> lista = treino.getExercicios();
                boolean mudou = false;
                for (TreinoExercicio te : lista) {
                    if (te.getExercicioId().equals(exercicioId)) {
                        te.setConcluido(true);
                        mudou = true;
                        break;
                    }
                }
                if (mudou) {
                    treino.setExercicios(lista);
                    db.collection("treinos_pacientes").document(treinoId).set(treino)
                            .addOnSuccessListener(aVoid -> {
                                executor.execute(() -> localDb.appDao().insertTreinos(List.of(treino)));
                                Toast.makeText(this, "Treino atualizado!", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                } else {
                    finish();
                }
            }
        });
    }
}
