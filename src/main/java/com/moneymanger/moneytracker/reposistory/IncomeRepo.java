package com.moneymanger.moneytracker.reposistory;

import com.moneymanger.moneytracker.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepo  extends JpaRepository<IncomeEntity,Long> {
    List<IncomeEntity> findByProfileEntity_IdOrderByDateDesc(Long profileEntityId);

    List<IncomeEntity> findTop5ByProfileEntity_IdOrderByDateDesc(Long profileEntityId);

    @Query("SELECT SUM(e.amount) FROM IncomeEntity e WHERE e.profileEntity.id = :profileEntityId")
    BigDecimal findTotalIncomeByProfileEntity_Id(@Param("profileEntityId") Long profileEntityId);

    List<IncomeEntity> findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sortOrder);
    List<IncomeEntity> findByProfileEntity_IdAndDateBetween(
            Long profile_id,
            LocalDate startDate,
            LocalDate endDate
    );

}
