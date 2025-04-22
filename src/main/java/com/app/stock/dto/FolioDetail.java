package com.app.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolioDetail {
    private String ticker;
    private int quantity;
    private BigDecimal price;
    private BigDecimal value;
}
