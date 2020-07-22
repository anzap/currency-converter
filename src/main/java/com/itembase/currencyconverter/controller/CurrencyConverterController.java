package com.itembase.currencyconverter.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itembase.currencyconverter.dtos.ConversionRequest;
import com.itembase.currencyconverter.dtos.ConversionResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/currency")
@Validated
public class CurrencyConverterController {
	
	@PostMapping(path = "/convert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public Mono<ConversionResponse> convert(@RequestBody @Valid ConversionRequest request) {
		return Mono.just(new ConversionResponse("USD", "EUR", new BigDecimal(1.0), new BigDecimal(0.9)));
	}
}
