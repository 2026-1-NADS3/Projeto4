package com.example.codigo_pi;

import com.google.firebase.Timestamp;

/**
 * Modelo que representa os dados de um usuário/paciente.
 */
public class Usuario {
    private String nome;
    private String email;
    private boolean aceite_lgpd;
    private Timestamp data_cadastro;
    private int pontos_exercicios;
    private int sessoes_realizadas;
    private int progresso_atual;
    private String id_paciente;

    // Construtor padrão necessário para o Firebase
    public Usuario() {
    }

    public Usuario(String nome, String email, boolean aceite_lgpd, Timestamp data_cadastro, int pontos_exercicios, int sessoes_realizadas, int progresso_atual, String id_paciente) {
        this.nome = nome;
        this.email = email;
        this.aceite_lgpd = aceite_lgpd;
        this.data_cadastro = data_cadastro;
        this.pontos_exercicios = pontos_exercicios;
        this.sessoes_realizadas = sessoes_realizadas;
        this.progresso_atual = progresso_atual;
        this.id_paciente = id_paciente;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAceite_lgpd() { return aceite_lgpd; }
    public void setAceite_lgpd(boolean aceite_lgpd) { this.aceite_lgpd = aceite_lgpd; }

    public Timestamp getData_cadastro() { return data_cadastro; }
    public void setData_cadastro(Timestamp data_cadastro) { this.data_cadastro = data_cadastro; }

    public int getPontos_exercicios() { return pontos_exercicios; }
    public void setPontos_exercicios(int pontos_exercicios) { this.pontos_exercicios = pontos_exercicios; }

    public int getSessoes_realizadas() { return sessoes_realizadas; }
    public void setSessoes_realizadas(int sessoes_realizadas) { this.sessoes_realizadas = sessoes_realizadas; }

    public int getProgresso_atual() { return progresso_atual; }
    public void setProgresso_atual(int progresso_atual) { this.progresso_atual = progresso_atual; }

    public String getId_paciente() { return id_paciente; }
    public void setId_paciente(String id_paciente) { this.id_paciente = id_paciente; }
}
