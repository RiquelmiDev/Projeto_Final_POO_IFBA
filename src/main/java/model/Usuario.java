package model;

public class Usuario extends AbstractModel<String> {
    private final String username;
    private final String senha;
    private final PerfilUsuario perfil;
    private final String nome;

    public Usuario(String username, String senha, PerfilUsuario perfil, String nome) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Nome de usuário é obrigatório.");
        }
        if (senha == null || senha.length() < 4) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 4 caracteres.");
        }
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil do usuário é obrigatório.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome completo do usuário é obrigatório.");
        }

        this.username = username.trim();
        this.senha = senha;
        this.perfil = perfil;
        this.nome = nome.trim();
        setId(this.username);
    }

    public String getUsername() {
        return username;
    }

    public String getSenha() {
        return senha;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public String getNome() {
        return nome;
    }

    public boolean validarSenha(String senhaInformada) {
        return senha.equals(senhaInformada);
    }
}
