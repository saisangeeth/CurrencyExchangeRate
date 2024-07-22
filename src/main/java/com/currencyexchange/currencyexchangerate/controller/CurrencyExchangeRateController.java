package com.currencyexchange.currencyexchangerate.controller;

import com.currencyexchange.currencyexchangerate.model.CurrencyExchangeRateModel;
import com.currencyexchange.currencyexchangerate.model.UserCurrencyExchangeRateModel;
import com.currencyexchange.currencyexchangerate.service.CurrencyExchangeRateService;
import com.currencyexchange.currencyexchangerate.service.CurrencyExchangeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
public class CurrencyExchangeRateController {
    private final CurrencyExchangeService currencyExchangeService;
    private final CurrencyExchangeRateService currencyExchangeRateService;

    public CurrencyExchangeRateController(CurrencyExchangeService currencyExchangeService, CurrencyExchangeRateService currencyExchangeRateService) {
        this.currencyExchangeService = currencyExchangeService;
        this.currencyExchangeRateService = currencyExchangeRateService;
    }

    @GetMapping
    public List<UserCurrencyExchangeRateModel> getExchangeRates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return currencyExchangeService.getExchangeRates(startDate, endDate);
    }

    @GetMapping("/export")
    public void exportExchangeRates(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                    @RequestParam String format, HttpServletResponse response) {
        currencyExchangeService.exportExchangeRates(startDate, endDate, format, response);
    }

    @GetMapping("/calculate")
    public List<Double> calculateExchangeRate(@RequestParam String currencyA, @RequestParam String currencyB) {
        return currencyExchangeRateService.calculateExchangeRate(currencyA, currencyB);
    }
}
