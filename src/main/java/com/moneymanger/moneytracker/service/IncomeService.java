package com.moneymanger.moneytracker.service;


import com.moneymanger.moneytracker.GlobalExceptionHandler.ResourceNotFoundException;
import com.moneymanger.moneytracker.dto.IncomeDTO;
import com.moneymanger.moneytracker.entity.CategoryEntity;
import com.moneymanger.moneytracker.entity.IncomeEntity;
import com.moneymanger.moneytracker.entity.ProfileEntity;
import com.moneymanger.moneytracker.reposistory.CategoryRepository;
import com.moneymanger.moneytracker.reposistory.IncomeRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepo  incomerepo;
    private final CategoryService  categoryservice;
    private final ProfileService profileService;
    private  final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public IncomeDTO addIncome(IncomeDTO incomeDTO) {

        ProfileEntity profileEntity= profileService.getCurrentProfile();

        CategoryEntity categoryEntity=categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));

      com.moneymanger.moneytracker.entity.IncomeEntity incomeEntity =  modelMapper.map(incomeDTO, IncomeEntity.class);
        incomeEntity.setCategoryEntity(categoryEntity);
        incomeEntity.setProfileEntity(profileEntity);
        IncomeEntity incomeEntity1= incomerepo.save(incomeEntity);
        IncomeDTO  incomeDTO1= modelMapper.map(incomeEntity1, IncomeDTO.class);
        return incomeDTO1;

    }

    public List<IncomeDTO> getAllIcomesforCurrentUser() {

      ProfileEntity profileEntity=  profileService.getCurrentProfile();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1); // Start of current month
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());

    List<IncomeEntity> incomeEntities= incomerepo.findByProfileEntity_IdAndDateBetween(profileEntity.getId(),startDate,endDate);

     List<IncomeDTO> incomeEntityList= incomeEntities.stream().map((entites)->modelMapper.map(entites,IncomeDTO.class)).collect(Collectors.toList());

    return  incomeEntityList;


    }


    public String  deleteincome(Long id){
        ProfileEntity profileEntity =  profileService.getCurrentProfile();
        com.moneymanger.moneytracker.entity.IncomeEntity incomeEntity = incomerepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Expense not found", HttpStatus.NOT_FOUND));

        if(incomeEntity.getProfileEntity().getId()!=profileEntity.getId())
            throw new RuntimeException("unauthorized to delete");
        incomerepo.deleteById(id);
        return  "Income deleted successfully "+id;
    }



    public List<IncomeDTO> getTopExpensesforCurrentUser() {
        ProfileEntity profileEntity=  profileService.getCurrentProfile();
        List<IncomeEntity> expenseEntities = incomerepo.findTop5ByProfileEntity_IdOrderByDateDesc(profileEntity.getId());

        List< IncomeDTO> expenseDTO=  expenseEntities.stream().map((entites)->modelMapper.map(entites, IncomeDTO.class)).collect(Collectors.toList());

        return  expenseDTO;
    }

    public List<IncomeDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sortOrder) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        List<IncomeEntity> expenses = incomerepo.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(profileEntity.getId(), startDate, endDate, keyword,sortOrder);
        List<IncomeDTO> incomesDtolist = expenses.stream().map(expenseEntity -> modelMapper.map(expenseEntity, IncomeDTO.class)).collect(Collectors.toList());
        return incomesDtolist;
    }


    public BigDecimal getTotalIncomesforCurrentUser() {
        ProfileEntity profileEntity=  profileService.getCurrentProfile();
      BigDecimal bigDecimal=  incomerepo.findTotalIncomeByProfileEntity_Id(profileEntity.getId());
      return bigDecimal;

    }
}
