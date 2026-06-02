package com.financial_tech_lab.financecontrol.exception.notfound;

import com.financial_tech_lab.financecontrol.exception.core.NotFoundException;

import java.util.Map;

public class CategoryNotFoundException extends NotFoundException {

    private static final String ERROR_CODE = "CATEGORY_NOT_FOUND";
    private static final String MESSAGE = "A categoria solicitada não foi encontrada.";

    public CategoryNotFoundException(Long categoryId) {
        super(ERROR_CODE, MESSAGE, Map.of("categoryId", categoryId));
    }
}
