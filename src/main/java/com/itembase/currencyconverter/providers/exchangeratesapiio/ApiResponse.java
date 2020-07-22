package com.itembase.currencyconverter.providers.exchangeratesapiio;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
class ApiResponse {
  private String base;
  private String date;
  private Map<String, BigDecimal> rates;
}
