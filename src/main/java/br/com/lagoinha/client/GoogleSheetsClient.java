package br.com.lagoinha.client;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsClient {

    private static final String APPLICATION_NAME = "Google Sheets API Java";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "src/main/java/br/com/lagoinha/config/credentials.json";

    private Sheets sheetsService;

    /**
     * Construtor que inicializa o serviço Google Sheets.
     *
     * @throws GeneralSecurityException em caso de falha de segurança
     * @throws IOException em caso de erro de entrada/saída
     */
    public GoogleSheetsClient() throws GeneralSecurityException, IOException {
        this.sheetsService = initializeSheetsService();
    }

    /**
     * Inicializa o serviço Google Sheets usando credenciais do arquivo JSON.
     *
     * @return Instância do serviço Google Sheets
     * @throws GeneralSecurityException em caso de falha de segurança
     * @throws IOException em caso de erro de entrada/saída
     */
    private Sheets initializeSheetsService() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY));

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Obtém os dados da planilha especificada.
     *
     * @param spreadsheetId ID da planilha
     * @param range O intervalo de células a ser lido
     * @return Lista de listas de objetos representando os dados da planilha
     * @throws IOException em caso de erro de entrada/saída
     */
    public List<List<Object>> getSpreadsheetData(String spreadsheetId, String range) throws IOException {
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        return response.getValues();
    }

}
