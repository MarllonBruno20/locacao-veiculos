package br.com.daw1.locacaoveiculos.repository;

import br.com.daw1.locacaoveiculos.model.Veiculo;
import br.com.daw1.locacaoveiculos.model.enums.StatusVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

     Optional<Veiculo> findByPlaca(String placa);

     List<Veiculo> findByStatus(StatusVeiculo status);

     Optional<Veiculo> findByCodigo(Long codigo);

     @Query(value = """
        SELECT T1.*
        FROM veiculo T1
        WHERE T1.status = 'ATIVO'
        AND T1.codigo NOT IN (
            SELECT T2.codigo_veiculo
            FROM locacao T2
            WHERE T2.status_locacao IN ('PENDENTE', 'ATIVA')
            AND T2.data_locacao_fim > :data_inicio_desejada
            AND T2.data_locacao_inicio < :data_fim_desejada
        )
        ORDER BY T1.valor_diaria ASC
    """, nativeQuery = true)
     List<Veiculo> findVeiculosDisponiveisNoPeriodo(
             @Param("data_inicio_desejada") LocalDateTime dataInicio,
             @Param("data_fim_desejada") LocalDateTime dataFim
     );

}
