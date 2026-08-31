package business;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditoriaServiceTest {

    @Test
    void deveListarInformacoesDeAuditoriaDosRegistros() {
        Usuario usuario = new Usuario("joao", "abc123", PerfilUsuario.ADMIN, "João Silva");
        usuario.setCreatedAt(LocalDateTime.of(2024, 1, 15, 9, 0));
        usuario.setCriadoPor("admin");
        usuario.setUpdatedAt(LocalDateTime.of(2024, 1, 16, 10, 30));
        usuario.setAtualizadoPor("admin");

        Coleta coleta = new Coleta("C-001", "P-1", TipoResiduo.RECICLAVEL, 20.5);
        coleta.setCreatedAt(LocalDateTime.of(2024, 2, 1, 8, 15));
        coleta.setCriadoPor("maria");
        coleta.setUpdatedAt(LocalDateTime.of(2024, 2, 2, 12, 0));
        coleta.setAtualizadoPor("joao");

        AuditoriaService service = new AuditoriaService();
        List<AuditoriaService.RegistroAuditoria> registros = service.listarAuditoria(List.of(usuario), List.of(), List.of(coleta));

        assertEquals(2, registros.size());
        assertTrue(registros.stream().anyMatch(r -> r.getIdentificador().equals("joao") && r.getCriadoPor().equals("admin")));
        assertTrue(registros.stream().anyMatch(r -> r.getIdentificador().equals("C-001") && r.getAtualizadoPor().equals("joao")));
    }
}
