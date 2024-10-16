package br.com.lagoinha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Configuration
public class DynamoDbConfig {

    private final DynamoDbClient dynamoDbClient;

    public DynamoDbConfig() {
        // Inicialize o cliente DynamoDb com a região apropriada
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1) // Defina a região aqui
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient) // Use o cliente já construído
            .build();
    }

    public void createTables() {
        CreateTableRequest cadastroTable = CreateTableRequest.builder()
                .tableName("cadastros")
                .keySchema(
                        KeySchemaElement.builder().attributeName("cpf").keyType("HASH").build()
//                        KeySchemaElement.builder().attributeName("cpf").keyType("RANGE").build()
                )
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("cpf").attributeType(ScalarAttributeType.S).build()         // Tipo String para id
//                        AttributeDefinition.builder().attributeName("cpf").attributeType(ScalarAttributeType.S).build()   // Tipo String para timestamp (ou N para número se for timestamp numérico)
                )
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                .build();

        CreateTableRequest presencaTable = CreateTableRequest.builder()
                .tableName("presencas")
                .keySchema(
                        KeySchemaElement.builder().attributeName("cadastroId").keyType("HASH").build(),
                        KeySchemaElement.builder().attributeName("aula").keyType("RANGE").build())
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("cadastroId").attributeType(ScalarAttributeType.S).build(),         // Tipo String para id
                        AttributeDefinition.builder().attributeName("aula").attributeType(ScalarAttributeType.S).build()   // Tipo String para timestamp (ou N para número se for timestamp numérico)
                )
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                .build();

        dynamoDbClient.createTable(cadastroTable);
        dynamoDbClient.createTable(presencaTable);
    }
}