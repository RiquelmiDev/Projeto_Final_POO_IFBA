package data;

import model.Conteiner;

import java.util.List;

public class ConteinerRepository extends GenericDAOImpl<Conteiner, String> {

    public void salvarConteiner(Conteiner conteiner) {
        storage.put(conteiner.getId(), conteiner);
    }

    public Conteiner buscarConteiner(String id) {
        return buscarPorId(id);
    }

    public List<Conteiner> listarConteineres() {
        return buscarTodos();
    }

    public void removerConteiner(String id) {
        deletar(id);
    }
}
