package br.com.evolucaoparking.repository;

import br.com.evolucaoparking.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {

    Optional<Vaga> findByNumero(int numero);

    List<Vaga> findAllByOrderByNumeroAsc();

    long countByOcupadaFalse();
}
