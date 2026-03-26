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

import java.util.Calendar;
import java.util.Locale;

public class Agenda_Consulta extends AppCompatActivity {

    private TextInputEditText editHorario;
    private MaterialCardView cardResumo;
    private TextView textResumoData;
    private String dataSelecionada = "";
    private String horarioSelecionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agenda_consulta);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Botão de voltar
        ImageButton btnBack = findViewById(R.id.btn_back_home);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        CalendarView calendarView = findViewById(R.id.calendarView);
        editHorario = findViewById(R.id.edit_horario);
        cardResumo = findViewById(R.id.card_resumo);
        textResumoData = findViewById(R.id.text_resumo_data);
        MaterialButton btnConfirmar = findViewById(R.id.btn_confirmar_agendamento);

        // Bloqueia as datas passadas
        long hoje = Calendar.getInstance().getTimeInMillis();
        calendarView.setMinDate(hoje);

        // Listener para seleção de data
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            if (isDiaDisponivel(year, month, dayOfMonth)) {
                dataSelecionada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                atualizarResumo();
            } else {
                Toast.makeText(Agenda_Consulta.this, "Este dia não está disponível para consultas.", Toast.LENGTH_SHORT).show();
                dataSelecionada = "";
                atualizarResumo();
            }
        });

        // Seletor de Horário
        editHorario.setOnClickListener(v -> mostrarSeletorHorario());
        editHorario.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mostrarSeletorHorario();
                v.clearFocus();
            }
        });

        btnConfirmar.setOnClickListener(v -> {
            if (dataSelecionada.isEmpty() || horarioSelecionado.isEmpty()) {
                Toast.makeText(this, "Por favor, selecione data e horário", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Consulta agendada para " + dataSelecionada + " às " + horarioSelecionado, Toast.LENGTH_LONG).show();
            }
        });

        // Configuração da Navegação (Padronizada)
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
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(this, Perfil.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

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
            if (hour < 8 || (hour >= 18 && minute > 0) || hour > 18) {
                Toast.makeText(this, "Horário indisponível (08:00 - 18:00)", Toast.LENGTH_LONG).show();
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

    private boolean isDiaDisponivel(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        return cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;
    }

    private void atualizarResumo() {
        if (!dataSelecionada.isEmpty() && !horarioSelecionado.isEmpty()) {
            String resumo = "Data: " + dataSelecionada + "\nHorário: " + horarioSelecionado;
            textResumoData.setText(resumo);
            cardResumo.setVisibility(View.VISIBLE);
        } else {
            cardResumo.setVisibility(View.GONE);
        }
    }
}
