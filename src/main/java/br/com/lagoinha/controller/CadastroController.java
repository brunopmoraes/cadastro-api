package br.com.lagoinha.controller;

import br.com.lagoinha.exception.CadastroNotFoundException;
import br.com.lagoinha.exception.HandleException;
import br.com.lagoinha.model.Cadastro;
import br.com.lagoinha.service.CadastroService;
import br.com.lagoinha.utils.Validador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cadastro")
public class CadastroController {

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }



    private ResponseEntity<Void> handleInvalidCpf() {
        return ResponseEntity.badRequest().build(); // Retorna 400 se o CPF for inválido
    }

    @Operation(summary = "Cria ou atualiza um cadastro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cadastro criado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<?> cadastro(@RequestBody Cadastro cadastro) {
        try {
            cadastroService.create(cadastro);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // Retorna 201 Created
        } catch (Exception e) {
            return HandleException.handleException(e);
        }
    }

    @Operation(summary = "Obtém um cadastro por CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro encontrado"),
            @ApiResponse(responseCode = "400", description = "CPF inválido"),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCadastro(@PathVariable String id) {
        try {
            Cadastro cadastro = cadastroService.getCadastroById(id);
            return ResponseEntity.ok(cadastro); // Retorna 200 com o cadastro
        } catch (Exception e) {
            return HandleException.handleException(e);
        }
    }

    @Operation(summary = "Deleta um cadastro por CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cadastro deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "CPF inválido"),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> deleteCadastro(@PathVariable String cpf) {
        if (!Validador.isCpfValid(cpf)) {
            return handleInvalidCpf();
        }
        try {
            cadastroService.deleteCadastro(cpf);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } catch (Exception e) {
            return HandleException.handleException(e);
        }
    }

    @Operation(summary = "Lista todos os cadastros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cadastros retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<?> listCadastros() {
        try {
            List<Cadastro> cadastros = cadastroService.listCadastros();
            return ResponseEntity.ok(cadastros); // Retorna 200 com a lista de cadastros
        } catch (Exception e) {
            return HandleException.handleException(e);
        }
    }

    @Operation(summary = "Emite cadastros que completaram o ciclo, mas ainda não possuem certificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cadastros retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/emitirCertificado")
    public ResponseEntity<?> emitirCertificado() {
        try {
            List<Cadastro> cadastros = cadastroService.completosSemCertificado();
            return ResponseEntity.ok(cadastros); // Retorna 200 com a lista de cadastros
        } catch (Exception e) {
            return HandleException.handleException(e);
        }
    }

    @Operation(summary = "Atualiza um cadastro", description = "Atualiza os dados de um cadastro existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCadastro(@PathVariable String id, @RequestBody Cadastro cadastro) {
        try {
            cadastroService.update(id, cadastro);
            return ResponseEntity.ok().build(); // Retorna 200 OK
        } catch (Exception e) {
            return HandleException.handleException(e);
        }
    }
}
