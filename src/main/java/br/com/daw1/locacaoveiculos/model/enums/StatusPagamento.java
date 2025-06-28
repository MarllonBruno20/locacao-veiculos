package br.com.daw1.locacaoveiculos.model.enums;

public enum StatusPagamento {
    PENDENTE, // Pagamento agendado, precisa que o administrador confirme
    PAGO,    // Pagamento efetuado
    CANCELADO // Pagamento cancelado
}
