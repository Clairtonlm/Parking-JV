package br.com.evolucaoparking.dto;

import br.com.evolucaoparking.model.RegistroEstacionamento;
import br.com.evolucaoparking.model.Vaga;

import java.util.List;

public record OperacaoView(
        int totalVagas,
        long vagasLivres,
        long vagasOcupadas,
        List<Vaga> vagas,
        List<RegistroEstacionamento> veiculosNoPatio
) {
}
