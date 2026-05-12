package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Tela de cadastro para o primeiro acesso do usuário.
 */
public class PrimeiroAcesso extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha, edtConfirmarSenha;
    private TextInputLayout layoutConfirmarSenha;
    private CheckBox checkBoxTermos;
    private Button btnCadastrar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_primeiro_acesso);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ajuste de preenchimento para as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtNome = findViewById(R.id.txt_nome_1acesso);
        edtEmail = findViewById(R.id.txt_email_1acesso);
        edtSenha = findViewById(R.id.txt_senha_1acesso);
        edtConfirmarSenha = findViewById(R.id.txt_senha_1acesso_confirmar);
        layoutConfirmarSenha = findViewById(R.id.layout_senha_confirmar);
        checkBoxTermos = findViewById(R.id.checkbox);
        btnCadastrar = findViewById(R.id.btn_cadastrar);

        // Fecha a tela ao clicar no ícone de voltar
        findViewById(R.id.icon_home).setOnClickListener(v -> finish());

        btnCadastrar.setEnabled(false);

        // Monitora campos para habilitar o botão de cadastro
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
        checkBoxTermos.setOnCheckedChangeListener((buttonView, isChecked) -> validarCampos());

        btnCadastrar.setOnClickListener(v -> cadastrarUsuario());
    }

    /**
     * Cria o usuário no Firebase Auth e salva os dados no Firestore.
     */
    private void cadastrarUsuario() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        btnCadastrar.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Atualiza o nome de exibição no Firebase Auth
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nome)
                                    .build();
                            user.updateProfile(profileUpdates);

                            // Cria o objeto de usuário para o Firestore
                            Usuario novoUsuario = new Usuario(
                                    nome,
                                    email,
                                    true,
                                    Timestamp.now(),
                                    0, // pontos_exercicios
                                    0,
                                    0,
                                    "FM" + user.getUid().substring(0, 5).toUpperCase()
                            );

                            db.collection("users").document(user.getUid())
                                    .set(novoUsuario)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(PrimeiroAcesso.this, "Bem-vindo(a), " + nome + "!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PrimeiroAcesso.this, homeMaya.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(PrimeiroAcesso.this, "Erro ao criar perfil no banco.", Toast.LENGTH_SHORT).show();
                                        btnCadastrar.setEnabled(true);
                                    });
                        }
                    } else {
                        Toast.makeText(PrimeiroAcesso.this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        btnCadastrar.setEnabled(true);
                    }
                });
    }

    /**
     * Valida se todos os requisitos para o cadastro foram atendidos.
     */
    private void validarCampos() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();
        String confirmarSenha = edtConfirmarSenha.getText().toString();
        boolean termosAceitos = checkBoxTermos.isChecked();

        boolean camposPreenchidos = !nome.isEmpty() && !email.isEmpty() && !senha.isEmpty() && !confirmarSenha.isEmpty();
        boolean senhasIguais = senha.equals(confirmarSenha);

        btnCadastrar.setEnabled(camposPreenchidos && senhasIguais && termosAceitos && senha.length() >= 6);
    }
}
