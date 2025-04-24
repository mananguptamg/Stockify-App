package com.app.stock.service;

import com.app.stock.dto.FolioDetail;
import com.app.stock.dto.FolioItemDTO;
import com.app.stock.dto.FolioResponse;
import com.app.stock.model.FolioItem;
import com.app.stock.model.User;
import com.app.stock.repository.FolioItemRepository;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FolioService {
    private final FolioItemRepository folioItemRepository;
    private final TwelveDataClient twelveDataClient;

    public void addFolioItem(FolioItemDTO request, User user) {
        String ticker = request.getTicker().trim();
        int quantity = request.getQuantity();

        Optional<FolioItem> existingItemOpt = folioItemRepository.findByUserAndTicker(user, ticker);

        if (existingItemOpt.isPresent()) {
            FolioItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            folioItemRepository.save(existingItem);
        } else {
            FolioItem newItem = FolioItem.builder()
                    .user(user)
                    .ticker(ticker)
                    .quantity(quantity)
                    .build();
            folioItemRepository.save(newItem);
        }
    }

    public List<FolioItem> getFolioByUser(User user) {
        return folioItemRepository.findByUser(user);
    }

    public FolioResponse getFolioValue(User user) {
        List<FolioItem> items = folioItemRepository.findByUser(user);
        List<FolioDetail> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (FolioItem item : items) {
            BigDecimal price = twelveDataClient.getPriceForTicker(item.getTicker());
            BigDecimal value = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            details.add(FolioDetail.builder()
                    .ticker(item.getTicker())
                    .quantity(item.getQuantity())
                    .price(price)
                    .value(value)
                    .build());

            total = total.add(value);
        }

        return FolioResponse.builder()
                .items(details)
                .totalValue(total)
                .build();
    }
}
