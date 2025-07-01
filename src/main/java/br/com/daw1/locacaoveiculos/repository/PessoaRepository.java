
package br.com.daw1.locacaoveiculos.repository;

import br.com.daw1.locacaoveiculos.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    Optional<Pessoa> findByCpf(String cpf);
    // Outros métodos se precisar
}