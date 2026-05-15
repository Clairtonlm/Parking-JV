package br.com.evolucaoparking.service;

import br.com.evolucaoparking.model.Turno;
import br.com.evolucaoparking.model.Usuario;
import br.com.evolucaoparking.repository.RegistroEstacionamentoRepository;
import br.com.evolucaoparking.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final RegistroEstacionamentoRepository registroRepository;

    public TurnoService(TurnoRepository turnoRepository, RegistroEstacionamentoRepository registroRepository) {
        this.turnoRepository = turnoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional
    public Turno abrirTurno(Usuario usuario) {
        turnoRepository.findByUsuarioAndLogoutEmIsNull(usuario).ifPresent(t -> {
            throw new IllegalStateException("Já existe um turno aberto para este usuário.");
        });
        return turnoRepository.save(new Turno(usuario, LocalDateTime.now()));
    }

    @Transactional
    public void encerrarTurno(Usuario usuario) {
        Turno turno = turnoRepository.findByUsuarioAndLogoutEmIsNull(usuario)
                .orElseThrow(() -> new IllegalStateException("Nenhum turno aberto para encerrar."));
        turno.setLogoutEm(LocalDateTime.now());
        turnoRepository.save(turno);
    }

    @Transactional(readOnly = true)
    public Turno buscarTurnoAberto(Usuario usuario) {
        return turnoRepository.findByUsuarioAndLogoutEmIsNull(usuario).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Turno> listarTodos() {
        return turnoRepository.findAllComUsuarioOrderByLoginEmDesc();
    }

    @Transactional(readOnly = true)
    public long contarCarrosNoTurno(Turno turno) {
        return registroRepository.countByTurnoEntradaAndTipoVeiculo(
                turno, br.com.evolucaoparking.model.TipoVeiculo.CARRO);
    }

    @Transactional(readOnly = true)
    public long contarMotosNoTurno(Turno turno) {
        return registroRepository.countByTurnoEntradaAndTipoVeiculo(
                turno, br.com.evolucaoparking.model.TipoVeiculo.MOTO);
    }

    @Transactional(readOnly = true)
    public java.math.BigDecimal caixaDoTurno(Turno turno) {
        return registroRepository.somarCaixaTurno(turno);
    }
}
