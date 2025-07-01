// src/main/java/br/com/daw1/locacaoveiculos/service/PessoaService.java
package br.com.daw1.locacaoveiculos.service;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Pessoa;
import br.com.daw1.locacaoveiculos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public Pessoa salvar(Pessoa pessoa) {
        // Exemplo de validação de regra de negócio
        Optional<Pessoa> pessoaExistente = pessoaRepository.findByCpf(pessoa.getCpf());
        if (pessoaExistente.isPresent() && !pessoaExistente.get().getCodigo().equals(pessoa.getCodigo())) {
            throw new RegraNegocioException("CPF já cadastrado para outra pessoa.");
        }
        return pessoaRepository.save(pessoa);
    }

    public Optional<Pessoa> buscarPorCodigo(Long codigo) {
        return pessoaRepository.findById(codigo);
    }

    public Optional<Pessoa> buscarPorCpf(String cpf) {
        return pessoaRepository.findByCpf(cpf);
    }

    // Outros métodos CRUD para Pessoa (listar, deletar, etc.)
}