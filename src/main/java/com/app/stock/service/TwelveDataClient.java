package com.app.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
@Service
@RequiredArgsConstructor
public class TwelveDataClient {

    @Value("${twelvedata.api.url}")
    private String baseUrl;

    @Value("${twelvedata.api.key}")
    private String apiKey;

    RestTemplate restTemplate = new RestTemplate();

    public BigDecimal getPriceForTicker(String ticker) {
        String url = String.format("%s?symbol=%s&apikey=%s", baseUrl, ticker, apiKey);
        try {
            TwelveDataResponse response = restTemplate.getForObject(url, TwelveDataResponse.class);
            if (response != null && response.getPrice() != null) {
                return new BigDecimal(response.getPrice());
            }
        } catch (Exception e) {
            System.out.println("Error fetching price for: " + ticker + " → " + e.getMessage());
        }
        return BigDecimal.ZERO; // fallback
    }

    static class TwelveDataResponse {
        private String price;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
