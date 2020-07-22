package com.itembase.currencyconverter.providers.exchangeratesapiio;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.itembase.currencyconverter.providers.CurrencyConversionRateProvider;

import reactor.core.publisher.Mono;

@Component
public class ExchangeRatesApiIOProvider implements CurrencyConversionRateProvider {

  @Override
  public Mono<BigDecimal> conversionRate() {
    System.out.println("ExchangeRatesApiIOProvider.conversionRate()");
    
    return null;
  }
	
}
