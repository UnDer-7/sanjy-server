package br.com.gorillaroxo.sanjy.server.core.ports.driven;

import org.springframework.web.multipart.MultipartFile;

public interface ExtractTextFromFileGateway {

    String extract(final MultipartFile file);

    boolean accept(final MultipartFile file);

}
