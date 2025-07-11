package br.com.daw1.locacaoveiculos.service;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.*;
import br.com.daw1.locacaoveiculos.model.enums.StatusLocacao;
import br.com.daw1.locacaoveiculos.model.enums.StatusPagamento;
import br.com.daw1.locacaoveiculos.model.enums.StatusVeiculo;
import br.com.daw1.locacaoveiculos.repository.LocacaoRepository;
import br.com.daw1.locacaoveiculos.repository.UsuarioRepository;
import br.com.daw1.locacaoveiculos.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LocacaoService {

    @Autowired
    private LocacaoRepository locacaoRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Locacao criarLocacaoAPartirDeRascunho(RascunhoLocacaoDTO rascunho, Usuario usuarioLogado) {

        // 1. Validação de dados essenciais do rascunho
        if (rascunho.getCodigoVeiculo() == null || rascunho.getDataRetirada() == null) {
            throw new RegraNegocioException("Não é possível finalizar a locação. Dados essenciais estão faltando.");
        }

        // 2. Buscar a entidade Veiculo completa do banco
        Veiculo veiculoEscolhido = veiculoRepository.findByCodigo(rascunho.getCodigoVeiculo())
                .orElseThrow(() -> new RegraNegocioException("Veículo selecionado é inválido."));

        // 3. Criar a entidade Pagamento
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setValorPago(rascunho.getValorTotal());
        novoPagamento.setFormaPagamento(rascunho.getFormaPagamento());
        novoPagamento.setStatusPagamento(StatusPagamento.PENDENTE); // Status inicial
        novoPagamento.setDataPagamento(null); // Data só será preenchida quando o admin confirmar

        // 4. Criar a entidade Locacao
        Locacao novaLocacao = new Locacao();

        // 5. "Traduzir" os dados do DTO para a entidade Locacao
        // Lembre-se da nossa regra de negócio de horários padrão
        novaLocacao.setDataLocacaoInicio(rascunho.getDataRetirada().atTime(14, 0));
        novaLocacao.setDataLocacaoFim(rascunho.getDataDevolucao().atTime(12, 0));
        novaLocacao.setLocalRetirada(rascunho.getLocalRetirada());
        novaLocacao.setLocalDevolucao(rascunho.getLocalDevolucao());
        novaLocacao.setValorTotal(rascunho.getValorTotal());
        novaLocacao.setStatusLocacao(StatusLocacao.PENDENTE); // Status inicial conforme seu Enum

        // 6. Ligar todas as entidades
        novaLocacao.setUsuario(usuarioLogado);
        novaLocacao.setVeiculo(veiculoEscolhido);
        novaLocacao.setPagamento(novoPagamento); // Associa o pagamento recém-criado

        // 7. Salvar a Locacao.
        // Se a relação Locacao->Pagamento for @OneToOne(cascade=CascadeType.ALL),
        // o Pagamento será salvo automaticamente junto com a Locação.
        return locacaoRepository.save(novaLocacao);
    }

    public List<Locacao> listarTodasOrdenadas(){
        return locacaoRepository.findAllComRelacionamentos();
    }

    public Optional<Locacao> buscarPorIdComRelacionamentos(Long id){
        return locacaoRepository.findByIdComRelacionamentos(id);
    }

    @Transactional
    public Locacao atualizarStatus(Long locacaoId, StatusLocacao novoStatusLocacao, StatusPagamento novoStatusPagamento) {

        // 1. Busca a entidade original do banco. É uma prática de segurança para garantir
        // que estamos trabalhando com dados consistentes e evitar alterações indesejadas.
        Locacao locacaoOriginal = buscarPorIdComRelacionamentos(locacaoId)
                .orElseThrow(() -> new RegraNegocioException("Locação não encontrada."));

        // 2. Atualiza os status na entidade original.
        locacaoOriginal.setStatusLocacao(novoStatusLocacao);
        locacaoOriginal.getPagamento().setStatusPagamento(novoStatusPagamento);

        // 3. APLICA REGRAS DE NEGÓCIO COM BASE NOS NOVOS STATUS

        // Se o pagamento foi confirmado, registra a data do pagamento.
        if (novoStatusPagamento == StatusPagamento.PAGO) {
            locacaoOriginal.getPagamento().setDataPagamento(LocalDateTime.now());
        }

        // Se a locação se tornou ATIVA, o veículo deve ser marcado como indisponível.
        if (novoStatusLocacao == StatusLocacao.ATIVA) {
            locacaoOriginal.getVeiculo().setStatus(StatusVeiculo.INATIVO); // Assumindo que INATIVO = não disponível para locação
        }

        // Se a locação foi FINALIZADA ou CANCELADA, o veículo volta a ficar disponível.
        if (novoStatusLocacao == StatusLocacao.FINALIZADA || novoStatusLocacao == StatusLocacao.CANCELADA) {
            locacaoOriginal.getVeiculo().setStatus(StatusVeiculo.ATIVO);
        }

        // 4. Salva a entidade. O JPA/Hibernate é inteligente e fará um UPDATE
        // em todas as entidades modificadas (Locacao, Pagamento, Veiculo).
        return locacaoRepository.save(locacaoOriginal);
    }

    public List<Locacao> listarPorUsuario(Usuario usuario) {
        if (usuario == null) {
            // Retorna uma lista vazia se não houver usuário, evitando NullPointerException.
            return Collections.emptyList();
        }
        return locacaoRepository.findByUsuarioIdComRelacionamentos(usuario.getCodigo());
    }

    public Optional<Locacao> buscarPorIdEUsuario(Long locacaoId, Usuario usuario) {
        if (locacaoId == null || usuario == null) {
            return Optional.empty();
        }
        return locacaoRepository.findByIdAndUsuarioIdComRelacionamentos(locacaoId, usuario.getCodigo());
    }

}
