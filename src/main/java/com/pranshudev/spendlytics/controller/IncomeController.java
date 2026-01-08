package com.pranshudev.spendlytics.controller;

import com.pranshudev.spendlytics.dto.ExpenseDTO;
import com.pranshudev.spendlytics.dto.IncomeDTO;
import com.pranshudev.spendlytics.service.ExpenseService;
import com.pranshudev.spendlytics.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addExpense(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO saved=incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }
    // ✅ GET expenses for current user in date range
    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getExpensesByDateRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {

        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = LocalDateTime.parse(end);

        List<IncomeDTO> incomes =
                incomeService.getCurrentPeriodIncomesForCurrentUser(
                        startDateTime,
                        endDateTime
                );

        return ResponseEntity.status(HttpStatus.FOUND).body(incomes);
    }
    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long incomeId

    ) {
        incomeService.deleteIncome(incomeId);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/top5Incomes")
    public ResponseEntity<List<IncomeDTO>> getTop5Incomes(
    ) {


        List<IncomeDTO> incomes =
                incomeService.getLatest5IncomesForCurrentUser(

                );

        return ResponseEntity.status(HttpStatus.FOUND).body(incomes);

    }

    @GetMapping("/totalIncomes")
    public ResponseEntity<BigDecimal> getTotalIncomes(
    ) {


        BigDecimal expenses =
                incomeService.getTotalIncomessForCurrentUser(

                );

        return ResponseEntity.status(HttpStatus.FOUND).body(expenses);
    }
}

