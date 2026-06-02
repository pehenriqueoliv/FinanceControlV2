package com.financial_tech_lab.financecontrol.exception.duplicate;

import com.financial_tech_lab.financecontrol.exception.core.DuplicateResourceException;

import java.util.Map;

public class DuplicateEmailException extends DuplicateResourceException {

    private static final String ERROR_CODE = "DUPLICATE_EMAIL";
    private static final String MESSAGE = "Já existe um usuário cadastrado com este e-mail.";

    public DuplicateEmailException(String email) {
        super(ERROR_CODE, MESSAGE, Map.of("email", email));
    }
}
