package br.com.daw1.locacaoveiculos.service;

import br.com.daw1.locacaoveiculos.repository.LocacaoRepository;
import br.com.daw1.locacaoveiculos.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocacaoService {

    @Autowired
    private LocacaoRepository locacaoRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;

}
