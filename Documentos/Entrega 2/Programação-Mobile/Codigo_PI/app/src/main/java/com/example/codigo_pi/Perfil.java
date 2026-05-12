package com.example.codigo_pi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Tela de Perfil do usuário com estatísticas e configurações de conta.
 */
public class Perfil extends AppCompatActivity {

    private static final String TAG = "PerfilActivity";
    
    private static final int CICLO_SESSOES = 12;
    private static final String STATUS_AGENDADO = "Agendado";
    
    private static final String COL_USERS = "users";
    private static final String COL_AGENDAMENTOS = "agendamentos";
    private static final String ATTR_USER_ID = "usuario_id";

    private TextView txtNome, statSessoes, statProgresso, statProxima, statPontos;
    private TextView txtPlanoResumo, txtHistoricoResumo;
    private ImageView imgProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    // Launcher para selecionar imagem da galeria
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    uploadImageToFirebase(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        initViews();
        setupListeners();
        carregarInformacoes();
    }

    /**
     * Inicializa os componentes de interface.
     */
    private void initViews() {
        txtNome = findViewById(R.id.txt_nome_perfil);
        statSessoes = findViewById(R.id.stat_sessoes);
        statProgresso = findViewById(R.id.stat_progresso);
        statProxima = findViewById(R.id.stat_proxima);
        statPontos = findViewById(R.id.stat_pontos);
        txtPlanoResumo = findViewById(R.id.txt_plano_resumo);
        txtHistoricoResumo = findViewById(R.id.txt_historico_resumo);
        imgProfile = findViewById(R.id.img_profile);

        // Ajuste de padding para barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Carrega foto de perfil se existir
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(imgProfile);
        }
    }

    /**
     * Configura os cliques e navegação.
     */
    private void setupListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.card_planos).setOnClickListener(v -> navegar(MeuProgresso.class));
        findViewById(R.id.card_historico).setOnClickListener(v -> abrirHistorico());
        findViewById(R.id.card_dados_pessoais).setOnClickListener(v -> abrirEdicaoDados());
        findViewById(R.id.card_logout).setOnClickListener(v -> executarLogout());
        imgProfile.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        setupBottomNav();
    }

    /**
     * Configura a barra de navegação inferior.
     */
    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        if (nav == null) return;
        
        nav.setSelectedItemId(R.id.nav_profile);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return navegar(homeMaya.class);
            if (id == R.id.nav_agenda) return navegar(Agenda_Consulta.class);
            if (id == R.id.nav_progress) return navegar(MeuProgresso.class);
            if (id == R.id.nav_chat) return navegar(TopicListActivity.class);
            return id == R.id.nav_profile;
        });
    }

    /**
     * Método auxiliar de navegação entre telas.
     */
    private boolean navegar(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        if (destino == homeMaya.class) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        if (destino != Perfil.class) finish();
        return true;
    }

    /**
     * Carrega dados do usuário e agendamentos do Firestore.
     */
    private void carregarInformacoes() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection(COL_USERS).document(user.getUid()).get()
            .addOnSuccessListener(this::processarDadosUsuario)
            .addOnFailureListener(e -> Log.e(TAG, "Erro ao carregar usuário", e));

        db.collection(COL_AGENDAMENTOS).whereEqualTo(ATTR_USER_ID, user.getUid()).get()
            .addOnSuccessListener(this::processarAgendamentos)
            .addOnFailureListener(e -> Log.e(TAG, "Erro ao carregar agendamentos", e));
    }

    /**
     * Atualiza a UI com os dados do modelo Usuario.
     */
    private void processarDadosUsuario(DocumentSnapshot doc) {
        Usuario u = doc.toObject(Usuario.class);
        if (u == null) return;

        txtNome.setText(u.getNome());
        statSessoes.setText(String.valueOf(u.getSessoes_realizadas()));
        statProgresso.setText(String.format(Locale.getDefault(), "%d%%", u.getProgresso_atual()));
        statPontos.setText(String.valueOf(u.getPontos_exercicios()));
        
        int realizadas = u.getSessoes_realizadas();
        int restantes = Math.max(0, CICLO_SESSOES - realizadas);
        
        txtPlanoResumo.setText(restantes > 0 
            ? String.format(Locale.getDefault(), "%d concluídas. Faltam %d para o ciclo.", realizadas, restantes)
            : "Parabéns! Ciclo de tratamento concluído.");
    }

    /**
     * Processa e exibe a próxima consulta agendada.
     */
    private void processarAgendamentos(QuerySnapshot qs) {
        List<Agendamento> agendamentos = qs.toObjects(Agendamento.class);
        ordenarLista(agendamentos);

        Optional<Agendamento> proximo = agendamentos.stream()
            .filter(a -> STATUS_AGENDADO.equalsIgnoreCase(a.getStatus()))
            .findFirst();

        proximo.ifPresentOrElse(
            a -> {
                statProxima.setText(a.getData().substring(0, 5));
                txtHistoricoResumo.setText(String.format("Próxima: %s às %s", a.getData(), a.getHorario()));
            },
            () -> {
                statProxima.setText("--");
                txtHistoricoResumo.setText(agendamentos.isEmpty() ? "Nenhuma consulta" : "Ver histórico");
            }
        );
    }

    /**
     * Abre um diálogo exibindo o histórico completo de agendamentos.
     */
    private void abrirHistorico() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection(COL_AGENDAMENTOS).whereEqualTo(ATTR_USER_ID, user.getUid()).get()
            .addOnSuccessListener(qs -> {
                List<Agendamento> list = qs.toObjects(Agendamento.class);
                ordenarLista(list);

                StringBuilder sb = new StringBuilder();
                list.forEach(a -> sb.append(STATUS_AGENDADO.equalsIgnoreCase(a.getStatus()) ? "📅 " : "✅ ")
                    .append(a.getData()).append(" - ").append(a.getHorario())
                    .append(" (").append(a.getStatus()).append(")\n\n"));

                new AlertDialog.Builder(this)
                    .setTitle("Histórico de Consultas")
                    .setMessage(sb.length() > 0 ? sb.toString() : "Nenhum registro encontrado.")
                    .setPositiveButton("Fechar", null).show();
            });
    }

    /**
     * Ordena agendamentos por data de criação decrescente.
     */
    private void ordenarLista(List<Agendamento> lista) {
        lista.sort(Comparator.comparing(Agendamento::getData_criacao, 
                   Comparator.nullsLast(Comparator.reverseOrder())));
    }

    /**
     * Abre diálogo para edição de e-mail e senha.
     */
    private void abrirEdicaoDados() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_edit_perfil, null);
        EditText email = v.findViewById(R.id.edit_email), pass = v.findViewById(R.id.edit_senha), 
                 curr = v.findViewById(R.id.edit_senha_atual);

        new AlertDialog.Builder(this)
            .setTitle("Editar Perfil")
            .setView(v)
            .setPositiveButton("Salvar", (d, w) -> {
                String senhaAtual = curr.getText().toString().trim();
                if (senhaAtual.isEmpty()) {
                    Toast.makeText(this, "Senha atual necessária para salvar", Toast.LENGTH_SHORT).show();
                    return;
                }
                processarAtualizacao(email.getText().toString().trim(), pass.getText().toString().trim(), senhaAtual);
            })
            .setNegativeButton("Cancelar", null).show();
    }

    /**
     * Reautentica o usuário e aplica as atualizações de conta.
     */
    private void processarAtualizacao(String novoEmail, String novaSenha, String atual) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) return;

        AuthCredential cred = EmailAuthProvider.getCredential(user.getEmail(), atual);
        user.reauthenticate(cred).addOnSuccessListener(aVoid -> {
            List<Task<Void>> tasks = new ArrayList<>();
            if (!novoEmail.isEmpty()) tasks.add(user.updateEmail(novoEmail));
            if (!novaSenha.isEmpty()) tasks.add(user.updatePassword(novaSenha));

            if (tasks.isEmpty()) return;

            Tasks.whenAllComplete(tasks).addOnCompleteListener(t -> {
                Toast.makeText(this, "Dados atualizados!", Toast.LENGTH_SHORT).show();
                carregarInformacoes();
            });
        }).addOnFailureListener(e -> Toast.makeText(this, "Senha atual incorreta", Toast.LENGTH_SHORT).show());
    }

    /**
     * Faz upload da imagem para o Firebase Storage e atualiza a foto de perfil.
     */
    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        StorageReference fileRef = storage.getReference().child("profile_pics/" + user.getUid() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri)
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Glide.with(this).load(uri).into(imgProfile);
                    Toast.makeText(Perfil.this, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show();
                }
            });
        })).addOnFailureListener(e -> Toast.makeText(Perfil.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show());
    }

    /**
     * Desloga o usuário e retorna para a tela inicial.
     */
    private void executarLogout() {
        mAuth.signOut();
        Intent i = new Intent(this, home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
