package business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaService {
    public static class RegistroAuditoria {
        private final String tipo;
        private final String identificador;
        private final String criadoPor;
        private final LocalDateTime dataCriacao;
        private final String atualizadoPor;
        private final LocalDateTime dataUltimaAtualizacao;

        public RegistroAuditoria(String tipo, String identificador, String criadoPor,
                                 LocalDateTime dataCriacao, String atualizadoPor,
                                 LocalDateTime dataUltimaAtualizacao) {
            this.tipo = tipo;
            this.identificador = identificador;
            this.criadoPor = criadoPor;
            this.dataCriacao = dataCriacao;
            this.atualizadoPor = atualizadoPor;
            this.dataUltimaAtualizacao = dataUltimaAtualizacao;
        }

        public String getTipo() {
            return tipo;
        }

        public String getIdentificador() {
            return identificador;
        }

        public String getCriadoPor() {
            return criadoPor;
        }

        public LocalDateTime getDataCriacao() {
            return dataCriacao;
        }

        public String getAtualizadoPor() {
            return atualizadoPor;
        }

        public LocalDateTime getDataUltimaAtualizacao() {
            return dataUltimaAtualizacao;
        }
    }

    public List<RegistroAuditoria> listarAuditoria(List<Usuario> usuarios, List<?> conteineres, List<Coleta> coletas) {
        List<RegistroAuditoria> registros = new ArrayList<>();

        if (usuarios != null) {
            for (Usuario usuario : usuarios) {
                registros.add(new RegistroAuditoria(
                        "USUARIO",
                        usuario.getUsername(),
                        usuario.getCriadoPor() == null ? "SISTEMA" : usuario.getCriadoPor(),
                        usuario.getCreatedAt() == null ? LocalDateTime.now() : usuario.getCreatedAt(),
                        usuario.getAtualizadoPor() == null ? "SISTEMA" : usuario.getAtualizadoPor(),
                        usuario.getUpdatedAt() == null ? LocalDateTime.now() : usuario.getUpdatedAt()
                ));
            }
        }

        if (coletas != null) {
            for (Coleta coleta : coletas) {
                registros.add(new RegistroAuditoria(
                        "COLETA",
                        coleta.getId(),
                        coleta.getCriadoPor() == null ? "SISTEMA" : coleta.getCriadoPor(),
                        coleta.getCreatedAt() == null ? LocalDateTime.now() : coleta.getCreatedAt(),
                        coleta.getAtualizadoPor() == null ? "SISTEMA" : coleta.getAtualizadoPor(),
                        coleta.getUpdatedAt() == null ? LocalDateTime.now() : coleta.getUpdatedAt()
                ));
            }
        }

        registros.sort((a, b) -> b.getDataUltimaAtualizacao().compareTo(a.getDataUltimaAtualizacao()));
        return registros;
    }
}
