package com.financial_tech_lab.financecontrol.exception.core;

import java.util.Map;

/*
 * Categoria de exceções para conflitos de duplicidade.
 * Sempre resulta em HTTP 409 (Conflict).
 * Usada quando o cliente tenta criar um recurso que já existe (email, nome único, etc.).
 */
public abstract class DuplicateResourceException extends DomainException {

    protected DuplicateResourceException(String errorCode, String message, Map<String, Object> metadata) {
        super(errorCode, message, metadata);
    }
}
