package br.com.evolucaoparking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime loginEm;

    private LocalDateTime logoutEm;

    protected Turno() {
    }

    public Turno(Usuario usuario, LocalDateTime loginEm) {
        this.usuario = usuario;
        this.loginEm = loginEm;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getLoginEm() {
        return loginEm;
    }

    public LocalDateTime getLogoutEm() {
        return logoutEm;
    }

    public void setLogoutEm(LocalDateTime logoutEm) {
        this.logoutEm = logoutEm;
    }

    public boolean isAberto() {
        return logoutEm == null;
    }
}
