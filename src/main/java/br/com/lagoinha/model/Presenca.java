package br.com.lagoinha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Presenca {
    private String id;
    private String cpf;
    private String cadastroId;
    private String timestamp;
    private String aula;
    private double latitude;
    private double longitude;

    @DynamoDbPartitionKey
    public String getCpf() {
        return cpf;

    }
}
