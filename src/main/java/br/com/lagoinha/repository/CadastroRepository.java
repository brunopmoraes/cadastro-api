package br.com.lagoinha.repository;

import br.com.lagoinha.model.Cadastro;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CadastroRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Cadastro> cadastroTable;

    /**
     * Construtor para injeção de dependências.
     * Inicializa a tabela "Cadastro" com o schema mapeado da classe Cadastro.
     *
     * @param enhancedClient Cliente DynamoDB aprimorado
     */
    public CadastroRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.cadastroTable = enhancedClient.table("Cadastro", TableSchema.fromBean(Cadastro.class));
    }

    /**
     * Busca um cadastro pelo CPF.
     *
     * @param cpf CPF do cadastro a ser buscado.
     * @return O cadastro encontrado ou null se não existir.
     */
    public Cadastro findByCpf(String cpf) {
        Key key = Key.builder()
                .partitionValue(cpf)  // Define a chave de partição (CPF) para o DynamoDB.
                .build();
        return cadastroTable.getItem(key);
    }

    /**
     * Salva ou atualiza um cadastro na tabela DynamoDB.
     *
     * @param cadastro O objeto cadastro a ser salvo ou atualizado.
     */
    public void save(Cadastro cadastro) {
        cadastroTable.putItem(cadastro);
    }

    /**
     * Retorna uma lista de todos os cadastros presentes na tabela.
     *
     * @return Lista de cadastros.
     */
    public List<Cadastro> findAll() {
        return cadastroTable.scan().items().stream().collect(Collectors.toList());
    }

    /**
     * Exclui um cadastro da tabela DynamoDB.
     *
     * @param cadastro O cadastro a ser excluído.
     */
    public void delete(Cadastro cadastro) {
        cadastroTable.deleteItem(cadastro);
    }

    /**
     * Busca todos os cadastros que ainda não possuem certificado.
     *
     * @return Lista de cadastros que não possuem certificado.
     */
    public List<Cadastro> findSemCertificado() {
        return cadastroTable.scan().items()
                .stream()
                .filter(cadastro -> !cadastro.isCertificado())  // Filtra cadastros sem certificado.
                .collect(Collectors.toList());
    }
}
