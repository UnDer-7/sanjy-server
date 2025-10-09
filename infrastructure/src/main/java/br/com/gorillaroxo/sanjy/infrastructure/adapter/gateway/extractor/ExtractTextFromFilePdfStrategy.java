package br.com.gorillaroxo.sanjy.infrastructure.adapter.gateway.extractor;

import br.com.gorillaroxo.sanjy.core.ports.driven.ExtractTextFromFileGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtractTextFromFilePdfStrategy implements ExtractTextFromFileGateway {

    @Override
    public String extract(final MultipartFile file) {
        log.info("Extracting text from PDF file");
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);
        } catch (final IOException e) {
            log.warn(
                "Error extracting text from file. Name: {} | ContentType: {} | Size: {} bytes",
                file.getName(),
                file.getContentType(),
                file.getSize(),
                e);

            // todo: Colocar exceptions
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean accept(final MultipartFile file) {
        return MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType());
    }

}
