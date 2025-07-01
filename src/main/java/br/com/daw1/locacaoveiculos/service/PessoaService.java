package br.com.daw1.locacaoveiculos.service;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Pessoa;
import br.com.daw1.locacaoveiculos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // Adicione esta importação
import java.util.Optional;

@Service
@Transactional // Garante que as operações sejam transacionais
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public Pessoa salvar(Pessoa pessoa) {
        // Validação de CPF único (para criação e atualização)
        Optional<Pessoa> pessoaExistente = pessoaRepository.findByCpf(pessoa.getCpf());
        if (pessoaExistente.isPresent() &&
            (pessoa.getCodigo() == null || !pessoaExistente.get().getCodigo().equals(pessoa.getCodigo()))) {
            throw new RegraNegocioException("CPF já cadastrado para outra pessoa.");
        }
        return pessoaRepository.save(pessoa);
    }

    public List<Pessoa> listarTodas() { // Método novo: Listar todas as pessoas
        return pessoaRepository.findAll();
    }

    public Optional<Pessoa> buscarPorCodigo(Long codigo) {
        return pessoaRepository.findById(codigo);
    }

    public Optional<Pessoa> buscarPorCpf(String cpf) {
        return pessoaRepository.findByCpf(cpf);
    }

    public void deletar(Long codigo) { // Método novo: Deletar pessoa por código
        if (!pessoaRepository.existsById(codigo)) {
            throw new RegraNegocioException("Pessoa não encontrada para exclusão.");
        }
        // TODO: Adicionar validação se a pessoa está vinculada a algum usuário ou locação
        // Se uma pessoa tiver usuários vinculados, não deve ser deletada diretamente sem antes deletar ou desvincular os usuários.
        pessoaRepository.deleteById(codigo);
    }

    public Pessoa alterar(Pessoa pessoa) { // Método novo: Alterar/Atualizar pessoa
        if (pessoa.getCodigo() == null) {
            throw new RegraNegocioException("O código da pessoa é obrigatório para alteração.");
        }
        return salvar(pessoa); // Reutiliza a lógica de salvar que já trata a unicidade do CPF
    }
}