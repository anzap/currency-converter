package com.itembase.currencyconverter.providers.exchangeratesapiio;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.itembase.currencyconverter.config.AppProperties;
import com.itembase.currencyconverter.exceptions.BusinessException;
import com.itembase.currencyconverter.providers.CurrencyConversionRateProvider;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Component
@RequiredArgsConstructor
public class ExchangeRatesApiIOProvider implements CurrencyConversionRateProvider {

  private final AppProperties config;

  @Override
  public Mono<Optional<BigDecimal>> conversionRate(String from, String to) {

    return client()
        .get()
        .uri(uriBuilder -> uriBuilder.queryParam("base", from).queryParam("symbols", to).build())
        .accept(APPLICATION_JSON)
        .retrieve()
        .onStatus(
            HttpStatus::is4xxClientError,
            response ->
                Mono.error(
                    new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "No conversion rate could be calculated for provided currencies!")))
        .onStatus(
                HttpStatus::is5xxServerError,
                response ->
                    Mono.error(
                        new BusinessException(
                            HttpStatus.BAD_GATEWAY,
                            "Rate provider connection failing!")))
        .bodyToMono(ApiResponse.class)
        .log()
        .map(r -> Optional.ofNullable(r.getRates().get(to)));
  }

  private WebClient client() {
    TcpClient tcpClient =
        TcpClient.create()
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                config.getProviders().getExchangeRatesApiIO().getTimeout().intValue())
            .doOnConnected(
                connection -> {
                  connection.addHandlerLast(
                      new ReadTimeoutHandler(
                          config.getProviders().getExchangeRatesApiIO().getTimeout(),
                          TimeUnit.MILLISECONDS));
                  connection.addHandlerLast(
                      new WriteTimeoutHandler(
                          config.getProviders().getExchangeRatesApiIO().getTimeout(),
                          TimeUnit.MILLISECONDS));
                });
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
        .baseUrl(config.getProviders().getExchangeRatesApiIO().getBaseurl())
        .build();
  }
}
