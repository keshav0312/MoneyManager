package com.moneymanger.moneytracker.service;

import com.moneymanger.moneytracker.dto.ExpenseDTO;
import com.moneymanger.moneytracker.dto.IncomeDTO;
import com.moneymanger.moneytracker.dto.RecentTransactionDto;
import com.moneymanger.moneytracker.entity.ExpenseEntity;
import com.moneymanger.moneytracker.entity.IncomeEntity;
import com.moneymanger.moneytracker.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private  final ProfileService  profileService;
    private final IncomeService  incomeService;
    private  final  ExpenseService   expenseService;

    public Map<String, Object> getDashboard() {

        ProfileEntity profileEntity = profileService.getCurrentProfile();

        List<IncomeDTO> incomeEntity = incomeService.getTopExpensesforCurrentUser();
        List<ExpenseDTO> expenseEntities = expenseService.getTopExpensesforCurrentUser();

        Map<String, Object> map = new HashMap<>();
        List<RecentTransactionDto> recentTransactionDtoList = Stream.concat(
                // Income stream
                incomeEntity.stream().map(entity -> RecentTransactionDto.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .amount(entity.getAmount())
                        .type("Income")
                        .date(entity.getDate())
                        .profileId(profileEntity.getId())
                        .createdDate(entity.getCreatedAt())
                        .updatedDate(entity.getUpdatedAt())
                        .icon(entity.getIcon())
                        .build()
                ),
                // Expense stream
                expenseEntities.stream().map(expense -> RecentTransactionDto.builder()
                        .id(expense.getId())
                        .name(expense.getName())
                        .amount(expense.getAmount())
                        .type("Expense")
                        .icon(expense.getIcon())
                        .date(expense.getDate())
                        .profileId(profileEntity.getId())
                        .createdDate(expense.getCreatedAt())
                        .updatedDate(expense.getUpdatedAt())
                        .build()
                )
        ).sorted((a,b)->
        {
            int cmp=a.getDate().compareTo(b.getDate());
            if(cmp==0){
                return a.getCreatedDate().compareTo(b.getCreatedDate());
            }
            return cmp;
        }).collect(Collectors.toList());
        BigDecimal totalIncome = incomeService.getTotalIncomesforCurrentUser();
        BigDecimal totalExpense = expenseService.getTotalExpensesforCurrentUser();

// Handle null values
        BigDecimal safeTotalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        BigDecimal safeTotalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;

        map.put("total_balance", safeTotalIncome.subtract(safeTotalExpense));
        map.put("total_expenses", safeTotalExpense);
        map.put("total_incomes", safeTotalIncome);
        map.put("Recent5incomes", incomeService.getTopExpensesforCurrentUser());
        map.put("Recent5expenses", expenseService.getTopExpensesforCurrentUser());
        map.put("RecentTransactions", recentTransactionDtoList);
        return map;

    }
}
