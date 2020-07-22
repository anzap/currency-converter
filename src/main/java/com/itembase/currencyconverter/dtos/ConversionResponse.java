package com.itembase.currencyconverter.dtos;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class ConversionResponse {
  private String from;
  private String to;
  private BigDecimal amount;
  private BigDecimal converted;
}
