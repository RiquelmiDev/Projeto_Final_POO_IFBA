package data;

import business.AbstractModel;
import business.RegraDeNegocioException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenericDAOImpl<T extends AbstractModel<ID>, ID> implements GenericDAO<T, ID> {
    protected final Map<ID, T> storage = new HashMap<>();

    @Override
    public ID salvar(T entidade) throws RegraDeNegocioException {
        if (entidade == null) {
            throw new RegraDeNegocioException("Entidade não pode ser nula.");
        }
        if (entidade.getId() == null) {
            throw new RegraDeNegocioException("Identificador da entidade é obrigatório.");
        }
        storage.put(entidade.getId(), entidade);
        return entidade.getId();
    }

    @Override
    public void atualizar(T entidade) throws RegraDeNegocioException {
        if (entidade == null) {
            throw new RegraDeNegocioException("Entidade não pode ser nula.");
        }
        if (entidade.getId() == null) {
            throw new RegraDeNegocioException("Identificador da entidade é obrigatório.");
        }
        storage.put(entidade.getId(), entidade);
    }

    @Override
    public T buscarPorId(ID id) {
        return storage.get(id);
    }

    @Override
    public void deletar(ID id) {
        storage.remove(id);
    }

    @Override
    public List<T> buscarTodos() {
        return new ArrayList<>(storage.values());
    }
}
