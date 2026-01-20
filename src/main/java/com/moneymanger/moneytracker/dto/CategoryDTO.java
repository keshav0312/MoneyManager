package com.moneymanger.moneytracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    private  Long id;
    private  String categoryName;
    private  String icon;
    private  String type;
    private  String profileId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
