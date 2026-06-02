package com.financial_tech_lab.financecontrol.messaging.consumer;

import com.financial_tech_lab.financecontrol.messaging.dto.TransactionNotificationMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
 * Consumer dedicado à Dead Letter Queue.
 * Quando uma mensagem falha 3 vezes na fila principal, o SQS a move
 * automaticamente para esta DLQ. Aqui apenas logamos para investigação.
 *
 * Em produção real, este consumer poderia:
 * - Persistir a mensagem em uma tabela de auditoria
 * - Enviar alerta para Slack/PagerDuty/Datadog
 * - Disparar um job de reprocessamento manual
 * - Notificar o time responsável via email
 */
@Component
public class DeadLetterQueueConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(DeadLetterQueueConsumer.class);

    @SqsListener("transaction-notifications-dlq")
    public void receiveDeadLetterMessage(TransactionNotificationMessage message) {
        LOG.error("=== MENSAGEM NA DLQ — REQUER INVESTIGAÇÃO ===");
        LOG.error("Transaction ID: {}", message.getTransactionId());
        LOG.error("User ID: {} | Email: {}", message.getUserId(), message.getUserEmail());
        LOG.error("Descrição: {}", message.getDescription());
        LOG.error("Tipo: {} | Valor: R$ {}", message.getType(), message.getAmount());
        LOG.error("Esta mensagem falhou 3 vezes no consumer principal.");
        LOG.error(
                "Ação recomendada: investigar logs do consumer principal e decidir entre reprocessar ou descartar."
        );
        LOG.error("=== FIM DA MENSAGEM NA DLQ ===");
    }
}
