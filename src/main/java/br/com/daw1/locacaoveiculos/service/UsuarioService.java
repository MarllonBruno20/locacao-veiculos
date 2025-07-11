package br.com.daw1.locacaoveiculos.service;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.model.enums.TipoUsuario;
import br.com.daw1.locacaoveiculos.repository.PessoaRepository;
import br.com.daw1.locacaoveiculos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private PessoaRepository pessoaRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario salvar(Usuario usuario) {
        // Verifica se já existe um usuário com o mesmo nome
        Optional<Usuario> usuarioExistente = usuarioRepository.findByNomeUsuario(usuario.getNomeUsuario());
        if (usuarioExistente.isPresent() && !usuarioExistente.get().getCodigo().equals(usuario.getCodigo())) {
            throw new RegraNegocioException("Nome de usuário já existe.");
        }

        // Lógica para tratamento da senha durante a criação ou edição
        if (usuario.getCodigo() == null || (usuario.getSenha() != null && !usuario.getSenha().isEmpty())) {
            // Se for um novo usuário OU se uma nova senha foi fornecida (não nula e não vazia)
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        } else if (usuario.getCodigo() != null && (usuario.getSenha() == null || usuario.getSenha().isEmpty())) {
            // Se for uma edição de usuário existente E a senha fornecida estiver vazia/nula,
            // significa que o usuário não quer alterar a senha.
            // Neste caso, recuperamos a senha existente do banco de dados e a mantemos.
            usuarioExistente.ifPresent(existingUser -> usuario.setSenha(existingUser.getSenha()));
        }
        // Se a senha for nula e for um novo usuário, o @NotBlank no modelo vai pegar.
        // Se a senha for nula e for um usuário existente e não encontrada no optional, será tratado no controller ou na tela.

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

    @Transactional
    public Usuario registrarNovoCliente(Usuario usuario) {
        // Validação de regras de negócio, como CPF ou nome de usuário duplicado
        if (usuarioRepository.findByNomeUsuario(usuario.getNomeUsuario()).isPresent()) {
            throw new RegraNegocioException("Este nome de usuário já está em uso.");
        }
        if (pessoaRepository.findByCpf(usuario.getPessoa().getCpf()).isPresent()) {
            throw new RegraNegocioException("Este CPF já está cadastrado.");
        }

        // 1. FORÇA O PAPEL DO USUÁRIO - PONTO DE SEGURANÇA CRÍTICO
        usuario.setTipo(TipoUsuario.CLIENTE);

        // 2. CRIPTOGRAFA A SENHA ANTES DE SALVAR
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // 3. SALVA A PESSOA E O USUÁRIO
        // Se a relação Usuario -> Pessoa tiver CascadeType.ALL ou PERSIST,
        // basta salvar o usuário que a pessoa será salva junto.
        pessoaRepository.save(usuario.getPessoa());
        return usuarioRepository.save(usuario);
    }

}