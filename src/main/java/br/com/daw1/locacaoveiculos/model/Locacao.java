package br.com.daw1.locacaoveiculos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "locacao")
public class Locacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="gerador_locacao", sequenceName="locacao_codigo_seq", allocationSize=1)
    @GeneratedValue(generator="gerador_locacao", strategy=GenerationType.SEQUENCE)
    private long codigo;

    @Column(name = "data_locacao_inicio")
    private LocalDateTime dataLocacaoInicio;

    @Column(name = "data_locacao_fim")
    private LocalDateTime dataLocacaoFim;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @OneToOne
    @JoinColumn(name = "codigo_veiculo")
    private Veiculo veiculo;

    @ManyToOne
    @JoinColumn(name = "codigo_usuario")
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name = "codigo_pagamento")
    private Pagamento pagamento;

    @Column(name = "status_locacao")
    @Enumerated(EnumType.STRING)
    private StatusLocacao statusLocacao;

}
