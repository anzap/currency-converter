package com.itembase.currencyconverter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ConfigurationProperties(prefix = "app")
@ConstructorBinding
@Getter
@RequiredArgsConstructor
public class AppConfig {
	
	private final ProvidersConfig providers;
	
	@Getter
	@RequiredArgsConstructor
	public static class ProvidersConfig {
		private final ExchangeRatesApiIOConfig exchangeRatesApiIO;
		
		private final ExchangeRatesApiComConfig exchangeRatesApiCom;
		
	}
	
	@Getter
	@RequiredArgsConstructor
	public static class ExchangeRatesApiIOConfig {
		private final String baseurl;
	}
	
	@Getter
	@RequiredArgsConstructor
	public static class ExchangeRatesApiComConfig {
		private final String baseurl;
	}
}
