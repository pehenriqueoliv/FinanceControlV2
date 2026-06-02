package com.financial_tech_lab.financecontrol.messaging.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/*
 * DTO que representa a mensagem publicada na fila SQS.
 * Não usamos record por compatibilidade com serialização Jackson
 * (mesma razão do BalanceSummaryResponse).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionNotificationMessage implements Serializable {

    private Long transactionId;
    private Long userId;
    private String userEmail;
    private String description;
    private BigDecimal amount;
    private String type;

    @JsonCreator
    public static TransactionNotificationMessage of(
            @JsonProperty("transactionId") Long transactionId,
            @JsonProperty("userId") Long userId,
            @JsonProperty("userEmail") String userEmail,
            @JsonProperty("description") String description,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("type") String type
    ) {
        return new TransactionNotificationMessage(
                transactionId, userId, userEmail, description, amount, type
        );
    }
}
