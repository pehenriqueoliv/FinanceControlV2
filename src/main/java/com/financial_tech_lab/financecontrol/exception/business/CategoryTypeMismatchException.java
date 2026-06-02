package com.financial_tech_lab.financecontrol.exception.business;

import com.financial_tech_lab.financecontrol.entity.TransactionType;
import com.financial_tech_lab.financecontrol.exception.core.BusinessRuleException;

import java.util.Map;

public class CategoryTypeMismatchException extends BusinessRuleException {

    private static final String ERROR_CODE = "CATEGORY_TYPE_MISMATCH";
    private static final String MESSAGE =
            "O tipo da categoria selecionada não é compatível com o tipo da transação.";

    public CategoryTypeMismatchException(TransactionType categoryType, TransactionType transactionType) {
        super(ERROR_CODE, MESSAGE, Map.of(
                "categoryType", categoryType.name(),
                "transactionType", transactionType.name()
        ));
    }
}
