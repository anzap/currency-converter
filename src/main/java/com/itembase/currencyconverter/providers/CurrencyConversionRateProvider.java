package com.itembase.currencyconverter.providers;

import java.math.BigDecimal;
import java.util.Optional;

import reactor.core.publisher.Mono;

public interface CurrencyConversionRateProvider {
  Mono<Optional<BigDecimal>> conversionRate(String from, String to);
}
