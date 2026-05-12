package com.example.codigo_pi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/historico")
public class HistoricoController {

    @Autowired
    private HistoricoRepository repository;

    @GetMapping("/usuario/{usuarioId}")
    public List<Historico> listarPorUsuario(@PathVariable String usuarioId) {
        return repository.findByUsuario_id(usuarioId);
    }

    @PostMapping
    public Historico salvar(@RequestBody Historico historico) {
        return repository.save(historico);
    }
}
