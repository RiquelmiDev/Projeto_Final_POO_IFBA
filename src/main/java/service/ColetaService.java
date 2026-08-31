package service;

import data.ColetaRepository;
import exception.RegraDeNegocioException;
import model.Coleta;
import model.Conteiner;
import model.Relatorio;
import model.TipoResiduo;
import model.Usuario;

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

    public void atualizarContainer(String id, double novaCapacidadeMaxima, String novoTipo, String novaLocalizacao, String usuarioResponsavel) {
        Conteiner conteiner = repository.buscarConteiner(id);
        if (conteiner == null) {
            throw new IllegalArgumentException("Conteiner não encontrado para atualização.");
        }

        Conteiner atualizado = new Conteiner(id, novaCapacidadeMaxima,
                novoTipo == null || novoTipo.isBlank() ? conteiner.getTipo() : novoTipo,
                novaLocalizacao == null || novaLocalizacao.isBlank() ? conteiner.getLocalizacao() : novaLocalizacao);
        atualizado.setCreatedAt(conteiner.getCreatedAt());
        atualizado.setCriadoPor(conteiner.getCriadoPor() == null ? "SISTEMA" : conteiner.getCriadoPor());
        atualizado.setUpdatedAt(LocalDateTime.now());
        atualizado.setAtualizadoPor((usuarioResponsavel == null || usuarioResponsavel.isBlank()) ? "SISTEMA" : usuarioResponsavel.trim());
        repository.salvarConteiner(atualizado);
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

    public void atualizarColeta(String coletaId, String novoContainerId, TipoResiduo novoTipoResiduo, double novoVolume, String usuarioResponsavel) {
        Coleta coleta = repository.buscarPorId(coletaId);
        if (coleta == null) {
            throw new IllegalArgumentException("Coleta não encontrada para atualização.");
        }

        Coleta atualizada = new Coleta(coletaId, novoContainerId == null || novoContainerId.isBlank() ? coleta.getContainerId() : novoContainerId,
                novoTipoResiduo == null ? coleta.getTipoResiduo() : novoTipoResiduo,
                novoVolume <= 0 ? coleta.getVolume() : novoVolume);
        atualizada.setCreatedAt(coleta.getCreatedAt());
        atualizada.setCriadoPor(coleta.getCriadoPor() == null ? "SISTEMA" : coleta.getCriadoPor());
        atualizada.setUpdatedAt(LocalDateTime.now());
        atualizada.setAtualizadoPor((usuarioResponsavel == null || usuarioResponsavel.isBlank()) ? "SISTEMA" : usuarioResponsavel.trim());
        repository.salvarColeta(atualizada);
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
