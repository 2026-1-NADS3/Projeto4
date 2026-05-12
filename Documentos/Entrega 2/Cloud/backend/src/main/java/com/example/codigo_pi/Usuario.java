package com.example.codigo_pi;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
public class Usuario {
    @Id
    private String id_paciente;
    private String nome;
    private String email;
    private boolean aceite_lgpd;
    private LocalDateTime data_cadastro;
    private int pontos_exercicios;
    private int sessoes_realizadas;
    private int progresso_atual;
}
