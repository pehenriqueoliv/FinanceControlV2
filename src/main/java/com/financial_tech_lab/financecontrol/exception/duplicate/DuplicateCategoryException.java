package com.financial_tech_lab.financecontrol.exception.duplicate;

import com.financial_tech_lab.financecontrol.exception.core.DuplicateResourceException;

import java.util.Map;

public class DuplicateCategoryException extends DuplicateResourceException {

    private static final String ERROR_CODE = "DUPLICATE_CATEGORY";
    private static final String MESSAGE = "Já existe uma categoria cadastrada com este nome.";

    public DuplicateCategoryException(String categoryName) {
        super(ERROR_CODE, MESSAGE, Map.of("categoryName", categoryName));
    }
}
