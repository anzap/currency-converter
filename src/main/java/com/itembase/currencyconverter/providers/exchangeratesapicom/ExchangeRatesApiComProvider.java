package com.itembase.currencyconverter.providers.exchangeratesapicom;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.itembase.currencyconverter.providers.CurrencyConversionRateProvider;

import reactor.core.publisher.Mono;

@Component
public class ExchangeRatesApiComProvider implements CurrencyConversionRateProvider {

  @Override
  public Mono<BigDecimal> conversionRate() {
	System.out.println("ExchangeRatesApiComProvider.conversionRate()");  
    return null;
  }
  
}
