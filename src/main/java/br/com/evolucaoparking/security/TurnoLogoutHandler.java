package br.com.evolucaoparking.security;

import br.com.evolucaoparking.model.PerfilUsuario;
import br.com.evolucaoparking.service.TurnoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class TurnoLogoutHandler implements LogoutHandler {

    private final TurnoService turnoService;

    public TurnoLogoutHandler(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        UsuarioDetails details = (UsuarioDetails) authentication.getPrincipal();
        if (details.getUsuario().getPerfil() == PerfilUsuario.FUNCIONARIO) {
            try {
                turnoService.encerrarTurno(details.getUsuario());
            } catch (IllegalStateException ignored) {
                // turno já encerrado
            }
        }
    }
}
