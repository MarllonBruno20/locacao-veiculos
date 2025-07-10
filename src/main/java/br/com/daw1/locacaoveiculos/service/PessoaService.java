package br.com.daw1.locacaoveiculos.service;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Pessoa;
import br.com.daw1.locacaoveiculos.model.Usuario; // Importação adicionada
import br.com.daw1.locacaoveiculos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private UsuarioService usuarioService; // INJETANDO UsuarioService

    public Pessoa salvar(Pessoa pessoa) {
        // Validação de CPF único (para criação e atualização)
        Optional<Pessoa> pessoaExistente = pessoaRepository.findByCpf(pessoa.getCpf());
        if (pessoaExistente.isPresent() &&
            (pessoa.getCodigo() == null || !pessoaExistente.get().getCodigo().equals(pessoa.getCodigo()))) {
            throw new RegraNegocioException("CPF já cadastrado para outra pessoa.");
        }
        return pessoaRepository.save(pessoa);
    }

    public List<Pessoa> listarTodas() {
        return pessoaRepository.findAll();
    }

    public Optional<Pessoa> buscarPorCodigo(Long codigo) {
        return pessoaRepository.findById(codigo);
    }

    public Optional<Pessoa> buscarPorCpf(String cpf) {
        return pessoaRepository.findByCpf(cpf);
    }

    public void deletar(Long codigo) {
        // 1. Verifica se a pessoa existe
        Pessoa pessoa = pessoaRepository.findById(codigo)
                .orElseThrow(() -> new RegraNegocioException("Pessoa não encontrada para exclusão."));

        // 2. Verifica se a pessoa está vinculada a algum usuário
        // Usamos .getUsuarios() que é o List<Usuario> mapeado em Pessoa
        if (pessoa.getUsuarios() != null && !pessoa.getUsuarios().isEmpty()) {
            // Para cada usuário vinculado, tenta deletá-lo
            for (Usuario usuario : pessoa.getUsuarios()) {
                // Antes de deletar o usuário, é importante verificar se ele está logado ou se é o próprio admin.
                // Isso pode ser uma validação mais complexa, mas para este caso, vamos tentar deletar.
                // Se a deleção do usuário falhar (ex: por regra de negócio no UsuarioService),
                // uma exceção pode ser lançada ou tratada aqui.
                try {
                    usuarioService.deletar(usuario.getCodigo()); // Chama o serviço de usuário para deletar o usuário
                } catch (Exception e) {
                    // Opcional: Lidar com erros de deleção de usuário aqui, ou relançar.
                    // Por exemplo, se o usuário estiver em alguma locação ativa e o UsuarioService impedir.
                    throw new RegraNegocioException("Não foi possível excluir a pessoa. Usuário(s) vinculado(s) não puderam ser excluídos: " + e.getMessage());
                }
            }
        }

        // 3. Deleta a pessoa
        pessoaRepository.deleteById(codigo);
    }

    public Pessoa alterar(Pessoa pessoa) {
        if (pessoa.getCodigo() == null) {
            throw new RegraNegocioException("O código da pessoa é obrigatório para alteração.");
        }
        return salvar(pessoa);
    }
}