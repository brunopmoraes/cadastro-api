package br.com.lagoinha.controller;

import br.com.lagoinha.model.Presenca;
import br.com.lagoinha.service.PresencaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/presenca")
public class PresencaController {

    private final PresencaService presencaService;

    /**
     * Busca presenças por CPF.
     *
     * @param cadastroId Id do cadastro cujas presenças serão buscadas
     * @return ResponseEntity com a lista de presenças ou 404 caso não seja encontrado
     */
    @Operation(summary = "Busca presenças por Cadastro ID", description = "Retorna uma lista de presenças associadas ao Cadastro ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de presenças retornada."),
            @ApiResponse(responseCode = "404", description = "Nenhuma presença encontrada para o CPF fornecido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    @GetMapping("/id/{cadastroId}")
    public ResponseEntity<List<Presenca>> getPresencasById(@PathVariable String cadastroId) {
        List<Presenca> presencas = presencaService.getPresencasByCadastroID(cadastroId);

        if (presencas.isEmpty()) {
            return ResponseEntity.notFound().build();  // Retorna 404 se nenhuma presença for encontrada
        }

        return ResponseEntity.ok(presencas);  // Retorna 200 com a lista de presenças
    }

    /**
     * Lista todas as presenças.
     *
     * @return ResponseEntity com a lista de todas as presenças
     */
    @Operation(summary = "Lista todas as presenças", description = "Retorna uma lista de todas as presenças.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de todas as presenças retornada."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    @GetMapping
    public ResponseEntity<List<Presenca>> listPresencas() {
        List<Presenca> presencas = presencaService.listPresencas();
        return ResponseEntity.ok(presencas);  // Retorna 200 com a lista de presenças
    }

    /**
     * Adiciona uma nova presença.
     *
     * @param presenca Objeto Presenca a ser adicionado
     * @return ResponseEntity com status de sucesso ou erro
     */
    @Operation(summary = "Adiciona presença", description = "Adiciona um novo registro de presença.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Presença adicionada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    @PostMapping
    public ResponseEntity<Void> addPresenca(@RequestBody Presenca presenca) {
        try {
            presencaService.addPresenca(presenca);
            return ResponseEntity.status(HttpStatus.CREATED).build();  // Retorna 201 Created
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Retorna 500 em caso de erro
        }
    }

    /**
     * Exclui uma presença pelo ID.
     *
     * @param id ID da presença a ser excluída
     * @return ResponseEntity com status de sucesso ou erro
     */
    @Operation(summary = "Deleta presença", description = "Remove o registro de presença associado ao ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Presença deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Presença não encontrada para o ID fornecido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePresenca(@PathVariable String id) {
        try {
            presencaService.deletePresenca(id);
            return ResponseEntity.noContent().build();  // Retorna 204 No Content em caso de sucesso
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Retorna 500 em caso de erro
        }
    }
}
