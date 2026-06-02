package com.financial_tech_lab.financecontrol.messaging.producer;

import com.financial_tech_lab.financecontrol.messaging.dto.TransactionNotificationMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
 * Producer responsável por publicar mensagens na fila SQS.
 * Usa SqsTemplate (similar ao JdbcTemplate ou RestTemplate) que é o
 * mecanismo principal do spring-cloud-aws-starter-sqs para envio.
 */
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationProducer.class);
    private static final String QUEUE_NAME = "transaction-notifications";

    private final SqsTemplate sqsTemplate;

    public void publishTransactionCreated(TransactionNotificationMessage message) {
        LOG.info(
                "Publicando mensagem na fila '{}': transactionId={}",
                QUEUE_NAME,
                message.getTransactionId()
        );

        sqsTemplate.send(to -> to.queue(QUEUE_NAME).payload(message));

        LOG.info("Mensagem publicada com sucesso");
    }
}
