package br.com.daw1.locacaoveiculos.service;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Veiculo;
import br.com.daw1.locacaoveiculos.model.enums.StatusVeiculo;
import br.com.daw1.locacaoveiculos.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VeiculoService {

    private VeiculoRepository veiculoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public void salvar(Veiculo veiculo) {

        if (veiculo.getCodigo() == null) {
            veiculo.setStatus(StatusVeiculo.ATIVO);
        }

        veiculoRepository.save(veiculo);

    }

    public List<Veiculo> listarTodosDisponiveis() {
        return veiculoRepository.findByStatus(StatusVeiculo.ATIVO);
    }

    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll(Sort.by("marca", "modelo"));
    }

    public Optional<Veiculo> buscarPorCodigo(Long codigo) {
        return veiculoRepository.findByCodigo(codigo);
    }

    public Veiculo inativar(Long codigo) {

        Veiculo veiculo = buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo nao encontrado - ID: " + codigo));

        veiculo.setStatus(StatusVeiculo.INATIVO);
        return veiculoRepository.save(veiculo);

    }

    public Veiculo alterar(Veiculo veiculo) {
        // Valida se a placa já existe, mas de forma inteligente para a edição
        Optional<Veiculo> veiculoExistente = veiculoRepository.findByCodigo(veiculo.getCodigo());

        // Se encontrou um veículo com a mesma placa E o código é diferente do que estamos editando, então é um erro.
        if (veiculoExistente.isPresent() && !veiculoExistente.get().getCodigo().equals(veiculo.getCodigo())) {
            throw new RegraNegocioException("A placa informada já está em uso por outro veículo.");
        }

        // Se for um veículo novo (sem código), definimos o status padrão.
        if (veiculo.getCodigo() == null) {
            veiculo.setStatus(StatusVeiculo.ATIVO);
        }

        return veiculoRepository.save(veiculo);
    }

}
