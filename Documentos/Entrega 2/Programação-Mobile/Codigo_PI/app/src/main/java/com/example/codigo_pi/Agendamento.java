package com.example.codigo_pi;

import com.google.firebase.Timestamp;

/**
 * Modelo que representa um agendamento de consulta.
 */
public class Agendamento {
    private String id;
    private String usuario_id;
    private String data;
    private String horario;
    private String status;
    private Timestamp data_criacao;

    // Construtor necessário para o Firebase
    public Agendamento() {
    }

    public Agendamento(String usuario_id, String data, String horario, String status, Timestamp data_criacao) {
        this.usuario_id = usuario_id;
        this.data = data;
        this.horario = horario;
        this.status = status;
        this.data_criacao = data_criacao;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuario_id() { return usuario_id; }
    public void setUsuario_id(String usuario_id) { this.usuario_id = usuario_id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getData_criacao() { return data_criacao; }
    public void setData_criacao(Timestamp data_criacao) { this.data_criacao = data_criacao; }
}
