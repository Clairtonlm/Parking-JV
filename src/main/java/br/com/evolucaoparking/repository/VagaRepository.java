package br.com.evolucaoparking.repository;

import br.com.evolucaoparking.model.TipoVeiculo;
import br.com.evolucaoparking.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {

    Optional<Vaga> findByNumeroAndTipoVeiculo(int numero, TipoVeiculo tipoVeiculo);

    List<Vaga> findAllByOrderByTipoVeiculoAscNumeroAsc();

    List<Vaga> findByTipoVeiculoAndOcupadaFalseOrderByNumeroAsc(TipoVeiculo tipoVeiculo);

    long countByOcupadaFalse();

    boolean existsByTipoVeiculo(TipoVeiculo tipoVeiculo);
}
