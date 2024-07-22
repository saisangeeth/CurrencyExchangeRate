package com.currencyexchange.currencyexchangerate.repository;

import com.currencyexchange.currencyexchangerate.model.CurrencyExchangeRateModel;
import com.currencyexchange.currencyexchangerate.model.UserCurrencyExchangeRateModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserCurrencyExchangeRepository extends JpaRepository<UserCurrencyExchangeRateModel, Long> {
    List<UserCurrencyExchangeRateModel> findByTimestampBetween(LocalDate start, LocalDate end);
}
