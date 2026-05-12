package com.example.codigo_pi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    @Autowired
    private TreinoRepository repository;

    @GetMapping("/usuario/{usuarioId}")
    public List<Treino> listarPorUsuario(@PathVariable String usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    @PostMapping
    public Treino salvar(@RequestBody Treino treino) {
        treino.setProgresso(treino.calcularProgresso());
        return repository.save(treino);
    }
}
