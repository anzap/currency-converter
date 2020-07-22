package com.itembase.currencyconverter.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itembase.currencyconverter.providers.CurrencyConversionRateProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrencyConverterService {
	
	private final List<CurrencyConversionRateProvider> providers;
	
	public void convert() {
		providers.forEach(p -> p.conversionRate());
	}
	
	
}
