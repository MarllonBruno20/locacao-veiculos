package br.com.daw1.locacaoveiculos.repository;

import br.com.daw1.locacaoveiculos.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
}
