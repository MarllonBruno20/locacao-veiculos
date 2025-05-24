package br.com.daw1.locacaoveiculos.model;

public enum StatusLocacao {
    PENDENTE, // Locação agendada, precisa que o administrador confirme
    ATIVA,    // Locação em andamento
    FINALIZADA, // Veículo devolvido e locação encerrada, precisa que o administrador confirme
    CANCELADA // Locação cancelada antes do início ou durante
}
