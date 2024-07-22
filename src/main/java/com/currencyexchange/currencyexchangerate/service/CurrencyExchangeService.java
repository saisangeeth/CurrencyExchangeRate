package com.currencyexchange.currencyexchangerate.service;

import com.currencyexchange.currencyexchangerate.model.CurrencyExchangeRateModel;
import com.currencyexchange.currencyexchangerate.model.UserCurrencyExchangeRateModel;
import com.currencyexchange.currencyexchangerate.repository.CurrencyExchangeRateRepository;
import com.currencyexchange.currencyexchangerate.repository.UserCurrencyExchangeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class CurrencyExchangeService {
    private final CurrencyExchangeRateRepository currencyExchangeRateRepository;
    private final UserCurrencyExchangeRepository userCurrencyExchangeRepository;

    public CurrencyExchangeService(CurrencyExchangeRateRepository currencyExchangeRateRepository,
                                   UserCurrencyExchangeRepository userCurrencyExchangeRepository) {
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
        this.userCurrencyExchangeRepository = userCurrencyExchangeRepository;
    }

    public List<UserCurrencyExchangeRateModel> getExchangeRates(LocalDate startDate, LocalDate endDate) {
        return userCurrencyExchangeRepository.findByTimestampBetween(startDate, endDate);
    }

    public void exportExchangeRates(LocalDate startDate, LocalDate endDate, String format, HttpServletResponse response) {
        List<UserCurrencyExchangeRateModel> exchangeRates = getExchangeRates(startDate, endDate);

        switch (format.toLowerCase()) {
            case "csv":
                exportToCsv(exchangeRates, response);
                break;
            case "json":
                exportToJson(exchangeRates, response);
                break;
            case "pdf":
                exportToPdf(exchangeRates, response);
                break;
            default:
                throw new IllegalArgumentException("Invalid format: " + format);
        }
    }

    private void exportToCsv(List<UserCurrencyExchangeRateModel> exchangeRates, HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=exchange_rates.csv");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            String[] header = {"Date", "CurrencyPair", "Rate"};
            writer.writeNext(header);

            for (UserCurrencyExchangeRateModel rate : exchangeRates) {
                writer.writeNext(new String[]{rate.getTimestamp().toString(), rate.getCurrencyFrom() + "-" + rate.getCurrencyTo(), String.valueOf(rate.getRate())});
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV", e);
        }
    }

    private void exportToJson(List<UserCurrencyExchangeRateModel> exchangeRates, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=exchange_rates.json");
        ObjectMapper objectMapper = null;

        try {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRates));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON", e);
        }
    }

    private void exportToPdf(List<UserCurrencyExchangeRateModel> exchangeRates, HttpServletResponse response) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=exchange_rates.pdf");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            for (UserCurrencyExchangeRateModel rate : exchangeRates) {
                document.add(new Paragraph("Date: " + rate.getTimestamp().toString()));
                document.add(new Paragraph("Currency From: " + rate.getCurrencyFrom()));
                document.add(new Paragraph("Currency To: " + rate.getCurrencyTo()));
                document.add(new Paragraph("Rate: " + rate.getRate()));
                document.add(new Paragraph(" "));
            }

            document.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Failed to write PDF", e);
        }
    }
}
