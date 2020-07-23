package com.itembase.currencyconverter.dtos;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConversionRequest {
  @NotBlank(message = "Currency to convert from must be provided")
  @Pattern(regexp = "^([A-Z]){3}$", message = "Invalid currency symbol provided")
  private String from;

  @NotBlank(message = "Currency to convert to must be provided")
  @Pattern(regexp = "^([A-Z]){3}$", message = "Invalid currency symbol provided")
  private String to;

  @NotNull(message = "Amount to convert must be provided")
  private BigDecimal amount;
}
