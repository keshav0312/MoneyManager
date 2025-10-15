package com.moneymanger.moneytracker.service;


import ch.qos.logback.core.joran.conditional.ElseAction;
import com.moneymanger.moneytracker.dto.CategoryDTO;
import com.moneymanger.moneytracker.entity.CategoryEntity;
import com.moneymanger.moneytracker.entity.ProfileEntity;
import com.moneymanger.moneytracker.reposistory.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ModelMapper modelMapper;

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        logger.info(profileEntity.toString());
      logger.info("category dto{}",categoryDTO.toString());
        // ✅ Check by categoryName + profileId
        boolean exists = categoryRepository.existsByCategoryNameAndProfileEntity_Id(
                categoryDTO.getCategoryName(),
                profileEntity.getId()
        );

        logger.info(exists ? "exists" : "not exists");

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }

        CategoryEntity categoryEntity = modelMapper.map(categoryDTO, CategoryEntity.class);
        categoryEntity.setType(categoryDTO.getType().toLowerCase());
        categoryEntity.setProfileEntity(profileEntity);

        CategoryEntity saved = categoryRepository.save(categoryEntity);

        return modelMapper.map(saved, CategoryDTO.class);
    }


    public List<CategoryDTO> getCategoriesCrrentUser() {
        // Get currently logged-in profile
        ProfileEntity profileEntity = profileService.getCurrentProfile();

        // Fetch categories of current user
        List<CategoryEntity> categoryEntities = categoryRepository.findByProfileEntity_Id(profileEntity.getId());

        // Map entities to DTOs
        List<CategoryDTO> categoryDTOs = categoryEntities.stream()
                .map(entity -> modelMapper.map(entity, CategoryDTO.class))
                .collect(Collectors.toList());

        return categoryDTOs;
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        List<CategoryEntity> categoryEntity = categoryRepository.findByTypeAndProfileEntity_Id(type, profileService.getCurrentProfile().getId());
        return categoryEntity.stream().map(entity -> modelMapper.map(entity, CategoryDTO.class)).collect(Collectors.toList());
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileEntity_Id(id, profileEntity.getId()).orElseThrow(() -> new RuntimeException("category not found"));

        categoryEntity.setCategoryName(categoryDTO.getCategoryName());
        categoryEntity.setProfileEntity(profileEntity);
        categoryEntity.setType(categoryDTO.getType());
        categoryEntity.setIcon(categoryDTO.getIcon());
        categoryRepository.save(categoryEntity);
        return modelMapper.map(categoryEntity, CategoryDTO.class);

    }

    public String DeleteCategory(Long id) {
        categoryRepository.deleteById(id);
        return "deleted";
    }
}