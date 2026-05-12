package com.example.codigo_pi;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "exercicios")
@Data
@NoArgsConstructor
public class Exercicio {
    @Id
    private String id;
    private String nome;
    @Column(length = 1000)
    private String orientacoes;
    
    @ElementCollection
    private List<String> imagens;
    
    private int repeticoes;
}
