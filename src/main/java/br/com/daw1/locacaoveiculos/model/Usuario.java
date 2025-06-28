package br.com.daw1.locacaoveiculos.model;

import java.io.Serializable;
import java.util.Objects;

import br.com.daw1.locacaoveiculos.model.enums.TipoUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="gerador_usuario", sequenceName="usuario_codigo_seq", allocationSize=1)
    @GeneratedValue(generator="gerador_usuario", strategy=GenerationType.SEQUENCE)
    private Long codigo;

    @NotBlank(message = "O nome de usuário é obrigatório")
    private String nomeUsuario;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
    
    // Aqui é definido o tipo usuario que pode ser CLIENTE ou ADMINISTRADOR
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo;

    @ManyToOne
    @JoinColumn(name = "codigo_pessoa")
    // O usuário precisa estar referenciado à classe Pessoa
    private Pessoa pessoa; 


    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public String toString() {
        return "Usuario [codigo=" + codigo + ", nomeUsuario=" + nomeUsuario + ", tipo=" + tipo + ", pessoa=" + pessoa + "]";
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
        Usuario other = (Usuario) obj;
        return Objects.equals(codigo, other.codigo);
    }
}
