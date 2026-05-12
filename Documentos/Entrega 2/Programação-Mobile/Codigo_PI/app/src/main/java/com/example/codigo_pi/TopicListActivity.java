package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity que exibe a lista de tópicos de suporte/chat do usuário.
 */
public class TopicListActivity extends AppCompatActivity {

    private RecyclerView rvTopics;
    private TopicAdapter adapter;
    private List<ChatTopic> topicList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();

        rvTopics = findViewById(R.id.rv_topics);
        topicList = new ArrayList<>();
        
        // Configura o clique no tópico para abrir a tela de chat correspondente
        adapter = new TopicAdapter(topicList, topic -> {
            Intent intent = new Intent(TopicListActivity.this, ChatActivity.class);
            intent.putExtra("TOPIC_ID", topic.getId());
            intent.putExtra("TOPIC_TITLE", topic.getTitle());
            startActivity(intent);
        });

        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        rvTopics.setAdapter(adapter);

        findViewById(R.id.btn_new_topic).setOnClickListener(v -> showNewTopicDialog());

        setupNavigation();
        loadTopics();
    }

    /**
     * Configura a navegação da barra inferior.
     */
    private void setupNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_chat);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, homeMaya.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_agenda) {
                    startActivity(new Intent(this, Agenda_Consulta.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    startActivity(new Intent(this, MeuProgresso.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_chat) {
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
     * Carrega os tópicos de chat do usuário logado a partir do Firestore.
     */
    private void loadTopics() {
        if (currentUserId == null) return;

        db.collection("chats").document(currentUserId).collection("topics")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        topicList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            ChatTopic topic = doc.toObject(ChatTopic.class);
                            topic.setId(doc.getId());
                            topicList.add(topic);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * Exibe um diálogo para criação de um novo tópico de ajuda.
     */
    private void showNewTopicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Novo Tópico de Ajuda");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_new_topic, null);
        final EditText input = viewInflated.findViewById(R.id.input_topic_title);
        builder.setView(viewInflated);

        builder.setPositiveButton("Criar", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (!title.isEmpty()) {
                fetchUserAndCreateTopic(title);
            } else {
                Toast.makeText(this, "O título não pode estar vazio", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Busca informações do usuário para vincular ao novo tópico.
     */
    private void fetchUserAndCreateTopic(String title) {
        db.collection("users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("nome");
                String patientId = documentSnapshot.getString("id_paciente");
                createNewTopic(title, userName, patientId);
            } else {
                createNewTopic(title, "Usuário Desconhecido", "N/A");
            }
        }).addOnFailureListener(e -> createNewTopic(title, "Erro ao carregar", "N/A"));
    }

    /**
     * Cria o documento do novo tópico no Firestore.
     */
    private void createNewTopic(String title, String userName, String patientId) {
        ChatTopic newTopic = new ChatTopic(null, title, "Nenhuma mensagem ainda", Timestamp.now(), userName, patientId);
        db.collection("chats").document(currentUserId).collection("topics")
                .add(newTopic)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Tópico criado!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao criar tópico", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_chat);
        }
    }
}
