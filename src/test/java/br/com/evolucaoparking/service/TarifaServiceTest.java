package br.com.evolucaoparking.service;

import br.com.evolucaoparking.config.ParkingProperties;
import br.com.evolucaoparking.model.ModalidadePagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarifaServiceTest {

    private TarifaService tarifaService;

    @BeforeEach
    void setUp() {
        ParkingProperties props = new ParkingProperties();
        props.setValorFixo24h(40.0);
        props.setTarifaHora(5.0);
        props.setToleranciaMinutos(15);
        props.setBloco5hValor(20.0);
        props.setBloco5hHoras(5);
        props.setBloco10hValor(45.0);
        props.setBloco10hHoras(10);
        props.setExcessoBloco15min(2.0);
        tarifaService = new TarifaService(props);
    }

    @Test
    void fracaoComTolerancia() {
        assertEquals(new BigDecimal("0.00"), tarifaService.calcular(ModalidadePagamento.FRACAO_HORA, 10));
        assertEquals(new BigDecimal("5.00"), tarifaService.calcular(ModalidadePagamento.FRACAO_HORA, 30));
    }

    @Test
    void bloco5hComExcedente() {
        assertEquals(new BigDecimal("20.00"), tarifaService.calcular(ModalidadePagamento.BLOCO_5H, 300));
        assertEquals(new BigDecimal("24.00"), tarifaService.calcular(ModalidadePagamento.BLOCO_5H, 316));
    }

    @Test
    void valorFixo24h() {
        assertEquals(new BigDecimal("40.00"), tarifaService.calcular(ModalidadePagamento.VALOR_FIXO_24H, 60));
        assertEquals(new BigDecimal("80.00"), tarifaService.calcular(ModalidadePagamento.VALOR_FIXO_24H, 24 * 60 + 1));
    }
}
