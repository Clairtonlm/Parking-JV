package br.com.evolucaoparking.dto;

import br.com.evolucaoparking.model.ModalidadePagamento;
import br.com.evolucaoparking.model.TipoVeiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EntradaRequest(
        @NotBlank(message = "Informe a placa do veículo")
        @Pattern(regexp = "^[A-Za-z]{3}[0-9][A-Za-z0-9][0-9]{2}$|^[A-Za-z]{3}-?[0-9]{4}$",
                message = "Placa inválida (use ABC1D23 ou ABC-1234)")
        String placa,

        @NotBlank(message = "Informe o nome do motorista")
        @Size(max = 120, message = "Nome com no máximo 120 caracteres")
        String nomeMotorista,

        @NotNull(message = "Selecione o tipo do veículo")
        TipoVeiculo tipoVeiculo,

        @NotNull(message = "Selecione a modalidade de pagamento")
        ModalidadePagamento modalidade
) {
    public static EntradaRequest vazio() {
        return new EntradaRequest("", "", TipoVeiculo.CARRO, ModalidadePagamento.FRACAO_HORA);
    }
}
