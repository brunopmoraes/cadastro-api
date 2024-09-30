package br.com.lagoinha.service;

import br.com.lagoinha.client.DynamoDbConfig;
import br.com.lagoinha.client.GoogleSheetsClient;
import br.com.lagoinha.dto.ImportarDTO;
import br.com.lagoinha.model.Cadastro;
import br.com.lagoinha.model.Presenca;
import br.com.lagoinha.repository.CadastroRepository;
import br.com.lagoinha.repository.PresencaRepository;
import br.com.lagoinha.utils.Converter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor
@Service
public class ManutencaoService {

    private static final String SPREADSHEET_ID = "1gpyOEAr5tfjPyWHSTg9jBi2fFIxbTS-K2CUXAl4GY2s"; // ID da planilha
    private final CadastroRepository cadastroRepository;
    private final PresencaRepository presencaRepository;
    private final GoogleSheetsClient googleSheetsClient;
    private final DynamoDbConfig dynamoDbConfig;

    public void importarDados(ImportarDTO importar) throws IOException {
        if (importar.isCreateTable()) {
            dynamoDbConfig.createTables();
        }

        String range = String.format("%s!A2:F", importar.getSheetName()); // Define o intervalo a ser lido
        List<List<Object>> dados = googleSheetsClient.getSpreadsheetData(SPREADSHEET_ID, range);

        // Salva os dados no DynamoDB
        salvarDados(dados, importar.getSheetName());
    }

    public void salvarDados(List<List<Object>> dados, String sheetName) {
        for (List<Object> row : dados) {
            Cadastro cadastro = criarCadastro(row);
            Presenca presenca = criarPresenca(sheetName, row, cadastro);

            if (cadastroRepository.findByCpf(cadastro.getCpf()) == null) {
                salvarCadastro(cadastro);
            } else {
                atualizarCadastro(cadastro);
            }
            salvarPresencaComCondicao(presenca);
        }
    }

    private Cadastro criarCadastro(List<Object> row) {
        return Cadastro.builder()
                .id(UUID.randomUUID().toString())
                .cpf((String) row.get(5))
                .nomeCompleto(StringUtils.capitalize((String) row.get(1)).trim())
                .sexo((String) row.get(2))
                .email(((String) row.get(3)).toLowerCase().trim())
                .celular((String) row.get(4))
                .certificado(false)
                .build();
    }

    private Presenca criarPresenca(String sheetName, List<Object> row, Cadastro cadastro) {
        String isoTimestamp = Converter.convertToISO((String) row.get(0));
        return Presenca.builder()
                .id(UUID.randomUUID().toString())
                .cpf(cadastro.getCpf())
                .cadastroId(cadastro.getId())
                .timestamp(isoTimestamp)
                .aula(sheetName)
                .build();
    }

    private void salvarCadastro(Cadastro cadastro) {
        cadastroRepository.save(cadastro);
    }

    private void atualizarCadastro(Cadastro updatedCadastro) {
        Cadastro existingCadastro = cadastroRepository.findByCpf(updatedCadastro.getCpf());

        if (existingCadastro != null) {
            existingCadastro.setNomeCompleto(updatedCadastro.getNomeCompleto());
            existingCadastro.setEmail(updatedCadastro.getEmail());
            existingCadastro.setCelular(updatedCadastro.getCelular());
            existingCadastro.setSexo(updatedCadastro.getSexo());
            cadastroRepository.save(existingCadastro);
        }
    }

    private void salvarPresencaComCondicao(Presenca presenca) {
        try {
            presencaRepository.save(presenca);
            System.out.println("Presença registrada com sucesso! - " + presenca.getId() + " / " + presenca.getAula());
        } catch (ConditionalCheckFailedException e) {
            System.out.println("Presença já existente, atualizando... - " + presenca.getId() + " / " + presenca.getAula());
            presencaRepository.save(presenca);  // Atualiza a presença existente
        }
    }

    private PutItemEnhancedRequest<Presenca> construirPutItemRequestComCondicao(Presenca presenca) {
        Map<String, String> expressionNames = Map.of(
                "#cadastroId", "cadastroId",
                "#aula", "aula"
        );

        Map<String, AttributeValue> expressionValues = Map.of(
                ":cadastroId", AttributeValue.builder().s(presenca.getCadastroId()).build(),
                ":aula", AttributeValue.builder().s(presenca.getAula()).build()
        );

        // Condição para garantir que não haja duplicação de CPF e aula
        Expression conditionExpression = Expression.builder()
                .expression("NOT (#cadastroId = :cadastroId AND #aula = :aula)")
                .expressionNames(expressionNames)
                .expressionValues(expressionValues)
                .build();

        return PutItemEnhancedRequest.builder(Presenca.class)
                .item(presenca)
                .conditionExpression(conditionExpression)
                .build();
    }

    public void atualizaIdCadastros() {
        List<Cadastro> cadastros = cadastroRepository.findAll();
        for (Cadastro cadastro : cadastros) {
            String cadastroId = Objects.requireNonNullElse(cadastro.getId(), UUID.randomUUID().toString());
            if (Objects.isNull(cadastro.getId())) {
                cadastro.setId(cadastroId);
                cadastroRepository.save(cadastro);
            }
            List<Presenca> presencas = presencaRepository.findByCpf(cadastro.getCpf());
            for (Presenca presenca : presencas) {
                if (Objects.isNull(presenca.getCadastroId()) || !presenca.getCadastroId().equals(cadastroId)) {
                    presenca.setCadastroId(cadastroId);
                    presencaRepository.save(presenca);
                }
            }
        }
    }
}
