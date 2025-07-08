package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.model.RascunhoLocacaoDTO;
import br.com.daw1.locacaoveiculos.model.enums.LocaisRetiradaDevolucao;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/locacao")
public class LocacaoController {

    @GetMapping("/nova")
    public String iniciarNovaLocacao(HttpSession session, Model model) {
        session.setAttribute("rascunhoLocacao", new RascunhoLocacaoDTO());
        model.addAttribute("titulo", "Nova Locação");

        return "public/locacao-wizard";
    }

    // --- MÉTODO 2: FORNECER O HTML DA ETAPA 1 ---
    // É chamado pelo hx-trigger="load" da página casca.
    @HxRequest
    @GetMapping("/etapa1")
    public String mostrarEtapa1(Model model, HttpSession session) {
        // Adiciona o rascunho (ainda vazio) e as opções de locais ao model
        model.addAttribute("rascunho", session.getAttribute("rascunhoLocacao"));
        model.addAttribute("locais", LocaisRetiradaDevolucao.values());
        // Retorna APENAS o fragmento da Etapa 1
        return "public/locacao-fragments :: etapa1";
    }

    // --- MÉTODO 3: PROCESSAR OS DADOS DA ETAPA 1 ---
    // É chamado quando o usuário clica em "Avançar" no formulário da Etapa 1.
    @HxRequest
    @PostMapping("/etapa1")
    public String processarEtapa1(@ModelAttribute RascunhoLocacaoDTO dadosEtapa1, HttpSession session, Model model) {
        // Recupera o rascunho da sessão
        RascunhoLocacaoDTO rascunho = (RascunhoLocacaoDTO) session.getAttribute("rascunhoLocacao");
        if (rascunho == null) {
            return "redirect:/locacao/nova"; // Sessão expirou, reinicia o processo
        }

        // Atualiza o rascunho com os dados recebidos do formulário
        rascunho.setDataRetirada(dadosEtapa1.getDataRetirada());
        rascunho.setDataDevolucao(dadosEtapa1.getDataDevolucao());
        rascunho.setLocalRetirada(dadosEtapa1.getLocalRetirada());
        rascunho.setLocalDevolucao(dadosEtapa1.getLocalDevolucao());

        // Salva o rascunho atualizado de volta na sessão
        session.setAttribute("rascunhoLocacao", rascunho);

        // Prepara os dados para a próxima etapa (a lista de veículos disponíveis)
        // List<Veiculo> veiculos = veiculoService.listarTodosDisponiveis(rascunho.getDataRetirada(), ...);
        // model.addAttribute("veiculos", veiculos);

        // Retorna o fragmento da PRÓXIMA etapa (Etapa 2)
        return "public/locacao-fragments :: etapa2";
    }

}
