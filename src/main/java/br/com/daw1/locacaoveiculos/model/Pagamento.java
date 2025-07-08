package br.com.daw1.locacaoveiculos.model;

import br.com.daw1.locacaoveiculos.model.enums.FormaPagamento;
import br.com.daw1.locacaoveiculos.model.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pagamento")
public class Pagamento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="gerador_pagamento", sequenceName="pagamento_codigo_seq", allocationSize=1)
    @GeneratedValue(generator="gerador_pagamento", strategy= GenerationType.SEQUENCE)
    private Long codigo;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "valor_pago")
    private BigDecimal valorPago;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;

}
