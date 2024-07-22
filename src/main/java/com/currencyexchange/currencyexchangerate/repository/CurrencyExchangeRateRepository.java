package com.currencyexchange.currencyexchangerate.repository;

import com.currencyexchange.currencyexchangerate.model.CurrencyExchangeRateModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyExchangeRateRepository extends JpaRepository<CurrencyExchangeRateModel, Long> {
    CurrencyExchangeRateModel findFirstByCurrencyToOrderByTimestampDesc(String currencyTo);
    List<CurrencyExchangeRateModel> findByTimestampBetween(LocalDate start, LocalDate end);
}
