package com.moneymanger.moneytracker.reposistory;

import com.moneymanger.moneytracker.dto.CategoryDTO;
import com.moneymanger.moneytracker.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {


    List<CategoryEntity> findByProfileEntity_Id(Long profileId);

    Optional<CategoryEntity> findByIdAndProfileEntity_Id(Long id, Long profileId);

    List<CategoryEntity> findByTypeAndProfileEntity_Id(String type, Long profileId);


    boolean existsByCategoryNameAndProfileEntity_Id(String categoryName, Long profileId);

    boolean existsByCategoryName(CategoryDTO categoryDTO);
}

