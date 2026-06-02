package com.financial_tech_lab.financecontrol.dto.response;

import com.financial_tech_lab.financecontrol.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String description,
        BigDecimal amount,
        TransactionType type,
        LocalDate date,
        CategoryResponse category,
        Long userId,
        LocalDateTime createdAt
) {}
