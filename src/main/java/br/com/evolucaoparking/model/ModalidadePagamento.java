package br.com.evolucaoparking.model;

public enum ModalidadePagamento {
    VALOR_FIXO_24H("Valor fixo (24 horas)"),
    FRACAO_HORA("Por fração (R$ 5,00 / hora)"),
    BLOCO_5H("Bloco 5 horas (R$ 20,00)"),
    BLOCO_10H("Bloco 10 horas (R$ 45,00)");

    private final String descricao;

    ModalidadePagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
