package br.com.lagoinha.repository;

import br.com.lagoinha.model.Cadastro;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CadastroRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Cadastro> cadastroTable;

    public CadastroRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.cadastroTable = enhancedClient.table("Cadastro", TableSchema.fromBean(Cadastro.class));
    }

    /**
     * Busca um cadastro pelo ID.
     *
     * @param id Id do cadastro a ser buscado.
     * @return O cadastro encontrado ou null se não existir.
     */
    public Optional<Cadastro> findById(String id) {
        // Aqui você pode fazer a verificação e retornar um Optional
        Cadastro cadastro = cadastroTable.getItem(Key.builder().partitionValue(id).build());
        return Optional.ofNullable(cadastro); // Retorna um Optional com o cadastro ou vazio, se não encontrado
    }

    /**
     * Busca um cadastro pelo CPF.
     *
     * @param cpf CPF do cadastro a ser buscado.
     * @return O cadastro encontrado ou null se não existir.
     */
    public Cadastro findByCpf(String cpf) {
        // Supondo que um índice secundário tenha sido criado para o CPF
        return cadastroTable.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(cpf).build())) // Utiliza a condição de consulta correta
                .items()
                .stream()
                .findFirst()
                .orElse(null);
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
        return cadastroTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList()); // Coleta os itens em uma lista
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
                .filter(cadastro -> !cadastro.isCertificado()) // Filtra cadastros sem certificado.
                .collect(Collectors.toList());
    }
}

