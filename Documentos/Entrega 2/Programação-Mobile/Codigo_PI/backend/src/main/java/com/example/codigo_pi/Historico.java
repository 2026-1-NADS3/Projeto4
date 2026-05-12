package com.example.codigo_pi;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico")
@Data
@NoArgsConstructor
public class Historico {
    @Id
    private String id;
    private String usuario_id;
    private String exercicio_id;
    private String exercicio_nome;
    private int nivel_dor;
    private String observacao;
    private LocalDateTime data_conclusao;
}
