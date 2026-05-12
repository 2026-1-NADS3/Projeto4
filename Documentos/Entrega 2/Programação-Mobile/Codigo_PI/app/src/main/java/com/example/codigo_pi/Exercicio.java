package com.example.codigo_pi;

import java.util.List;

/**
 * Modelo que representa um exercício individual.
 */
public class Exercicio {
    private String id;
    private String nome;
    private String orientacoes;
    private List<String> imagens;
    private int repeticoes;

    // Construtor padrão necessário para o Firebase
    public Exercicio() {}

    public Exercicio(String id, String nome, String orientacoes, List<String> imagens, int repeticoes) {
        this.id = id;
        this.nome = nome;
        this.orientacoes = orientacoes;
        this.imagens = imagens;
        this.repeticoes = repeticoes;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getOrientacoes() { return orientacoes; }
    public void setOrientacoes(String orientacoes) { this.orientacoes = orientacoes; }

    public List<String> getImagens() { return imagens; }
    public void setImagens(List<String> imagens) { this.imagens = imagens; }

    public int getRepeticoes() { return repeticoes; }
    public void setRepeticoes(int repeticoes) { this.repeticoes = repeticoes; }
}
