package business;

import data.ColetaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ColetaService {
    private final ColetaRepository repository;

    public ColetaService(ColetaRepository repository) {
        this.repository = repository;
    }

    public void cadastrarContainer(String id, double capacidadeMaxima, String tipo, String localizacao) {
        Conteiner Conteiner = new Conteiner(id, capacidadeMaxima, tipo, localizacao);
        repository.salvarConteiner(Conteiner);
    }

    public void registrarColeta(String containerId, String coletaId, TipoResiduo tipoResiduo, double volume) {
        Conteiner Conteiner = repository.buscarConteiner(containerId);
        if (Conteiner == null) {
            throw new IllegalArgumentException("Conteiner não encontrado.");
        }

        double volumeAtual = repository.totalVolumeColetadoPorContainer(containerId);
        if (volumeAtual + volume > Conteiner.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Operação abortada: coleta ultrapassa a capacidade máxima do Conteiner.");
        }

        Coleta coleta = new Coleta(coletaId, containerId, tipoResiduo, volume);
        repository.salvarColeta(coleta);
    }

    public List<Coleta> listarColetasOrdenadasPorPrioridade() {
        return repository.listarColetas().stream()
                .sorted(Comparator.comparing(Coleta::isPerigoso).reversed())
                .collect(Collectors.toList());
    }

    public Relatorio gerarRelatorio() {
        List<Coleta> coletas = repository.listarColetas();
        double volumeTotal = coletas.stream().mapToDouble(Coleta::getVolume).sum();
        long reciclaveis = coletas.stream()
                .filter(c -> c.getTipoResiduo() == TipoResiduo.RECICLAVEL)
                .count();

        double taxaReciclagem = coletas.isEmpty() ? 0.0 : (double) reciclaveis / coletas.size();
        return new Relatorio(coletas.size(), volumeTotal, taxaReciclagem);
    }
}
