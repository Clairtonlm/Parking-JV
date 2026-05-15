package br.com.evolucaoparking.repository;

import br.com.evolucaoparking.model.Turno;
import br.com.evolucaoparking.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    Optional<Turno> findByUsuarioAndLogoutEmIsNull(Usuario usuario);

    List<Turno> findAllByOrderByLoginEmDesc();

    @Query("SELECT t FROM Turno t JOIN FETCH t.usuario ORDER BY t.loginEm DESC")
    List<Turno> findAllComUsuarioOrderByLoginEmDesc();
}
