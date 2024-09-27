package br.com.lagoinha.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Converter {

    public static String convertToISO(String googleSheetTimestamp) {
        // Defina o formato do timestamp vindo do Google Sheets (ajuste conforme o formato que está na planilha)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss");

        // Converta a string para LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(googleSheetTimestamp, formatter);

        // Converta para o formato ISO 8601
        return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

}
