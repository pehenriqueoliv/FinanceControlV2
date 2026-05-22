# 02 — Código-Fonte (`src/`) explicado linha a linha

Este documento detalha **cada arquivo de código-fonte e configuração da aplicação**,
explicando o que cada linha faz.

---

## `src/main/java/com/financial_tech_lab/structure/StructureApplication.java`

**Objetivo:** é o **ponto de entrada (bootstrap)** da aplicação. É a classe que o Java
executa para iniciar todo o servidor Spring Boot.

```java
package com.financial_tech_lab.structure;
```
**Linha 1 —** Declara o pacote da classe. O pacote `com.financial_tech_lab.structure`
reflete o domínio invertido da organização (`financial_tech_lab`) seguido do nome do serviço
(`structure`). Toda a aplicação fica sob esse pacote-raiz; o Spring usa isso para o
*component scan* (varredura de componentes).

```java
import org.springframework.boot.SpringApplication;
```
**Linha 3 —** Importa a classe utilitária `SpringApplication`, responsável por inicializar e
executar a aplicação Spring Boot (cria o contexto, sobe o servidor embarcado, etc.).

```java
import org.springframework.boot.autoconfigure.SpringBootApplication;
```
**Linha 4 —** Importa a anotação `@SpringBootApplication`, que ativa a autoconfiguração do
Spring Boot.

```java
@SpringBootApplication
```
**Linha 6 —** Anotação que marca esta como a classe principal. Ela combina três anotações:
`@Configuration` (permite definir beans), `@EnableAutoConfiguration` (liga a
autoconfiguração baseada nas dependências do classpath) e `@ComponentScan` (varre o pacote
atual e subpacotes em busca de componentes Spring — controllers, services, etc.).

```java
public class StructureApplication {
```
**Linha 7 —** Declaração da classe pública. Por convenção do Spring Boot, ela fica no
pacote-raiz para que o *component scan* alcance todos os subpacotes do serviço.

```java
    public static void main(String[] args) {
```
**Linha 9 —** Método `main` padrão do Java — o ponto exato onde a JVM começa a executar.
`args` recebe os argumentos passados na linha de comando.

```java
        SpringApplication.run(StructureApplication.class, args);
```
**Linha 10 —** Inicia a aplicação. `run(...)` cria o contexto de aplicação Spring, dispara a
autoconfiguração, sobe o servidor e mantém o processo ativo. O primeiro parâmetro indica a
classe de configuração principal; o segundo repassa os argumentos da linha de comando.

```java
    }
}
```
**Linhas 11–12 —** Fecham o método `main` e a classe.

---

## `src/main/resources/application.yaml`

**Objetivo:** arquivo central de **configuração da aplicação**. O Spring Boot o lê
automaticamente na inicialização. Define banco de dados, cache, integrações AWS, cliente
HTTP (Feign), servidor web/SSL e exposição de métricas.

> **Atenção:** muitos valores usam a sintaxe `${VARIAVEL}` ou `${VARIAVEL:default}`, ou seja,
> vêm de **variáveis de ambiente**. Quando não há valor padrão (`:default`), a variável é
> **obrigatória** — sem ela a aplicação falha ao subir.

```yaml
spring:
  application:
    name: structure
```
**Linhas 1–3 —** Define o nome lógico da aplicação como `structure`. Esse nome aparece em
logs, em métricas e é usado por ferramentas de service discovery/tracing.

```yaml
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```
**Linhas 4–7 —** Limites de upload. `max-file-size` é o tamanho máximo de um único arquivo
(10 MB) e `max-request-size` é o tamanho máximo da requisição inteira (também 10 MB,
considerando múltiplos arquivos + campos).

```yaml
  jpa:
    properties:
      hibernate:
        default_schema: financial_lab_structure
```
**Linhas 8–11 —** Configuração do JPA/Hibernate. Define `financial_lab_structure` como o
**schema padrão** do PostgreSQL onde as tabelas serão criadas/consultadas. (Requer as
dependências de JPA, ainda não declaradas no `pom.xml`.)

```yaml
  datasource:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    driver-class-name: org.postgresql.Driver
    url: ${JDBC_URL:jdbc:postgresql://localhost:5433/financial-lab-structure}
    username: ${DB_USER}
    password: ${DB_PASS}
```
**Linhas 12–17 —** Conexão com o banco:
- `database-platform` / `driver-class-name`: dizem ao Hibernate para usar o dialeto e o
  driver do **PostgreSQL**.
- `url`: a string de conexão JDBC. Usa a variável `JDBC_URL`; se ela não existir, cai no
  **default** `jdbc:postgresql://localhost:5433/financial-lab-structure` (note a porta
  `5433`).
- `username` / `password`: vêm **obrigatoriamente** das variáveis `DB_USER` e `DB_PASS`.

```yaml
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      password: ${REDIS_PASSWORD}
```
**Linhas 19–23 —** Conexão com o **Redis** (cache/estruturas em memória). Host, porta e senha
são todos obrigatórios via variáveis de ambiente.

```yaml
aws:
  region: ${AWS_REGION}
  s3:
    user-documents: ${S3_USER_DOCUMENTS}
    path-user-documents: ${S3_PATH_USER_DOCUMENTS}
```
**Linhas 25–29 —** Configurações **customizadas** da AWS (não são propriedades padrão do
Spring; seriam lidas por uma classe `@ConfigurationProperties` própria). Definem a região e,
para o S3, o nome do bucket de documentos de usuário (`user-documents`) e o caminho/prefixo
dentro dele (`path-user-documents`).

```yaml
feign:
  client:
    config:
      default:
        logger-level: warn
  okhttp:
    enabled: true
```
**Linhas 31–37 —** Configuração do **Feign** (cliente HTTP declarativo do Spring Cloud, usado
para chamar outros serviços). `logger-level: warn` reduz o ruído de log do cliente para o
nível de aviso; `okhttp.enabled: true` faz o Feign usar o **OkHttp** como cliente HTTP
subjacente (melhor pool de conexões/desempenho).

```yaml
server:
  port: ${SERVER_PORT}
  ssl:
    enabled: ${ENABLE_SSL}
    client-auth: none
    key-store: ${SSL_CERT_PATH}
    key-store-password: ${SSL_CERT_PASS}
    key-store-type: ${SSL_STORE_TYPE}
    key-alias: ${SSL_CERT_ALIAS}
```
**Linhas 39–47 —** Configuração do servidor web embarcado:
- `port`: porta HTTP da aplicação (obrigatória, via `SERVER_PORT`).
- `ssl.enabled`: liga/desliga HTTPS conforme `ENABLE_SSL`.
- `client-auth: none`: **não** exige certificado do cliente (sem mTLS).
- `key-store` / `key-store-password` / `key-store-type` / `key-alias`: localização, senha,
  formato (ex.: PKCS12/JKS) e alias do certificado dentro do keystore. Todos vêm de
  variáveis de ambiente para não expor segredos no código.

```yaml
management:
  server:
    port: ${PROMETHEUS_PORT}
  endpoints:
    web:
      exposure:
        include:
          - prometheus
          - health
  metrics:
    export:
      prometheus:
        enabled: true
```
**Linhas 49–61 —** Configuração do **Spring Boot Actuator** (monitoramento):
- `server.port`: expõe os endpoints de gestão em uma **porta separada** da aplicação
  (definida por `PROMETHEUS_PORT`), uma boa prática de segurança.
- `endpoints.web.exposure.include`: expõe **apenas** os endpoints `prometheus` (métricas) e
  `health` (saúde) — tudo o mais permanece fechado.
- `metrics.export.prometheus.enabled: true`: habilita a exportação de métricas no formato
  Prometheus.

> O `prometheus.yml` (ver doc `04`) faz scraping em `/actuator/prometheus`. Garanta que a
> porta configurada lá (`8081` no exemplo) corresponda ao valor de `PROMETHEUS_PORT`.

---

## `src/test/java/com/financial_tech_lab/structure/StructureApplicationTests.java`

**Objetivo:** **teste de fumaça (smoke test)** que verifica se o contexto da aplicação Spring
sobe sem erros. É o teste mínimo gerado junto com qualquer projeto Spring Boot e serve de
base para os testes futuros.

```java
package com.financial_tech_lab.structure;
```
**Linha 1 —** Mesmo pacote da aplicação. Manter o teste no mesmo pacote facilita o acesso a
componentes e mantém a organização espelhada entre `main` e `test`.

```java
import org.junit.jupiter.api.Test;
```
**Linha 3 —** Importa a anotação `@Test` do **JUnit 5 (Jupiter)**, que marca um método como
caso de teste.

```java
import org.springframework.boot.test.context.SpringBootTest;
```
**Linha 4 —** Importa a anotação `@SpringBootTest`, que carrega o **contexto completo** do
Spring Boot durante o teste (como se a aplicação estivesse subindo de verdade).

```java
@SpringBootTest
```
**Linha 6 —** Indica que este é um teste de integração que inicializa o contexto da
aplicação inteiro.

```java
class StructureApplicationTests {
```
**Linha 7 —** Classe de teste. Não precisa ser `public` no JUnit 5.

```java
    @Test
    void contextLoads() {}
```
**Linhas 9–10 —** O caso de teste. O corpo está **vazio de propósito**: o teste passa se o
contexto Spring conseguir ser carregado sem lançar exceção. É uma verificação rápida de que
a configuração e o *wiring* de beans estão consistentes.

```java
}
```
**Linha 11 —** Fecha a classe.

---

## Variáveis de ambiente exigidas pela aplicação

Resumo das variáveis referenciadas no `application.yaml`. As marcadas como **obrigatórias**
não têm valor padrão — a aplicação não sobe sem elas.

| Variável | Uso | Obrigatória? |
|----------|-----|--------------|
| `JDBC_URL` | URL de conexão JDBC do PostgreSQL | Não (default `jdbc:postgresql://localhost:5433/financial-lab-structure`) |
| `DB_USER` | Usuário do banco | Sim |
| `DB_PASS` | Senha do banco | Sim |
| `REDIS_HOST` | Host do Redis | Sim |
| `REDIS_PORT` | Porta do Redis | Sim |
| `REDIS_PASSWORD` | Senha do Redis | Sim |
| `AWS_REGION` | Região AWS | Sim |
| `S3_USER_DOCUMENTS` | Nome do bucket S3 de documentos | Sim |
| `S3_PATH_USER_DOCUMENTS` | Prefixo/caminho dentro do bucket | Sim |
| `SERVER_PORT` | Porta HTTP da aplicação | Sim |
| `ENABLE_SSL` | Liga/desliga HTTPS (`true`/`false`) | Sim |
| `SSL_CERT_PATH` | Caminho do keystore SSL | Sim (se SSL ligado) |
| `SSL_CERT_PASS` | Senha do keystore | Sim (se SSL ligado) |
| `SSL_STORE_TYPE` | Tipo do keystore (PKCS12/JKS) | Sim (se SSL ligado) |
| `SSL_CERT_ALIAS` | Alias do certificado no keystore | Sim (se SSL ligado) |
| `PROMETHEUS_PORT` | Porta dos endpoints do Actuator | Sim |
