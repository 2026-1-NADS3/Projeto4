package com.example.codigo_pi;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tela de conversa (chat) entre o usuário e o suporte/ADM.
 */
public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatMessage> messageList;
    private EditText editMessage;
    private ImageButton btnSend;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String topicId;
    private String topicTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();

        // Recupera dados do tópico passados por Intent
        topicId = getIntent().getStringExtra("TOPIC_ID");
        topicTitle = getIntent().getStringExtra("TOPIC_TITLE");

        if (topicId == null) {
            Toast.makeText(this, "Erro ao carregar tópico", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configura a Toolbar com o título do tópico
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(topicTitle);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_chat);
        editMessage = findViewById(R.id.edit_message);
        btnSend = findViewById(R.id.btn_send);

        messageList = new ArrayList<>();
        adapter = new ChatAdapter(messageList);

        // Configura a lista para começar do final (mensagens mais recentes)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());

        listenMessages();
    }

    /**
     * Escuta novas mensagens no Firestore em tempo real.
     */
    private void listenMessages() {
        db.collection("chats").document(currentUserId)
                .collection("topics").document(topicId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                messageList.add(dc.getDocument().toObject(ChatMessage.class));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    /**
     * Envia uma nova mensagem para o Firestore.
     */
    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        ChatMessage message = new ChatMessage(
                currentUserId,
                "ADM",
                text,
                Timestamp.now()
        );

        db.collection("chats").document(currentUserId)
                .collection("topics").document(topicId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    editMessage.setText("");
                    updateLastMessage(text);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao enviar", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Atualiza o resumo da última mensagem no documento do tópico.
     */
    private void updateLastMessage(String text) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", text);
        updates.put("timestamp", Timestamp.now());

        db.collection("chats").document(currentUserId)
                .collection("topics").document(topicId)
                .update(updates);
    }
}
