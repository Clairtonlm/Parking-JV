package br.com.evolucaoparking.service;

import br.com.evolucaoparking.config.ParkingProperties;
import br.com.evolucaoparking.model.ModalidadePagamento;
import br.com.evolucaoparking.model.RegistroEstacionamento;
import br.com.evolucaoparking.model.TipoVeiculo;
import br.com.evolucaoparking.repository.RegistroEstacionamentoRepository;
import br.com.evolucaoparking.repository.VagaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstacionamentoServiceTest {

    @Mock
    private VagaRepository vagaRepository;

    @Mock
    private RegistroEstacionamentoRepository registroRepository;

    @Mock
    private ParkingProperties properties;

    @Mock
    private TarifaService tarifaService;

    @Mock
    private TurnoService turnoService;

    @InjectMocks
    private EstacionamentoService estacionamentoService;

    @Test
    void atualizarModalidade_alteraRegistroAtivo() {
        RegistroEstacionamento registro = new RegistroEstacionamento(
                "ABC1D23",
                "João",
                TipoVeiculo.CARRO,
                ModalidadePagamento.FRACAO_HORA,
                3,
                LocalDateTime.now().minusHours(1),
                null
        );

        when(registroRepository.findByIdAndSaidaIsNull(1L)).thenReturn(Optional.of(registro));
        when(registroRepository.save(registro)).thenReturn(registro);

        RegistroEstacionamento atualizado = estacionamentoService.atualizarModalidade(1L, ModalidadePagamento.BLOCO_5H);

        assertEquals(ModalidadePagamento.BLOCO_5H, atualizado.getModalidade());
        verify(registroRepository).save(registro);
    }

    @Test
    void atualizarModalidade_rejeitaMesmaModalidade() {
        RegistroEstacionamento registro = new RegistroEstacionamento(
                "ABC1D23",
                "João",
                TipoVeiculo.CARRO,
                ModalidadePagamento.FRACAO_HORA,
                3,
                LocalDateTime.now(),
                null
        );

        when(registroRepository.findByIdAndSaidaIsNull(1L)).thenReturn(Optional.of(registro));

        assertThrows(IllegalStateException.class,
                () -> estacionamentoService.atualizarModalidade(1L, ModalidadePagamento.FRACAO_HORA));
    }

    @Test
    void atualizarModalidade_rejeitaVeiculoInexistente() {
        when(registroRepository.findByIdAndSaidaIsNull(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> estacionamentoService.atualizarModalidade(99L, ModalidadePagamento.BLOCO_10H));
    }
}
