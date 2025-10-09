package br.com.gorillaroxo.sanjy.core.service;

import br.com.gorillaroxo.sanjy.core.ports.driven.ExtractTextFromFileGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtractTextFromFileService {

    private final List<ExtractTextFromFileGateway> extractors;

    public String execute(final MultipartFile file) {
        return extractors.stream()
            .filter(e -> e.accept(file))
            .findFirst()
            .orElseThrow(() -> {
                // todo: Colocar exceptions
                return new RuntimeException("Gateway to extract text from file not found.");
            })
            .extract(file);
    }
}
