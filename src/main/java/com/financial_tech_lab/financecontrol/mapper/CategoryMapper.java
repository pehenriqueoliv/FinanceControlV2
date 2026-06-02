package com.financial_tech_lab.financecontrol.mapper;

import com.financial_tech_lab.financecontrol.dto.request.CategoryRequest;
import com.financial_tech_lab.financecontrol.dto.response.CategoryResponse;
import com.financial_tech_lab.financecontrol.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .type(request.type())
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType()
        );
    }
}
