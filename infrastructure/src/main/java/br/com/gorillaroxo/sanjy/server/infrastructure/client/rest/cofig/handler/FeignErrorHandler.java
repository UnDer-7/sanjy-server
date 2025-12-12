package br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.cofig.handler;

import br.com.gorillaroxo.sanjy.server.core.exception.BusinessException;
import feign.Response;

public interface FeignErrorHandler {

    BusinessException handle(Response response, String responseBodyJson);

    boolean canHandle(Response response, String responseBodyJson);

}
