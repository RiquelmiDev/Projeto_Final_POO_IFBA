package business;

import data.UsuarioRepository;

import java.util.List;

public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void cadastrarUsuario(String username, String senha, PerfilUsuario perfil, String nome) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Nome de usuário é obrigatório.");
        }
        if (repository.existe(username.trim())) {
            throw new IllegalArgumentException("Usuário já cadastrado no sistema.");
        }

        Usuario usuario = new Usuario(username, senha, perfil, nome);
        repository.salvar(usuario);
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
