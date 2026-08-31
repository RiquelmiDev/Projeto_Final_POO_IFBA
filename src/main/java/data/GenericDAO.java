package data;

import business.AbstractModel;
import business.RegraDeNegocioException;

import java.util.List;

public interface GenericDAO<T extends AbstractModel<ID>, ID> {
    ID salvar(T entidade) throws RegraDeNegocioException;

    void atualizar(T entidade) throws RegraDeNegocioException;

    T buscarPorId(ID id);

    void deletar(ID id);

    List<T> buscarTodos();
}
