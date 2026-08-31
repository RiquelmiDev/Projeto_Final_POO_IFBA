package data;

import model.Coleta;

import java.util.List;

public class ColetaRepository extends GenericDAOImpl<Coleta, String> {

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
