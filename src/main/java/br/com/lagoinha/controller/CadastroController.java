package br.com.lagoinha.controller;

import br.com.lagoinha.exception.CadastroNotFoundException;
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

    // Injeta o CadastroService via construtor (Princípio da Inversão de Dependência)
    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    /**
     * Endpoint para criar ou atualizar um cadastro.
     *
     * @param cadastro - dados do cadastro
     */
    @Operation(summary = "Cria ou atualiza um cadastro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cadastro criado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<Void> cadastro(@RequestBody Cadastro cadastro) {
        try {
            cadastroService.create(cadastro);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // Retorna 201 Created
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 em caso de erro
        }
    }

    /**
     * Endpoint para obter um cadastro por CPF.
     *
     * @param id - ID a ser consultado
     * @return cadastro encontrado ou erro
     */
    @Operation(summary = "Obtém um cadastro por CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro encontrado"),
            @ApiResponse(responseCode = "400", description = "CPF inválido"),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Cadastro> getCadastro(@PathVariable String id) {
        try {
            Cadastro cadastro = cadastroService.getCadastroById(id);
            return ResponseEntity.ok(cadastro); // Retorna 200 com o cadastro
        } catch (CadastroNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 se o cadastro não for encontrado
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 em caso de erro
        }
    }

    /**
     * Endpoint para deletar um cadastro por CPF.
     *
     * @param cpf - CPF do cadastro a ser removido
     */
    @Operation(summary = "Deleta um cadastro por CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cadastro deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "CPF inválido"),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteCadastro(@PathVariable String cpf) {
        if (!Validador.isCpfValid(cpf)) {
            return ResponseEntity.badRequest().build(); // Retorna 400 se o CPF for inválido
        }
        try {
            cadastroService.deleteCadastro(cpf);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content após remoção bem-sucedida
        } catch (CadastroNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 se não encontrado
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 em caso de erro
        }
    }

    /**
     * Endpoint para listar todos os cadastros.
     *
     * @return lista de cadastros
     */
    @Operation(summary = "Lista todos os cadastros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cadastros retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<List<Cadastro>> listCadastros() {
        try {
            List<Cadastro> cadastros = cadastroService.listCadastros();
            return ResponseEntity.ok(cadastros); // Retorna 200 com a lista de cadastros
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 em caso de erro
        }
    }

    /**
     * Endpoint para confirmar o recebimento do certificado de um cadastro.
     *
     * @param cpf - CPF do cadastro
     */
    @Operation(summary = "Confirma o recebimento do certificado de um cadastro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificado confirmado com sucesso"),
            @ApiResponse(responseCode = "400", description = "CPF inválido"),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/certificado/{cpf}")
    public ResponseEntity<Void> confirmarCertificado(@PathVariable String cpf) {
        if (!Validador.isCpfValid(cpf)) {
            return ResponseEntity.badRequest().build(); // Retorna 400 se o CPF for inválido
        }
        try {
            cadastroService.confirmarCertificado(cpf);
            return ResponseEntity.ok().build(); // Retorna 200 OK após confirmação
        } catch (CadastroNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 se o cadastro não for encontrado
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 em caso de erro
        }
    }

    /**
     * Endpoint para emitir cadastros que completaram o ciclo, mas ainda não possuem certificado.
     *
     * @return lista de cadastros aptos para receber certificado
     */
    @Operation(summary = "Emite cadastros que completaram o ciclo, mas ainda não possuem certificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cadastros retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/emitirCertificado")
    public ResponseEntity<List<Cadastro>> emitirCertificado() {
        try {
            List<Cadastro> cadastros = cadastroService.completosSemCertificado();
            return ResponseEntity.ok(cadastros); // Retorna 200 com a lista de cadastros
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 em caso de erro
        }
    }

    /**
     * Atualiza um cadastro existente.
     *
     * @param id - ID do cadastro a ser atualizado
     * @param cadastro - dados atualizados do cadastro
     * @return ResponseEntity com o status da operação
     */
    @Operation(summary = "Atualiza um cadastro", description = "Atualiza os dados de um cadastro existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCadastro(@PathVariable String id, @RequestBody Cadastro cadastro) {
        try {
            cadastroService.update(id, cadastro);
            return ResponseEntity.ok().build(); // Retorna 200 OK após atualização
        } catch (CadastroNotFoundException e) {
            return ResponseEntity.notFound().build(); // Retorna 404 se não encontrado
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500 em caso de erro
        }
    }
}
