package com.financial_tech_lab.financecontrol.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/*
 * Response padronizado para todos os erros da API.
 * Baseado no padrão RFC 7807 (Problem Details for HTTP APIs) com adaptações.
 *
 * Campos:
 * - timestamp: momento exato em que o erro ocorreu (UTC)
 * - status: código HTTP
 * - errorCode: identificador único para o frontend (ex: TRANSACTION_NOT_FOUND)
 * - title: título resumido do erro
 * - detail: mensagem amigável para o usuário final
 * - instance: path da requisição que gerou o erro
 * - traceId: UUID único que correlaciona response e logs (para suporte/debug)
 * - metadata: dados estruturados sobre o erro (IDs, valores, etc.)
 * - errors: lista de erros de validação de campo (usado apenas em 400)
 *
 * @JsonInclude(NON_NULL) faz com que campos nulos sejam omitidos do JSON,
 * mantendo o response limpo.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetail {

    private final Instant timestamp;
    private final int status;
    private final String errorCode;
    private final String title;
    private final String detail;
    private final String instance;
    private final String traceId;
    private final Map<String, Object> metadata;
    private final List<FieldError> errors;

    /*
     * Representa um erro de validação específico de um campo.
     * Usado nas respostas 400 quando o @Valid falha.
     */
    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String message;
        private final Object rejectedValue;
    }
}
