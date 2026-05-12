package com.example.codigo_pi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.Timestamp;

/**
 * Entidade que representa o histórico de execução de um exercício.
 * Mapeada para a tabela "historico" no Room.
 */
@Entity(tableName = "historico")
public class Historico {
    @PrimaryKey
    @NonNull
    private String id;
    private String usuario_id;
    private String exercicio_id;
    private String exercicio_nome;
    private int nivel_dor;
    private String observacao;
    private Timestamp data_conclusao;

    // Construtor padrão necessário para o Room/Firebase
    public Historico() {
        this.id = "";
    }

    public Historico(String usuario_id, String exercicio_id, String exercicio_nome, int nivel_dor, String observacao, Timestamp data_conclusao) {
        this.id = java.util.UUID.randomUUID().toString();
        this.usuario_id = usuario_id;
        this.exercicio_id = exercicio_id;
        this.exercicio_nome = exercicio_nome;
        this.nivel_dor = nivel_dor;
        this.observacao = observacao;
        this.data_conclusao = data_conclusao;
    }

    // Getters e Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUsuario_id() { return usuario_id; }
    public void setUsuario_id(String usuario_id) { this.usuario_id = usuario_id; }

    public String getExercicio_id() { return exercicio_id; }
    public void setExercicio_id(String exercicio_id) { this.exercicio_id = exercicio_id; }

    public String getExercicio_nome() { return exercicio_nome; }
    public void setExercicio_nome(String exercicio_nome) { this.exercicio_nome = exercicio_nome; }

    public int getNivel_dor() { return nivel_dor; }
    public void setNivel_dor(int nivel_dor) { this.nivel_dor = nivel_dor; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Timestamp getData_conclusao() { return data_conclusao; }
    public void setData_conclusao(Timestamp data_conclusao) { this.data_conclusao = data_conclusao; }
}
