package com.financial_tech_lab.financecontrol.exception.notfound;

import com.financial_tech_lab.financecontrol.exception.core.NotFoundException;

import java.util.Map;

public class TransactionNotFoundException extends NotFoundException {

    private static final String ERROR_CODE = "TRANSACTION_NOT_FOUND";
    private static final String MESSAGE = "A transação solicitada não foi encontrada.";

    public TransactionNotFoundException(Long transactionId) {
        super(ERROR_CODE, MESSAGE, Map.of("transactionId", transactionId));
    }
}
