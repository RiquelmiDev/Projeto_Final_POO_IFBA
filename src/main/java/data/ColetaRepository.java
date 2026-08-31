package data;

import model.Coleta;
import model.Conteiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColetaRepository extends GenericDAOImpl<Coleta, String> {
    private final Map<String, Conteiner> containers = new HashMap<>();

    public void salvarConteiner(Conteiner conteiner) {
        containers.put(conteiner.getId(), conteiner);
    }

    public Conteiner buscarConteiner(String id) {
        return containers.get(id);
    }

    public List<Conteiner> listarConteineres() {
        return new ArrayList<>(containers.values());
    }

    public void salvarColeta(Coleta coleta) {
        storage.put(coleta.getId(), coleta);
    }

    public List<Coleta> listarColetas() {
        return buscarTodos();
    }

    public double totalVolumeColetadoPorContainer(String containerId) {
        return storage.values().stream()
                .filter(coleta -> coleta.getContainerId().equals(containerId))
                .mapToDouble(Coleta::getVolume)
                .sum();
    }
}
