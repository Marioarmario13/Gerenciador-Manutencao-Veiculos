package model;

import java.math.BigDecimal;
import java.sql.Date;

/** Relação entre um carro e um serviço na agenda. */
public class Agendamento {
    public static final String AGENDADO = "AGENDADO";
    public static final String CONCLUIDO = "CONCLUIDO";
    public static final String CANCELADO = "CANCELADO";

    private int id;
    private int carroId;      // chave estrangeira para carro
    private int servicoId;    // chave estrangeira para servico
    private String servicoNome;
    private BigDecimal preco;
    private Date data;
    private String status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCarroId() { return carroId; }
    public void setCarroId(int carroId) { this.carroId = carroId; }
    public int getServicoId() { return servicoId; }
    public void setServicoId(int servicoId) { this.servicoId = servicoId; }
    public String getServicoNome() { return servicoNome; }
    public void setServicoNome(String servicoNome) { this.servicoNome = servicoNome; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusTexto() {
        if (CONCLUIDO.equals(status)) return "Concluído";
        if (CANCELADO.equals(status)) return "Cancelado";
        return "Agendado";
    }
}
