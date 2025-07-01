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
    public String salvarUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult, // Apenas UM BindingResult para o objeto principal
            Model model,
            HttpServletResponse response) {

        // 1. Verifica erros de validação (tanto de Usuario quanto da Pessoa aninhada)
        if (bindingResult.hasErrors()) {
            // Se houver erros, retorna o formulário para exibir as mensagens.
            // O Spring automaticamente adiciona o 'usuario' com erros de volta ao modelo.
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return "admin/cadastrar_usuario :: formularioUsuario";
        }

        try {
            // 2. Lógica de Negócio: Salva as entidades
            // Extrai a pessoa do objeto usuário para salvá-la primeiro.
            Pessoa pessoaParaSalvar = usuario.getPessoa();
            Pessoa pessoaSalva = pessoaService.salvar(pessoaParaSalvar);

            // Atribui a pessoa salva (agora com um 'codigo') de volta ao usuário e salva o usuário.
            usuario.setPessoa(pessoaSalva);
            usuarioService.salvar(usuario);

        } catch (RegraNegocioException e) {
            // 3. Trata exceções de regras de negócio (ex: CPF ou usuário duplicado)
            // Adiciona o erro ao BindingResult para ser exibido no campo correto do formulário.
            if (e.getMessage().toLowerCase().contains("cpf")) {
                bindingResult.rejectValue("pessoa.cpf", "error.pessoa", e.getMessage());
            } else {
                bindingResult.rejectValue("nomeUsuario", "error.usuario", e.getMessage());
            }

            // Retorna ao formulário com a mensagem de erro da exceção.
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return "admin/cadastrar_usuario :: formularioUsuario";
        }

        // 4. Sucesso: Redireciona via HTMX
        // Se tudo correu bem, envia o header para o HTMX fazer o redirecionamento da página.
        response.setHeader("HX-Redirect", "/admin/usuarios");
        return ""; // Retorna corpo vazio, pois o HTMX cuidará do redirecionamento.
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