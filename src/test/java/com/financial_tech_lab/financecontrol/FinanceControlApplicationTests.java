package com.financial_tech_lab.financecontrol;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FinanceControlApplicationTests {

    /*
     * Mocks necessários para o contexto de testes.
     * Redis e SQS não estão disponíveis no ambiente de CI/testes unitários.
     * - RedisConnectionFactory: requerido por RedisConfig para criar o CacheManager
     * - SqsTemplate: requerido por NotificationProducer (SQS desabilitado via property)
     * Os mocks satisfazem as dependências sem tentar conexões reais.
     */
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private SqsTemplate sqsTemplate;

    @Test
    void contextLoads() {
    }
}
