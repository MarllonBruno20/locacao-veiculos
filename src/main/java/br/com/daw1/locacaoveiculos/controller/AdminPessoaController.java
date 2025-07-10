package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Pessoa;
import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.model.enums.TipoUsuario;
import br.com.daw1.locacaoveiculos.service.PessoaService;
import br.com.daw1.locacaoveiculos.service.UsuarioService; // Importar UsuarioService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private UsuarioService usuarioService; // Injetar UsuarioService

    @GetMapping
    public String listarPessoas(Model model, HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getTipo() != TipoUsuario.ADMINISTRADOR) {
            return "redirect:/auth/login";
        }

        model.addAttribute("pessoas", pessoaService.listarTodas());
        model.addAttribute("titulo", "Gerenciar Pessoas");
        return "admin/lista_pessoas";
    }

    @HxRequest
    @GetMapping("/novo")
    public String mostrarFormularioCadastroPessoa(Model model, HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getTipo() != TipoUsuario.ADMINISTRADOR) {
            return "redirect:/auth/login";
        }

        model.addAttribute("pessoa", new Pessoa());
        model.addAttribute("usuario", new Usuario()); // Adicionar um objeto Usuario vazio ao modelo
        model.addAttribute("tiposUsuario", TipoUsuario.values()); // Adicionar tipos de usuário para o select
        return "admin/cadastrar_pessoa :: formularioPessoa";
    }

    // Novo método para HTMX carregar os campos de usuário
    @HxRequest
    @GetMapping("/toggleUsuarioFields")
    public String toggleUsuarioFields(@RequestParam(name = "criarUsuario", defaultValue = "false") boolean criarUsuario, Model model) {
        if (criarUsuario) {
            model.addAttribute("usuario", new Usuario()); // Passa um objeto Usuario para o fragmento
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return "admin/cadastrar_pessoa :: usuarioFields"; // Retorna o fragmento com os campos de usuário
        }
        return "admin/cadastrar_pessoa :: emptyFields"; // Retorna um fragmento vazio para ocultar os campos
    }


    @HxRequest
    @PostMapping("/salvar")
    public String salvarPessoa(@Valid @ModelAttribute("pessoa") Pessoa pessoa,
                               BindingResult bindingResultPessoa, // Renomeado para evitar conflito
                               @ModelAttribute("usuario") Usuario usuario, // Novo ModelAttribute para o Usuario
                               BindingResult bindingResultUsuario, // Novo BindingResult para o Usuario
                               Model model,
                               HttpServletResponse response,
                               HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getTipo() != TipoUsuario.ADMINISTRADOR) {
            response.setHeader("HX-Redirect", "/auth/login");
            return "";
        }

        // Validação da Pessoa
        if (bindingResultPessoa.hasErrors()) {
            model.addAttribute("tiposUsuario", TipoUsuario.values()); // Garante que os tipos estejam no modelo
            return "admin/cadastrar_pessoa :: formularioPessoa";
        }

        try {
            // 1. Salva a Pessoa
            Pessoa pessoaSalva = pessoaService.salvar(pessoa);

            // 2. Verifica se os dados do Usuário foram fornecidos (se o checkbox foi marcado e campos preenchidos)
            if (usuario != null && usuario.getNomeUsuario() != null && !usuario.getNomeUsuario().isEmpty()) {
                // Se o usuário não tiver um tipo definido, define como CLIENTE por padrão
                if (usuario.getTipo() == null) {
                    usuario.setTipo(TipoUsuario.CLIENTE);
                }
                // Associa a Pessoa recém-salva ao Usuário
                usuario.setPessoa(pessoaSalva);

                // Validação do Usuário (se necessário, pode adicionar mais validações aqui)
                // Para simplificar, vamos assumir que o UsuarioService já valida o nome de usuário único
                // Se houver erros específicos do usuário, você pode usar bindingResultUsuario.rejectValue
                // e retornar o fragmento novamente.
                usuarioService.salvar(usuario); // Salva o Usuário
            }

        } catch (RegraNegocioException e) {
            // Erros de regra de negócio (ex: CPF duplicado)
            if (e.getMessage().toLowerCase().contains("cpf")) {
                bindingResultPessoa.rejectValue("cpf", "error.pessoa", e.getMessage());
            } else if (e.getMessage().toLowerCase().contains("usuário")) { // Para erros de nome de usuário duplicado
                bindingResultUsuario.rejectValue("nomeUsuario", "error.usuario", e.getMessage());
            } else {
                bindingResultPessoa.reject("global.error", e.getMessage()); // Erro genérico
            }
            model.addAttribute("tiposUsuario", TipoUsuario.values()); // Garante que os tipos estejam no modelo
            return "admin/cadastrar_pessoa :: formularioPessoa";
        } catch (Exception e) {
            // Outros erros inesperados
            bindingResultPessoa.reject("global.error", "Erro inesperado ao salvar: " + e.getMessage());
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return "admin/cadastrar_pessoa :: formularioPessoa";
        }

        response.setHeader("HX-Redirect", "/admin/pessoas");
        return "";
    }

    @HxRequest
    @GetMapping("/editar/{codigo}")
    public String mostrarFormularioEdicaoPessoa(@PathVariable Long codigo, Model model, HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getTipo() != TipoUsuario.ADMINISTRADOR) {
            return "redirect:/auth/login";
        }

        Pessoa pessoa = pessoaService.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada - Código: " + codigo));
        model.addAttribute("pessoa", pessoa);
        // Ao editar uma pessoa, não passamos um objeto Usuario novo
        // Se a pessoa já tem um usuário, você pode carregar o primeiro usuário associado
        // ou lidar com múltiplos usuários por pessoa de outra forma na edição.
        // Por enquanto, o foco é na criação.
        return "admin/formulario_edicao_pessoa :: formularioPessoaEdicao";
    }

    @HxRequest
    @PostMapping("/deletar/{codigo}")
    public String deletarPessoa(@PathVariable Long codigo, HttpServletResponse response, HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getTipo() != TipoUsuario.ADMINISTRADOR) {
            response.setHeader("HX-Redirect", "/auth/login");
            return "";
        }

        try {
            pessoaService.deletar(codigo);
            response.setHeader("HX-Trigger", "pessoaDeletada");
        } catch (RegraNegocioException e) {
            response.setHeader("HX-Trigger", "{\"mensagemErro\": \"" + e.getMessage() + "\"}");
        }
        return "";
    }
}