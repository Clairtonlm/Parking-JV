package br.com.evolucaoparking.service;

import br.com.evolucaoparking.config.ParkingProperties;
import br.com.evolucaoparking.dto.EntradaRequest;
import br.com.evolucaoparking.dto.OperacaoView;
import br.com.evolucaoparking.dto.SaidaResponse;
import br.com.evolucaoparking.model.ModalidadePagamento;
import br.com.evolucaoparking.model.PerfilUsuario;
import br.com.evolucaoparking.model.RegistroEstacionamento;
import br.com.evolucaoparking.model.TipoVeiculo;
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
        var vagas = vagaRepository.findAllByOrderByTipoVeiculoAscNumeroAsc();
        long ocupadas = vagas.stream().filter(Vaga::isOcupada).count();
        return new OperacaoView(
                properties.getVagasCarro() + properties.getVagasMoto(),
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
        return previewValor(localizarAtivo(placaOuNome));
    }

    @Transactional(readOnly = true)
    public BigDecimal previewValorSaidaPorId(Long registroId) {
        return previewValor(buscarAtivoPorId(registroId));
    }

    @Transactional
    public RegistroEstacionamento registrarEntrada(EntradaRequest request, Usuario funcionario) {
        Turno turno = obterTurnoObrigatorio(funcionario);

        String placa = normalizarPlaca(request.placa());
        if (registroRepository.findByPlacaAndSaidaIsNull(placa).isPresent()) {
            throw new IllegalStateException("Veículo com placa " + placa + " já está no pátio.");
        }

        Vaga vaga = vagaRepository.findByNumeroAndTipoVeiculo(request.numeroVaga(), request.tipoVeiculo())
                .orElseThrow(() -> new IllegalStateException(
                        "Vaga " + request.numeroVaga() + " não encontrada para " + labelTipo(request.tipoVeiculo()) + "."));

        if (vaga.isOcupada()) {
            throw new IllegalStateException("Vaga " + vaga.getRotulo() + " já está ocupada.");
        }

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
        return finalizarSaida(buscarAtivo(localizarAtivo(placaOuNome)), funcionario);
    }

    @Transactional
    public SaidaResponse registrarSaidaPorId(Long registroId, Usuario funcionario) {
        return finalizarSaida(buscarAtivoPorId(registroId), funcionario);
    }

    @Transactional
    public RegistroEstacionamento atualizarModalidade(Long registroId, ModalidadePagamento novaModalidade) {
        if (novaModalidade == null) {
            throw new IllegalArgumentException("Selecione a modalidade de pagamento.");
        }

        RegistroEstacionamento registro = buscarAtivoPorId(registroId);
        if (registro.getModalidade() == novaModalidade) {
            throw new IllegalStateException("O veículo já está nesta modalidade.");
        }

        registro.setModalidade(novaModalidade);
        return registroRepository.save(registro);
    }

    private SaidaResponse finalizarSaida(RegistroEstacionamento registro, Usuario funcionario) {
        Turno turno = obterTurnoObrigatorio(funcionario);

        LocalDateTime saida = LocalDateTime.now();
        long minutos = Duration.between(registro.getEntrada(), saida).toMinutes();
        BigDecimal valor = tarifaService.calcular(registro.getModalidade(), minutos);

        registro.setSaida(saida);
        registro.setValorPago(valor);
        registro.setTurnoSaida(turno);
        registroRepository.save(registro);

        Vaga vaga = vagaRepository.findByNumeroAndTipoVeiculo(registro.getNumeroVaga(), registro.getTipoVeiculo())
                .orElseThrow(() -> new IllegalStateException("Vaga não encontrada para liberar."));
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

    private BigDecimal previewValor(RegistroEstacionamento registro) {
        long minutos = Duration.between(registro.getEntrada(), LocalDateTime.now()).toMinutes();
        return tarifaService.calcular(registro.getModalidade(), minutos);
    }

    private RegistroEstacionamento buscarAtivoPorId(Long id) {
        return registroRepository.findByIdAndSaidaIsNull(id)
                .orElseThrow(() -> new IllegalStateException("Veículo não encontrado no pátio."));
    }

    private RegistroEstacionamento buscarAtivo(RegistroEstacionamento registro) {
        if (!registro.isAtivo()) {
            throw new IllegalStateException("Este veículo já teve a saída registrada.");
        }
        return registro;
    }

    private RegistroEstacionamento localizarAtivo(String placaOuNome) {
        String termo = placaOuNome.trim();
        if (termo.isBlank()) {
            throw new IllegalStateException("Informe a placa ou selecione o veículo.");
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

    private String labelTipo(TipoVeiculo tipo) {
        return tipo == TipoVeiculo.CARRO ? "carro" : "moto";
    }

    public static String rotuloVaga(RegistroEstacionamento registro) {
        String tipo = registro.getTipoVeiculo() == TipoVeiculo.CARRO ? "Carro" : "Moto";
        return String.format("%02d (%s)", registro.getNumeroVaga(), tipo);
    }
}
