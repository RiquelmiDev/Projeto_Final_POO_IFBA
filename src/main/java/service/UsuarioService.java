package service;

import data.UsuarioRepository;
import exception.RegraDeNegocioException;
import model.PerfilUsuario;
import model.Usuario;

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
        cadastrarUsuario(username, senha, perfil, nome, "SISTEMA");
    }

    private boolean existeAdministradorCadastrado() {
        return repository.listar().stream().anyMatch(usuario -> usuario.getPerfil() == PerfilUsuario.ADMIN);
    }

    private void validarCadastroAdmin(String usuarioResponsavel) {
        if (existeAdministradorCadastrado()) {
            String responsavel = (usuarioResponsavel == null || usuarioResponsavel.isBlank()) ? "SISTEMA" : usuarioResponsavel.trim();
            Usuario usuarioAdmin = repository.buscar(responsavel);
            if (usuarioAdmin == null || usuarioAdmin.getPerfil() != PerfilUsuario.ADMIN) {
                throw new IllegalArgumentException("Cadastro de administrador só pode ser realizado por um usuário administrador autenticado.");
            }
        }
    }

    public void cadastrarUsuario(String username, String senha, PerfilUsuario perfil, String nome, String usuarioResponsavel) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Nome de usuário é obrigatório.");
        }
        if (repository.existe(username.trim())) {
            throw new IllegalArgumentException("Usuário já cadastrado no sistema.");
        }
        if (perfil == PerfilUsuario.ADMIN) {
            validarCadastroAdmin(usuarioResponsavel);
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

    public void atualizarUsuario(String username, String novaSenha, PerfilUsuario novoPerfil, String novoNome, String usuarioResponsavel) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Informe o usuário que será atualizado.");
        }

        Usuario usuario = repository.buscar(username.trim());
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado para atualização.");
        }

        if (novoPerfil == PerfilUsuario.ADMIN) {
            String responsavel = (usuarioResponsavel == null || usuarioResponsavel.isBlank()) ? "SISTEMA" : usuarioResponsavel.trim();
            Usuario usuarioLogado = repository.buscar(responsavel);
            if (usuarioLogado == null || usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
                throw new IllegalArgumentException("Cadastro de administrador só pode ser realizado por um usuário administrador autenticado.");
            }
        }

        try {
            Usuario atualizado = new Usuario(username.trim(), novaSenha == null ? usuario.getSenha() : novaSenha,
                    novoPerfil == null ? usuario.getPerfil() : novoPerfil,
                    novoNome == null || novoNome.isBlank() ? usuario.getNome() : novoNome);
            atualizado.setCreatedAt(usuario.getCreatedAt());
            atualizado.setCriadoPor(usuario.getCriadoPor() == null ? "SISTEMA" : usuario.getCriadoPor());
            atualizado.setUpdatedAt(LocalDateTime.now());
            atualizado.setAtualizadoPor((usuarioResponsavel == null || usuarioResponsavel.isBlank()) ? "SISTEMA" : usuarioResponsavel.trim());
            repository.salvar(atualizado);
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
