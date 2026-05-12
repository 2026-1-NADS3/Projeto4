package com.example.codigo_pi;

import com.google.firebase.Timestamp;

/**
 * Modelo que representa um tópico de conversa no chat.
 */
public class ChatTopic {
    private String id;
    private String title;
    private String lastMessage;
    private Timestamp timestamp;
    private String userName;
    private String patientId;

    // Construtor padrão necessário para o Firebase
    public ChatTopic() {}

    public ChatTopic(String id, String title, String lastMessage, Timestamp timestamp, String userName, String patientId) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.userName = userName;
        this.patientId = patientId;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
}
