package br.com.evolucaoparking.repository;

import br.com.evolucaoparking.model.RegistroEstacionamento;
import br.com.evolucaoparking.model.TipoVeiculo;
import br.com.evolucaoparking.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RegistroEstacionamentoRepository extends JpaRepository<RegistroEstacionamento, Long> {

    Optional<RegistroEstacionamento> findByPlacaAndSaidaIsNull(String placa);

    Optional<RegistroEstacionamento> findByIdAndSaidaIsNull(Long id);

    List<RegistroEstacionamento> findBySaidaIsNullOrderByEntradaDesc();

    List<RegistroEstacionamento> findBySaidaIsNotNullOrderBySaidaDesc();

    long countByTurnoEntradaAndTipoVeiculo(Turno turno, TipoVeiculo tipo);

    @Query("SELECT COALESCE(SUM(r.valorPago), 0) FROM RegistroEstacionamento r WHERE r.turnoSaida = :turno")
    BigDecimal somarCaixaTurno(@Param("turno") Turno turno);

    long countByTurnoEntrada(Turno turno);
}
