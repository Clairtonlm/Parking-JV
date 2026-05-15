package br.com.evolucaoparking.service;

import br.com.evolucaoparking.config.ParkingProperties;
import br.com.evolucaoparking.model.ModalidadePagamento;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TarifaService {

    private final ParkingProperties properties;

    public TarifaService(ParkingProperties properties) {
        this.properties = properties;
    }

    public BigDecimal calcular(ModalidadePagamento modalidade, long minutosPermanencia) {
        return switch (modalidade) {
            case VALOR_FIXO_24H -> calcularValorFixo24h(minutosPermanencia);
            case FRACAO_HORA -> calcularFracaoHora(minutosPermanencia);
            case BLOCO_5H -> calcularBloco(
                    minutosPermanencia,
                    properties.getBloco5hHoras() * 60L,
                    properties.getBloco5hValor());
            case BLOCO_10H -> calcularBloco(
                    minutosPermanencia,
                    properties.getBloco10hHoras() * 60L,
                    properties.getBloco10hValor());
        };
    }

    private BigDecimal calcularValorFixo24h(long minutos) {
        long periodos24h = (minutos + (24 * 60) - 1) / (24 * 60);
        if (periodos24h < 1) {
            periodos24h = 1;
        }
        return moeda(properties.getValorFixo24h() * periodos24h);
    }

    private BigDecimal calcularFracaoHora(long minutos) {
        if (minutos <= properties.getToleranciaMinutos()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        long minutosCobraveis = minutos - properties.getToleranciaMinutos();
        long horas = (minutosCobraveis + 59) / 60;
        return moeda(horas * properties.getTarifaHora());
    }

    private BigDecimal calcularBloco(long minutos, long minutosBloco, double valorBloco) {
        if (minutos <= properties.getToleranciaMinutos()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (minutos <= minutosBloco) {
            return moeda(valorBloco);
        }
        long minutosExcesso = minutos - minutosBloco;
        long blocos15 = (minutosExcesso + 14) / 15;
        return moeda(valorBloco + blocos15 * properties.getExcessoBloco15min());
    }

    private BigDecimal moeda(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);
    }
}
