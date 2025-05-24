package br.com.daw1.locacaoveiculos.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

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

    @Column(name = "placa", unique = true, nullable = false)
    @NotBlank(message = "A placa do veículo é obrigatória")
    private String placa;

    private String cor;
    
    private double valorDiaria;

   

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(double valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    @Override
    public String toString() {
        return "Veiculo [codigo=" + codigo + ", marca=" + marca + ", modelo=" + modelo + ", placa=" + placa + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Veiculo other = (Veiculo) obj;
        return Objects.equals(codigo, other.codigo);
    }
}
