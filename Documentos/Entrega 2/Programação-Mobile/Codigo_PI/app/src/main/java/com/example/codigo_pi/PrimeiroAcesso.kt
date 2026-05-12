package com.example.codigo_pi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PrimeiroAcesso : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var edtConfirmarSenha: EditText
    private lateinit var checkBoxTermos: CheckBox
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_primeiro_acesso)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializando os componentes
        edtNome = findViewById(R.id.txt_nome_1acesso)
        edtEmail = findViewById(R.id.txt_email_1acesso)
        edtSenha = findViewById(R.id.txt_senha_1acesso)
        edtConfirmarSenha = findViewById(R.id.txt_senha_1acesso_confirmar)
        checkBoxTermos = findViewById(R.id.checkbox)
        btnCadastrar = findViewById(R.id.btn_cadastrar)
        val btnHome = findViewById<ImageButton>(R.id.icon_home)

        // BOTÃO HOME (Ícone)
        btnHome.setOnClickListener {
            val intent = Intent(this, home::class.java)
            startActivity(intent)
            finish()
        }

        // Desativar botão inicialmente
        btnCadastrar.isEnabled = false

        // Escutador de mudanças de texto
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        edtNome.addTextChangedListener(watcher)
        edtEmail.addTextChangedListener(watcher)
        edtSenha.addTextChangedListener(watcher)
        edtConfirmarSenha.addTextChangedListener(watcher)

        // Escutador do checkbox
        checkBoxTermos.setOnCheckedChangeListener { _, _ ->
            validarCampos()
        }

        btnCadastrar.setOnClickListener {
            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            // Lógica para salvar os dados...
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validarCampos() {
        val nome = edtNome.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val senha = edtSenha.text.toString()
        val confirmarSenha = edtConfirmarSenha.text.toString()
        val termosAceitos = checkBoxTermos.isChecked

        // O botão só ativa se:
        // 1. Todos os campos estiverem preenchidos
        // 2. As senhas forem iguais
        // 3. O checkbox de termos estiver marcado
        val camposPreenchidos = nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && confirmarSenha.isNotEmpty()
        val senhasIguais = senha == confirmarSenha

        btnCadastrar.isEnabled = camposPreenchidos && senhasIguais && termosAceitos

        // Erro visual no campo de confirmar senha
        if (senha.isNotEmpty() && confirmarSenha.isNotEmpty() && !senhasIguais) {
            edtConfirmarSenha.error = "As senhas não coincidem"
        } else {
            edtConfirmarSenha.error = null
        }
    }
}