package com.moneymanger.moneytracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private  String keyword;
    private String sortBy;
    private String sortOrder;
}
