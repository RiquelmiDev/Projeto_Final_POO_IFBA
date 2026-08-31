package model;

import java.time.LocalDateTime;

public class Coleta extends AbstractModel<String> {
    private final String containerId;
    private final TipoResiduo tipoResiduo;
    private final double volume;
    private final LocalDateTime dataHora;

    public Coleta(String id, String containerId, TipoResiduo tipoResiduo, double volume) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificação da coleta é obrigatória.");
        }
        if (containerId == null || containerId.isBlank()) {
            throw new IllegalArgumentException("Conteiner da coleta é obrigatório.");
        }
        if (tipoResiduo == null) {
            throw new IllegalArgumentException("Tipo de resíduo é obrigatório.");
        }
        if (volume <= 0) {
            throw new IllegalArgumentException("Volume da coleta deve ser positivo.");
        }

        setId(id.trim());
        this.containerId = containerId;
        this.tipoResiduo = tipoResiduo;
        this.volume = volume;
        this.dataHora = LocalDateTime.now();
    }

    public String getContainerId() {
        return containerId;
    }

    public TipoResiduo getTipoResiduo() {
        return tipoResiduo;
    }

    public double getVolume() {
        return volume;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public boolean isPerigoso() {
        return tipoResiduo == TipoResiduo.PERIGOSO;
    }
}
