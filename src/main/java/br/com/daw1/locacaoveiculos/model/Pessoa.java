package br.com.daw1.locacaoveiculos.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pessoa")
public class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="gerador_pessoa", sequenceName="pessoa_codigo_seq", allocationSize=1)
    @GeneratedValue(generator="gerador_pessoa", strategy=GenerationType.SEQUENCE)
    private Long codigo;

    @NotBlank(message = "O nome da pessoa é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF da pessoa é obrigatório")
    private String cpf;

    @NotNull(message = "A data de nascimento da pessoa é obrigatória")
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @NotNull(message = "O Telefone da pessoa é obrigatória")
    private String telefone;

    private String email;

    //Se uma pessoa tiver mais de um usuário podemos ter controle por meio da Lista 
    @OneToMany(mappedBy = "pessoa") // Mapeia a relação com a classe Usuario
    private List<Usuario> usuarios; 

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public String toString() {
        return "Pessoa [codigo=" + codigo + ", nome=" + nome + ", cpf=" + cpf + ", dataNascimento=" + dataNascimento + "]";
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
        Pessoa other = (Pessoa) obj;
        return Objects.equals(codigo, other.codigo);
    }
}
