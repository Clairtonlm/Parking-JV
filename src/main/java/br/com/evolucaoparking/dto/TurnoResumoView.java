package br.com.evolucaoparking.dto;

import br.com.evolucaoparking.model.Turno;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TurnoResumoView(
        Long turnoId,
        String funcionarioNome,
        LocalDateTime loginEm,
        LocalDateTime logoutEm,
        long carros,
        long motos,
        long totalEntradas,
        BigDecimal caixa
) {
    public static TurnoResumoView from(Turno turno, long carros, long motos, BigDecimal caixa) {
        return new TurnoResumoView(
                turno.getId(),
                turno.getUsuario().getNome(),
                turno.getLoginEm(),
                turno.getLogoutEm(),
                carros,
                motos,
                carros + motos,
                caixa
        );
    }
}
