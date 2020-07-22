package com.itembase.currencyconverter.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itembase.currencyconverter.dtos.ConversionRequest;
import com.itembase.currencyconverter.dtos.ConversionResponse;
import com.itembase.currencyconverter.services.CurrencyConverterService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/currency")
@Validated
@RequiredArgsConstructor
public class CurrencyConverterController {

  private final CurrencyConverterService service;

  @PostMapping(
      path = "/convert",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<ConversionResponse>> convert(
      @RequestBody @Valid ConversionRequest request) {
    return service
        .convert(request)
        .map(r -> ResponseEntity.ok(r))
        .onErrorReturn(ResponseEntity.status(503).build());
  }
}
