package com.pranshudev.spendlytics.service;

import com.pranshudev.spendlytics.dto.ExpenseDTO;
import com.pranshudev.spendlytics.dto.IncomeDTO;
import com.pranshudev.spendlytics.dto.RecentTransactionDTO;
import com.pranshudev.spendlytics.entity.ProfileEntity;
import com.pranshudev.spendlytics.repository.ExpenseRepository;
import com.pranshudev.spendlytics.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final ProfileService profileService;
    private final ModelMapper modelMapper;

    public Map<String,Object> getDashboardData()
    {
        ProfileEntity currentProfile= profileService.getCurrentProfile();
        Map<String,Object> map = new LinkedHashMap<>();
        List<IncomeDTO> lastest5Incomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latest5Expenses = expenseService.getLatest5ExpensesForCurrentUser();
        // 🔥 Merge incomes + expenses into recent transactions
        List<RecentTransactionDTO> recentTransactions =
                Stream.concat(lastest5Incomes.stream()
                                        .map(income->
                                                RecentTransactionDTO.builder()
                                                        .id(income.getId())
                                                        .profileId(currentProfile.getId())
                                                        .icon(income.getIcon())
                                                        .name(income.getName())
                                                        .amount(income.getAmount())
                                                        .date(income.getDate())
                                                        .createdAt(income.getCreatedAt())
                                                        .updatedAt(income.getUpdatedAt())
                                                        .type("income")
                                                        .build()),
                                latest5Expenses.stream()
                                        .map(expense->{
                                            RecentTransactionDTO dto=
                                                    modelMapper.map(expense,RecentTransactionDTO.class);

                                            dto.setType("expense");

                                            return dto;})
                        ) .sorted(
                                Comparator
                                        .comparing(RecentTransactionDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                                        .thenComparing(
                                                RecentTransactionDTO::getCreatedAt,
                                                Comparator.nullsLast(Comparator.naturalOrder())
                                        )
                                        .reversed()
                        )

                        .collect(Collectors.toList());
        map.put("totalBalance",incomeService.getTotalIncomessForCurrentUser().subtract(expenseService.getTotalExpensesForCurrentUser()));
        map.put("totalIncome",incomeService.getTotalIncomessForCurrentUser());
        map.put("totalExpenses",expenseService.getTotalExpensesForCurrentUser());
        map.put("recent5Expenses",latest5Expenses);
        map.put("recent5Incomes",lastest5Incomes);
        map.put("recentTransactions",recentTransactions);
        return map;


    }
}
