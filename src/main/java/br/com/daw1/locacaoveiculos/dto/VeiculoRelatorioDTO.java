package br.com.daw1.locacaoveiculos.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VeiculoRelatorioDTO {
    private Long veiculoId;
    private String marca;
    private String modelo;
    private String placa;
    private String categoria;
    private String status;
    private Long totalLocacoes;
    private BigDecimal faturamentoTotal;
    private BigDecimal total_dias_alugado;
    private BigDecimal valor_diaria;
    private BigDecimal totalDiasAlugado;
    private BigDecimal valorDiaria;

    private List<LocacaoRelatorioDTO> locacoes;
}
