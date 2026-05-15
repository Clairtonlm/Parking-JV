package br.com.evolucaoparking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.parking")
public class ParkingProperties {

    private int totalVagas = 20;
    private int vagasCarro = 15;
    private int vagasMoto = 5;
    private double valorFixo24h = 40.0;
    private double tarifaHora = 5.0;
    private int toleranciaMinutos = 15;
    private double bloco5hValor = 20.0;
    private int bloco5hHoras = 5;
    private double bloco10hValor = 45.0;
    private int bloco10hHoras = 10;
    private double excessoBloco15min = 2.0;

    public int getTotalVagas() {
        return totalVagas;
    }

    public void setTotalVagas(int totalVagas) {
        this.totalVagas = totalVagas;
    }

    public int getVagasCarro() {
        return vagasCarro;
    }

    public void setVagasCarro(int vagasCarro) {
        this.vagasCarro = vagasCarro;
    }

    public int getVagasMoto() {
        return vagasMoto;
    }

    public void setVagasMoto(int vagasMoto) {
        this.vagasMoto = vagasMoto;
    }

    public double getValorFixo24h() {
        return valorFixo24h;
    }

    public void setValorFixo24h(double valorFixo24h) {
        this.valorFixo24h = valorFixo24h;
    }

    public double getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(double tarifaHora) {
        this.tarifaHora = tarifaHora;
    }

    public int getToleranciaMinutos() {
        return toleranciaMinutos;
    }

    public void setToleranciaMinutos(int toleranciaMinutos) {
        this.toleranciaMinutos = toleranciaMinutos;
    }

    public double getBloco5hValor() {
        return bloco5hValor;
    }

    public void setBloco5hValor(double bloco5hValor) {
        this.bloco5hValor = bloco5hValor;
    }

    public int getBloco5hHoras() {
        return bloco5hHoras;
    }

    public void setBloco5hHoras(int bloco5hHoras) {
        this.bloco5hHoras = bloco5hHoras;
    }

    public double getBloco10hValor() {
        return bloco10hValor;
    }

    public void setBloco10hValor(double bloco10hValor) {
        this.bloco10hValor = bloco10hValor;
    }

    public int getBloco10hHoras() {
        return bloco10hHoras;
    }

    public void setBloco10hHoras(int bloco10hHoras) {
        this.bloco10hHoras = bloco10hHoras;
    }

    public double getExcessoBloco15min() {
        return excessoBloco15min;
    }

    public void setExcessoBloco15min(double excessoBloco15min) {
        this.excessoBloco15min = excessoBloco15min;
    }
}
