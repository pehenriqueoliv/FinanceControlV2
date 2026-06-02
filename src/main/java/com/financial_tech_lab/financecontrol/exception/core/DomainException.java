package com.financial_tech_lab.financecontrol.exception.core;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

/*
 * Classe base de todas as exceções de domínio do sistema.
 * Carrega três informações essenciais:
 * - errorCode: identificador único, usado pelo frontend para mapear traduções e ações
 * - message: mensagem amigável em português para o usuário final
 * - metadata: dados estruturados (IDs, valores, etc.) que NÃO devem aparecer na mensagem
 */
@Getter
public abstract class DomainException extends RuntimeException {

    private final String errorCode;
    private final Map<String, Object> metadata;

    protected DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.metadata = Collections.emptyMap();
    }

    protected DomainException(String errorCode, String message, Map<String, Object> metadata) {
        super(message);
        this.errorCode = errorCode;
        this.metadata = metadata == null ? Collections.emptyMap() : Map.copyOf(metadata);
    }
}
