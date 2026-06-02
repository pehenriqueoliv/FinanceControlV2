package com.financial_tech_lab.financecontrol.exception;

import com.financial_tech_lab.financecontrol.exception.core.BusinessRuleException;
import com.financial_tech_lab.financecontrol.exception.core.DomainException;
import com.financial_tech_lab.financecontrol.exception.core.DuplicateResourceException;
import com.financial_tech_lab.financecontrol.exception.core.NotFoundException;
import com.financial_tech_lab.financecontrol.exception.response.ProblemDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/*
 * Handler global de exceções da aplicação.
 *
 * Estratégia de logging (Opção B - métodos separados):
 * - Erros 4xx (culpa do cliente): WARN sem stack trace, apenas contexto
 * - Erros 5xx (culpa do servidor): ERROR com stack trace completo
 *
 * Todo response inclui um traceId (UUID) que também vai no log,
 * permitindo correlacionar exatamente um erro reportado com seus logs.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ============================================================
    // 404 - Recurso não encontrado
    // ============================================================
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            NotFoundException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        logClientError(traceId, ex, request, HttpStatus.NOT_FOUND);

        ProblemDetail body = ProblemDetail.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode(ex.getErrorCode())
                .title("Recurso não encontrado")
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .traceId(traceId)
                .metadata(ex.getMetadata())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ============================================================
    // 409 - Conflito por duplicidade
    // ============================================================
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(
            DuplicateResourceException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        logClientError(traceId, ex, request, HttpStatus.CONFLICT);

        ProblemDetail body = ProblemDetail.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .errorCode(ex.getErrorCode())
                .title("Recurso duplicado")
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .traceId(traceId)
                .metadata(ex.getMetadata())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // ============================================================
    // 422 - Regra de negócio violada
    // ============================================================
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRule(
            BusinessRuleException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        logClientError(traceId, ex, request, HttpStatus.UNPROCESSABLE_ENTITY);

        ProblemDetail body = ProblemDetail.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .errorCode(ex.getErrorCode())
                .title("Regra de negócio violada")
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .traceId(traceId)
                .metadata(ex.getMetadata())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    // ============================================================
    // 400 - Validação do Bean Validation (@Valid)
    // ============================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        List<ProblemDetail.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ProblemDetail.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .toList();

        LOG.warn("[{}] 400 Bad Request - VALIDATION_FAILED - {} - {} campo(s) com erro",
                traceId, request.getRequestURI(), fieldErrors.size());

        ProblemDetail body = ProblemDetail.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_FAILED")
                .title("Dados inválidos")
                .detail("Os dados enviados não passaram na validação. Verifique os campos abaixo.")
                .instance(request.getRequestURI())
                .traceId(traceId)
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============================================================
    // 400 - JSON malformado ou ausente
    // ============================================================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMalformedJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        LOG.warn("[{}] 400 Bad Request - MALFORMED_JSON - {} - {}",
                traceId, request.getRequestURI(), ex.getMostSpecificCause().getMessage());

        ProblemDetail body = ProblemDetail.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("MALFORMED_JSON")
                .title("JSON inválido")
                .detail("O corpo da requisição não é um JSON válido ou está ausente.")
                .instance(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============================================================
    // 400 - Parâmetro obrigatório ausente
    // ============================================================
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        LOG.warn("[{}] 400 Bad Request - MISSING_PARAMETER - {} - parâmetro '{}' ausente",
                traceId, request.getRequestURI(), ex.getParameterName());

        ProblemDetail body = ProblemDetail.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("MISSING_PARAMETER")
                .title("Parâmetro obrigatório ausente")
                .detail("O parâmetro '" + ex.getParameterName() + "' é obrigatório e não foi informado.")
                .instance(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============================================================
    // 400 - Tipo de parâmetro incompatível
    // ============================================================
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        LOG.warn("[{}] 400 Bad Request - TYPE_MISMATCH - {} - parâmetro '{}' com valor inválido",
                traceId, request.getRequestURI(), ex.getName());

        String expectedType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "tipo desconhecido";

        ProblemDetail body = ProblemDetail.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("TYPE_MISMATCH")
                .title("Tipo de parâmetro inválido")
                .detail("O parâmetro '" + ex.getName() + "' deveria ser do tipo " + expectedType + ".")
                .instance(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============================================================
    // 409 - Violação de integridade no banco
    // ============================================================
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        LOG.warn("[{}] 409 Conflict - DATA_INTEGRITY_VIOLATION - {} - {}",
                traceId, request.getRequestURI(), ex.getMostSpecificCause().getMessage());

        ProblemDetail body = ProblemDetail.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .errorCode("DATA_INTEGRITY_VIOLATION")
                .title("Conflito de integridade")
                .detail("A operação viola uma restrição de integridade dos dados.")
                .instance(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // ============================================================
    // 500 - Fallback para qualquer outra exceção não tratada
    // ============================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(
            Exception ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        logServerError(traceId, ex, request);

        ProblemDetail body = ProblemDetail.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("INTERNAL_ERROR")
                .title("Erro interno do servidor")
                .detail("Ocorreu um erro inesperado. Nossa equipe foi notificada. "
                        + "Use o traceId acima para reportar o problema.")
                .instance(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // ============================================================
    // Métodos auxiliares de logging (Opção B - separados por categoria)
    // ============================================================

    /*
     * Log para erros 4xx (culpa do cliente).
     * WARN sem stack trace - não é problema nosso, mas é bom registrar.
     */
    private void logClientError(
            String traceId, DomainException ex, HttpServletRequest request, HttpStatus status) {
        LOG.warn("[{}] {} {} - {} - {} - {}",
                traceId,
                status.value(),
                status.getReasonPhrase(),
                ex.getErrorCode(),
                request.getRequestURI(),
                ex.getMessage());
    }

    /*
     * Log para erros 5xx (culpa do servidor).
     * ERROR com stack trace completo - precisamos investigar.
     */
    private void logServerError(String traceId, Exception ex, HttpServletRequest request) {
        LOG.error("[{}] 500 Internal Server Error - {} - {}",
                traceId,
                request.getRequestURI(),
                ex.getMessage(),
                ex);
    }

    /*
     * Gera um UUID único para cada erro.
     * Esse ID vai tanto no response (campo traceId) quanto nos logs,
     * permitindo correlação direta entre o erro reportado pelo cliente
     * e os logs do servidor.
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
