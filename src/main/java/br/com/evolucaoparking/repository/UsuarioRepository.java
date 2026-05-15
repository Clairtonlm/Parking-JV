package br.com.evolucaoparking.repository;

import br.com.evolucaoparking.model.PerfilUsuario;
import br.com.evolucaoparking.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLoginIgnoreCase(String login);

    boolean existsByLoginIgnoreCaseAndIdNot(String login, Long id);

    List<Usuario> findByPerfilOrderByNomeAsc(PerfilUsuario perfil);
}
