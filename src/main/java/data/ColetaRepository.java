package data;

import business.Coleta;
import business.Conteiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColetaRepository {
    private final Map<String, Conteiner> containers = new HashMap<>();
    private final List<Coleta> coletas = new ArrayList<>();

    public void salvarConteiner(Conteiner Conteiner) {
        containers.put(Conteiner.getId(), Conteiner);
    }

    public Conteiner buscarConteiner(String id) {
        return containers.get(id);
    }

    public List<Conteiner> listarConteineres() {
        return new ArrayList<>(containers.values());
    }

    public void salvarColeta(Coleta coleta) {
        coletas.add(coleta);
    }

    public List<Coleta> listarColetas() {
        return new ArrayList<>(coletas);
    }

    public double totalVolumeColetadoPorContainer(String containerId) {
        return coletas.stream()
                .filter(coleta -> coleta.getContainerId().equals(containerId))
                .mapToDouble(Coleta::getVolume)
                .sum();
    }
}
