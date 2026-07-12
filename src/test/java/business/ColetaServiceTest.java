package business;

import data.ColetaRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColetaServiceTest {

    @Test
    void deveAbortarQuandoColetaUltrapassarCapacidade() {
        ColetaRepository repository = new ColetaRepository();
        ColetaService service = new ColetaService(repository);

        service.cadastrarContainer("C-001", 100.0, "Reciclável", "Bloco A");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarColeta("C-001", "R-001", TipoResiduo.RECICLAVEL, 150.0)
        );

        assertTrue(exception.getMessage().contains("capacidade"));
    }

    @Test
    void deveDarPrioridadeParaResiduosPerigosos() {
        ColetaRepository repository = new ColetaRepository();
        ColetaService service = new ColetaService(repository);

        service.cadastrarContainer("C-001", 100.0, "Perigoso", "Bloco A");
        service.cadastrarContainer("C-002", 100.0, "Reciclável", "Bloco B");

        service.registrarColeta("C-001", "R-001", TipoResiduo.PERIGOSO, 20.0);
        service.registrarColeta("C-002", "R-002", TipoResiduo.RECICLAVEL, 15.0);

        assertEquals(TipoResiduo.PERIGOSO, service.listarColetasOrdenadasPorPrioridade().get(0).getTipoResiduo());
    }

    @Test
    void deveCalcularMetricasDeReciclagemCorretamente() {
        ColetaRepository repository = new ColetaRepository();
        ColetaService service = new ColetaService(repository);

        service.cadastrarContainer("C-001", 100.0, "Reciclável", "Bloco A");
        service.cadastrarContainer("C-002", 100.0, "Orgânico", "Bloco B");

        service.registrarColeta("C-001", "R-001", TipoResiduo.RECICLAVEL, 10.0);
        service.registrarColeta("C-002", "R-002", TipoResiduo.ORGANICO, 10.0);
        service.registrarColeta("C-001", "R-003", TipoResiduo.RECICLAVEL, 10.0);

        Relatorio relatorio = service.gerarRelatorio();

        assertEquals(3, relatorio.getTotalColetas());
        assertEquals(30.0, relatorio.getVolumeTotal());
        assertEquals(2.0 / 3.0, relatorio.getTaxaReciclagem(), 0.0001);
    }
}
