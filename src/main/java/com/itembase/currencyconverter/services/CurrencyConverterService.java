package com.itembase.currencyconverter.services;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;

import com.itembase.currencyconverter.dtos.ConversionRequest;
import com.itembase.currencyconverter.dtos.ConversionResponse;
import com.itembase.currencyconverter.providers.CurrencyConversionRateProvider;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CurrencyConverterService {

  private final List<CurrencyConversionRateProvider> providers;

  private final SecureRandom random = new SecureRandom();

  public Mono<ConversionResponse> convert(ConversionRequest request) {
    int randomIndex = random.nextInt(providers.size());
    CurrencyConversionRateProvider rateProvider = providers.get(randomIndex);

    return rateProvider
        .conversionRate(request.getFrom(), request.getTo())
        .onErrorResume(
            e ->
                providers
                    .get(providers.size() - 1 - randomIndex)
                    .conversionRate(request.getFrom(), request.getTo()))
        .map(r -> conversionResponseMapper.apply(request, r));
  }

  BiFunction<ConversionRequest, BigDecimal, ConversionResponse> conversionResponseMapper =
      new BiFunction<ConversionRequest, BigDecimal, ConversionResponse>() {

        @Override
        public ConversionResponse apply(ConversionRequest request, BigDecimal rate) {
          return new ConversionResponse(
              request.getFrom(),
              request.getTo(),
              request.getAmount(),
              request.getAmount().multiply(rate));
        }
      };
}