package model;

import java.math.BigDecimal;
import util.Util;

public class Servico {
    private int id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int intervaloKm;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public int getIntervaloKm() { return intervaloKm; }
    public void setIntervaloKm(int intervaloKm) { this.intervaloKm = intervaloKm; }

    /** Texto exibido nos combos. */
    @Override
    public String toString() {
        return nome + " (" + Util.moeda(preco) + ")";
    }
}
