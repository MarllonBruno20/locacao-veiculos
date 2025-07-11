package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Locacao;
import br.com.daw1.locacaoveiculos.model.RascunhoLocacaoDTO;
import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.model.Veiculo;
import br.com.daw1.locacaoveiculos.model.enums.FormaPagamento;
import br.com.daw1.locacaoveiculos.model.enums.LocaisRetiradaDevolucao;
import br.com.daw1.locacaoveiculos.repository.UsuarioRepository;
import br.com.daw1.locacaoveiculos.service.LocacaoService;
import br.com.daw1.locacaoveiculos.service.VeiculoService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @Autowired
    private LocacaoService locacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @HxRequest
    @GetMapping("/nova")
    public void iniciarNovaLocacao(HttpSession session, HttpServletResponse response) throws IOException {
        // Prepara o rascunho na sessão
        session.setAttribute("rascunhoLocacao", new RascunhoLocacaoDTO());

        // AQUI ESTÁ A MUDANÇA: Envia o header de redirect para o HTMX
        response.setHeader("HX-Redirect", "/locacao/wizard");
    }

    @GetMapping("/wizard")
    public String mostrarPaginaWizard(Model model) {
        model.addAttribute("titulo", "Nova Locação");

        // Ele só precisa retornar o caminho para o seu template "casca".
        // O hx-trigger="load" dentro deste template cuidará de carregar a Etapa 1.
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
    public String processarEtapa1(@ModelAttribute RascunhoLocacaoDTO dadosEtapa1, HttpSession session,
                                  Model model, HttpServletResponse response) {
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

        // --- A MUDANÇA ESTÁ AQUI ---
        // Instrui o HTMX a mudar a URL na barra de endereço do navegador para a URL da Etapa 2
        response.setHeader("HX-Push-Url", "/locacao/etapa2");

        // Retorna o fragmento da PRÓXIMA etapa (Etapa 2)
        return "public/locacao-fragments :: resposta-etapa2";
    }

    // --- MÉTODO PARA PROCESSAR A ETAPA 2 E PREPARAR A ETAPA 3 ---
    @HxRequest
    @PostMapping("/etapa2")
    public String processarEtapa2(@ModelAttribute RascunhoLocacaoDTO dadosEtapa2, HttpSession session,
                                  Model model, HttpServletResponse response) {
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

        // --- A MUDANÇA ESTÁ AQUI ---
        // Instrui o HTMX a mudar a URL para a URL da Etapa 3
        response.setHeader("HX-Push-Url", "/locacao/etapa3");

        // 6. Retorna os fragmentos da Etapa 3 (conteúdo do resumo + indicador de progresso).
        return "public/locacao-fragments :: resposta-etapa3";
    }

    @HxRequest
    @GetMapping("/voltar-para-etapa2")
    public String voltarParaEtapa2(HttpSession session, Model model, HttpServletResponse response) {

        // 1. Recupera o rascunho da sessão para obter as datas.
        RascunhoLocacaoDTO rascunho = (RascunhoLocacaoDTO) session.getAttribute("rascunhoLocacao");

        // 2. Segurança: se não houver rascunho ou datas, reinicia o fluxo.
        if (rascunho == null || rascunho.getDataRetirada() == null) {
            response.setHeader("HX-Redirect", "/locacao/nova");
            return "";
        }

        // 3. Re-executa a lógica de busca de veículos que estava no 'processarEtapa1'
        LocalDateTime inicioRealDaLocacao = rascunho.getDataRetirada().atTime(14, 0);
        LocalDateTime fimRealDaLocacao = rascunho.getDataDevolucao().atTime(12, 0);

        List<Veiculo> veiculos = veiculoService.listarVeiculosDisponiveisParaPeriodo(inicioRealDaLocacao, fimRealDaLocacao);
        model.addAttribute("veiculos", veiculos);

        // 4. Retorna o pacote de fragmentos da Etapa 2 (conteúdo + indicador de progresso)
        return "public/locacao-fragments :: resposta-etapa2";
    }


    @HxRequest
    @PostMapping("/finalizar")
    public String finalizarLocacao(@ModelAttribute RascunhoLocacaoDTO dadosFinaisDoForm, @AuthenticationPrincipal Usuario usuarioLogado,
            HttpSession session, Model model) {

        // 1. Recupera o rascunho final da sessão
        RascunhoLocacaoDTO rascunho = (RascunhoLocacaoDTO) session.getAttribute("rascunhoLocacao");
        if (rascunho == null) {
            // Se a sessão expirou ou o usuário não está logado, reinicia o processo.
            return "redirect:/locacao/nova";
        }

        rascunho.setFormaPagamento(dadosFinaisDoForm.getFormaPagamento());

        try {
            // 2. Chama o service para criar a locação permanente no banco
            Locacao locacaoFinalizada = locacaoService.criarLocacaoAPartirDeRascunho(rascunho, usuarioLogado);

            // 3. Limpeza Crucial: Remove o rascunho da sessão para liberar memória
            // e impedir que o usuário reenvie o mesmo pedido.
            session.removeAttribute("rascunhoLocacao");

            // 4. Prepara o model para a tela de sucesso
            model.addAttribute("locacao", locacaoFinalizada);
            return "public/locacao-fragments :: etapa-sucesso";

        } catch (RegraNegocioException e) {
            // Em caso de erro, exibe a mensagem para o usuário
            model.addAttribute("erro", e.getMessage());
            // Retorna para a etapa anterior
            model.addAttribute("rascunho", rascunho);
            model.addAttribute("formasDePagamento", FormaPagamento.values());
            return "public/locacao-fragments :: etapa3";
        }
    }

    @GetMapping("/minhas-locacoes")
    public String mostrarMinhasLocacoesPaginaCompleta(@AuthenticationPrincipal Usuario usuarioLogado, Model model) {

        // 1. O Spring Security injeta o usuário logado diretamente com @AuthenticationPrincipal
        //    (requer configuração no seu UserDetailsService).
        if (usuarioLogado == null) {
            // Se, por algum motivo, o usuário não estiver logado, redireciona para o login.
            return "redirect:/login";
        }

        // 2. Busca as locações específicas deste usuário.
        List<Locacao> minhasLocacoes = locacaoService.listarPorUsuario(usuarioLogado);

        // 3. Adiciona os dados ao Model.
        model.addAttribute("locacoes", minhasLocacoes);
        model.addAttribute("titulo", "Minhas Locações");

        // 4. Retorna a nova view.
        return "public/minhas-locacoes";
    }

    @GetMapping("/detalhes/{id}")
    public String mostrarDetalhesLocacao(@PathVariable("id") Long locacaoId, @AuthenticationPrincipal Usuario usuarioLogado,
                                         Model model) {

        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        // 1. Chama o serviço seguro, que já verifica a posse da locação.
        Locacao locacao = locacaoService.buscarPorIdEUsuario(locacaoId, usuarioLogado)
                .orElseThrow(() -> new RegraNegocioException("Locação não encontrada ou não pertence a você."));

        // 2. Adiciona a locação e o título ao Model.
        model.addAttribute("locacao", locacao);
        model.addAttribute("titulo", "Detalhes da Locação #" + locacao.getCodigo());

        // 3. Retorna a nova view de detalhes.
        return "public/detalhe-locacao";
    }

}
