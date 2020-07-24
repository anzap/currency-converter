package com.itembase.currencyconverter.config.errors;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.itembase.currencyconverter.exceptions.BusinessException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

  private HttpStatus status = HttpStatus.BAD_REQUEST;
  private String message = "please provide some message";

  @Override
  public Map<String, Object> getErrorAttributes(
      ServerRequest request, ErrorAttributeOptions options) {

    Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

    Throwable error = getError(request);

    if (error instanceof BusinessException) {
      BusinessException businessException = (BusinessException) error;
      errorAttributes.put("status", businessException.getStatus());
      errorAttributes.put("message", businessException.getMessage());
    }

    if (error instanceof WebExchangeBindException) {
      WebExchangeBindException webExchangeBindException = (WebExchangeBindException) error;
      errorAttributes.put("status", webExchangeBindException.getStatus());

      String message =
          webExchangeBindException
              .getFieldErrors()
              .stream()
              .map(FieldError::getDefaultMessage)
              .collect(Collectors.joining(", "));
      errorAttributes.put("message", message);
    }

    return errorAttributes;
  }
}
