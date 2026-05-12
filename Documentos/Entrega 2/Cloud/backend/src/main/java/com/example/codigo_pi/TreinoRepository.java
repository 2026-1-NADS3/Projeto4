package com.example.codigo_pi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TreinoRepository extends JpaRepository<Treino, String> {
    List<Treino> findByUsuarioId(String usuarioId);
}
