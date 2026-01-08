package com.pranshudev.spendlytics.dto;

import lombok.*;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterDTO {
    private String type;
    private String keyword;
    private String sortField;
    private String sortOrder;

    private LocalDateTime startDate;
    private LocalDateTime endDate;


}
