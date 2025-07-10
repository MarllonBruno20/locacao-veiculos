package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.RascunhoLocacaoDTO;
import br.com.daw1.locacaoveiculos.model.Veiculo;
import br.com.daw1.locacaoveiculos.model.enums.FormaPagamento;
import br.com.daw1.locacaoveiculos.model.enums.LocaisRetiradaDevolucao;
import br.com.daw1.locacaoveiculos.service.VeiculoService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/locacao")
public class LocacaoController {

    @Autowired
    private VeiculoService veiculoService;

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
        return "public/locacao-fragments :: resposta-etapa1";
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

        // Supondo que você recebeu os dados da Etapa 1 no seu DTO
        LocalDate dataRetiradaDoForm = rascunho.getDataRetirada();
        LocalDate dataDevolucaoDoForm = rascunho.getDataDevolucao();

        // --- AQUI ESTÁ A CONVERSÃO ---
        // Regra de Negócio: As retiradas são sempre a partir das 14:00.
        LocalDateTime inicioRealDaLocacao = dataRetiradaDoForm.atTime(14, 0);

        // Regra de Negócio: As devoluções são sempre até as 12:00.
        LocalDateTime fimRealDaLocacao = dataDevolucaoDoForm.atTime(12, 0);

        // Salva o rascunho atualizado de volta na sessão
        session.setAttribute("rascunhoLocacao", rascunho);

        // Prepara os dados para a próxima etapa (a lista de veículos disponíveis)
        List<Veiculo> veiculos = veiculoService.listarVeiculosDisponiveisParaPeriodo(inicioRealDaLocacao, fimRealDaLocacao);
        model.addAttribute("veiculos", veiculos);

        // Retorna o fragmento da PRÓXIMA etapa (Etapa 2)
        return "public/locacao-fragments :: resposta-etapa2";
    }

    // --- MÉTODO PARA PROCESSAR A ETAPA 2 E PREPARAR A ETAPA 3 ---
    @HxRequest
    @PostMapping("/etapa2")
    public String processarEtapa2(@ModelAttribute RascunhoLocacaoDTO dadosEtapa2, HttpSession session, Model model) {
        // 1. Recupera o rascunho da sessão.
        RascunhoLocacaoDTO rascunho = (RascunhoLocacaoDTO) session.getAttribute("rascunhoLocacao");
        if (rascunho == null) {
            return "redirect:/locacao/nova"; // Segurança: se a sessão expirou, reinicia.
        }

        // 2. Atualiza o rascunho com os dados recebidos (ID e nome do veículo).
        rascunho.setCodigoVeiculo(dadosEtapa2.getCodigoVeiculo());
        rascunho.setNomeVeiculo(dadosEtapa2.getNomeVeiculo());
        rascunho.setPlaca(dadosEtapa2.getPlaca());

        // 3. LÓGICA DE CÁLCULO DO VALOR TOTAL
        // Busca o veículo completo no banco para obter o valor da diária.
        Veiculo veiculoEscolhido = veiculoService.buscarPorCodigo(rascunho.getCodigoVeiculo())
                .orElseThrow(() -> new RegraNegocioException("Veículo selecionado é inválido."));

        // Calcula o número de dias da locação.
        long numeroDeDias = ChronoUnit.DAYS.between(rascunho.getDataRetirada(), rascunho.getDataDevolucao());
        if (numeroDeDias == 0) numeroDeDias = 1; // Mínimo de 1 diária

        // Calcula o valor total e o armazena no rascunho.
        BigDecimal valorTotal = veiculoEscolhido.getValorDiaria().multiply(new BigDecimal(numeroDeDias));
        rascunho.setValorTotal(valorTotal);

        // 4. Salva o rascunho, agora completo, de volta na sessão.
        session.setAttribute("rascunhoLocacao", rascunho);

        // 5. Prepara o Model para a próxima etapa.
        model.addAttribute("rascunho", rascunho);
        model.addAttribute("formasDePagamento", FormaPagamento.values()); // Para o <select> de pagamento

        // 6. Retorna os fragmentos da Etapa 3 (conteúdo do resumo + indicador de progresso).
        return "public/locacao-fragments :: resposta-etapa3";
    }

}
