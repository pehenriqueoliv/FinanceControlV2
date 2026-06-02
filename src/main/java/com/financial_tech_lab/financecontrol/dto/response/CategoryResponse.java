package com.financial_tech_lab.financecontrol.dto.response;

import com.financial_tech_lab.financecontrol.entity.TransactionType;

public record CategoryResponse(
        Long id,
        String name,
        TransactionType type
) {}
