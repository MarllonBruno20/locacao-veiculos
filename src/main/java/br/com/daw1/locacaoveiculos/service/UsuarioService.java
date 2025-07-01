// src/main/java/br/com/daw1/locacaoveiculos/service/UsuarioService.java
package br.com.daw1.locacaoveiculos.service;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.model.enums.TipoUsuario;
import br.com.daw1.locacaoveiculos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // AVISO: Em um projeto real, você usaria um PasswordEncoder para criptografar a senha!
    // Ex: import org.springframework.security.crypto.password.PasswordEncoder;
    // @Autowired private PasswordEncoder passwordEncoder;

    public Usuario salvar(Usuario usuario) {
        // Validação se o nome de usuário já existe
        Optional<Usuario> usuarioExistente = usuarioRepository.findByNomeUsuario(usuario.getNomeUsuario());
        if (usuarioExistente.isPresent() && !usuarioExistente.get().getCodigo().equals(usuario.getCodigo())) {
            throw new RegraNegocioException("Nome de usuário já existe.");
        }

        // AVISO: Criptografar a senha antes de salvar!
        // usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorCodigo(Long codigo) {
        return usuarioRepository.findById(codigo);
    }

    public Optional<Usuario> buscarPorNomeUsuario(String nomeUsuario) {
        return usuarioRepository.findByNomeUsuario(nomeUsuario);
    }

    public void deletar(Long codigo) {
        usuarioRepository.deleteById(codigo);
    }

    // Método para o administrador cadastrar novos usuários (incluindo admins)
    public Usuario cadastrarNovoUsuarioPorAdmin(Usuario usuario) {
        // Aqui você pode adicionar lógica específica para admin,
        // como forçar o TipoUsuario a ser CLIENTE se não for explicitamente ADMINISTRADOR
        // ou permitir que o admin defina o tipo.
        if (usuario.getTipo() == null) {
            usuario.setTipo(TipoUsuario.CLIENTE); // Default se não for especificado
        }
        return salvar(usuario);
    }
}