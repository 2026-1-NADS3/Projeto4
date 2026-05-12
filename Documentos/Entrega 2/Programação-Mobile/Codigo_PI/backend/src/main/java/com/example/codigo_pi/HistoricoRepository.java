package com.example.codigo_pi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoRepository extends JpaRepository<Historico, String> {
    List<Historico> findByUsuario_id(String usuarioId);
}
