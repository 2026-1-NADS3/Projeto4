package com.example.codigo_pi;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "treinos")
@Data
@NoArgsConstructor
public class Treino {
    @Id
    private String id;
    private String usuarioId;
    private String titulo;
    private LocalDateTime dataCriacao;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "treino_id")
    private List<TreinoExercicio> exercicios;
    
    private int progresso;

    public int calcularProgresso() {
        if (exercicios == null || exercicios.isEmpty()) return 0;
        long concluidos = exercicios.stream().filter(TreinoExercicio::isConcluido).count();
        return (int) ((concluidos * 100) / exercicios.size());
    }
}
