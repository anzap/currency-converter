package com.itembase.currencyconverter;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.itembase.currencyconverter.controllers.CurrencyConverterController;
import com.itembase.currencyconverter.dtos.ConversionRequest;
import com.itembase.currencyconverter.dtos.ConversionResponse;
import com.itembase.currencyconverter.services.CurrencyConverterService;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
public class CurrencyConverterControllerTest {
  private WebTestClient client;

  @MockBean private CurrencyConverterService service;

  @BeforeEach
  void beforeEach() {
    this.client =
        WebTestClient.bindToController(new CurrencyConverterController(service))
            .configureClient()
            .baseUrl("/currency")
            .build();
  }

  @Test
  void validPayload() {

    ConversionRequest request =
        ConversionRequest.builder().from("USD").to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    when(service.convert(request))
        .thenReturn(
            Mono.just(
                new ConversionResponse(
                    "USD", "EUR", BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.9))));

    this.client
        .post()
        .uri("/convert")
        .body(Mono.just(request), ConversionRequest.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.from")
        .isEqualTo("USD")
        .jsonPath("$.to")
        .isEqualTo("EUR")
        .jsonPath("$.amount")
        .isEqualTo("1.0")
        .jsonPath("$.converted")
        .isEqualTo("0.9");

    verify(service, times(1)).convert(request);
  }

  @Test
  void fromCurrencyNotProvided() {

    ConversionRequest request =
        ConversionRequest.builder().to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    this.client
        .post()
        .uri("/convert")
        .body(Mono.just(request), ConversionRequest.class)
        .exchange()
        .expectStatus()
        .isBadRequest();

    verifyNoInteractions(service);
  }

  @Test
  void toCurrencyNotProvided() {

    ConversionRequest request =
        ConversionRequest.builder().from("USD").amount(BigDecimal.valueOf(1.0)).build();

    this.client
        .post()
        .uri("/convert")
        .body(Mono.just(request), ConversionRequest.class)
        .exchange()
        .expectStatus()
        .isBadRequest();

    verifyNoInteractions(service);
  }

  @Test
  void amountNotProvided() {

    ConversionRequest request = ConversionRequest.builder().from("USD").to("EUR").build();

    this.client
        .post()
        .uri("/convert")
        .body(Mono.just(request), ConversionRequest.class)
        .exchange()
        .expectStatus()
        .isBadRequest();

    verifyNoInteractions(service);
  }
}
