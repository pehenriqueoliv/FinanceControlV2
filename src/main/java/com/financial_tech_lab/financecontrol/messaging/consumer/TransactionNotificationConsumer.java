package com.financial_tech_lab.financecontrol.messaging.consumer;

import com.financial_tech_lab.financecontrol.messaging.dto.TransactionNotificationMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransactionNotificationConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionNotificationConsumer.class);

    /*
     * Flag de simulação para testar o fluxo da DLQ.
     * Quando true, o consumer sempre lança exceção, forçando o SQS
     * a tentar 3 vezes e depois mover para a DLQ.
     * Em produção real esta flag não existiria.
     */
    @Value("${app.simulate-consumer-failure:false}")
    private boolean simulateFailure;

    @SqsListener("transaction-notifications")
    public void receiveTransactionNotification(TransactionNotificationMessage message) {
        LOG.info("=== MENSAGEM RECEBIDA DA FILA ===");
        LOG.info("Transaction ID: {}", message.getTransactionId());
        LOG.info("User: {} ({})", message.getUserId(), message.getUserEmail());
        LOG.info("Descrição: {}", message.getDescription());
        LOG.info("Tipo: {} | Valor: R$ {}", message.getType(), message.getAmount());

        if (simulateFailure) {
            LOG.error("[SIMULAÇÃO DE FALHA ATIVA] Lançando exceção propositalmente");
            throw new RuntimeException("Falha simulada para testar fluxo da DLQ");
        }

        LOG.info("[SIMULAÇÃO] Email enviado para {}", message.getUserEmail());
        LOG.info("=== FIM DO PROCESSAMENTO ===");
    }
}
