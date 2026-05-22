# 04 — Infraestrutura, Docker e Ambiente Local

Cobre tudo que sobe e dá suporte ao serviço fora do código de aplicação: empacotamento em
container (`Dockerfile`), atalhos de comando (`Makefile`), o ambiente local de apoio
(`setup/`) e a observabilidade.

---

## `Dockerfile`

**Objetivo:** construir a **imagem Docker** do serviço usando um build **multi-stage** —
um estágio compila o projeto e outro, bem mais enxuto, apenas executa o `.jar`. Isso resulta
em uma imagem final pequena e segura.

```dockerfile
FROM maven:3.9.8-eclipse-temurin-21 AS builder
```
**Linha 1 —** Primeiro estágio, chamado `builder`. Parte de uma imagem que já contém
**Maven 3.9.8 + JDK 21 (Eclipse Temurin)** — tem tudo para compilar.

```dockerfile
WORKDIR /app
```
**Linha 2 —** Define `/app` como diretório de trabalho dentro do container.

```dockerfile
COPY pom.xml .
COPY checkstyle.xml .
RUN mvn dependency:go-offline
```
**Linhas 4–6 —** Copia primeiro **apenas** o `pom.xml` e o `checkstyle.xml` e baixa todas as
dependências (`dependency:go-offline`). Esse passo é separado de propósito: enquanto o
`pom.xml` não mudar, o Docker **reaproveita o cache** dessa camada, evitando rebaixar
dependências a cada build.

```dockerfile
COPY src ./src
RUN mvn clean package
```
**Linhas 8–9 —** Só então copia o código-fonte (`src`) e roda `mvn clean package`, que
valida (Checkstyle), compila, testa e empacota o `.jar` em `target/`.

```dockerfile
FROM eclipse-temurin:21-jre AS launcher
```
**Linha 11 —** Segundo estágio, `launcher`. Parte de uma imagem que contém **apenas o JRE 21**
(sem Maven nem JDK) — menor e com menor superfície de ataque.

```dockerfile
WORKDIR /app
```
**Linha 13 —** Diretório de trabalho do estágio final.

```dockerfile
COPY --from=builder /app/target/*.jar app.jar
```
**Linha 15 —** Copia o `.jar` gerado **do estágio `builder`** para o estágio final,
renomeando-o para `app.jar`. Nada do Maven/JDK vem junto.

```dockerfile
RUN groupadd -r spring && useradd -r -g spring spring
USER spring
```
**Linhas 17–18 —** Cria um grupo e um usuário de sistema chamados `spring` e passa a rodar
como esse **usuário não-root** — boa prática de segurança (limita o impacto caso o container
seja comprometido).

```dockerfile
EXPOSE 3000
```
**Linha 20 —** Documenta que o container expõe a porta `3000`. (É informativo; a porta real
da aplicação vem de `SERVER_PORT` no `application.yaml` — alinhe os dois ao usar.)

```dockerfile
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```
**Linha 22 —** Comando executado quando o container inicia: roda a aplicação com
`java -jar /app/app.jar`.

---

## `Makefile`

**Objetivo:** oferecer **atalhos de linha de comando** para tarefas repetitivas do
desenvolvimento — subir/parar o ambiente local e interagir com a infraestrutura simulada.
Use com `make <alvo>` (ex.: `make up`).

```makefile
all: up
```
**Linha 1 —** Alvo padrão (`make` sem argumento) — equivale a `make up`.

```makefile
up:
	@cd setup && docker-compose up -d
```
**Linhas 3–4 —** `make up`: entra em `setup/` e sobe todos os contêineres em segundo plano
(`-d`, detached). O `@` evita ecoar o comando no terminal.

```makefile
stop:
	@cd setup && docker-compose stop
```
**Linhas 6–7 —** `make stop`: para os contêineres **sem removê-los** (preserva volumes e
estado).

```makefile
destroy:
	@cd setup && docker-compose stop && docker-compose rm -f
```
**Linhas 9–10 —** `make destroy`: para e **remove** os contêineres (`rm -f`). Use para limpar
o ambiente.

```makefile
logs:
	@cd setup && docker-compose logs -f
```
**Linhas 12–13 —** `make logs`: acompanha os logs de todos os serviços em tempo real (`-f`,
follow).

```makefile
prettier:
	@mvn prettier:write
```
**Linhas 15–16 —** `make prettier`: formata o código Java automaticamente.

```makefile
lint:
	@mvn prettier:check
```
**Linhas 18–19 —** `make lint`: apenas **verifica** se o código está formatado (não altera),
útil em CI.

```makefile
get-secret:
	@cd setup && docker-compose exec -T financial-lab-localstack bash -c 'awslocal secretsmanager get-secret-value --region "us-east-1" --secret-id "$(secret)"'
```
**Linhas 21–22 —** `make get-secret secret=<id>`: lê um segredo do **Secrets Manager
simulado** dentro do container do LocalStack (região `us-east-1`, a mesma usada pelo
`init.sh`). O `$(secret)` é um parâmetro passado na chamada.

```makefile
download-bucket:
	@aws --region "us-east-1" --endpoint-url=http://localhost:5566 s3 cp s3://$(bucket) ./setup/localstack/s3-saidas/$(bucket) --recursive
```
**Linhas 24–25 —** `make download-bucket bucket=<nome>`: baixa **recursivamente** o conteúdo
de um bucket S3 do LocalStack (endpoint local `http://localhost:5566`, região `us-east-1`)
para `setup/localstack/s3-saidas/`.

```makefile
con-aws:
	@cd setup && docker-compose exec -it financial-lab-localstack /bin/bash
```
**Linhas 27–28 —** `make con-aws`: abre um **shell interativo** dentro do container do
LocalStack, útil para depurar a AWS simulada com a CLI `awslocal`.

---

## `setup/docker-compose.yml`

**Objetivo:** orquestra os **serviços de apoio locais** necessários para rodar o
microsserviço durante o desenvolvimento: AWS simulada, cache, banco e monitoramento.

```yaml
services:
```
**Linha 1 —** Início da lista de serviços (contêineres).

```yaml
  financial-lab-localstack:
    image: localstack/localstack:3.6.0
    environment:
      - DATA_DIR=./localstack/s3-entradas/
      - SERVICES=s3,sqs,secretsmanager
      - DEFAULT_REGION=us-east-1
    volumes:
      - ./localstack/init:/etc/localstack/init/ready.d
      - ./localstack/s3-entradas:/s3-files
      - ./localstack/secrets:/secrets
    ports:
      - "5566-5599:4566-4599"
```
**Linhas 2–13 —** O **LocalStack**, que emula serviços AWS localmente:
- `image`: versão `3.6.0` do LocalStack.
- `DATA_DIR`: diretório de persistência de dados.
- `SERVICES=s3,sqs,secretsmanager`: emula apenas **S3**, **SQS** e **Secrets Manager**.
- `DEFAULT_REGION=us-east-1`: região padrão.
- Volumes: monta o script de init em `ready.d` (executado quando o LocalStack fica pronto),
  e diretórios para arquivos S3 e secrets.
- `ports`: mapeia a faixa `5566-5599` (host) para `4566-4599` (container) — por isso a CLI
  local usa o endpoint `localhost:5566`.

```yaml
  financial-lab-redis:
    image: "redis"
    ports:
      - "6379:6379"
    environment:
      - REDIS_REPLICATION_MODE=master
      - REDIS_PASSWORD={{REDIS_PASSWORD}}
```
**Linhas 14–20 —** O **Redis** (cache em memória), na porta padrão `6379`, configurado como
nó `master`. O valor `{{REDIS_PASSWORD}}` é um **placeholder de template** — deve ser
substituído por uma senha real (ou injetado por uma ferramenta de template) antes de subir.

```yaml
  financial-lab-postgresql:
    image: postgres
    restart: always
    ports:
      - "5433:5433"
    environment:
      - POSTGRES_USER={{POSTGRES_USER}}
      - POSTGRES_PASSWORD={{POSTGRES_PASSWORD}}
      - POSTGRES_DB={{POSTGRES_DB}}
    volumes:
      - ./db/dumps:/docker-entrypoint-initdb.d/
```
**Linhas 22–32 —** O **PostgreSQL**:
- `restart: always`: reinicia automaticamente se cair.
- Porta `5433` no host mapeada para a `5433` do container. A porta `5433` no host coincide
  com o default do `JDBC_URL` no `application.yaml`, então a aplicação conecta sem
  configuração extra; usar `5433` no host também evita conflito com um PostgreSQL local
  rodando na `5433`.
- `POSTGRES_USER/PASSWORD/DB`: também **placeholders** a serem substituídos.
- O volume monta `db/dumps` em `/docker-entrypoint-initdb.d/` — o PostgreSQL executa
  **automaticamente** os `.sql` desse diretório (em ordem) na **primeira** inicialização do
  banco. É aqui que `V_0_init.sql` e `V_0_insert.sql` rodam.

```yaml
  financial-lab-prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./observability/prometheus:/etc/prometheus
```
**Linhas 34–39 —** O **Prometheus** (coleta de métricas), na porta padrão `9090`. Monta a
pasta `observability/prometheus` como sua configuração, de onde lê o `prometheus.yml`.

---

## `setup/localstack/init/init.sh`

**Objetivo:** script executado pelo LocalStack quando ele fica pronto. **Provisiona os
recursos AWS simulados** que o serviço espera encontrar: buckets S3, filas SQS (com
dead-letter queue) e um segredo no Secrets Manager.

```bash
#!/bin/bash

DEFAULT_REGION="us-east-1"
```
**Linhas 1–3 —** Shebang (indica que é um script bash) e definição da região padrão.

```bash
buckets=(
  "bucket_example"
)

queues=(
  "queue_example"
)
```
**Linhas 5–11 —** Dois arrays com os recursos a criar: um bucket S3 (`bucket_example`) e uma
fila SQS (`queue_example`). São exemplos — substitua pelos nomes reais do seu serviço.

```bash
for i in ${buckets[*]}; do
  awslocal s3 mb s3://${i}
  awslocal s3api put-bucket-acl --bucket ${i} --acl public-read
done
```
**Linhas 13–16 —** Para cada bucket da lista: cria o bucket (`s3 mb` = make bucket) e define a
ACL como `public-read` (leitura pública). `awslocal` é a CLI da AWS apontada para o
LocalStack.

```bash
if [ -d "/s3-files" ]; then
  for dir in "/s3-files"/*/; do
    bucket_name=$(basename "$dir")
    echo "Sincronizando '$dir' com o bucket S3 '$bucket_name'..."
    awslocal s3 sync "$dir" "s3://$bucket_name/"
  done
fi
```
**Linhas 18–24 —** Se existir o diretório `/s3-files` (montado do host), para cada subpasta
ele sincroniza o conteúdo local com o bucket S3 de mesmo nome — útil para pré-carregar
arquivos no S3 simulado.

```bash
for i in ${queues[*]}; do
  awslocal sqs create-queue --region ${DEFAULT_REGION} --queue-name "${i}_DLQ"
  awslocal sqs create-queue --region ${DEFAULT_REGION} --queue-name "${i}" \
    --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:${DEFAULT_REGION}:000000000000:${i}_DLQ\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"}"
done
```
**Linhas 26–30 —** Para cada fila: primeiro cria a **DLQ** (Dead-Letter Queue, `<nome>_DLQ`)
e depois a fila principal, configurando uma **RedrivePolicy** que envia mensagens para a DLQ
após **3 tentativas** de processamento falhas (`maxReceiveCount: 3`). É o padrão de
resiliência recomendado nas guidelines.

```bash
if [ -f "/secrets/structure_credentials" ]; then
  read structure_CLIENT_ID < <(sed -n '1p' "/secrets/structure_credentials")
  read structure_CLIENT_SECRET < <(sed -n '2p' "/secrets/structure_credentials")
else
  structure_CLIENT_ID="client_FINANCIAL-LAB"
  structure_CLIENT_SECRET="kMc0bIbrS6B5RxOg6RLIPZwARR1Cz8H3"
fi
```
**Linhas 32–38 —** Define as credenciais do serviço: se existir o arquivo
`/secrets/structure_credentials`, lê o `client_id` da primeira linha e o `client_secret` da
segunda; caso contrário, usa **valores padrão de desenvolvimento**. (Esses defaults são
apenas para ambiente local; nunca devem ir para produção.)

```bash
awslocal secretsmanager create-secret \
    --region "${DEFAULT_REGION}" \
    --name "financial_lab/structure_credentials" \
    --description "Auth credentials structure" \
    --secret-string '{"client_id":"'"$structure_CLIENT_ID"'","client_secret":"'"$structure_CLIENT_SECRET"'"}'
```
**Linhas 40–44 —** Cria, no Secrets Manager simulado, o segredo
`financial_lab/structure_credentials` contendo um JSON com `client_id` e `client_secret`. É
assim que a aplicação obteria credenciais de forma análoga à produção (onde viriam do AWS
Secrets Manager real).

---

## `setup/db/dumps/V_0_init.sql` e `V_0_insert.sql`

**Objetivo:** scripts SQL executados automaticamente pelo PostgreSQL na primeira
inicialização (via `docker-entrypoint-initdb.d`). A convenção de nome (`V_0_...`) segue o
padrão de versionamento de migrações.

- **`V_0_init.sql`** — destinado à **criação do schema e das tabelas** iniciais.
- **`V_0_insert.sql`** — destinado à **carga inicial (seed)** de dados.

> Ambos estão **atualmente vazios** — são placeholders. Ao criar um serviço real, preencha
> aqui o schema inicial. As guidelines recomendam, no longo prazo, migrar para **Flyway**
> em vez de scripts soltos no init do container.

---

## `setup/observability/prometheus/prometheus.yml`

**Objetivo:** configurar o **Prometheus** para coletar (scrape) as métricas expostas pela
aplicação Spring Boot.

```yaml
global:
  scrape_interval: 5s
```
**Linhas 1–2 —** Configuração global: coleta métricas a cada **5 segundos**.

```yaml
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8081']
```
**Linhas 3–7 —** Define um job de scraping chamado `spring-boot-app`:
- `metrics_path: /actuator/prometheus`: endpoint do Actuator que expõe as métricas (ver doc
  `02`).
- `targets: host.docker.internal:8081`: alvo da coleta. `host.docker.internal` é o nome DNS
  que, de dentro do container, aponta para a **máquina host** — assim o Prometheus (no
  Docker) alcança a aplicação rodando no host na porta `8081`.

> A porta `8081` deve corresponder ao `PROMETHEUS_PORT` configurado no `application.yaml`.

---

## `setup/observability/grafana/.gitkeep`

**Objetivo:** arquivo **vazio** cuja única função é fazer o Git versionar a pasta
`grafana/` (o Git não rastreia diretórios vazios). É um placeholder para a futura
configuração de dashboards do Grafana, prevista nas guidelines de observabilidade.
