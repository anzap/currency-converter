package com.itembase.currencyconverter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.itembase.currencyconverter.config.AppConfig.RandomGenerator;
import com.itembase.currencyconverter.dtos.ConversionRequest;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class CurrencyConverterIntegrationTest {

  @Autowired private WebTestClient client;

  private WireMockServer wireMockServer;

  @Profile("test")
  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    public RandomGenerator randomGenerator() {
      return new RandomGenerator() {

        @Override
        public int random(int bound) {
          return 0;
        }
      };
    }
  }

  @BeforeAll
  void setup() {

    wireMockServer = new WireMockServer(wireMockConfig().port(9999));
    wireMockServer.start();
    configureFor("localhost", wireMockServer.port());
  }

  @AfterAll
  public void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void conversionOk() {

    stubFor(
        get(urlEqualTo("/exchangeratesapicom/USD"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"base\":\"USD\",\"date\":\"2020-07-22\",\"time_last_updated\":1595376245,\"rates\":{\"USD\":1,\"EUR\":0.87}}")));

    stubFor(
        get(urlEqualTo("/exchangeratesapiio?base=USD&symbols=EUR"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"rates\":{\"EUR\":0.9},\"base\":\"USD\",\"date\":\"2020-07-22\"}")));

    ConversionRequest request =
        ConversionRequest.builder().from("USD").to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    this.client
        .post()
        .uri("/currency/convert")
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
        .isEqualTo("0.87");
  }

  @Test
  void providerFallbackBadRequest() {

    stubFor(get(urlEqualTo("/exchangeratesapicom/USD")).willReturn(aResponse().withStatus(400)));

    stubFor(
        get(urlEqualTo("/exchangeratesapiio?base=USD&symbols=EUR"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"rates\":{\"EUR\":0.9},\"base\":\"USD\",\"date\":\"2020-07-22\"}")));

    ConversionRequest request =
        ConversionRequest.builder().from("USD").to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    this.client
        .post()
        .uri("/currency/convert")
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
  }

  @Test
  void providerFallbackInternalServerError() {

    stubFor(get(urlEqualTo("/exchangeratesapicom/USD")).willReturn(aResponse().withStatus(500)));

    stubFor(
        get(urlEqualTo("/exchangeratesapiio?base=USD&symbols=EUR"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"rates\":{\"EUR\":0.9},\"base\":\"USD\",\"date\":\"2020-07-22\"}")));

    ConversionRequest request =
        ConversionRequest.builder().from("USD").to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    this.client
        .post()
        .uri("/currency/convert")
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
  }

  @Test
  void providerFallbackTimeout() {

    stubFor(
        get(urlEqualTo("/exchangeratesapicom/USD"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withFixedDelay(1000) // Adding 1 second delay here, more than 500 millis timeout setting defined in application-test.properties
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"base\":\"USD\",\"date\":\"2020-07-22\",\"time_last_updated\":1595376245,\"rates\":{\"USD\":1,\"EUR\":0.87}}")));

    stubFor(
        get(urlEqualTo("/exchangeratesapiio?base=USD&symbols=EUR"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"rates\":{\"EUR\":0.9},\"base\":\"USD\",\"date\":\"2020-07-22\"}")));

    ConversionRequest request =
        ConversionRequest.builder().from("USD").to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    this.client
        .post()
        .uri("/currency/convert")
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
  }
  
  @Test
  void allProvidersFAilure() {

    stubFor(
        get(urlEqualTo("/exchangeratesapicom/USD"))
            .willReturn(
                aResponse()
                    .withStatus(500)));

    stubFor(
        get(urlEqualTo("/exchangeratesapiio?base=USD&symbols=EUR"))
            .willReturn(
                aResponse()
                    .withStatus(500)));

    ConversionRequest request =
        ConversionRequest.builder().from("USD").to("EUR").amount(BigDecimal.valueOf(1.0)).build();

    this.client
        .post()
        .uri("/currency/convert")
        .body(Mono.just(request), ConversionRequest.class)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_GATEWAY);
  }
}
