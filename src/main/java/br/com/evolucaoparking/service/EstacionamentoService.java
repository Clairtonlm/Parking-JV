package br.com.evolucaoparking.service;

import br.com.evolucaoparking.config.ParkingProperties;
import br.com.evolucaoparking.dto.EntradaRequest;
import br.com.evolucaoparking.dto.OperacaoView;
import br.com.evolucaoparking.dto.SaidaResponse;
import br.com.evolucaoparking.model.RegistroEstacionamento;
import br.com.evolucaoparking.model.PerfilUsuario;
import br.com.evolucaoparking.model.Turno;
import br.com.evolucaoparking.model.Usuario;
import br.com.evolucaoparking.model.Vaga;
import br.com.evolucaoparking.repository.RegistroEstacionamentoRepository;
import br.com.evolucaoparking.repository.VagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EstacionamentoService {

    private final VagaRepository vagaRepository;
    private final RegistroEstacionamentoRepository registroRepository;
    private final ParkingProperties properties;
    private final TarifaService tarifaService;
    private final TurnoService turnoService;

    public EstacionamentoService(
            VagaRepository vagaRepository,
            RegistroEstacionamentoRepository registroRepository,
            ParkingProperties properties,
            TarifaService tarifaService,
            TurnoService turnoService) {
        this.vagaRepository = vagaRepository;
        this.registroRepository = registroRepository;
        this.properties = properties;
        this.tarifaService = tarifaService;
        this.turnoService = turnoService;
    }

    @Transactional(readOnly = true)
    public OperacaoView carregarOperacao() {
        List<Vaga> vagas = vagaRepository.findAllByOrderByNumeroAsc();
        long ocupadas = vagas.stream().filter(Vaga::isOcupada).count();
        return new OperacaoView(
                properties.getTotalVagas(),
                vagas.size() - ocupadas,
                ocupadas,
                vagas,
                registroRepository.findBySaidaIsNullOrderByEntradaDesc()
        );
    }

    @Transactional(readOnly = true)
    public RegistroEstacionamento buscarRegistro(Long id) {
        return registroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro não encontrado."));
    }

    @Transactional(readOnly = true)
    public BigDecimal previewValorSaida(String placaOuNome) {
        RegistroEstacionamento registro = localizarAtivo(placaOuNome);
        long minutos = Duration.between(registro.getEntrada(), LocalDateTime.now()).toMinutes();
        return tarifaService.calcular(registro.getModalidade(), minutos);
    }

    @Transactional
    public RegistroEstacionamento registrarEntrada(EntradaRequest request, Usuario funcionario) {
        Turno turno = obterTurnoObrigatorio(funcionario);

        String placa = normalizarPlaca(request.placa());
        if (registroRepository.findByPlacaAndSaidaIsNull(placa).isPresent()) {
            throw new IllegalStateException("Veículo com placa " + placa + " já está no pátio.");
        }

        Vaga vaga = vagaRepository.findAllByOrderByNumeroAsc().stream()
                .filter(v -> !v.isOcupada())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Não há vagas disponíveis no momento."));

        vaga.setOcupada(true);
        vagaRepository.save(vaga);

        RegistroEstacionamento registro = new RegistroEstacionamento(
                placa,
                request.nomeMotorista().trim(),
                request.tipoVeiculo(),
                request.modalidade(),
                vaga.getNumero(),
                LocalDateTime.now(),
                turno
        );

        return registroRepository.save(registro);
    }

    @Transactional
    public SaidaResponse registrarSaida(String placaOuNome, Usuario funcionario) {
        Turno turno = obterTurnoObrigatorio(funcionario);

        RegistroEstacionamento registro = localizarAtivo(placaOuNome);
        LocalDateTime saida = LocalDateTime.now();
        long minutos = Duration.between(registro.getEntrada(), saida).toMinutes();
        BigDecimal valor = tarifaService.calcular(registro.getModalidade(), minutos);

        registro.setSaida(saida);
        registro.setValorPago(valor);
        registro.setTurnoSaida(turno);
        registroRepository.save(registro);

        Vaga vaga = vagaRepository.findByNumero(registro.getNumeroVaga())
                .orElseThrow(() -> new IllegalStateException("Vaga " + registro.getNumeroVaga() + " não encontrada."));
        vaga.setOcupada(false);
        vagaRepository.save(vaga);

        return new SaidaResponse(
                registro.getId(),
                registro.getCodigoRecibo(),
                registro.getPlaca(),
                registro.getNomeMotorista(),
                registro.getTipoVeiculo(),
                registro.getModalidade(),
                registro.getNumeroVaga(),
                registro.getEntrada(),
                saida,
                minutos,
                valor
        );
    }

    private RegistroEstacionamento localizarAtivo(String placaOuNome) {
        String termo = placaOuNome.trim();
        if (termo.isBlank()) {
            throw new IllegalStateException("Informe a placa ou o nome do motorista.");
        }

        String placa = normalizarPlaca(termo);
        var porPlaca = registroRepository.findByPlacaAndSaidaIsNull(placa);
        if (porPlaca.isPresent()) {
            return porPlaca.get();
        }

        return registroRepository.findBySaidaIsNullOrderByEntradaDesc().stream()
                .filter(r -> r.getNomeMotorista().equalsIgnoreCase(termo))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Nenhum veículo ativo encontrado para: " + termo));
    }

    private Turno obterTurnoObrigatorio(Usuario usuario) {
        if (usuario.getPerfil() == PerfilUsuario.ADMIN) {
            return null;
        }
        Turno turno = turnoService.buscarTurnoAberto(usuario);
        if (turno == null) {
            throw new IllegalStateException("Nenhum turno aberto. Faça login novamente.");
        }
        return turno;
    }

    private String normalizarPlaca(String placa) {
        return placa.replace("-", "").replace(" ", "").toUpperCase().trim();
    }
}
