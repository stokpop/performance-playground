package nl.stokpop.server;

import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MakePdfService {

    public byte[] makePdfHelloWorldByteArray() throws IOException {

        // based on the cookbook: https://pdfbox.apache.org/1.8/cookbook/documentcreation.html

        // Create a document and add a page to it and ensure that the document is properly closed
        try (PDDocument document = new PDDocument()) {
            createHelloWorldPdf(document);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                // Save the results
                document.save(outputStream);
                return outputStream.toByteArray();
            }
        }
    }

    public void makePdfHelloWorldOutputStream(OutputStream outputStream) throws IOException {

        // based on the cookbook: https://pdfbox.apache.org/1.8/cookbook/documentcreation.html

        // Create a document and add a page to it and ensure that the document is properly closed
        try (PDDocument document = new PDDocument()) {
            createHelloWorldPdf(document);

            // Save the results
            document.save(outputStream);
        }
    }

    @SneakyThrows
    private void createHelloWorldPdf(PDDocument document) {

        for (int i = 0; i < 10; i++) {
            addHelloWorldPage(document);
        }
    }

    private void addHelloWorldPage(PDDocument document) throws IOException {
        PDPage page = new PDPage();
        document.addPage(page);

        // Create a new font object selecting one of the PDF base fonts
        PDFont font = PDType1Font.HELVETICA_BOLD;

        // Start a new content stream which will "hold" the to be created content
        // Make sure that the content stream is closed:
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            ThreadLocalRandom localRandom = ThreadLocalRandom.current();

            // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
            contentStream.beginText();
            int fontSize = localRandom.nextInt(10, 50);
            contentStream.setFont(font, fontSize);
            int tx = localRandom.nextInt(30,200);
            int ty = localRandom.nextInt(70, 500);
            contentStream.moveTextPositionByAmount(tx, ty);
            contentStream.drawString("Hello World");
            contentStream.endText();

        }
    }
}
