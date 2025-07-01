package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Pessoa;
import br.com.daw1.locacaoveiculos.service.PessoaService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse; // Importação adicionada
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/pessoas")
public class AdminPessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public String listarPessoas(Model model) {
        model.addAttribute("pessoas", pessoaService.listarTodas());
        model.addAttribute("titulo", "Gerenciar Pessoas");
        return "admin/lista_pessoas";
    }

    @HxRequest
    @GetMapping("/novo")
    public String mostrarFormularioCadastroPessoa(Model model) {
        model.addAttribute("pessoa", new Pessoa());
        return "admin/cadastrar_pessoa :: formularioPessoa";
    }

    @HxRequest
    @PostMapping("/salvar")
    public String salvarPessoa(@Valid @ModelAttribute("pessoa") Pessoa pessoa,
                               BindingResult bindingResult,
                               Model model,
                               HttpServletResponse response) { // HttpServletResponse adicionado
        if (bindingResult.hasErrors()) {
            return "admin/cadastrar_pessoa :: formularioPessoa";
        }
        try {
            pessoaService.salvar(pessoa);
        } catch (RegraNegocioException e) {
            bindingResult.rejectValue("cpf", "error.pessoa", e.getMessage()); // Rejeita o campo 'cpf' com a mensagem de erro
            return "admin/cadastrar_pessoa :: formularioPessoa";
        }

        // Se tudo deu certo, redirecione via HTMX
        response.setHeader("HX-Redirect", "/admin/pessoas");
        return ""; // Retorna string vazia para o HTMX seguir o HX-Redirect
    }

    @HxRequest
    @GetMapping("/editar/{codigo}")
    public String mostrarFormularioEdicaoPessoa(@PathVariable Long codigo, Model model) {
        Pessoa pessoa = pessoaService.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada - Código: " + codigo));
        model.addAttribute("pessoa", pessoa);
        return "admin/formulario_edicao_pessoa :: formularioPessoaEdicao";
    }

    @HxRequest
    @PostMapping("/deletar/{codigo}")
    public String deletarPessoa(@PathVariable Long codigo, HttpServletResponse response) {
        try {
            pessoaService.deletar(codigo);
            response.setHeader("HX-Trigger", "pessoaDeletada"); // Dispara um evento HTMX para a lista
        } catch (RegraNegocioException e) {
            // Em caso de erro (ex: pessoa vinculada), você pode retornar uma mensagem
            // Uma forma simples de exibir o erro ao usuário é via JS ou HTMX com um swap de um elemento de mensagem
            // Por simplicidade, estou usando HX-Trigger para sinalizar um erro, que você pode capturar no frontend
            response.setHeader("HX-Trigger", "{\"mensagemErro\": \"" + e.getMessage() + "\"}");
        }
        return "";
    }
}