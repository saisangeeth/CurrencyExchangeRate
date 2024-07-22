package com.currencyexchange.currencyexchangerate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class UserCurrencyExchangeRateModel {
    // This is a POJO class
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String currencyFrom;
    private String currencyTo;
    private double rate;
    private LocalDate timestamp;

    public UserCurrencyExchangeRateModel() {
    }

    public UserCurrencyExchangeRateModel( String currencyFrom, String currencyTo, double rate, LocalDate timestamp) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.rate = rate;
        this.timestamp = timestamp;
    }


}
