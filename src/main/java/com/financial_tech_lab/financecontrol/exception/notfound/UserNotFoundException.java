package com.financial_tech_lab.financecontrol.exception.notfound;

import com.financial_tech_lab.financecontrol.exception.core.NotFoundException;

import java.util.Map;

public class UserNotFoundException extends NotFoundException {

    private static final String ERROR_CODE = "USER_NOT_FOUND";
    private static final String MESSAGE = "O usuário solicitado não foi encontrado.";

    public UserNotFoundException(Long userId) {
        super(ERROR_CODE, MESSAGE, Map.of("userId", userId));
    }
}
