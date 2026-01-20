package com.moneymanger.moneytracker.controller;


import com.moneymanger.moneytracker.dto.ExpenseDTO;
import com.moneymanger.moneytracker.dto.IncomeDTO;
import com.moneymanger.moneytracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private  final ExpenseService expenseService;
    @PostMapping("/")
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody  ExpenseDTO expenseDTO){

        ExpenseDTO   expenseDto= expenseService.addExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseDto);
    }

    @GetMapping("/")
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses(){
        List<ExpenseDTO> expenseDTOS = expenseService.getAllexpensesforCurrentUser();

        return ResponseEntity.status(HttpStatus.OK).body(expenseDTOS);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ExpenseDTO>  updateExpense(@PathVariable Long id, @RequestBody  ExpenseDTO expenseDTO){

     ExpenseDTO expenseDTO1 =   expenseService.UpdateExpense(id,expenseDTO);
     return   ResponseEntity.status(HttpStatus.OK).body(expenseDTO1);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<String> deleteExpense(@PathVariable("id") Long id){
       String string = expenseService.deleteExpense(id);
       return ResponseEntity.status(HttpStatus.OK).body(string);
    }

    @GetMapping("/total_expense")
    public BigDecimal getTotalExpense(){
        return  expenseService.getTotalExpensesforCurrentUser();
    }

    @GetMapping("/top_expenses")
    public  List<ExpenseDTO> getTop5Incomes(){
        List<ExpenseDTO> incomeDTOs =   expenseService.getTopExpensesforCurrentUser();
        return incomeDTOs;
    }
}
