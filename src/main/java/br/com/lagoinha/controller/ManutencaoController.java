package br.com.lagoinha.controller;

import br.com.lagoinha.dto.ImportarDTO;
import br.com.lagoinha.service.ManutencaoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/manutencao")
public class ManutencaoController {

    private final ManutencaoService manutencaoService;

    /**
     * Endpoint para importar dados de uma planilha do Google Sheets e salvar no DynamoDB.
     * @param importar Objeto contendo informações para a importação.
     * @return ResponseEntity com a mensagem de sucesso ou erro.
     */
    @PostMapping("/importar")
    public ResponseEntity<String> importarPlanilha(@RequestBody ImportarDTO importar) {
        try {
            manutencaoService.importarDados(importar);
            return ResponseEntity.ok("Processo Finalizado com sucesso!");
        } catch (Exception e) {
            // Retorna mensagem de erro com o código 500
            return ResponseEntity.status(500).body("Erro ao importar planilha: " + e.getMessage());
        }
    }


    @PostMapping("/criarId")
    public ResponseEntity<String> atualizaIdCadastros(){
        try {
            manutencaoService.atualizaIdCadastros();
            return ResponseEntity.ok("Processo Finalizado com sucesso!");
        } catch (Exception e) {
            // Retorna mensagem de erro com o código 500
            return ResponseEntity.status(500).body("Erro ao importar planilha: " + e.getMessage());
        }
    }
}
