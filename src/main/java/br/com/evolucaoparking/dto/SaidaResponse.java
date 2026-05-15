package br.com.evolucaoparking.dto;

import br.com.evolucaoparking.model.ModalidadePagamento;
import br.com.evolucaoparking.model.TipoVeiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaidaResponse(
        Long registroId,
        String codigoRecibo,
        String placa,
        String nomeMotorista,
        TipoVeiculo tipoVeiculo,
        ModalidadePagamento modalidade,
        int numeroVaga,
        LocalDateTime entrada,
        LocalDateTime saida,
        long minutosPermanencia,
        BigDecimal valorPago
) {
}
