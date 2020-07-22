package com.itembase.currencyconverter.providers;

import java.math.BigDecimal;

import reactor.core.publisher.Mono;

public interface CurrencyConversionRateProvider {
	Mono<BigDecimal> conversionRate();
}
