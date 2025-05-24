package br.com.daw1.locacaoveiculos.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Pagamento {

    private long id;

    private LocalDateTime dataPagamento;
    private BigDecimal valorPago;
    private boolean concluido;

    private Locacao locacao;

}
