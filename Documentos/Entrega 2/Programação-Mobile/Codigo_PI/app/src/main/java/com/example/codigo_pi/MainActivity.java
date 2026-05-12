package com.example.codigo_pi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Tela de Login principal do aplicativo.
 */
public class MainActivity extends AppCompatActivity {

    private EditText email, senha;
    private CheckBox checkBox;
    private Button continuara;
    private ImageButton icon_home;
    private TextView txtEsqueciSenha;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicialização dos componentes de UI
        email = findViewById(R.id.email_login);
        senha = findViewById(R.id.senha_login);
        checkBox = findViewById(R.id.checkbox);
        continuara = findViewById(R.id.btn_continuar);
        icon_home = findViewById(R.id.icon_home);
        txtEsqueciSenha = findViewById(R.id.txt_esqueci_senha);

        // Fecha a tela ao clicar no ícone de home
        icon_home.setOnClickListener(v -> finish());

        // Tenta realizar o login
        continuara.setOnClickListener(v -> logarUsuario());

        if (txtEsqueciSenha != null) {
            txtEsqueciSenha.setOnClickListener(v -> recuperarSenha());
        }

        continuara.setEnabled(false);

        // Monitora mudanças nos campos para habilitar o botão de login
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
     * Realiza a autenticação com Firebase Auth.
     */
    private void logarUsuario() {
        String emailTexto = email.getText().toString().trim();
        String senhaTexto = senha.getText().toString();

        continuara.setEnabled(false);

        mAuth.signInWithEmailAndPassword(emailTexto, senhaTexto)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        verificarLGPDeIrParaHome(mAuth.getCurrentUser().getUid());
                    } else {
                        Toast.makeText(MainActivity.this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        continuara.setEnabled(true);
                    }
                });
    }

    /**
     * Envia e-mail de redefinição de senha.
     */
    private void recuperarSenha() {
        String emailTexto = email.getText().toString().trim();
        if (emailTexto.isEmpty()) {
            Toast.makeText(this, "Digite seu e-mail para recuperar a senha", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(emailTexto)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "E-mail de recuperação enviado!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao enviar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Verifica o aceite da LGPD no Firestore antes de navegar para a Home.
     */
    private void verificarLGPDeIrParaHome(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getBoolean("aceite_lgpd") != null && documentSnapshot.getBoolean("aceite_lgpd")) {
                        startActivity(new Intent(MainActivity.this, homeMaya.class));
                        finish();
                    } else {
                        startActivity(new Intent(MainActivity.this, homeMaya.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    startActivity(new Intent(MainActivity.this, homeMaya.class));
                    finish();
                });
    }

    /**
     * Habilita o botão de continuar apenas se as regras forem atendidas.
     */
    private void validarCampos() {
        String emailTexto = email.getText().toString().trim();
        String senhaTexto = senha.getText().toString().trim();
        boolean termosAceitos = checkBox.isChecked();
        continuara.setEnabled(!emailTexto.isEmpty() && !senhaTexto.isEmpty() && termosAceitos);
    }
}
