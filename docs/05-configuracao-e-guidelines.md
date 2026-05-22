# 05 — Configuração do Repositório e Guidelines de Engenharia

Cobre os arquivos de configuração do repositório (`.gitignore`, `.gitattributes`), o
`README.md` e as guidelines de engenharia em `.claude/`, que definem como os serviços devem
ser construídos.

---

## `README.md`

**Objetivo:** apresentação curta do microsserviço, explicando que ele é a **estrutura base**
para novos serviços e como utilizá-lo.

```markdown
# Financial Tech Lab

## Estrutura
Microsserviço Estrutura responsável por apresentar a estrutura inicial que será utilizada como base para
a criação dos novos serviços do backend.

### Utilização
Para melhor utilização e copy-paste do código, o ideal é clonar o repositório e copiar os arquivos para o novo serviço.
```
**Conteúdo —** Deixa claro o propósito do repositório (ser um template) e a forma de uso
recomendada: **clonar e copiar os arquivos** para o novo serviço. Esta pasta `docs/`
complementa o README com o detalhamento por arquivo.

---

## `.gitignore`

**Objetivo:** lista os arquivos e diretórios que o **Git deve ignorar** (não versionar) —
artefatos de build, metadados de IDEs e arquivos temporários.

```gitignore
target/
.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/
```
**Linhas 1–4 —** Ignora a pasta `target/` (saída do build Maven) e o `maven-wrapper.jar`
(baixado automaticamente). As linhas com `!` são **exceções** (re-incluem) para eventuais
pastas `target/` que existam dentro de `src/main` ou `src/test`.

```gitignore
### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache
```
**Linhas 6–13 —** Metadados do **Spring Tool Suite / Eclipse**.

```gitignore
### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr
```
**Linhas 15–19 —** Metadados do **IntelliJ IDEA** (configurações pessoais de IDE, que não
devem ser compartilhadas). Por isso a pasta `.idea/` não é documentada aqui.

```gitignore
### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/
```
**Linhas 21–29 —** Metadados e saídas de build do **NetBeans** (e a pasta genérica `build/`,
com as mesmas exceções de antes).

```gitignore
### VS Code ###
.vscode/
```
**Linhas 31–32 —** Configurações do **Visual Studio Code**.

---

## `.gitattributes`

**Objetivo:** padronizar o **fim de linha (EOL)** de certos arquivos, evitando problemas
entre Windows (CRLF) e Unix (LF) — importante porque os scripts do wrapper precisam do
formato certo para funcionar em cada sistema.

```gitattributes
/mvnw text eol=lf
*.cmd text eol=crlf
```
**Linha 1 —** Força o `mvnw` (script Unix) a sempre usar **LF** — caso contrário, não
executaria em Linux/macOS.
**Linha 2 —** Força arquivos `.cmd` (scripts Windows, como `mvnw.cmd`) a usarem **CRLF**, o
formato esperado pelo `cmd` do Windows.

---

## `.claude/claude.md`

**Objetivo:** documento de **guidelines de engenharia** do Financial Tech Lab. Não é código:
é o conjunto de **regras, padrões e princípios** que todos os serviços derivados deste
template devem seguir. Funciona tanto como referência para o time quanto como contexto para
ferramentas de IA.

Resumo das seções:

- **Project Overview / Tech Stack —** descreve o propósito (simular sistemas financeiros
  reais com arquitetura de produção) e a stack-alvo (Java 21, Spring Boot, PostgreSQL,
  Docker, AWS, Redis, DynamoDB, GitHub Actions, Testcontainers, Terraform, Grafana,
  Prometheus).
- **Architecture —** exige **Clean Architecture** com as camadas `domain`, `application`,
  `infrastructure` e `entrypoints`; o domínio não pode depender de Spring; controllers devem
  ser finos.
- **Coding Standards / Naming —** preferir imutabilidade e composição, usar `records` para
  DTOs, evitar classes utilitárias e estado estático; nomes explícitos e métodos que
  descrevem comportamento.
- **REST API Standards —** convenções REST, retornar `ResponseEntity`, erros no formato
  **RFC 7807**, validar payloads, nunca expor entidades diretamente.
- **Database Standards —** usar **Flyway**, nunca `ddl-auto update`, preferir **UUID** como
  PK, incluir `created_at`/`updated_at`.
- **Security Rules —** nunca logar dados sensíveis, mascarar documentos/tokens, princípio do
  menor privilégio, segredos vindos do **AWS Secrets Manager**.
- **Testing Strategy —** preferir testes de integração para fluxos críticos, usar
  **Testcontainers**, mockar só dependências externas, regras de domínio com testes
  unitários puros.
- **Anti-Patterns —** evitar fat controllers, god services, lógica de negócio em
  repositórios, estado mutável compartilhado e classes "Util" genéricas.
- **Development Workflow —** antes de implementar: entender o impacto no domínio, definir
  fronteiras, definir contratos, criar testes e implementar incrementalmente.
- **Important Financial Rules —** valores monetários sempre em **BigDecimal** (nunca
  `double`); todas as operações rastreáveis; fluxos de liquidação auditáveis.
- **Microservice / Domain Rules —** cada serviço é independentemente implantável e dono do
  seu banco; preferir comunicação assíncrona e consistência eventual; usar bounded contexts
  e linguagem ubíqua; expor health checks e métricas.
- **Observability Standards —** logs estruturados em JSON, `correlationId` em toda
  requisição, `traceId` em eventos assíncronos, padrões OpenTelemetry, métricas Prometheus,
  dashboards e alertas.
- **Resilience Standards —** timeouts e retries com backoff exponencial em chamadas
  externas, DLQ para consumidores críticos, idempotência, evitar falhas em cascata.
- **Definition of Done —** uma feature só está pronta com testes, cobertura ≥ 95%, OpenAPI
  atualizado, métricas expostas, logs padronizados, migração Flyway criada, eventos
  documentados, CI passando e regras de arquitetura respeitadas.
- **Event Standards —** todo evento deve conter `eventId`, `occurredAt`, `producer`,
  `correlationId` e `payload`, e ser registrado no catálogo de eventos.

---

## `.claude/skills/skills.md`

**Objetivo:** **catálogo de competências (skills) de engenharia** esperadas no laboratório.
Para cada área, define um **objetivo**, um conjunto de **regras** e os **entregáveis**
esperados. Serve como guia de qualidade e de onboarding técnico, complementando o
`claude.md`.

Áreas cobertas: Backend Engineering, Distributed Systems, Event-Driven Architecture,
Financial Domain Modeling, API Design, Database Engineering, Cloud & Infrastructure,
Observability, Resilience Engineering, Testing Strategy, Security Engineering, CI/CD
Engineering, Performance Engineering e Developer Experience.

Em conjunto, `claude.md` (as **regras**) e `skills.md` (as **competências e entregáveis**)
formam o "contrato de qualidade" que os serviços construídos sobre este template devem
honrar.
