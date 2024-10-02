package br.com.lagoinha.repository;

import br.com.lagoinha.model.Cadastro;
import br.com.lagoinha.model.Presenca;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PresencaRepository {

    private final DynamoDbTable<Presenca> presencaTable;

    public PresencaRepository(DynamoDbEnhancedClient enhancedClient) {
        this.presencaTable = enhancedClient.table("Presenca", TableSchema.fromBean(Presenca.class));
    }

    /**
     * Retorna todas as presenças da tabela "Presenca".
     *
     * @return Lista de presenças.
     */
    public List<Presenca> findAll() {
        return presencaTable.scan().items().stream().collect(Collectors.toList());
    }

    /**
     * Busca presenças por cadastroId.
     *
     * @param cadastroId Cadastro ID.
     * @return Lista de presenças filtradas.
     */
    public List<Presenca> findByCadastroId(String cadastroId) {
        return presencaTable.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(cadastroId).build()))
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public List<Presenca> findByCadastro(Cadastro cadastro) {
        return presencaTable.query(
                        QueryConditional.keyEqualTo(Key.builder()
                                .partitionValue(cadastro.getCpf()) // Aqui você deve usar o valor correspondente à chave de partição
//                                .sortValue(cadastro.getCpf()) // Use o valor correspondente à chave de ordenação
                                .build()))
                .items()
                .stream()
                .collect(Collectors.toList());
    }


    /**
     * Busca presenças por CPF.
     *
     * @param cpf Cpf do cadastro.
     * @return Lista de presenças filtradas.
     */
    public List<Presenca> findByCpf(String cpf) {
        return presencaTable.scan()
                .items()
                .stream()
                .filter(presenca -> presenca.getCpf().equals(cpf))  // Filtra por CPF
                .sorted(Comparator.comparing(Presenca::getAula))  // Ordena por Aula
                .collect(Collectors.toList());
    }


    /**
     * Salva ou atualiza uma presença.
     *
     * @param presenca Objeto presença a ser salvo ou atualizado.
     */
    public void save(Presenca presenca) {
        presencaTable.putItem(presenca);
    }

    /**
     * Exclui uma presença com base no ID.
     *
     * @param id ID da presença a ser excluída.
     */
    public void delete(String id) {
        Key key = Key.builder().partitionValue(id).build();
        presencaTable.deleteItem(key);
    }

    /**
     * Conta o número de presenças de um participante com base no cadastro ID.
     *
     * @param cadastroId Cadastro ID.
     * @return O número de presenças.
     */
    public long countByCadastroId(String cadastroId) {
        return presencaTable.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(cadastroId).build()))
                .items()
                .stream()
                .count();
    }
}
