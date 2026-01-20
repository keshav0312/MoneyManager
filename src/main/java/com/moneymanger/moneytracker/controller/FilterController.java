package com.moneymanger.moneytracker.controller;

import com.moneymanger.moneytracker.dto.ExpenseDTO;
import com.moneymanger.moneytracker.dto.FilterDto;
import com.moneymanger.moneytracker.dto.IncomeDTO;
import com.moneymanger.moneytracker.service.ExpenseService;
import com.moneymanger.moneytracker.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {
    private  final ExpenseService expenseService;
    private  final IncomeService  incomeService;

    @PostMapping("/")
    public ResponseEntity<?> filteredData(@RequestBody FilterDto filterDto)
    {
        LocalDate startDate= filterDto.getStartDate()!=null?filterDto.getStartDate():LocalDate.MIN;
        LocalDate endDate=filterDto.getEndDate()!=null?filterDto.getEndDate():LocalDate.now();
        String sortBy=filterDto.getSortBy()!=null?filterDto.getSortBy():"date";
        String keyword=filterDto.getKeyword()!=null?filterDto.getKeyword():"";
        Sort.Direction sortDirection="desc".equalsIgnoreCase(filterDto.getSortOrder())?Sort.Direction.DESC:Sort.Direction.ASC;
        Sort sort=Sort.by(sortDirection,sortBy);
        if("income".equalsIgnoreCase(filterDto.getType()))
        {
          List<IncomeDTO> incomeDTOS =  incomeService.filterExpenses(startDate,endDate,keyword,sort);
          return ResponseEntity.ok(incomeDTOS);
        }
        else if("expense".equalsIgnoreCase(filterDto.getType())) {
            List<ExpenseDTO> expenseDTOS = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenseDTOS);
        }
       else
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Type must be expense or income..");
        }
    }
}
