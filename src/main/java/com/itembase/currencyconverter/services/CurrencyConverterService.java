package com.itembase.currencyconverter.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;

import com.itembase.currencyconverter.config.AppConfig.RandomGenerator;
import com.itembase.currencyconverter.dtos.ConversionRequest;
import com.itembase.currencyconverter.dtos.ConversionResponse;
import com.itembase.currencyconverter.providers.CurrencyConversionRateProvider;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CurrencyConverterService {

  private final List<CurrencyConversionRateProvider> providers;

  private final RandomGenerator randomGenerator;

  public Mono<ConversionResponse> convert(ConversionRequest request) {
    int randomIndex = randomGenerator.random(providers.size());
    CurrencyConversionRateProvider rateProvider = providers.get(randomIndex);

    return rateProvider
        .conversionRate(request.getFrom(), request.getTo())
        .onErrorResume(
            e ->
                providers
                    .get(providers.size() - 1 - randomIndex)
                    .conversionRate(request.getFrom(), request.getTo()))
        .log()
        .map(r -> conversionResponseMapper.apply(request, r));
  }

  BiFunction<ConversionRequest, Optional<BigDecimal>, ConversionResponse> conversionResponseMapper =
      new BiFunction<ConversionRequest, Optional<BigDecimal>, ConversionResponse>() {

        @Override
        public ConversionResponse apply(ConversionRequest request, Optional<BigDecimal> rate) {
          if (rate.isEmpty()) {
        	  throw new RuntimeException("No rate provided!");
          }

          return new ConversionResponse(
              request.getFrom(),
              request.getTo(),
              request.getAmount(),
              request.getAmount().multiply(rate.get()).stripTrailingZeros());
        }
      };
}
