package business;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class Relatorio {
    private final int totalColetas;
    private final double volumeTotal;
    private final double taxaReciclagem;
    private final int totalConteineres;
    private final int totalColetasPerigosas;
    private final Map<TipoResiduo, Double> volumePorTipo;
    private final Map<String, Double> volumePorContainer;

    public Relatorio(int totalColetas, double volumeTotal, double taxaReciclagem,
                     int totalConteineres, int totalColetasPerigosas,
                     Map<TipoResiduo, Double> volumePorTipo, Map<String, Double> volumePorContainer) {
        this.totalColetas = totalColetas;
        this.volumeTotal = volumeTotal;
        this.taxaReciclagem = taxaReciclagem;
        this.totalConteineres = totalConteineres;
        this.totalColetasPerigosas = totalColetasPerigosas;

        this.volumePorTipo = new EnumMap<>(TipoResiduo.class);
        for (TipoResiduo tipoResiduo : TipoResiduo.values()) {
            this.volumePorTipo.put(tipoResiduo, 0.0);
        }
        if (volumePorTipo != null) {
            volumePorTipo.forEach(this.volumePorTipo::put);
        }

        this.volumePorContainer = new HashMap<>();
        if (volumePorContainer != null) {
            this.volumePorContainer.putAll(volumePorContainer);
        }
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

    public int getTotalConteineres() {
        return totalConteineres;
    }

    public int getTotalColetasPerigosas() {
        return totalColetasPerigosas;
    }

    public double getVolumePorTipo(TipoResiduo tipoResiduo) {
        return volumePorTipo.getOrDefault(tipoResiduo, 0.0);
    }

    public double getVolumePorContainer(String containerId) {
        return volumePorContainer.getOrDefault(containerId, 0.0);
    }

    public Map<TipoResiduo, Double> getVolumePorTipoDetalhado() {
        return new EnumMap<>(volumePorTipo);
    }

    public Map<String, Double> getVolumePorContainerDetalhado() {
        return new HashMap<>(volumePorContainer);
    }

    @Override
    public String toString() {
        StringBuilder resumo = new StringBuilder();
        resumo.append("Total de coletas: ")
                .append(totalColetas)
                .append("\n");
        resumo.append("Volume total coletado: ")
                .append(formatarNumero(volumeTotal))
                .append(" L\n");
        resumo.append("Taxa de reciclagem: ")
                .append(formatarPercentual(taxaReciclagem))
                .append("\n");
        resumo.append("Total de contêineres cadastrados: ")
                .append(totalConteineres)
                .append("\n");
        resumo.append("Coletas com resíduos perigosos: ")
                .append(totalColetasPerigosas)
                .append("\n\n");

        resumo.append("Volume por tipo:\n");
        for (TipoResiduo tipoResiduo : TipoResiduo.values()) {
            resumo.append(" - ")
                    .append(tipoResiduo)
                    .append(": ")
                    .append(formatarNumero(getVolumePorTipo(tipoResiduo)))
                    .append(" L\n");
        }

        resumo.append("\nVolume por contêiner:\n");
        if (volumePorContainer.isEmpty()) {
            resumo.append(" - Nenhuma coleta registrada em contêineres.\n");
        } else {
            String linhasContainer = volumePorContainer.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> " - " + entry.getKey() + ": " + formatarNumero(entry.getValue()) + " L")
                    .collect(Collectors.joining("\n"));
            resumo.append(linhasContainer).append("\n");
        }

        return resumo.toString();
    }

    private String formatarNumero(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private String formatarPercentual(double valor) {
        return String.format(Locale.US, "%.2f%%", valor * 100.0);
    }
}
