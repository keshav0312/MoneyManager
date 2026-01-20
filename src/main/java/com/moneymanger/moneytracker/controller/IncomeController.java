package com.moneymanger.moneytracker.controller;


import com.moneymanger.moneytracker.dto.ExpenseDTO;
import com.moneymanger.moneytracker.dto.IncomeDTO;
import com.moneymanger.moneytracker.service.ExpenseService;
import com.moneymanger.moneytracker.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private  final IncomeService incomeService;
    @PostMapping("/")
    public ResponseEntity<IncomeDTO> addExpense(@RequestBody  IncomeDTO incomeDTO){
        IncomeDTO  incomedto=incomeService.addIncome (incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(incomedto);
    }

    @GetMapping("/")
     public ResponseEntity<List<IncomeDTO>> getAllIncomes(){
     List<IncomeDTO> incomeDTOS=incomeService.getAllIcomesforCurrentUser();
     return   ResponseEntity.status(HttpStatus.OK).body(incomeDTOS);
    }
    @DeleteMapping("/{id}")
    public  ResponseEntity<String> deleteExpense(@PathVariable("id") Long id){
        String string = incomeService.deleteincome(id);
        return ResponseEntity.status(HttpStatus.OK).body(string);
    }

    @GetMapping("/total_incomes")
    public BigDecimal  getTotalExpense(){
        return  incomeService.getTotalIncomesforCurrentUser();
    }


    @GetMapping("/top_incomes")
    public  List<IncomeDTO> getTop5Incomes(){
      List<IncomeDTO> incomeDTOs =   incomeService.getTopExpensesforCurrentUser();
      return incomeDTOs;
    }
}
