package com.itembase.currencyconverter.providers.exchangeratesapicom;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.itembase.currencyconverter.config.AppProperties;
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
public class ExchangeRatesApiComProvider implements CurrencyConversionRateProvider {

  private final AppProperties config;

  @Override
  public Mono<Optional<BigDecimal>> conversionRate(String from, String to) {

    return client()
        .get()
        .uri(uriBuilder -> uriBuilder.path("/{symbol}").build(from))
        .accept(APPLICATION_JSON)
        .retrieve()
        .bodyToMono(ApiResponse.class)
        .log()
        .map(r -> Optional.ofNullable(r.getRates().get(to)));
  }

  private WebClient client() {
    TcpClient tcpClient =
        TcpClient.create()
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                config.getProviders().getExchangeRatesApiCom().getTimeout().intValue())
            .doOnConnected(
                connection -> {
                  connection.addHandlerLast(
                      new ReadTimeoutHandler(
                          config.getProviders().getExchangeRatesApiCom().getTimeout(),
                          TimeUnit.MILLISECONDS));
                  connection.addHandlerLast(
                      new WriteTimeoutHandler(
                          config.getProviders().getExchangeRatesApiCom().getTimeout(),
                          TimeUnit.MILLISECONDS));
                });
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
        .baseUrl(config.getProviders().getExchangeRatesApiCom().getBaseurl())
        .build();
  }
}
