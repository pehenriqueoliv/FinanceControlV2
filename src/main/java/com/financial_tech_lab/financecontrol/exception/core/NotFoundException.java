package com.financial_tech_lab.financecontrol.exception.core;

import java.util.Map;

/*
 * Categoria de exceções para recursos não encontrados.
 * Sempre resulta em HTTP 404.
 */
public abstract class NotFoundException extends DomainException {

    protected NotFoundException(String errorCode, String message, Map<String, Object> metadata) {
        super(errorCode, message, metadata);
    }
}
