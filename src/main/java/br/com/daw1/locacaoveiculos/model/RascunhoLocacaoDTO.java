package br.com.daw1.locacaoveiculos.model;

import br.com.daw1.locacaoveiculos.model.enums.FormaPagamento;
import br.com.daw1.locacaoveiculos.model.enums.LocaisRetiradaDevolucao;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RascunhoLocacaoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate dataRetirada;
    private LocalDate dataDevolucao;
    private LocaisRetiradaDevolucao localRetirada;
    private LocaisRetiradaDevolucao localDevolucao;

    private Long codigoVeiculo;
    private String nomeVeiculo;

    private BigDecimal valorTotal;
    private FormaPagamento formaPagamento;
}
