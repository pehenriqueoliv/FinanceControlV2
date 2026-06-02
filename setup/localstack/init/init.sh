#!/bin/bash

DEFAULT_REGION="us-east-1"

# ============================================================
# Secret: credenciais do banco de dados
# Usado pelo Spring Cloud AWS Secrets Manager na inicialização
# da aplicação (perfil local) para injetar username/password
# do PostgreSQL.
# ============================================================
awslocal secretsmanager create-secret \
  --region "${DEFAULT_REGION}" \
  --name finance-control/database \
  --description "Credenciais do banco de dados do FinanceControl" \
  --secret-string '{"username":"root","password":"root"}'
echo "Secret 'finance-control/database' criado"

# ============================================================
# Fila SQS principal: notificações de transações
# ============================================================
awslocal sqs create-queue \
  --region "${DEFAULT_REGION}" \
  --queue-name transaction-notifications
echo "Fila SQS 'transaction-notifications' criada"

# ============================================================
# Dead Letter Queue: recebe mensagens que falharam 3 vezes
# na fila principal. Permite investigação isolada sem travar
# a fila principal.
# ============================================================
awslocal sqs create-queue \
  --region "${DEFAULT_REGION}" \
  --queue-name transaction-notifications-dlq
echo "DLQ 'transaction-notifications-dlq' criada"

# ============================================================
# Redrive policy: configura a fila principal para mover
# mensagens automaticamente para a DLQ após 3 tentativas
# de processamento sem sucesso.
# ============================================================
awslocal sqs set-queue-attributes \
  --region "${DEFAULT_REGION}" \
  --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/transaction-notifications \
  --attributes '{"RedrivePolicy":"{\"deadLetterTargetArn\":\"arn:aws:sqs:us-east-1:000000000000:transaction-notifications-dlq\",\"maxReceiveCount\":\"3\"}"}'
echo "Redrive policy configurada na fila principal (maxReceiveCount=3)"
