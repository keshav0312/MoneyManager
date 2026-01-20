package com.moneymanger.moneytracker.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RecentTransactionDto {
    private  Long id;
    private  Long profileId;
    private  LocalDate date;
    private  String name;
    private  BigDecimal amount;
    private  String type;
    private String icon;
    private  LocalDateTime createdDate;
    private  LocalDateTime updatedDate;

}
