package com.financial_tech_lab.financecontrol.exception.core;

import java.util.Map;

/*
 * Categoria de exceções para violações de regras de negócio.
 * Sempre resulta em HTTP 422 (Unprocessable Entity).
 * O cliente enviou dados sintaticamente válidos, mas que violam alguma regra.
 */
public abstract class BusinessRuleException extends DomainException {

    protected BusinessRuleException(String errorCode, String message, Map<String, Object> metadata) {
        super(errorCode, message, metadata);
    }
}
