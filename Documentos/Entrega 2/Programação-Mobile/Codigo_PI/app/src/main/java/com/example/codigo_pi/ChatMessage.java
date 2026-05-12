package com.example.codigo_pi;

import com.google.firebase.Timestamp;

/**
 * Modelo que representa uma mensagem individual no chat.
 */
public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String message;
    private Timestamp timestamp;

    // Construtor padrão necessário para o Firebase
    public ChatMessage() {
    }

    public ChatMessage(String senderId, String receiverId, String message, Timestamp timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters e Setters
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
