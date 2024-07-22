package com.currencyexchange.currencyexchangerate.service;

import com.currencyexchange.currencyexchangerate.model.CurrencyExchangeRateModel;
import com.currencyexchange.currencyexchangerate.model.UserCurrencyExchangeRateModel;
import com.currencyexchange.currencyexchangerate.repository.CurrencyExchangeRateRepository;
import com.currencyexchange.currencyexchangerate.repository.UserCurrencyExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyExchangeRateService {
    @Value("${exchangerates.api.url}")
    private String apiUrl;

    private final LocalDate timestamp = LocalDate.now();

    @Autowired
    private final RestTemplate restTemplate;
    private final CurrencyExchangeRateRepository exchangeRateRepository;
    private final UserCurrencyExchangeRepository userCurrencyExchangeRepository;

    public CurrencyExchangeRateService(RestTemplate restTemplate,
                                       CurrencyExchangeRateRepository exchangeRateRepository, UserCurrencyExchangeRepository userCurrencyExchangeRepository) {
        this.restTemplate = restTemplate;
        this.exchangeRateRepository = exchangeRateRepository;
        this.userCurrencyExchangeRepository = userCurrencyExchangeRepository;
    }

    @Scheduled(fixedRate = 3600000)
    public void fetchAndStoreExchangeRates() {
        String url = apiUrl;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response != null) {
            Map<String, Double> rates = extractRates(response);
            saveExchangeRates(rates);
        }
    }

    private Map<String, Double> extractRates(Map<String, Object> responseBody) {
        Map<String, Double> rates = new HashMap<>();

        Object ratesObj = responseBody.get("conversion_rates");
        if (ratesObj instanceof Map) {
            Map<?, ?> rawRates = (Map<?, ?>) ratesObj;

            rawRates.forEach((key, value) -> {
                if (key instanceof String && value instanceof Number) {
                    String currency = (String) key;
                    Double rate = ((Number) value).doubleValue();
                    rates.put(currency, rate);
                }
            });
        } else {
            System.err.println("Unexpected format for conversion_rates.");
        }

        return rates;
    }

    private void saveExchangeRates(Map<String, Double> rates) {
        rates.forEach((currency, rate) -> {
            CurrencyExchangeRateModel exchangeRate = new CurrencyExchangeRateModel("USD", currency, rate, timestamp);
            exchangeRateRepository.save(exchangeRate);
        });
    }


    public List<Double> calculateExchangeRate(String currencyA, String currencyB) {
        List<Double> currencyExchangeList = new ArrayList<>();
        CurrencyExchangeRateModel rateA = exchangeRateRepository.findFirstByCurrencyToOrderByTimestampDesc(currencyA);
        CurrencyExchangeRateModel rateB = exchangeRateRepository.findFirstByCurrencyToOrderByTimestampDesc(currencyB);
        CurrencyExchangeRateModel rateC = exchangeRateRepository.findFirstByCurrencyToOrderByTimestampDesc("USD");
        double rateACurrencyExchange = 0.0d;
        double rateBCurrencyExchange = 0.0d;
        double rateABCurrencyExchange = 0.0d;
        if (currencyA != null && rateC != null) {
            rateACurrencyExchange = convertToRoundedValue(rateA.getRate() / rateC.getRate());
            currencyExchangeList.add(rateACurrencyExchange);
            userCurrencyExchangeRepository.save(new UserCurrencyExchangeRateModel(currencyA, "USD", rateACurrencyExchange, timestamp));
        }
        if (currencyB != null && rateC != null) {
            rateBCurrencyExchange = convertToRoundedValue(rateB.getRate() / rateC.getRate()); // 0.92/1.0 = 0.92
            currencyExchangeList.add(rateBCurrencyExchange);
            userCurrencyExchangeRepository.save(new UserCurrencyExchangeRateModel(currencyB, "USD", rateBCurrencyExchange, timestamp));
        }
        rateABCurrencyExchange = convertToRoundedValue(rateBCurrencyExchange / rateACurrencyExchange); // 0.92/83.75 = 0.010
        currencyExchangeList.add(rateABCurrencyExchange);
        userCurrencyExchangeRepository.save(new UserCurrencyExchangeRateModel(currencyA, currencyB, rateABCurrencyExchange, timestamp));
        return currencyExchangeList;
    }

    public double convertToRoundedValue(double input) {
        return Math.round(input * 100.0) / 100.0;
    }
}






































