package com.itembase.currencyconverter.config.errors;

import java.util.Map;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.itembase.currencyconverter.exceptions.BusinessException;

import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

  public GlobalErrorWebExceptionHandler(
      GlobalErrorAttributes g,
      ApplicationContext applicationContext,
      ServerCodecConfigurer serverCodecConfigurer) {
    super(g, new ResourceProperties(), applicationContext);
    super.setMessageWriters(serverCodecConfigurer.getWriters());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(
      final ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {

    final Map<String, Object> errorPropertiesMap =
        getErrorAttributes(request, ErrorAttributeOptions.defaults());

    HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    if (errorPropertiesMap.containsKey("status")) {
      responseStatus = (HttpStatus) errorPropertiesMap.get("status");
    }

    return ServerResponse.status(responseStatus)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(errorPropertiesMap));
  }
}
