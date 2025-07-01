package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Pessoa;
import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.model.enums.TipoUsuario;
import br.com.daw1.locacaoveiculos.service.PessoaService;
import br.com.daw1.locacaoveiculos.service.UsuarioService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse; // Importação adicionada
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional; // Importação adicionada

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("titulo", "Gerenciar Usuários");
        return "admin/lista_usuarios";
    }

    @HxRequest
    @GetMapping("/novo")
    public String mostrarFormularioCadastroUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("pessoa", new Pessoa());
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        return "admin/cadastrar_usuario :: formularioUsuario";
    }

    @HxRequest
    @PostMapping("/salvar")
    public String salvarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                BindingResult bindingResultUsuario,
                                @Valid @ModelAttribute("pessoa") Pessoa pessoa,
                                BindingResult bindingResultPessoa,
                                Model model,
                                HttpServletResponse response) { // HttpServletResponse adicionado

        // Verificar erros de validação da Pessoa ou Usuário
        if (bindingResultPessoa.hasErrors() || bindingResultUsuario.hasErrors()) {
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return "admin/cadastrar_usuario :: formularioUsuario";
        }

        try {
            // Salva a Pessoa primeiro
            Pessoa pessoaSalva = pessoaService.salvar(pessoa);
            // Associa a Pessoa ao Usuário
            usuario.setPessoa(pessoaSalva);
            // Salva o Usuário
            usuarioService.salvar(usuario);
        } catch (RegraNegocioException e) {
            // Se o Service lançar um erro (ex: CPF duplicado, nome de usuário duplicado), adicione o erro ao BindingResult
            // e retorne para a tela de formulário para exibir a mensagem.
            if (e.getMessage().contains("CPF")) {
                bindingResultPessoa.rejectValue("cpf", "error.pessoa", e.getMessage());
            } else {
                bindingResultUsuario.rejectValue("nomeUsuario", "error.usuario", e.getMessage());
            }
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return "admin/cadastrar_usuario :: formularioUsuario";
        }

        // Se tudo deu certo, redirecione para outra página (como a lista de usuários) via HTMX.
        // Isso evita o reenvio do formulário se o usuário atualizar a página (Padrão Post-Redirect-Get).
        response.setHeader("HX-Redirect", "/admin/usuarios");

        return ""; // Retorna string vazia para o HTMX seguir o HX-Redirect
    }

    // Métodos de edição e exclusão (já existentes ou a serem adicionados)
    @HxRequest
    @GetMapping("/editar/{codigo}")
    public String mostrarFormularioEdicaoUsuario(@PathVariable Long codigo, Model model) {
        Usuario usuario = usuarioService.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado - Código: " + codigo));
        model.addAttribute("usuario", usuario);
        model.addAttribute("pessoa", usuario.getPessoa()); // Carrega a pessoa associada
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        return "admin/formulario_edicao_usuario :: formularioUsuarioEdicao"; // Você precisará criar esta view
    }

    @HxRequest
    @PostMapping("/deletar/{codigo}")
    public String deletarUsuario(@PathVariable Long codigo, HttpServletResponse response) {
        usuarioService.deletar(codigo);
        response.setHeader("HX-Trigger", "usuarioDeletado"); // Evento para HTMX atualizar a lista
        return ""; // Ou um fragmento vazio
    }
}