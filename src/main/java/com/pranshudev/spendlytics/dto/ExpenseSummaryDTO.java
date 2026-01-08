package com.pranshudev.spendlytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseSummaryDTO {
    private String name;
    private String categoryName;
    private BigDecimal amount;
    private String time; // formatted time as string, e.g. "15:29"
}
