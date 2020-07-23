package com.itembase.currencyconverter.config;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public RandomGenerator randomGenerator() {
    return new RandomGenerator() {

      SecureRandom random = new SecureRandom();

      @Override
      public int random(int bound) {
        return random.nextInt(bound);
      }
    };
  }

  public interface RandomGenerator {
    int random(int bound);
  }
}
