package com.pranshudev.spendlytics.service;

import com.pranshudev.spendlytics.dto.ExpenseDTO;
import com.pranshudev.spendlytics.dto.ExpenseSummaryDTO;
import com.pranshudev.spendlytics.entity.CategoryEntity;
import com.pranshudev.spendlytics.entity.ExpenseEntity;
import com.pranshudev.spendlytics.entity.ProfileEntity;
import com.pranshudev.spendlytics.repository.CategoryRepository;
import com.pranshudev.spendlytics.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final ProfileService profileService;

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        ProfileEntity profile= profileService.getCurrentProfile();
        CategoryEntity  category = categoryRepository.findById(expenseDTO.getCategoryId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Category not found"));
        ExpenseEntity newExpense = modelMapper.map(expenseDTO, ExpenseEntity.class);
        newExpense.setProfile(profile);   // set the current profile entity
        newExpense.setCategory(category);
        newExpense= expenseRepository.save(newExpense);

        return modelMapper.map(newExpense,ExpenseDTO.class);
    }


    public List<ExpenseDTO> getCurrentPeriodExpensesForCurrentUser(LocalDateTime start, LocalDateTime end) {
        ProfileEntity profile= profileService.getCurrentProfile();
        List<ExpenseEntity> list= expenseRepository.findByProfileIdAndDateBetween(profile.getId(), start, end);
        return list.stream().map( expense ->modelMapper.map(expense,ExpenseDTO.class)).toList();
    }
//delete expense by id for current user

    public void deleteExpense(Long expenseId) {
        ProfileEntity profile= profileService.getCurrentProfile();
        ExpenseEntity byProfileIdAndExpenseId = expenseRepository.findByIdAndProfileId(expenseId, profile.getId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, " Expense not found with expense id " + expenseId));
        expenseRepository.delete(byProfileIdAndExpenseId);
    }

    //get latest 5 expenses for current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile= profileService.getCurrentProfile();
        List<ExpenseEntity> list= expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map( expense ->modelMapper.map(expense,ExpenseDTO.class)).toList();
    }

    public BigDecimal getTotalExpensesForCurrentUser() {
        ProfileEntity profile= profileService.getCurrentProfile();
        BigDecimal total=expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total;
    }

    //filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDateTime start, LocalDateTime end, String name, Sort sort) {
        ProfileEntity profile= profileService.getCurrentProfile();
        List<ExpenseEntity> list=expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), start,end,name,sort);

        List<ExpenseDTO> filteredExpenses=list.stream().map(expense->modelMapper.map(expense,ExpenseDTO.class)).toList();
        return filteredExpenses;
    }

    //notifications
//notifications
    public List<ExpenseSummaryDTO> getExpensesSummaryForProfileIdAndDate(Long profileId, LocalDateTime startDate, LocalDateTime endDate) {
        List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDateBetween(profileId, startDate, endDate);

        return expenses.stream().map(expense -> {
            ExpenseSummaryDTO dto = new ExpenseSummaryDTO();
            dto.setName(expense.getName());
            dto.setCategoryName(expense.getCategory() != null ? expense.getCategory().getName() : "");
            dto.setAmount(expense.getAmount());
            dto.setTime(expense.getDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))); // format time as HH:mm
            return dto;
        }).toList();
    }

}
