package com.moneymanger.moneytracker.reposistory;

import com.moneymanger.moneytracker.dto.CategoryDTO;
import com.moneymanger.moneytracker.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByProfileEntity_Id(Long profileId);

    Optional<CategoryEntity> findByIdAndProfileEntity_Id(Long id, Long profileId);

    List<CategoryEntity> findByTypeAndProfileEntity_Id(String type, Long profileId);

    boolean existsByCategoryNameAndProfileEntity_Id(String categoryName, Long profileId);

    // ✅ FIXED: Remove this invalid method or replace with correct implementation
    // Option 1: Remove if not needed
    // boolean existsByCategoryName(CategoryDTO categoryDTO);


}