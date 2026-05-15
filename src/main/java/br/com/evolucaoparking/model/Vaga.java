package br.com.evolucaoparking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "vagas", uniqueConstraints = @UniqueConstraint(columnNames = {"numero", "tipo_veiculo"}))
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_veiculo", nullable = false, length = 10)
    private TipoVeiculo tipoVeiculo;

    @Column(nullable = false)
    private boolean ocupada;

    protected Vaga() {
    }

    public Vaga(int numero, TipoVeiculo tipoVeiculo) {
        this.numero = numero;
        this.tipoVeiculo = tipoVeiculo;
        this.ocupada = false;
    }

    public Long getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public String getRotulo() {
        String tipo = tipoVeiculo == TipoVeiculo.CARRO ? "Carro" : "Moto";
        return String.format("%02d (%s)", numero, tipo);
    }
}
