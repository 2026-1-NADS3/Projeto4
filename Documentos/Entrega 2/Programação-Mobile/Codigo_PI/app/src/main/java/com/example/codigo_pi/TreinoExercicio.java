package com.example.codigo_pi;

/**
 * Modelo que representa a relação entre um treino e um exercício, incluindo o status de conclusão.
 */
public class TreinoExercicio {
    private String exercicioId;
    private String nome;
    private boolean concluido;

    // Construtor necessário para o Firebase
    public TreinoExercicio() {}

    public TreinoExercicio(String exercicioId, String nome, boolean concluido) {
        this.exercicioId = exercicioId;
        this.nome = nome;
        this.concluido = concluido;
    }

    // Getters e Setters
    public String getExercicioId() { return exercicioId; }
    public void setExercicioId(String exercicioId) { this.exercicioId = exercicioId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }
}
