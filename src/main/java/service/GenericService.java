package service;

import exception.RegraDeNegocioException;
import model.AbstractModel;
import model.Coleta;
import model.PerfilUsuario;
import model.Relatorio;
import model.TipoResiduo;
import model.Usuario;

import java.util.List;

public interface GenericService<T extends AbstractModel<ID>, ID> {
    ID salvar(T entidade) throws RegraDeNegocioException;

    void atualizar(T entidade) throws RegraDeNegocioException;

    T buscarPorId(ID id);

    void deletar(ID id);

    List<T> buscarTodos();

    void validar(T entidade) throws RegraDeNegocioException;

    default void cadastrarUsuario(String username, String senha, PerfilUsuario perfil, String nome) {
        throw new UnsupportedOperationException("Operação não suportada para esta entidade.");
    }

    default Usuario autenticar(String username, String senha) {
        throw new UnsupportedOperationException("Operação não suportada para esta entidade.");
    }

    default Usuario buscarUsuario(String username) {
        throw new UnsupportedOperationException("Operação não suportada para esta entidade.");
    }

    default List<Usuario> listarUsuarios() {
        throw new UnsupportedOperationException("Operação não suportada para esta entidade.");
    }

    default void removerUsuario(String username) {
        throw new UnsupportedOperationException("Operação não suportada para esta entidade.");
    }

    default void cadastrarContainer(String id, double capacidadeMaxima, String tipo, String localizacao) {
        throw new UnsupportedOperationException("Operação não suportada para esta entidade.");
    }

    default void registrarColeta(String containerId, String coletaId, TipoResiduo tipoResiduo, double volume) {
        throw new UnsupportedOperationException("Operação não suportada para esta entidade.");
    }

    default List<Coleta> listarColetasOrdenadasPorPrioridade() {
        throw new UnsupportedOperationException("Operação não suportada para esta entidade.");
    }

    default Relatorio gerarRelatorio() {
        throw new UnsupportedOperationException("Operação não suportada para esta entidade.");
    }
}
