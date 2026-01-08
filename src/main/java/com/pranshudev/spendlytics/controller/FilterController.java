package com.pranshudev.spendlytics.controller;

import com.pranshudev.spendlytics.dto.ExpenseDTO;
import com.pranshudev.spendlytics.dto.FilterDTO;
import com.pranshudev.spendlytics.dto.IncomeDTO;
import com.pranshudev.spendlytics.repository.IncomeRepository;
import com.pranshudev.spendlytics.service.ExpenseService;
import com.pranshudev.spendlytics.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    @PostMapping("/filter")
    public ResponseEntity<?> filterTransaction(@RequestBody FilterDTO filterDTO) {

        System.out.println(filterDTO.getType());
        LocalDateTime startDate = filterDTO.getStartDate() != null
                ? filterDTO.getStartDate()
                : LocalDateTime.MIN;

        LocalDateTime endDate = filterDTO.getEndDate() != null
                ? filterDTO.getEndDate()
                : LocalDateTime.now();

        String keyword = filterDTO.getKeyword() != null
                ? filterDTO.getKeyword()
                : "";

        String sortField = filterDTO.getSortField() != null
                ? filterDTO.getSortField()
                : "date";

        Sort.Direction direction =
                "desc".equalsIgnoreCase(filterDTO.getSortOrder())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
        System.out.println(direction);

        Sort sort = Sort.by(direction, sortField);

        if ("income".equalsIgnoreCase(filterDTO.getType())) {
            List<IncomeDTO> incomes =
                    incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        }

        if ("expense".equalsIgnoreCase(filterDTO.getType())) {
            List<ExpenseDTO> expenses =
                    expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
        }

        return ResponseEntity.badRequest()
                .body("Invalid filter type. Must be 'income' or 'expense'");
    }


}
