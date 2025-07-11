package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.dto.VeiculoRelatorioDTO;
import br.com.daw1.locacaoveiculos.service.LocacaoService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/relatorios")
public class RelatorioController {

    private final LocacaoService locacaoService;

    public RelatorioController(LocacaoService locacaoService) {
        this.locacaoService = locacaoService;
    }

    @GetMapping("/frota")
    public ResponseEntity<byte[]> gerarRelatorioFrota() {
        try {
            // 1. Prepara os dados (continua igual)
            List<VeiculoRelatorioDTO> dados = locacaoService.prepararDadosRelatorioFrota();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);

            // 2. Carrega o ARQUIVO FONTE .jrxml do relatório principal
            InputStream relatorioPrincipalStream = getClass().getResourceAsStream("/reports/relatorio-frota.jrxml");

            // 3. COMPILA o .jrxml para um objeto JasperReport em memória
            JasperReport jasperReport = JasperCompileManager.compileReport(relatorioPrincipalStream);

            // 4. Carrega e COMPILA o sub-relatório para passar como parâmetro
            InputStream subRelatorioStream = getClass().getResourceAsStream("/reports/subreport_locacao_por_veiculo.jrxml");
            JasperReport subReportCompilado = JasperCompileManager.compileReport(subRelatorioStream);

            // 5. Prepara os parâmetros. Agora passamos o sub-relatório JÁ COMPILADO.
            Map<String, Object> parametros = new HashMap<>();
            // O nome "SUB_REPORT_LOCACOES" deve ser o nome de um PARÂMETRO criado no seu relatório principal
            parametros.put("SUB_REPORT_LOCACOES", subReportCompilado);

            // 6. Preenche o relatório já compilado com os dados
            JasperPrint relatorioPreenchido = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            // 7. Exporta para PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(relatorioPreenchido);

            // 8. Configura a resposta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "relatorio-frota.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
