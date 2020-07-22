package com.itembase.currencyconverter.providers.exchangeratesapicom;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
class ApiResponse {
  private String base;
  private String date;
  private Long timeLastUpdated;
  private Map<String, BigDecimal> rates;
}
