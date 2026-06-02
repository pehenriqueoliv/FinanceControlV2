package com.financial_tech_lab.financecontrol.dto.request;

import com.financial_tech_lab.financecontrol.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        String name,

        @NotNull(message = "Category type is required")
        TransactionType type
) {}
