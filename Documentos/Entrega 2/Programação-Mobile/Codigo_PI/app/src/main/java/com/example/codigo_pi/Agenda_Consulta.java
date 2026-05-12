package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Locale;

/**
 * Tela para agendamento de consultas
 */
public class Agenda_Consulta extends AppCompatActivity {

    private TextInputEditText editHorario;
    private MaterialCardView cardResumo;
    private TextView textResumoData, txtHorariosOcupados;
    private String dataSelecionada = "";
    private String horarioSelecionado = "";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agenda_consulta);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Configuração de preenchimento para as barras do sistema
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Botão para voltar à tela anterior
        ImageButton btnBack = findViewById(R.id.btn_back_home);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Inicialização dos componentes da tela
        CalendarView calendarView = findViewById(R.id.calendarView);
        editHorario = findViewById(R.id.edit_horario);
        cardResumo = findViewById(R.id.card_resumo);
        textResumoData = findViewById(R.id.text_resumo_data);
        txtHorariosOcupados = findViewById(R.id.text_horarios_ocupados);
        MaterialButton btnConfirmar = findViewById(R.id.btn_confirmar_agendamento);

        // Bloqueia seleção de datas passadas
        long hoje = Calendar.getInstance().getTimeInMillis();
        calendarView.setMinDate(hoje);

        // Listener para mudança de data no calendário
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            if (isDiaDisponivel(year, month, dayOfMonth)) {
                dataSelecionada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                buscarHorariosOcupados(dataSelecionada);
                atualizarResumo();
            } else {
                Toast.makeText(Agenda_Consulta.this, "Este dia não está disponível para consultas.", Toast.LENGTH_SHORT).show();
                dataSelecionada = "";
                txtHorariosOcupados.setVisibility(View.GONE);
                atualizarResumo();
            }
        });

        // Clique no campo de horário abre o seletor (TimePicker)
        editHorario.setOnClickListener(v -> mostrarSeletorHorario());
        editHorario.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mostrarSeletorHorario();
                v.clearFocus();
            }
        });

        // Botão para validar e salvar o agendamento
        btnConfirmar.setOnClickListener(v -> validarESalvar());

        // Configuração da navegação inferior
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_agenda);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(this, homeMaya.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_agenda) {
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    startActivity(new Intent(this, MeuProgresso.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    startActivity(new Intent(this, TopicListActivity.class));
                    finish();
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

    /**
     * Busca no Firestore os horários ocupados na data selecionada.
     */
    private void buscarHorariosOcupados(String data) {
        db.collection("agendamentos")
                .whereEqualTo("data", data)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    StringBuilder ocupados = new StringBuilder("Horários ocupados: ");
                    boolean temOcupado = false;
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String h = doc.getString("horario");
                        if (h != null) {
                            ocupados.append(h).append(", ");
                            temOcupado = true;
                        }
                    }
                    if (temOcupado) {
                        txtHorariosOcupados.setText(ocupados.substring(0, ocupados.length() - 2));
                        txtHorariosOcupados.setVisibility(View.VISIBLE);
                    } else {
                        txtHorariosOcupados.setText("Todos os horários disponíveis!");
                        txtHorariosOcupados.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * Valida se os campos foram preenchidos e se o horário está livre.
     */
    private void validarESalvar() {
        if (dataSelecionada.isEmpty() || horarioSelecionado.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione data e horário", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("agendamentos")
                .whereEqualTo("data", dataSelecionada)
                .whereEqualTo("horario", horarioSelecionado)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "⚠️ Este horário já foi preenchido por outra pessoa.", Toast.LENGTH_LONG).show();
                        buscarHorariosOcupados(dataSelecionada);
                    } else {
                        salvarAgendamento();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao validar disponibilidade.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Persiste o novo agendamento no Firestore.
     */
    private void salvarAgendamento() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("agendamentos").document();
        String agendamentoId = docRef.getId();

        Agendamento agendamento = new Agendamento(
                uid,
                dataSelecionada,
                horarioSelecionado,
                "Agendado",
                Timestamp.now()
        );
        agendamento.setId(agendamentoId);

        docRef.set(agendamento)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Consulta confirmada para " + dataSelecionada + " às " + horarioSelecionado, Toast.LENGTH_LONG).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Erro ao agendar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    /**
     * Exibe o seletor de horário do Material Design.
     */
    private void mostrarSeletorHorario() {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(8)
                .setMinute(0)
                .setTitleText("Selecione o Horário")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();
            // Verifica horário de atendimento (ex: 08h às 18h)
            if (hour < 8 || (hour >= 18 && minute > 0) || hour > 18) {
                Toast.makeText(this, "Horário indisponível (Atendimento: 08h às 18h)", Toast.LENGTH_LONG).show();
                horarioSelecionado = "";
                editHorario.setText("");
            } else {
                horarioSelecionado = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                editHorario.setText(horarioSelecionado);
            }
            atualizarResumo();
        });
        picker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    /**
     * Verifica se o dia selecionado é válido para agendamento (ex: bloqueia domingos).
     */
    private boolean isDiaDisponivel(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        return cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;
    }

    /**
     * Atualiza o card visual com o resumo da data e hora escolhidas.
     */
    private void atualizarResumo() {
        if (!dataSelecionada.isEmpty() && !horarioSelecionado.isEmpty()) {
            String resumo = "Confirmar para: " + dataSelecionada + " às " + horarioSelecionado;
            textResumoData.setText(resumo);
            cardResumo.setVisibility(View.VISIBLE);
        } else {
            cardResumo.setVisibility(View.GONE);
        }
    }
}
