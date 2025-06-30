package br.com.daw1.locacaoveiculos.repository;

import br.com.daw1.locacaoveiculos.model.Veiculo;
import br.com.daw1.locacaoveiculos.model.enums.StatusVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

     Optional<Veiculo> findByPlaca(String placa);

     List<Veiculo> findByStatus(StatusVeiculo status);

     Optional<Veiculo> findByCodigo(Long codigo);

}
