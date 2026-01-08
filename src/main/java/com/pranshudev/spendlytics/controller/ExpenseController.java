package com.pranshudev.spendlytics.controller;
import com.pranshudev.spendlytics.dto.ExpenseDTO;
import com.pranshudev.spendlytics.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO saved=expenseService.addExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }

    // ✅ GET expenses for current user in date range
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpensesByDateRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {

        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = LocalDateTime.parse(end);

        List<ExpenseDTO> expenses =
                expenseService.getCurrentPeriodExpensesForCurrentUser(
                        startDateTime,
                        endDateTime
                );

        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long expenseId
    ) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/top5Expenses")
    public ResponseEntity<List<ExpenseDTO>> getLatest5Expenses(
    ) {


        List<ExpenseDTO> expenses =
                expenseService.getLatest5ExpensesForCurrentUser(

                );

        return ResponseEntity.status(HttpStatus.FOUND).body(expenses);

    }

    @GetMapping("/totalExpenses")
    public ResponseEntity<BigDecimal> getTotalExpenses(
    ) {


        BigDecimal expenses =
                expenseService.getTotalExpensesForCurrentUser(

                );

        return ResponseEntity.status(HttpStatus.FOUND).body(expenses);
    }
}
