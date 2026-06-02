package com.financial_tech_lab.financecontrol.service;

import com.financial_tech_lab.financecontrol.dto.request.CategoryRequest;
import com.financial_tech_lab.financecontrol.dto.response.CategoryResponse;
import com.financial_tech_lab.financecontrol.entity.Category;
import com.financial_tech_lab.financecontrol.entity.TransactionType;
import com.financial_tech_lab.financecontrol.exception.duplicate.DuplicateCategoryException;
import com.financial_tech_lab.financecontrol.exception.notfound.CategoryNotFoundException;
import com.financial_tech_lab.financecontrol.mapper.CategoryMapper;
import com.financial_tech_lab.financecontrol.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() {
        CategoryRequest request = new CategoryRequest("Salary", TransactionType.INCOME);

        Category entity = Category.builder()
                .id(1L)
                .name("Salary")
                .type(TransactionType.INCOME)
                .build();

        CategoryResponse response = new CategoryResponse(1L, "Salary", TransactionType.INCOME);

        when(categoryRepository.existsByName("Salary")).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(categoryMapper.toResponse(entity)).thenReturn(response);

        CategoryResponse result = categoryService.create(request);

        assertThat(result.name()).isEqualTo("Salary");
        assertThat(result.type()).isEqualTo(TransactionType.INCOME);
        verify(categoryRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw DuplicateCategoryException when category name already exists")
    void shouldThrowWhenCategoryNameAlreadyExists() {
        CategoryRequest request = new CategoryRequest("Salary", TransactionType.INCOME);

        when(categoryRepository.existsByName("Salary")).thenReturn(true);

        assertThrows(DuplicateCategoryException.class, () -> categoryService.create(request));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return category by id")
    void shouldReturnCategoryById() {
        Category entity = Category.builder()
                .id(1L)
                .name("Food")
                .type(TransactionType.EXPENSE)
                .build();

        CategoryResponse response = new CategoryResponse(1L, "Food", TransactionType.EXPENSE);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(categoryMapper.toResponse(entity)).thenReturn(response);

        CategoryResponse result = categoryService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when category not found")
    void shouldThrowWhenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(99L));
    }

    @Test
    @DisplayName("Should return all categories")
    void shouldReturnAllCategories() {
        Category c1 = Category.builder().id(1L).name("Salary").type(TransactionType.INCOME).build();
        Category c2 = Category.builder().id(2L).name("Food").type(TransactionType.EXPENSE).build();

        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));
        when(categoryMapper.toResponse(c1)).thenReturn(new CategoryResponse(1L, "Salary", TransactionType.INCOME));
        when(categoryMapper.toResponse(c2)).thenReturn(new CategoryResponse(2L, "Food", TransactionType.EXPENSE));

        List<CategoryResponse> result = categoryService.findAll();

        assertThat(result).hasSize(2);
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Should return categories filtered by type")
    void shouldReturnCategoriesByType() {
        Category c1 = Category.builder().id(1L).name("Salary").type(TransactionType.INCOME).build();
        Category c2 = Category.builder().id(2L).name("Freelance").type(TransactionType.INCOME).build();

        when(categoryRepository.findByType(TransactionType.INCOME)).thenReturn(List.of(c1, c2));
        when(categoryMapper.toResponse(c1)).thenReturn(new CategoryResponse(1L, "Salary", TransactionType.INCOME));
        when(categoryMapper.toResponse(c2)).thenReturn(new CategoryResponse(2L, "Freelance", TransactionType.INCOME));

        List<CategoryResponse> result = categoryService.findByType(TransactionType.INCOME);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.type() == TransactionType.INCOME);
        verify(categoryRepository).findByType(TransactionType.INCOME);
    }

    @Test
    @DisplayName("Should delete category successfully")
    void shouldDeleteCategory() {
        Category entity = Category.builder().id(1L).name("Food").type(TransactionType.EXPENSE).build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));

        categoryService.delete(1L);

        verify(categoryRepository).delete(entity);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent category")
    void shouldThrowWhenDeletingNonExistentCategory() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.delete(99L));

        verify(categoryRepository, never()).delete(any());
    }
}
