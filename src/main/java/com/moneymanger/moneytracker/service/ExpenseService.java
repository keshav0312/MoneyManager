package com.moneymanger.moneytracker.service;

import com.moneymanger.moneytracker.GlobalExceptionHandler.ResourceNotFoundException;
import com.moneymanger.moneytracker.dto.ExpenseDTO;
import com.moneymanger.moneytracker.dto.IncomeDTO;
import com.moneymanger.moneytracker.entity.CategoryEntity;
import com.moneymanger.moneytracker.entity.ExpenseEntity;
import com.moneymanger.moneytracker.entity.ProfileEntity;
import com.moneymanger.moneytracker.reposistory.CategoryRepository;
import com.moneymanger.moneytracker.reposistory.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private  final CategoryService categoryservice;
    private final ProfileService profileservice;
    private final ExpenseRepo expenserepo;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

private  final Logger logger= LoggerFactory.getLogger(this.getClass());

    public ExpenseDTO addExpense(ExpenseDTO expensedto) {
     logger.info(expensedto.toString());
       ProfileEntity profileEntity= profileservice.getCurrentProfile();
        logger.info(expensedto.toString());
     if(expensedto.getCategoryId()==null)
        throw  new RuntimeException("categoryId is null");
        CategoryEntity categoryEntity=categoryRepository.findById(expensedto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));

      ExpenseEntity expenseEntity=  modelMapper.map(expensedto, ExpenseEntity.class);
      expenseEntity.setCategoryEntity(categoryEntity);
      expenseEntity.setProfileEntity(profileEntity);
       ExpenseEntity expenseEntity1= expenserepo.save(expenseEntity);
      ExpenseDTO expenseDTO= modelMapper.map(expenseEntity1, ExpenseDTO.class);
      return expenseDTO;

    }

    public List<ExpenseDTO> getAllexpensesforCurrentUser() {

        ProfileEntity profileEntity=  profileservice.getCurrentProfile();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1); // Start of current month
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth()); // End of current month

        logger.info(" {} {} ",startDate.toString(),endDate.toString());
        List<ExpenseEntity> incomeEntities= expenserepo.findByProfileEntity_IdAndDateBetween(profileEntity.getId(),startDate,endDate);

        List<ExpenseDTO> incomeEntityList= incomeEntities.stream().map((entites)->modelMapper.map(entites, ExpenseDTO.class)).collect(Collectors.toList());

        return  incomeEntityList;


    }

    public  String deleteExpense(Long id) {


     ProfileEntity profileEntity =  profileservice.getCurrentProfile();
      ExpenseEntity expenseEntity = expenserepo.findById(id).orElseThrow(()->new RuntimeException("Expense not found"));

      if(expenseEntity.getProfileEntity().getId()!=profileEntity.getId())
          throw new RuntimeException("unauthorized to delete");
        expenserepo.deleteById(id);
       return  "expense deleted successfully "+id;
    }

    public List<ExpenseDTO> getTopExpensesforCurrentUser() {
        ProfileEntity profileEntity=  profileservice.getCurrentProfile();
        List<ExpenseEntity> expenseEntities = expenserepo.findTop5ByProfileEntity_IdOrderByDateDesc(profileEntity.getId());
        List< ExpenseDTO> expenseDTO=  expenseEntities.stream().map((entites)->modelMapper.map(entites, ExpenseDTO.class)).collect(Collectors.toList());
        return  expenseDTO;
    }

    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sortOrder) {
        ProfileEntity profileEntity = profileservice.getCurrentProfile();
        List<ExpenseEntity> expenses = expenserepo.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(profileEntity.getId(), startDate, endDate, keyword,sortOrder);
        return expenses.stream().map(expenseEntity -> modelMapper.map(expenseEntity, ExpenseDTO.class)).collect(Collectors.toList());
    }

//    Notification
    public  List<ExpenseDTO> filterExpensesForToday(Long profileID,LocalDate date)
    {
      List<ExpenseEntity> expenseDTOS =  expenserepo.findByProfileEntity_IdAndDate(profileID,date);
     return expenseDTOS.stream().map(expenseEntity -> modelMapper.map(expenseEntity, ExpenseDTO.class)).collect(Collectors.toList());
    }

    public BigDecimal getTotalExpensesforCurrentUser() {
        ProfileEntity profileEntity=  profileservice.getCurrentProfile();
        BigDecimal bigDecimal=  expenserepo.findTotalExpenseByProfileEntity_Id(profileEntity.getId());
        return bigDecimal;

    }

    public ExpenseDTO UpdateExpense(Long id, ExpenseDTO expenseDTO) {
        ExpenseEntity expenseDTO1 = expenserepo.findById(id).orElseThrow(()->new ResourceNotFoundException("expense not found", HttpStatus.NOT_FOUND));
        expenseDTO1.setAmount(expenseDTO.getAmount());
        expenseDTO1.setId(expenseDTO1.getId());
        expenseDTO1.setName(expenseDTO1.getName());
        expenseDTO1.setCategoryEntity(expenseDTO1.getCategoryEntity());
       expenseDTO1.setIcon(expenseDTO1.getIcon());
      ExpenseEntity expenseEntity=  expenserepo.save(expenseDTO1);
    ExpenseDTO expenseDTO2=  modelMapper.map(expenseDTO1,ExpenseDTO.class);
      return expenseDTO2;
    }
}





