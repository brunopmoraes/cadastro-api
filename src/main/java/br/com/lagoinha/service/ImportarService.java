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
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor
@Service
public class ImportarService {

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

        // Obtém os dados da planilha
        List<List<Object>> dados = googleSheetsClient.getSpreadsheetData(SPREADSHEET_ID, range);

        // Salva os dados no DynamoDB
        salvarDados(dados, importar.getSheetName());
    }

    /**
     * Salva os dados da planilha no DynamoDB, incluindo cadastros e presenças.
     *
     * @param dados     Lista de listas de objetos representando os dados a serem salvos.
     * @param sheetName Nome da planilha que fornece o contexto para a aula.
     */
    public void salvarDados(List<List<Object>> dados, String sheetName) {
        for (List<Object> row : dados) {
            Cadastro cadastro = criarCadastro(row);
            Presenca presenca = criarPresenca(sheetName, row, cadastro);

            salvarCadastro(cadastro);
            salvarPresencaComCondicao(presenca);
        }
    }

    /**
     * Cria um objeto Cadastro a partir de uma linha de dados.
     *
     * @param row Dados da linha da planilha.
     * @return Cadastro criado.
     */
    private Cadastro criarCadastro(List<Object> row) {
        return Cadastro.builder()
                .id(UUID.randomUUID().toString())
                .cpf((String) row.get(5))
                .nomeCompleto(((String) row.get(1)).trim())
                .sexo((String) row.get(2))
                .email(((String) row.get(3)).toLowerCase().trim())
                .celular((String) row.get(4))
                .certificado(false)
                .build();
    }

    /**
     * Cria um objeto Presenca a partir de uma linha de dados e o nome da planilha.
     *
     * @param sheetName Nome da planilha (aula).
     * @param row       Dados da linha da planilha.
     * @return Presenca criada.
     */
    private Presenca criarPresenca(String sheetName, List<Object> row, Cadastro cadastro) {
        String isoTimestamp = Converter.convertToISO((String) row.get(0));
        String presencaId = UUID.randomUUID().toString();

        return Presenca.builder()
                .id(presencaId)
                .cadastroId(cadastro.getId())
                .timestamp(isoTimestamp)
                .aula(sheetName)
                .build();
    }

    /**
     * Salva o cadastro no repositório.
     *
     * @param cadastro Cadastro a ser salvo.
     */
    private void salvarCadastro(Cadastro cadastro) {
        cadastroRepository.save(cadastro);
    }

    /**
     * Salva a presença no repositório, garantindo que não haja duplicação de CPF e aula.
     *
     * @param presenca Presença a ser salva.
     */
    private void salvarPresencaComCondicao(Presenca presenca) {
        PutItemEnhancedRequest<Presenca> putItemRequest = construirPutItemRequestComCondicao(presenca);

        try {
            presencaRepository.save(putItemRequest);
            System.out.println("Presença registrada com sucesso! - " + presenca.getId() + " / " + presenca.getAula());
        } catch (ConditionalCheckFailedException e) {
            System.out.println("Presença já existente, atualizando... - " + presenca.getId() + " / " + presenca.getAula());
            presencaRepository.save(presenca);  // Atualiza a presença existente
        }
    }

    /**
     * Constrói a requisição para salvar a presença com a condição de não duplicar CPF e aula.
     *
     * @param presenca Presença que será condicionada.
     * @return PutItemEnhancedRequest para a operação no DynamoDB.
     */
    private PutItemEnhancedRequest<Presenca> construirPutItemRequestComCondicao(Presenca presenca) {
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#cadastroId", "cadastroId");
        expressionNames.put("#aula", "aula");

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":cadastroId", AttributeValue.builder().s(presenca.getCadastroId()).build());
        expressionValues.put(":aula", AttributeValue.builder().s(presenca.getAula()).build());

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

    public void atualizaIdCadastros(){
        List<Cadastro> cadastros = cadastroRepository.findAll();
        for (Cadastro cadastro : cadastros) {
            String cadastroId = Objects.isNull(cadastro.getId()) ? UUID.randomUUID().toString() :  cadastro.getId();
            if(Objects.isNull(cadastro.getId())) {
                cadastro.setId(cadastroId);
                cadastroRepository.save(cadastro);
            }
            List<Presenca> presencas = presencaRepository.findByCpf(cadastro.getCpf());
            for (Presenca presenca : presencas) {
                if(Objects.isNull(presenca.getCadastroId()) || !presenca.getCadastroId().equals(cadastroId)) {
                    presenca.setCadastroId(cadastroId);
                    presencaRepository.save(presenca);
                }
            }
        }
    }
}
