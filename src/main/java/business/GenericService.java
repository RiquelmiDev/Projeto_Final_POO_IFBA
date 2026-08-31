package business;

import java.util.List;

public interface GenericService<T extends AbstractModel<ID>, ID> {
    ID salvar(T entidade) throws RegraDeNegocioException;

    void atualizar(T entidade) throws RegraDeNegocioException;

    T buscarPorId(ID id);

    void deletar(ID id);

    List<T> buscarTodos();

    void validar(T entidade) throws RegraDeNegocioException;
}
