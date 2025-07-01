
package br.com.daw1.locacaoveiculos.repository;

import br.com.daw1.locacaoveiculos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNomeUsuario(String nomeUsuario); // Para o login
    // Você pode adicionar outros métodos de busca se precisar
}