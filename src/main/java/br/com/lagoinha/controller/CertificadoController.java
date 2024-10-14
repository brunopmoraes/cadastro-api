package br.com.lagoinha.controller;

import br.com.lagoinha.service.CertificadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificados")
@RequiredArgsConstructor
public class CertificadoController {

    private final CertificadoService certificadoService;

    @PostMapping("/{cpf}/enviar")
    public ResponseEntity<String> enviarCertificadoPorEmail(@PathVariable String cpf) {
        try {
            certificadoService.enviarCertificadoPorEmail(cpf);
            return ResponseEntity.ok("Certificado enviado com sucesso.");
        } catch (IllegalArgumentException ex) {
            // Retorna 400 Bad Request
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            // Retorna 500 Internal Server Error para qualquer outra exceção
            return ResponseEntity.status(500).body("Erro ao enviar o certificado: " + ex.getMessage());
        }
    }
}
