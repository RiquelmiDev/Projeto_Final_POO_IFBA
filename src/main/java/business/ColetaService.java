package business;

import data.ColetaRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColetaService extends GenericServiceImpl<Coleta, String> {
    private final ColetaRepository repository;

    public ColetaService(ColetaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public void validar(Coleta entidade) throws RegraDeNegocioException {
        if (entidade == null) {
            throw new RegraDeNegocioException("Coleta não pode ser nula.");
        }
        if (entidade.getId() == null || entidade.getId().isBlank()) {
            throw new RegraDeNegocioException("Identificação da coleta é obrigatória.");
        }
    }

    public void cadastrarContainer(String id, double capacidadeMaxima, String tipo, String localizacao) {
        cadastrarContainer(id, capacidadeMaxima, tipo, localizacao, "SISTEMA");
    }

    public void cadastrarContainer(String id, double capacidadeMaxima, String tipo, String localizacao, String usuarioResponsavel) {
        Conteiner conteiner = new Conteiner(id, capacidadeMaxima, tipo, localizacao);
        String responsavel = (usuarioResponsavel == null || usuarioResponsavel.isBlank()) ? "SISTEMA" : usuarioResponsavel.trim();
        conteiner.setCreatedAt(LocalDateTime.now());
        conteiner.setCriadoPor(responsavel);
        conteiner.setUpdatedAt(LocalDateTime.now());
        conteiner.setAtualizadoPor(responsavel);
        repository.salvarConteiner(conteiner);
    }

    public void registrarColeta(String containerId, String coletaId, TipoResiduo tipoResiduo, double volume) {
        registrarColeta(containerId, coletaId, tipoResiduo, volume, "SISTEMA");
    }

    public void registrarColeta(String containerId, String coletaId, TipoResiduo tipoResiduo, double volume, String usuarioResponsavel) {
        Conteiner conteiner = repository.buscarConteiner(containerId);
        if (conteiner == null) {
            throw new IllegalArgumentException("Conteiner não encontrado.");
        }

        double volumeAtual = repository.totalVolumeColetadoPorContainer(containerId);
        if (volumeAtual + volume > conteiner.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Operação abortada: coleta ultrapassa a capacidade máxima do Conteiner.");
        }

        Coleta coleta = new Coleta(coletaId, containerId, tipoResiduo, volume);
        String responsavel = (usuarioResponsavel == null || usuarioResponsavel.isBlank()) ? "SISTEMA" : usuarioResponsavel.trim();
        coleta.setCreatedAt(LocalDateTime.now());
        coleta.setCriadoPor(responsavel);
        coleta.setUpdatedAt(LocalDateTime.now());
        coleta.setAtualizadoPor(responsavel);
        repository.salvarColeta(coleta);
    }

    public List<Coleta> listarColetasOrdenadasPorPrioridade() {
        return repository.listarColetas().stream()
                .sorted(Comparator.comparing(Coleta::isPerigoso).reversed())
                .collect(Collectors.toList());
    }

    public Relatorio gerarRelatorio() {
        List<Coleta> coletas = repository.listarColetas();
        List<Conteiner> conteineres = repository.listarConteineres();

        double volumeTotal = coletas.stream().mapToDouble(Coleta::getVolume).sum();
        long reciclaveis = coletas.stream()
                .filter(c -> c.getTipoResiduo() == TipoResiduo.RECICLAVEL)
                .count();

        double taxaReciclagem = coletas.isEmpty() ? 0.0 : (double) reciclaveis / coletas.size();

        Map<TipoResiduo, Double> volumePorTipo = new EnumMap<>(TipoResiduo.class);
        for (TipoResiduo tipoResiduo : TipoResiduo.values()) {
            volumePorTipo.put(tipoResiduo, 0.0);
        }

        Map<String, Double> volumePorContainer = new HashMap<>();
        for (Coleta coleta : coletas) {
            volumePorTipo.put(coleta.getTipoResiduo(),
                    volumePorTipo.get(coleta.getTipoResiduo()) + coleta.getVolume());
            volumePorContainer.merge(coleta.getContainerId(), coleta.getVolume(), Double::sum);
        }

        int totalColetasPerigosas = (int) coletas.stream()
                .filter(Coleta::isPerigoso)
                .count();

        return new Relatorio(
                coletas.size(),
                volumeTotal,
                taxaReciclagem,
                conteineres.size(),
                totalColetasPerigosas,
                volumePorTipo,
                volumePorContainer
        );
    }
}
