package com.financial_tech_lab.financecontrol.dto.request;

import com.financial_tech_lab.financecontrol.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Type is required")
        TransactionType type,

        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotNull(message = "User is required")
        Long userId
) {}
