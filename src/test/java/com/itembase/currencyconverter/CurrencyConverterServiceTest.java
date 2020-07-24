package com.itembase.currencyconverter;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.itembase.currencyconverter.config.AppConfig.RandomGenerator;
import com.itembase.currencyconverter.dtos.ConversionRequest;
import com.itembase.currencyconverter.dtos.ConversionResponse;
import com.itembase.currencyconverter.providers.exchangeratesapicom.ExchangeRatesApiComProvider;
import com.itembase.currencyconverter.providers.exchangeratesapiio.ExchangeRatesApiIOProvider;
import com.itembase.currencyconverter.services.CurrencyConverterService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrencyConverterServiceTest {

  @MockBean private ExchangeRatesApiComProvider exchangeRatesApiComProvider;

  @MockBean private ExchangeRatesApiIOProvider exchangeRatesApiIOProvider;

  @MockBean private RandomGenerator randomGenerator;

  private CurrencyConverterService service;

  @BeforeEach
  void setup() {
    this.service =
        new CurrencyConverterService(
            List.of(exchangeRatesApiComProvider, exchangeRatesApiIOProvider), randomGenerator);
  }

  @Test
  void shouldReturnConversionResultFromExchangeRatesApiCom() {
    ConversionRequest request =
        ConversionRequest.builder().from("USD").to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    when(randomGenerator.random(2)).thenReturn(0);

    when(exchangeRatesApiComProvider.conversionRate(request.getFrom(), request.getTo()))
        .thenReturn(Mono.just(Optional.of(BigDecimal.valueOf(0.9))));

    Mono<ConversionResponse> result = service.convert(request);

    StepVerifier.create(result)
        .expectNext(
            new ConversionResponse("USD", "EUR", BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.9)))
        .verifyComplete();

    verify(exchangeRatesApiComProvider, times(1))
        .conversionRate(request.getFrom(), request.getTo());
    verifyNoInteractions(exchangeRatesApiIOProvider);
  }

  @Test
  void shouldReturnConversionResultFromExchangeRatesApiIO() {
    ConversionRequest request =
        ConversionRequest.builder().from("USD").to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    when(randomGenerator.random(2)).thenReturn(1);

    when(exchangeRatesApiIOProvider.conversionRate(request.getFrom(), request.getTo()))
        .thenReturn(Mono.just(Optional.of(BigDecimal.valueOf(0.9))));

    Mono<ConversionResponse> result = service.convert(request);

    StepVerifier.create(result)
        .expectNext(
            new ConversionResponse("USD", "EUR", BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.9)))
        .verifyComplete();

    verify(exchangeRatesApiIOProvider, times(1)).conversionRate(request.getFrom(), request.getTo());
    verifyNoInteractions(exchangeRatesApiComProvider);
  }

  @Test
  void shouldFallbackToNextProviderOnError() {
    ConversionRequest request =
        ConversionRequest.builder().from("USD").to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    when(randomGenerator.random(2)).thenReturn(0);

    when(exchangeRatesApiComProvider.conversionRate(request.getFrom(), request.getTo()))
        .thenReturn(Mono.error(new RuntimeException()));

    when(exchangeRatesApiIOProvider.conversionRate(request.getFrom(), request.getTo()))
        .thenReturn(Mono.just(Optional.of(BigDecimal.valueOf(0.9))));

    Mono<ConversionResponse> result = service.convert(request);

    StepVerifier.create(result)
        .expectNext(
            new ConversionResponse("USD", "EUR", BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.9)))
        .verifyComplete();
    
    verify(exchangeRatesApiComProvider, times(1)).conversionRate(request.getFrom(), request.getTo());
    verify(exchangeRatesApiIOProvider, times(1)).conversionRate(request.getFrom(), request.getTo());
  }
}
