package data;

import exception.RegraDeNegocioException;
import model.Usuario;

import java.util.List;

public class UsuarioRepository extends GenericDAOImpl<Usuario, String> {
    @Override
    public String salvar(Usuario usuario) throws RegraDeNegocioException {
        return super.salvar(usuario);
    }

    public Usuario buscar(String username) {
        return buscarPorId(username);
    }

    public boolean existe(String username) {
        return storage.containsKey(username);
    }

    public List<Usuario> listar() {
        return buscarTodos();
    }

    public void remover(String username) {
        deletar(username);
    }
}
