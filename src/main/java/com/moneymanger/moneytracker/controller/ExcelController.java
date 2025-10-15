package com.moneymanger.moneytracker.controller;

import com.moneymanger.moneytracker.service.ExcelService;
import com.moneymanger.moneytracker.service.ExpenseService;
import com.moneymanger.moneytracker.service.IncomeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/excel")
@AllArgsConstructor
public class ExcelController {

    private final IncomeService  incomeService;
    private final ExpenseService expenseService;
    private final ExcelService  excelService;

    @GetMapping("/download/income")
        public void downloadIncomeExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=income.xlsx");
        response.setHeader("Content-Description", "income.xlsx");
        response.setHeader("Content-Transfer-Encoding", "binary");

        excelService.writeIncomesToExcel(response.getOutputStream(),incomeService.getAllIcomesforCurrentUser());

        }

    @GetMapping("/download/expense")
    public void downloadExpenseExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=expense.xlsx");
        response.setHeader("Content-Description", "expense.xlsx");
        response.setHeader("Content-Transfer-Encoding", "binary");

        excelService.writeExpensesToExcel(response.getOutputStream(),expenseService.getAllexpensesforCurrentUser());

    }
}
