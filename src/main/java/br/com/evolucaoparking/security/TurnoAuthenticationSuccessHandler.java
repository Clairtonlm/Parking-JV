package br.com.evolucaoparking.security;

import br.com.evolucaoparking.model.PerfilUsuario;
import br.com.evolucaoparking.service.TurnoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TurnoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TurnoService turnoService;

    public TurnoAuthenticationSuccessHandler(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UsuarioDetails details = (UsuarioDetails) authentication.getPrincipal();

        if (details.getUsuario().getPerfil() == PerfilUsuario.FUNCIONARIO) {
            turnoService.abrirTurno(details.getUsuario());
            response.sendRedirect(request.getContextPath() + "/operacao");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin");
    }
}
