package br.com.daw1.locacaoveiculos.model;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.daw1.locacaoveiculos.model.enums.CategoriaVeiculo;
import br.com.daw1.locacaoveiculos.model.enums.CombustivelVeiculo;
import br.com.daw1.locacaoveiculos.model.enums.StatusVeiculo;
import br.com.daw1.locacaoveiculos.model.enums.TipoCambioVeiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "veiculo")
public class Veiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="gerador_veiculo", sequenceName="veiculo_codigo_seq", allocationSize=1)
    @GeneratedValue(generator="gerador_veiculo", strategy=GenerationType.SEQUENCE)
    private Long codigo;

    @NotBlank(message = "A marca do veículo é obrigatória")
    private String marca;

    @NotBlank(message = "O modelo do veículo é obrigatório")
    private String modelo;

    @NotBlank(message = "A placa do veículo é obrigatória")
    private String placa;

    @NotNull(message = "O número de portas é obrigatório")
    @Min(message = "O número de portas deve ser maior ou igual a 2", value = 2)
    @Max(message = "O número de portas deve ser menor ou igual a 4", value = 4)
    @Column(name = "numero_portas", nullable = false)
    private Integer numeroPortas;

    @NotNull(message = "O número de passageiros é obrigatório")
    @Min(message = "O número de passageiros deve ser maior ou igual a 2", value = 2)
    @Max(message = "O número de passageiros deve ser menor ou igual a 7", value = 7)
    @Column(name = "numero_passageiros", nullable = false)
    private Integer numeroPassageiros;

    @NotNull(message = "O valor diária é obrigatório")
    @Positive(message = "O valor da diária deve ser positivo")
    @Column(name = "valor_diaria", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDiaria;

    @NotNull(message = "O status do veículo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVeiculo status;

    @NotNull(message = "A categoria do veículo é obrigatória")
    @Enumerated(EnumType.STRING)
    private CategoriaVeiculo categoria;

    @NotNull(message = "O combustível do veículo é obrigatório")
    @Enumerated(EnumType.STRING)
    private CombustivelVeiculo combustivel;

    @NotNull(message = "O tipo de cambio do veículo é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoCambioVeiculo tipoCambio;

    @NotNull(message = "A imagem do veículo é obrigatória")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
