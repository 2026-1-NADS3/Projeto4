package com.example.codigo_pi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.Timestamp;
import java.util.List;

/**
 * Entidade que representa um conjunto de exercícios (treino) para o usuário.
 */
@Entity(tableName = "treinos")
public class Treino {
    @PrimaryKey
    @NonNull
    private String id;
    private String usuarioId;
    private String titulo;
    private Timestamp dataCriacao;
    private List<TreinoExercicio> exercicios;
    private int progresso;

    // Construtor padrão para Room/Firebase
    public Treino() {}

    public Treino(String id, String usuarioId, String titulo, Timestamp dataCriacao, List<TreinoExercicio> exercicios) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.dataCriacao = dataCriacao;
        this.exercicios = exercicios;
        this.progresso = calcularProgresso();
    }

    /**
     * Calcula a porcentagem de conclusão do treino baseado nos exercícios finalizados.
     */
    public int calcularProgresso() {
        if (exercicios == null || exercicios.isEmpty()) return 0;
        int concluidos = 0;
        for (TreinoExercicio ex : exercicios) {
            if (ex.isConcluido()) concluidos++;
        }
        return (concluidos * 100) / exercicios.size();
    }

    // Getters e Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Timestamp getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Timestamp dataCriacao) { this.dataCriacao = dataCriacao; }

    public List<TreinoExercicio> getExercicios() { return exercicios; }
    public void setExercicios(List<TreinoExercicio> exercicios) { 
        this.exercicios = exercicios;
        this.progresso = calcularProgresso();
    }

    public int getProgresso() { return progresso; }
    public void setProgresso(int progresso) { this.progresso = progresso; }
}
