package br.com.lagoinha.repository;

import br.com.lagoinha.model.Presenca;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PresencaRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Presenca> presencaTable;

    public PresencaRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
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
     * Busca presenças por CPF.
     * Retorna uma lista de presenças filtrada pelo CPF e ordenada por timestamp.
     *
     * @param cpf CPF do participante.
     * @return Lista de presenças do participante.
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
     * Conta o número de presenças de um participante com base no CPF.
     *
     * @param cpf CPF do participante.
     * @return O número de presenças.
     */
    public long countByCpf(String cpf) {
        return presencaTable.scan()
                .items()
                .stream()
                .filter(presenca -> presenca.getCpf().equals(cpf))
                .count();
    }
}
