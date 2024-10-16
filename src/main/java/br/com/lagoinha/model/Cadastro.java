package br.com.lagoinha.model;

import br.com.lagoinha.utils.FormatterUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Cadastro {

    private String id;
    private String cpf;
    private String nomeCompleto;
    private String sexo;
    private String email;
    private String celular;
    private boolean certificado;
    private List<Presenca> presencas;
    private long totalPresencas;

    @DynamoDbPartitionKey
    public String getId() {
        return id;

    }

    // Método getter para o CPF formatado
    public String getCpfFormatado() {
        return FormatterUtils.formatString(cpf, "###.###.###-##");
    }

    // Método getter para o celular formatado
    public String getCelularFormatado() {
        return FormatterUtils.formatString(celular, "(##) #####-####");
    }

}