package com.itembase.currencyconverter.dtos;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConversionResponse {
  private String from;
  private String to;
  private BigDecimal amount;
  private BigDecimal converted;
}
