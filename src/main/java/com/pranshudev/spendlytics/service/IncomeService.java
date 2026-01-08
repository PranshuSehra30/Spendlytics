package com.pranshudev.spendlytics.service;

import com.pranshudev.spendlytics.dto.ExpenseDTO;
import com.pranshudev.spendlytics.dto.IncomeDTO;
import com.pranshudev.spendlytics.entity.CategoryEntity;
import com.pranshudev.spendlytics.entity.ExpenseEntity;
import com.pranshudev.spendlytics.entity.IncomeEntity;
import com.pranshudev.spendlytics.entity.ProfileEntity;
import com.pranshudev.spendlytics.repository.CategoryRepository;
import com.pranshudev.spendlytics.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final ModelMapper modelMapper;

    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile= profileService.getCurrentProfile();
        System.out.println(incomeDTO.getCategoryId());
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Category not found"));
        IncomeEntity newIncome = modelMapper.map(incomeDTO, IncomeEntity.class);
        newIncome.setProfile(profile);   // set the current profile entity
        newIncome.setCategory(category); // set the category entity fetched from DB
        newIncome= incomeRepository.save(newIncome);

        return modelMapper.map(newIncome,IncomeDTO.class);
    }

    public List<IncomeDTO> getCurrentPeriodIncomesForCurrentUser(LocalDateTime start, LocalDateTime end) {
        ProfileEntity profile= profileService.getCurrentProfile();
        List<IncomeEntity> list= incomeRepository.findByProfileIdAndDateBetween(profile.getId(), start, end);
        return list.stream().map( income ->modelMapper.map(income,IncomeDTO.class)).toList();
    }

    public void deleteIncome(Long incomeId) {
        ProfileEntity profile= profileService.getCurrentProfile();
        IncomeEntity byProfileIdAndIncomeId = incomeRepository.findByIdAndProfileId(incomeId, profile.getId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, " Incomes not found with income id " + incomeId));
        incomeRepository.delete(byProfileIdAndIncomeId);
    }
    //get latest 5 expenses for current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        ProfileEntity profile= profileService.getCurrentProfile();
        List<IncomeEntity> list= incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map( income ->modelMapper.map(income,IncomeDTO.class)).toList();
    }

    public BigDecimal getTotalIncomessForCurrentUser() {
        ProfileEntity profile= profileService.getCurrentProfile();
        BigDecimal total=incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total;
    }

    //filter incomes
    public List<IncomeDTO> filterIncomes(LocalDateTime start, LocalDateTime end, String name, Sort sort) {
        ProfileEntity profile= profileService.getCurrentProfile();
        List<IncomeEntity> list=incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), start,end,name,sort);

        return    list.stream()
                .map(Income->modelMapper.map
                        (Income,IncomeDTO.class)).toList();

    }

}
