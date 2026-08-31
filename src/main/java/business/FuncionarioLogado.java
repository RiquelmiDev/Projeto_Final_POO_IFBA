package business;

public class FuncionarioLogado {
    private final Usuario usuario;

    public FuncionarioLogado(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário logado é obrigatório.");
        }
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public PerfilUsuario getPerfil() {
        return usuario.getPerfil();
    }
}
