package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.model.Veiculo;
import br.com.daw1.locacaoveiculos.service.VeiculoService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    private VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping("/veiculos-disponiveis")
    public String listarVeiculosDisponiveis(Model model) {
        // 1. Chama o service para buscar a lista de veículos disponíveis
        List<Veiculo> veiculosDisponiveis = veiculoService.listarTodosDisponiveis();

        // 2. Adiciona a lista ao Model para que o Thymeleaf possa acessá-la
        model.addAttribute("veiculos", veiculosDisponiveis);

        // 3. Define o título da página
        model.addAttribute("titulo", "Nossa Frota de Veículos");

        // 4. Retorna o caminho para o arquivo HTML
        return "public/lista_veiculos :: conteudoDaLista";
    }

    @HxRequest
    @GetMapping("/{id}")
    public String mostrarDetalhesDoVeiculos(@PathVariable("id") Long id, Model model) {
        // 1. Usa o service para buscar o veículo específico pelo ID recebido na URL.
        //    O .orElseThrow garante que, se um ID inválido for passado, um erro apropriado aconteça.
        Veiculo veiculo = veiculoService.buscarPorCodigo(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado - ID: " + id));

        // 2. Adiciona o objeto 'veiculo' encontrado ao model.
        model.addAttribute("veiculo", veiculo);

        // 3. Adiciona um título dinâmico para a página.
        model.addAttribute("titulo", veiculo.getMarca() + " " + veiculo.getModelo());

        // 4. Retorna o caminho para o novo template HTML que vamos criar.
        return "public/detalhes_veiculo :: detalhesVeiculo";
    }

}
