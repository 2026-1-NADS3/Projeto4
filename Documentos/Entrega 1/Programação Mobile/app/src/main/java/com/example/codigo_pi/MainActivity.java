package com.example.codigo_pi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity de Login do aplicativo Clínica Maya.
 * Gerencia a autenticação do usuário, validação de campos obrigatórios
 * e redirecionamento para o dashboard principal (homeMaya).
 */
public class MainActivity extends AppCompatActivity {

    private EditText email, senha;
    private CheckBox checkBox;
    private Button continuar;
    private ImageButton icon_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_main);

        // Inicialização dos componentes da interface de login
        email = findViewById(R.id.email_login);
        senha = findViewById(R.id.senha_login);
        checkBox = findViewById(R.id.checkbox);
        continuar = findViewById(R.id.btn_continuar);
        icon_home = findViewById(R.id.icon_home);

        // Retorno à tela de boas-vindas
        icon_home.setOnClickListener(v -> {
            finish();
        });

        // Autenticação e transição para a Dashboard (homeMaya) via Intent
        continuar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, homeMaya.class);
            startActivity(intent);
            finish();
        });

        // O botão iniciar como desabilitado até validação completa dos dados de entrada
        continuar.setEnabled(false);

        // Observador para monitorar em tempo real a entrada de dados e habilitar o login
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarCampos();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        email.addTextChangedListener(watcher);
        senha.addTextChangedListener(watcher);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> validarCampos());
    }

    /**
     * Regra de negócio para validar se os campos de email, senha e
     * o aceite dos termos de uso foram preenchidos corretamente.
     */
    private void validarCampos() {
        String emailTexto = email.getText().toString().trim();
        String senhaTexto = senha.getText().toString().trim();
        boolean termosAceitos = checkBox.isChecked();

        boolean camposPreenchidos = !emailTexto.isEmpty() && !senhaTexto.isEmpty() && termosAceitos;
        continuar.setEnabled(camposPreenchidos);
    }
}
