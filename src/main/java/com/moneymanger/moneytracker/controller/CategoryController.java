package com.moneymanger.moneytracker.controller;


import com.moneymanger.moneytracker.dto.CategoryDTO;
import com.moneymanger.moneytracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
public class CategoryController {

   private final CategoryService categoryService;

   @PostMapping("/")
   public ResponseEntity<CategoryDTO> saveCategory(@RequestBody  CategoryDTO categoryDTO)
    {
      log.info("Inside saveCategory {}", categoryDTO);
      CategoryDTO categoryDTO1=  categoryService.saveCategory(categoryDTO);

      return  new ResponseEntity<>(categoryDTO1, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public  ResponseEntity<String>  DeleteCategory(@PathVariable Long id)
    {
        String  string = categoryService.DeleteCategory(id);
        return  ResponseEntity.ok(string);
    }

    @GetMapping("/")
    public ResponseEntity<List<CategoryDTO>> getCategoriesForCurrentUser()
    {
       List< CategoryDTO> categoryDTO=  categoryService.getCategoriesCrrentUser();
        return   new  ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }
    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByTypeForCurrentUser(@PathVariable String type)
    {
        List< CategoryDTO> categoryDTO=  categoryService.getCategoriesByTypeForCurrentUser(type);
        return   new  ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> UpdateCategory(@PathVariable Long id,@RequestBody CategoryDTO categoryDTO)
    {
        CategoryDTO categoryDTO1= categoryService.updateCategory(id,categoryDTO);
        return  new ResponseEntity<>(categoryDTO1, HttpStatus.OK);
    }
}
