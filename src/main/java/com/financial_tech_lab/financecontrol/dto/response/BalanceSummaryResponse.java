package com.financial_tech_lab.financecontrol.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceSummaryResponse implements Serializable {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;

    @JsonCreator
    public static BalanceSummaryResponse of(
            @JsonProperty("totalIncome") BigDecimal totalIncome,
            @JsonProperty("totalExpense") BigDecimal totalExpense,
            @JsonProperty("balance") BigDecimal balance
    ) {
        return new BalanceSummaryResponse(totalIncome, totalExpense, balance);
    }
}
