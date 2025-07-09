// src/main/java/br/com/daw1/locacaoveiculos/service/UsuarioService.java
package br.com.daw1.locacaoveiculos.service;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.model.enums.TipoUsuario;
import br.com.daw1.locacaoveiculos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // import adicionado
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Encoder injetado

    public Usuario salvar(Usuario usuario) {
        // Verifica se já existe um usuário com o mesmo nome
        Optional<Usuario> usuarioExistente = usuarioRepository.findByNomeUsuario(usuario.getNomeUsuario());
        if (usuarioExistente.isPresent() && !usuarioExistente.get().getCodigo().equals(usuario.getCodigo())) {
            throw new RegraNegocioException("Nome de usuário já existe.");
        }

        // Criptografa a senha se for novo usuário ou senha nova
        if (usuario.getCodigo() == null || !usuario.getSenha().equals(usuarioExistente.map(Usuario::getSenha).orElse(""))) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario autenticar(String nomeUsuario, String senhaDigitada) {
        return usuarioRepository.findByNomeUsuario(nomeUsuario)
                .filter(u -> passwordEncoder.matches(senhaDigitada, u.getSenha()))
                .orElse(null);
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

    public Usuario cadastrarNovoUsuarioPorAdmin(Usuario usuario) {
        if (usuario.getTipo() == null) {
            usuario.setTipo(TipoUsuario.CLIENTE);
        }
        return salvar(usuario);
    }
}
