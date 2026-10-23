package model;

/** Carro do cliente (tabela carro). Pode ter um mecânico atribuído. */
public class Carro {
    private int id;
    private String placa;
    private String modelo;
    private int ano;
    private int km;
    private String problema;
    private int mecanicoId;       // chave estrangeira para mecanico (0 = nenhum)
    private String mecanicoNome;  // preenchido nas consultas

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public int getKm() { return km; }
    public void setKm(int km) { this.km = km; }
    public String getProblema() { return problema; }
    public void setProblema(String problema) { this.problema = problema; }
    public int getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(int mecanicoId) { this.mecanicoId = mecanicoId; }
    public String getMecanicoNome() { return mecanicoNome; }
    public void setMecanicoNome(String mecanicoNome) { this.mecanicoNome = mecanicoNome; }

    /** ABC1D23 -> ABC-1D23 */
    public String getPlacaFormatada() {
        return placa != null && placa.length() == 7 ? placa.substring(0, 3) + "-" + placa.substring(3) : placa;
    }
}
