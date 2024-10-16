package br.com.lagoinha.service;

import br.com.lagoinha.model.Cadastro;
import br.com.lagoinha.repository.CadastroRepository;
import br.com.lagoinha.utils.EmailSender;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
@Service
public class CertificadoService {

    final String pdfFilePath = "src/main/resources/certificado/start_certificado.pdf";
    final String outputFilePath = "src/main/resources/certificado/start_certificado_1.pdf";

    private final CadastroRepository cadastroRepository;

    public void enviarCertificadoPorEmail(String cpf) {
        Cadastro cadastro = cadastroRepository.findByCpf(cpf);
        if (cadastro == null) {
            throw new IllegalArgumentException("CPF não encontrado");
        }
        gerarPDF(cadastro);
        enviarEmail(cadastro);
        cadastro.setCertificado(true);
        cadastroRepository.save(cadastro);
    }

    private void enviarEmail(Cadastro cadastro) {
        // Enviar o PDF gerado por e-mail
        EmailSender.sendEmailWithAttachment(cadastro.getEmail(),
                "Cerficado de Conclusão de Curso Start",
                "Corpo do E-mail", outputFilePath);
    }

    private void gerarPDF(Cadastro cadastro) {
        String data = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        try {
            // Carregar o PDF existente
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            String nome = cadastro.getNomeCompleto();
            // Iterar sobre as páginas do documento
            for (PDPage page : document.getPages()) {
                System.out.println("Page Width: " + page.getMediaBox().getWidth());
                System.out.println("Page Height: " + page.getMediaBox().getHeight());

                PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);

                // Adicionar o nome e a data no PDF
                contentStream.beginText();
                PDType1Font font = PDType1Font.HELVETICA_BOLD;  // Definindo a fonte
                float fontSize = 12; // Tamanho da fonte
                contentStream.setFont(font, fontSize); // Configurar fonte e tamanho
                contentStream.setNonStrokingColor(0f, 0f, 0f); // Cor preta

                // Calcular a largura do texto
                float textWidth = font.getStringWidth(nome) / 1000 * fontSize; // A largura do texto em pontos
                float pageWidth = page.getMediaBox().getWidth(); // Largura total da página

                // Calcular a posição x para centralizar o texto
                float x = (pageWidth - textWidth) / 2; // Posição x centralizada
                float y = 175; // Posição y (ajuste conforme necessário)

                System.out.println("Page x: " + (x - 60));
                System.out.println("Page y: " + (y-94));

                contentStream.newLineAtOffset(x, y); // Ajustando a posição onde o texto será adicionado
                contentStream.showText(nome);
                contentStream.endText();

                // Adicionar a data em uma posição fixa
                float fixedX = 174.732f; // Posição fixa x da data
                float fixedY = 81.0f; // Posição fixa y da data
                contentStream.beginText();
                contentStream.setFont(font, fontSize); // Usando a mesma fonte e tamanho
                contentStream.setNonStrokingColor(0f, 0f, 0f); // Cor preta
                contentStream.newLineAtOffset(fixedX, fixedY); // Setting the fixed position
                contentStream.showText(data);
                contentStream.endText();

                contentStream.close();
            }

            // Salvar o PDF modificado
            document.save(outputFilePath);
            document.close();

            System.out.println("Texto adicionado com sucesso ao PDF!");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
