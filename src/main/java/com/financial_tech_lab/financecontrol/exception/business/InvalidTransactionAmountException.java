package com.financial_tech_lab.financecontrol.exception.business;

import com.financial_tech_lab.financecontrol.exception.core.BusinessRuleException;

import java.math.BigDecimal;
import java.util.Map;

/*
 * Exceção preparada para uso futuro, quando regras adicionais
 * de valor de transação forem implementadas (ex: limite máximo,
 * valor não pode ser zero, etc.).
 */
public class InvalidTransactionAmountException extends BusinessRuleException {

    private static final String ERROR_CODE = "INVALID_TRANSACTION_AMOUNT";
    private static final String MESSAGE = "O valor da transação é inválido.";

    public InvalidTransactionAmountException(BigDecimal amount, String reason) {
        super(ERROR_CODE, MESSAGE, Map.of(
                "amount", amount,
                "reason", reason
        ));
    }
}
