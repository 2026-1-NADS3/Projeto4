package com.example.codigo_pi;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "treino_exercicios")
@Data
@NoArgsConstructor
public class TreinoExercicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String exercicioId;
    private String nome;
    private boolean concluido;
}
