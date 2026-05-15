package br.com.evolucaoparking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registros")
public class RegistroEstacionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String codigoRecibo;

    @Column(nullable = false, length = 10)
    private String placa;

    @Column(nullable = false, length = 120)
    private String nomeMotorista;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoVeiculo tipoVeiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadePagamento modalidade;

    @Column(nullable = false)
    private int numeroVaga;

    @Column(nullable = false)
    private LocalDateTime entrada;

    private LocalDateTime saida;

    private BigDecimal valorPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_entrada_id")
    private Turno turnoEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_saida_id")
    private Turno turnoSaida;

    protected RegistroEstacionamento() {
    }

    public RegistroEstacionamento(
            String placa,
            String nomeMotorista,
            TipoVeiculo tipoVeiculo,
            ModalidadePagamento modalidade,
            int numeroVaga,
            LocalDateTime entrada,
            Turno turnoEntrada) {
        this.codigoRecibo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.placa = placa;
        this.nomeMotorista = nomeMotorista;
        this.tipoVeiculo = tipoVeiculo;
        this.modalidade = modalidade;
        this.numeroVaga = numeroVaga;
        this.entrada = entrada;
        this.turnoEntrada = turnoEntrada;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoRecibo() {
        return codigoRecibo;
    }

    public String getPlaca() {
        return placa;
    }

    public String getNomeMotorista() {
        return nomeMotorista;
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public ModalidadePagamento getModalidade() {
        return modalidade;
    }

    public int getNumeroVaga() {
        return numeroVaga;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public void setSaida(LocalDateTime saida) {
        this.saida = saida;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public Turno getTurnoEntrada() {
        return turnoEntrada;
    }

    public Turno getTurnoSaida() {
        return turnoSaida;
    }

    public void setTurnoSaida(Turno turnoSaida) {
        this.turnoSaida = turnoSaida;
    }

    public boolean isAtivo() {
        return saida == null;
    }
}
