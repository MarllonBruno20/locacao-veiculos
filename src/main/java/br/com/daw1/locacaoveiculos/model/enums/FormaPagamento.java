package br.com.daw1.locacaoveiculos.model.enums;

public enum FormaPagamento {
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
    PIX("PIX"),
    DINHEIRO("Dinheiro");

    private final String nomeFormatado;

    FormaPagamento(String nomeFormatado) {
        this.nomeFormatado = nomeFormatado;
    }

    public String getNomeFormatado() {
        return nomeFormatado;
    }

    @Override
    public String toString() {
        return nomeFormatado;
    }
}
