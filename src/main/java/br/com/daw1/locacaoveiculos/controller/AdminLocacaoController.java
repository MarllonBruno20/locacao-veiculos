package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.model.Locacao;
import br.com.daw1.locacaoveiculos.model.enums.StatusLocacao;
import br.com.daw1.locacaoveiculos.model.enums.StatusPagamento;
import br.com.daw1.locacaoveiculos.service.LocacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/locacao")
public class AdminLocacaoController {

    @Autowired
    private LocacaoService locacaoService;

    @GetMapping
    public String listarTodasAsLocacoes(Model model) {
        // 1. Busca os dados através do Service
        List<Locacao> todasLocacoes = locacaoService.listarTodasOrdenadas();

        // 2. Adiciona os dados e o título ao Model para a view poder usar
        model.addAttribute("locacoes", todasLocacoes);
        model.addAttribute("titulo", "Gerenciamento de Locações");

        // 3. Retorna o caminho para o arquivo HTML da view
        return "admin/lista-locacoes";
    }

    @GetMapping("/gerenciar/{id}")
    public String mostrarPaginaDeGerenciamento(@PathVariable("id") Long id, Model model) {

        // 1. Busca a locação completa com todos os seus relacionamentos
        Locacao locacao = locacaoService.buscarPorIdComRelacionamentos(id) // Você precisará criar este método no service/repository
                .orElseThrow(() -> new IllegalArgumentException("Locação não encontrada - ID: " + id));

        // 2. Adiciona a locação encontrada ao model para o Thymeleaf usar
        model.addAttribute("locacao", locacao);
        model.addAttribute("titulo", "Gerenciar Locação #" + id);

        // 3. Adiciona as listas de TODOS os status possíveis para popular os dropdowns
        model.addAttribute("todosStatusLocacao", StatusLocacao.values());
        model.addAttribute("todosStatusPagamento", StatusPagamento.values());

        // 4. Retorna o caminho para o arquivo HTML da view de detalhes/edição
        return "admin/detalhes-locacao";
    }

    @PostMapping("/atualizar-status")
    public String atualizarStatusDaLocacao(@ModelAttribute Locacao locacaoDoFormulario,
                                           HttpServletRequest request, HttpServletResponse response) {
        // Recebemos os dados do formulário no objeto 'locacaoDoFormulario'.
        // Ele conterá o ID da locação e os novos status selecionados.

        locacaoService.atualizarStatus(
                locacaoDoFormulario.getCodigo(),
                locacaoDoFormulario.getStatusLocacao(),
                locacaoDoFormulario.getPagamento().getStatusPagamento()
        );

        // Lógica de redirect para HTMX e fallback
        // Verificamos se a requisição tem o header do HTMX
        if (request.getHeader("HX-Request") != null) {
            // Se for HTMX, enviamos o header de redirect
            response.setHeader("HX-Redirect", "/admin/locacao");
            return "";
        }

        // Se for uma requisição normal, usamos o redirect do Spring
        return "redirect:/admin/locacao";
    }

}
