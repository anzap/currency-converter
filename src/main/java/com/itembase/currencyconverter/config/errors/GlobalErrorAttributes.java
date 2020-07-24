package com.itembase.currencyconverter.config.errors;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
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
	    errorAttributes.put("status", ((BusinessException) error).getStatus());
	    errorAttributes.put("message", ((BusinessException) error).getMessage());
    }

    return errorAttributes;
  }
}
