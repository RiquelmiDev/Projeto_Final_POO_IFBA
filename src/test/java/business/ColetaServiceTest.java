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
    void deveEnriquecerRelatorioComMetricasPorTipoEContainer() {
        ColetaRepository repository = new ColetaRepository();
        ColetaService service = new ColetaService(repository);

        service.cadastrarContainer("C-001", 100.0, "Reciclável", "Bloco A");
        service.cadastrarContainer("C-002", 100.0, "Orgânico", "Bloco B");

        service.registrarColeta("C-001", "R-001", TipoResiduo.RECICLAVEL, 10.0);
        service.registrarColeta("C-001", "R-002", TipoResiduo.PERIGOSO, 15.0);
        service.registrarColeta("C-002", "R-003", TipoResiduo.ORGANICO, 20.0);

        Relatorio relatorio = service.gerarRelatorio();

        assertEquals(2, relatorio.getTotalConteineres());
        assertEquals(10.0, relatorio.getVolumePorTipo(TipoResiduo.RECICLAVEL), 0.0001);
        assertEquals(15.0, relatorio.getVolumePorTipo(TipoResiduo.PERIGOSO), 0.0001);
        assertEquals(20.0, relatorio.getVolumePorTipo(TipoResiduo.ORGANICO), 0.0001);
        assertEquals(25.0, relatorio.getVolumePorContainer("C-001"), 0.0001);
        assertEquals(20.0, relatorio.getVolumePorContainer("C-002"), 0.0001);
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
