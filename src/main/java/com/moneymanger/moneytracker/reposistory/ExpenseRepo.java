package com.moneymanger.moneytracker.reposistory;

import com.moneymanger.moneytracker.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepo extends JpaRepository<ExpenseEntity,Long> {

    List<ExpenseEntity> findByProfileEntity_IdOrderByDateDesc(Long profileEntityId);

    List<ExpenseEntity> findTop5ByProfileEntity_IdOrderByDateDesc(Long profileEntityId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profileEntity.id = :profileEntityId")
    BigDecimal findTotalExpenseByProfileEntity_Id(@Param("profileEntityId") Long profileEntityId);

    List<ExpenseEntity> findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,


            Sort sortOrder);
  List<ExpenseEntity> findByProfileEntity_IdAndDateBetween(
          Long profile_id,
          LocalDate startDate,
          LocalDate endDate
  );

  List<ExpenseEntity> findByProfileEntity_IdAndDate(
          Long profile_id,
          LocalDate Date);

}
