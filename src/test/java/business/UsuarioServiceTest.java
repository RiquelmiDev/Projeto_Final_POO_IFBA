package business;

import data.UsuarioRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    @Test
    void deveAutenticarUsuarioValidoERejeitarSenhaInvalida() {
        UsuarioRepository repository = new UsuarioRepository();
        UsuarioService service = new UsuarioService(repository);

        service.cadastrarUsuario("admin", "123456", PerfilUsuario.ADMIN, "Administrador");
        service.cadastrarUsuario("maria", "654321", PerfilUsuario.COLABORADOR, "Maria");

        Usuario admin = service.autenticar("admin", "123456");
        Usuario colaborador = service.autenticar("maria", "654321");

        assertNotNull(admin);
        assertNotNull(colaborador);
        assertEquals("admin", admin.getUsername());
        assertEquals(PerfilUsuario.ADMIN, admin.getPerfil());
        assertEquals("maria", colaborador.getUsername());
        assertEquals(PerfilUsuario.COLABORADOR, colaborador.getPerfil());
        assertThrows(IllegalArgumentException.class, () -> service.autenticar("admin", "senha-errada"));
    }

    @Test
    void devePermitirCadastroDeUsuarioAdministrativoEColaborador() {
        UsuarioRepository repository = new UsuarioRepository();
        UsuarioService service = new UsuarioService(repository);

        service.cadastrarUsuario("joao", "abc123", PerfilUsuario.COLABORADOR, "João");
        service.cadastrarUsuario("luis", "senha@321", PerfilUsuario.ADMIN, "Luís");

        assertEquals(2, service.listarUsuarios().size());
        assertEquals(PerfilUsuario.COLABORADOR, service.buscarUsuario("joao").getPerfil());
        assertEquals(PerfilUsuario.ADMIN, service.buscarUsuario("luis").getPerfil());
    }

    @Test
    void deveRegistrarQuemExecutouOCadastroNoHistoricoDeAuditoria() {
        UsuarioRepository repository = new UsuarioRepository();
        UsuarioService service = new UsuarioService(repository);

        service.cadastrarUsuario("joao", "abc123", PerfilUsuario.COLABORADOR, "João", "admin");

        Usuario usuario = service.buscarUsuario("joao");
        assertNotNull(usuario);
        assertEquals("admin", usuario.getCriadoPor());
        assertEquals("admin", usuario.getAtualizadoPor());
    }
}
