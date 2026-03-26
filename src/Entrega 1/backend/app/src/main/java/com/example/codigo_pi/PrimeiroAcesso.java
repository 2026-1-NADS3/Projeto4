package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

public class PrimeiroAcesso extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha, edtConfirmarSenha;
    private TextInputLayout layoutConfirmarSenha;
    private CheckBox checkBoxTermos;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_primeiro_acesso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializando os componentes
        edtNome = findViewById(R.id.txt_nome_1acesso);
        edtEmail = findViewById(R.id.txt_email_1acesso);
        edtSenha = findViewById(R.id.txt_senha_1acesso);
        edtConfirmarSenha = findViewById(R.id.txt_senha_1acesso_confirmar);
        layoutConfirmarSenha = findViewById(R.id.layout_senha_confirmar);
        checkBoxTermos = findViewById(R.id.checkbox);
        btnCadastrar = findViewById(R.id.btn_cadastrar);
        ImageButton btnHome = findViewById(R.id.icon_home);

        // BOTÃO HOME (Ícone)
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(PrimeiroAcesso.this, home.class);
            startActivity(intent);
            finish();
        });

        // Desativar botão inicialmente
        btnCadastrar.setEnabled(false);

        // Escutador de mudanças de texto
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

        edtNome.addTextChangedListener(watcher);
        edtEmail.addTextChangedListener(watcher);
        edtSenha.addTextChangedListener(watcher);
        edtConfirmarSenha.addTextChangedListener(watcher);

        // Escutador do checkbox
        checkBoxTermos.setOnCheckedChangeListener((buttonView, isChecked) -> validarCampos());

        btnCadastrar.setOnClickListener(v -> {
            // Apenas prossegue para a próxima tela
            Intent intent = new Intent(PrimeiroAcesso.this, homeMaya.class);
            startActivity(intent);
            finish();
        });
    }

    private void validarCampos() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();
        String confirmarSenha = edtConfirmarSenha.getText().toString();
        boolean termosAceitos = checkBoxTermos.isChecked();

        boolean camposPreenchidos = !nome.isEmpty() && !email.isEmpty() && !senha.isEmpty() && !confirmarSenha.isEmpty();
        boolean senhasIguais = senha.equals(confirmarSenha);

        btnCadastrar.setEnabled(camposPreenchidos && senhasIguais && termosAceitos);

        // Erro visual no TextInputLayout para não sobrepor o ícone de visibilidade
        if (!senha.isEmpty() && !confirmarSenha.isEmpty() && !senhasIguais) {
            layoutConfirmarSenha.setError("As senhas não coincidem");
        } else {
            layoutConfirmarSenha.setError(null);
        }
    }
}
