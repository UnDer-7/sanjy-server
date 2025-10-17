package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.extractor;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.ExtractTextFromFileGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtractTextFromFileTextPlainStrategy implements ExtractTextFromFileGateway {

    @Override
    public String extract(final MultipartFile file) {
        log.info("Extracting text from TextPlain file");

        try {
            byte[] bytes = file.getBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error extracting text from file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to extract text from file", e);
        }
    }

    @Override
    public boolean accept(final MultipartFile file) {
        return MediaType.TEXT_MARKDOWN_VALUE.equals(file.getContentType()) || MediaType.TEXT_PLAIN_VALUE.equals(file.getContentType());
    }

}
