package business;

import data.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

public class UsuarioService extends GenericServiceImpl<Usuario, String> {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public void validar(Usuario entidade) throws RegraDeNegocioException {
        if (entidade == null) {
            throw new RegraDeNegocioException("Usuário não pode ser nulo.");
        }
        if (entidade.getUsername() == null || entidade.getUsername().isBlank()) {
            throw new RegraDeNegocioException("Nome de usuário é obrigatório.");
        }
        if (repository.existe(entidade.getUsername().trim())) {
            throw new RegraDeNegocioException("Usuário já cadastrado no sistema.");
        }
    }

    public void cadastrarUsuario(String username, String senha, PerfilUsuario perfil, String nome) {
        cadastrarUsuario(username, senha, perfil, nome, username == null ? "SISTEMA" : username.trim());
    }

    public void cadastrarUsuario(String username, String senha, PerfilUsuario perfil, String nome, String usuarioResponsavel) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Nome de usuário é obrigatório.");
        }
        if (repository.existe(username.trim())) {
            throw new IllegalArgumentException("Usuário já cadastrado no sistema.");
        }

        try {
            Usuario usuario = new Usuario(username, senha, perfil, nome);
            String responsavel = (usuarioResponsavel == null || usuarioResponsavel.isBlank()) ? "SISTEMA" : usuarioResponsavel.trim();
            usuario.setCreatedAt(LocalDateTime.now());
            usuario.setCriadoPor(responsavel);
            usuario.setUpdatedAt(LocalDateTime.now());
            usuario.setAtualizadoPor(responsavel);
            repository.salvar(usuario);
        } catch (RegraDeNegocioException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public Usuario autenticar(String username, String senha) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Informe o usuário.");
        }
        Usuario usuario = repository.buscar(username.trim());
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (!usuario.validarSenha(senha)) {
            throw new IllegalArgumentException("Senha inválida.");
        }
        return usuario;
    }

    public Usuario buscarUsuario(String username) {
        return repository.buscar(username);
    }

    public List<Usuario> listarUsuarios() {
        return repository.listar();
    }

    public void removerUsuario(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Informe o usuário para remover.");
        }
        if (!repository.existe(username.trim())) {
            throw new IllegalArgumentException("Usuário não encontrado para remoção.");
        }
        repository.remover(username.trim());
    }
}
