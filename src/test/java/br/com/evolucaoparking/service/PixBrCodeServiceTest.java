package br.com.evolucaoparking.service;

import br.com.evolucaoparking.config.PixProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PixBrCodeServiceTest {

    private PixBrCodeService service;

    @BeforeEach
    void setUp() {
        PixProperties props = new PixProperties();
        props.setChave("+558592187076");
        props.setNomeBeneficiario("Clairton Lima");
        props.setCidade("Fortaleza");
        service = new PixBrCodeService(props);
    }

    @Test
    void geraPayloadComValor() {
        String payload = service.gerarPayload(new BigDecimal("25.50"), "ABC123");
        assertTrue(payload.startsWith("000201"));
        assertTrue(payload.contains("br.gov.bcb.pix"));
        assertTrue(payload.contains("25.50"));
        assertTrue(payload.endsWith(payload.substring(payload.length() - 4)));
    }

    @Test
    void geraPayloadSemValor() {
        String payload = service.gerarPayload(null, "ENTRADA1");
        assertTrue(payload.contains("br.gov.bcb.pix"));
        assertTrue(!payload.contains("540"));
    }
}
