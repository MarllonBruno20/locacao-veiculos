package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Veiculo;
import br.com.daw1.locacaoveiculos.model.enums.CategoriaVeiculo;
import br.com.daw1.locacaoveiculos.model.enums.CombustivelVeiculo;
import br.com.daw1.locacaoveiculos.model.enums.StatusVeiculo;
import br.com.daw1.locacaoveiculos.model.enums.TipoCambioVeiculo;
import br.com.daw1.locacaoveiculos.service.VeiculoService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/veiculos")
public class AdminVeiculoController {

    private VeiculoService veiculoService;

    public AdminVeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @HxRequest
    @GetMapping("/novo")
    public String mostrarFormularioDeCadastro(Model model) {

        // 1. Passa um objeto 'veiculo' vazio para o formulário.
        // Isso é crucial para o Thymeleaf conseguir ligar os campos do formulário ao objeto (data-binding).
        model.addAttribute("veiculo", new Veiculo());

        // 2. Passa as listas de opções para os seus dropdowns (Enums).
        // Isso permite que o Thymeleaf crie os <option> dinamicamente.
        model.addAttribute("listaDeCategorias", CategoriaVeiculo.values());
        model.addAttribute("listaDeCombustiveis", CombustivelVeiculo.values());
        model.addAttribute("listaDeCambios", TipoCambioVeiculo.values());

        // 3. Retorna o nome do arquivo HTML que deve ser renderizado.
        return "admin/cadastrar_veiculo :: formulario"; // Ex: /templates/admin/cadastra-veiculo.html
    }

    @HxRequest
    @PostMapping("/salvar")
    public String salvarVeiculo(@Valid @ModelAttribute("veiculo") Veiculo veiculo,
                                BindingResult bindingResult,
                                Model model,
                                HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("listaDeCategorias", CategoriaVeiculo.values());
            model.addAttribute("listaDeCombustiveis", CombustivelVeiculo.values());
            model.addAttribute("listaDeCambios", TipoCambioVeiculo.values());

            return "admin/cadastrar_veiculo :: formulario";
        }

        try {
            veiculoService.salvar(veiculo);
        } catch (RegraNegocioException e) {
            // Se o Service lançar um erro (ex: placa duplicada), adicione o erro ao BindingResult
            // e retorne para a tela de formulário para exibir a mensagem.
            bindingResult.rejectValue("placa", "error.veiculo", e.getMessage());

            model.addAttribute("listaDeCategorias", CategoriaVeiculo.values());
            model.addAttribute("listaDeCombustiveis", CombustivelVeiculo.values());
            model.addAttribute("listaDeCambios", TipoCambioVeiculo.values());

            return "admin/cadastrar_veiculo :: formulario";
        }

        // 3. Se tudo deu certo, redirecione para outra página (como a lista de veículos).
        // Isso evita o reenvio do formulário se o usuário atualizar a página (Padrão Post-Redirect-Get).
        response.setHeader("HX-Redirect", "/admin/veiculos/veiculos-disponiveis");

        return "";
    }

    @GetMapping("/veiculos-disponiveis")
    public String listarVeiculosDisponiveis(Model model) {
        // 1. Chama o service para buscar a lista de veículos disponíveis
        List<Veiculo> veiculosDisponiveis = veiculoService.listarTodos();

        // 2. Adiciona a lista ao Model para que o Thymeleaf possa acessá-la
        model.addAttribute("veiculos", veiculosDisponiveis);

        // 3. Define o título da página
        model.addAttribute("titulo", "Nossa Frota de Veículos");

        // 4. Retorna o caminho para o arquivo HTML
        return "admin/lista_veiculos";
    }

    @HxRequest
    @PostMapping("/inativar/{id}")
    public String inativarVeiculo(@PathVariable Long id, Model model) {
        // 1. Chama o serviço para fazer a lógica de inativação
        Veiculo veiculoAtualizado = veiculoService.inativar(id);

        // 2. Adiciona o veículo ATUALIZADO ao model
        model.addAttribute("veiculo", veiculoAtualizado);

        // 3. Retorna o fragmento do card para o HTMX
        //    Isso enviará o HTML de um único card atualizado de volta.
        return "admin/lista_veiculos :: veiculoCard";
    }

    @HxRequest
    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEdicao(@PathVariable("id") Long id, Model model) {
        // 1. Busca o veículo a ser editado pelo ID.
        Veiculo veiculo = veiculoService.buscarPorCodigo(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo inválido - ID: " + id));

        // 2. Adiciona o objeto 'veiculo' (JÁ PREENCHIDO) ao model.
        // O Thymeleaf usará este objeto para preencher os campos do formulário.
        model.addAttribute("veiculo", veiculo);

        // 3. Adiciona as listas para os dropdowns, assim como no formulário de cadastro.
        model.addAttribute("listaDeStatus", StatusVeiculo.values());
        model.addAttribute("listaDeCategorias", CategoriaVeiculo.values());
        model.addAttribute("listaDeCombustiveis", CombustivelVeiculo.values());
        model.addAttribute("listaDeCambios", TipoCambioVeiculo.values());

        // 4. Retorna o caminho para o novo arquivo HTML de edição.
        return "admin/formulario_edicao_veiculo :: formularioEdicao";
    }

}
