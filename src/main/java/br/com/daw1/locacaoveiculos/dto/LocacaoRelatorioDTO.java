package br.com.daw1.locacaoveiculos.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LocacaoRelatorioDTO {
    private Long codigo;
    private String nomeCliente;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private BigDecimal valorPago;
    private String formaPagamento;
    private String statusLocacao;
}
