package data;

import business.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioRepository {
    private final Map<String, Usuario> usuarios = new HashMap<>();

    public void salvar(Usuario usuario) {
        usuarios.put(usuario.getUsername(), usuario);
    }

    public Usuario buscar(String username) {
        return usuarios.get(username);
    }

    public boolean existe(String username) {
        return usuarios.containsKey(username);
    }

    public List<Usuario> listar() {
        return new ArrayList<>(usuarios.values());
    }

    public void remover(String username) {
        usuarios.remove(username);
    }
}
