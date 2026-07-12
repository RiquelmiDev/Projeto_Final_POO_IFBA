package business;

public class Relatorio {
    private final int totalColetas;
    private final double volumeTotal;
    private final double taxaReciclagem;

    public Relatorio(int totalColetas, double volumeTotal, double taxaReciclagem) {
        this.totalColetas = totalColetas;
        this.volumeTotal = volumeTotal;
        this.taxaReciclagem = taxaReciclagem;
    }

    public int getTotalColetas() {
        return totalColetas;
    }

    public double getVolumeTotal() {
        return volumeTotal;
    }

    public double getTaxaReciclagem() {
        return taxaReciclagem;
    }
}
