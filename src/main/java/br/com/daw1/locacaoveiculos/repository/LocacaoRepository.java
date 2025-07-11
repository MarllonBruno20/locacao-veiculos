package br.com.daw1.locacaoveiculos.repository;

import br.com.daw1.locacaoveiculos.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
    @Query("SELECT l FROM Locacao l " +
            "JOIN FETCH l.usuario u " +
            "JOIN FETCH u.pessoa p " +
            "JOIN FETCH l.veiculo v " +
            "JOIN FETCH l.pagamento pag " +
            "ORDER BY l.dataLocacaoInicio DESC")
    List<Locacao> findAllComRelacionamentos();

    @Query("SELECT l FROM Locacao l " +
            "JOIN FETCH l.usuario u " +
            "JOIN FETCH u.pessoa p " +
            "JOIN FETCH l.veiculo v " +
            "JOIN FETCH l.pagamento pag " +
            "WHERE l.codigo = :idLocacao ")
    Optional<Locacao> findByIdComRelacionamentos(@Param("idLocacao") Long idLocacao);

    @Query("SELECT l FROM Locacao l " +
            "JOIN FETCH l.usuario u " +
            "JOIN FETCH u.pessoa p " +
            "JOIN FETCH l.veiculo v " +
            "JOIN FETCH l.pagamento pag " +
            "WHERE u.codigo = :usuarioId " +
            "ORDER BY l.dataLocacaoInicio DESC")
    List<Locacao> findByUsuarioIdComRelacionamentos(@Param("usuarioId") Long usuarioId);

    @Query("SELECT l FROM Locacao l " +
            "JOIN FETCH l.usuario u " +
            "JOIN FETCH u.pessoa p " +
            "JOIN FETCH l.veiculo v " +
            "JOIN FETCH l.pagamento pag " +
            "WHERE l.codigo = :locacaoId AND u.codigo = :usuarioId")
    Optional<Locacao> findByIdAndUsuarioIdComRelacionamentos(
            @Param("locacaoId") Long locacaoId,
            @Param("usuarioId") Long usuarioId
    );

}
