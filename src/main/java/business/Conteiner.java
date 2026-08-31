package business;

public class Conteiner extends AbstractModel<String> {
    private final double capacidadeMaxima;
    private final String tipo;
    private final String localizacao;

    public Conteiner(String id, double capacidadeMaxima, String tipo, String localizacao) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificação do Conteiner é obrigatória.");
        }
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("Capacidade máxima deve ser positiva.");
        }
        setId(id.trim());
        this.capacidadeMaxima = capacidadeMaxima;
        this.tipo = tipo;
        this.localizacao = localizacao;
    }

    public double getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLocalizacao() {
        return localizacao;
    }
}
