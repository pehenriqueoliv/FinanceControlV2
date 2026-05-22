# Documentação do Projeto `structure`

> **Financial Tech Lab — Microsserviço Estrutura (boilerplate)**

Esta documentação descreve, arquivo por arquivo, **o objetivo de cada parte do projeto**
e explica, **linha a linha**, o código-fonte e os arquivos de configuração mais
importantes.

O objetivo central deste repositório é servir como **estrutura inicial (template / base de
código)** para a criação de novos microsserviços do backend do Financial Tech Lab. A ideia
é que um novo serviço seja criado clonando este repositório e copiando os arquivos para o
novo projeto (conforme descrito no `README.md` da raiz).

Esta pasta `docs/` existe para que **qualquer pessoa desenvolvedora — sênior ou júnior —
consiga entender rapidamente o papel de cada arquivo** e fazer um onboarding eficiente.

---

## Como ler esta documentação

A documentação está dividida por área de responsabilidade. Comece pela visão geral abaixo e
depois aprofunde no documento da área que você precisa entender.

| Documento | O que cobre |
|-----------|-------------|
| [`01-visao-geral.md`](./01-visao-geral.md) | Visão geral da arquitetura, stack, árvore de diretórios e fluxo de execução |
| [`02-codigo-fonte.md`](./02-codigo-fonte.md) | Código Java (`src/`) e `application.yaml` — explicado **linha a linha** |
| [`03-build-e-qualidade.md`](./03-build-e-qualidade.md) | `pom.xml`, Maven Wrapper (`mvnw`), `checkstyle.xml` — build, dependências e qualidade |
| [`04-infraestrutura-e-docker.md`](./04-infraestrutura-e-docker.md) | `Dockerfile`, `Makefile`, `docker-compose.yml`, LocalStack, banco e observabilidade |
| [`05-configuracao-e-guidelines.md`](./05-configuracao-e-guidelines.md) | `.gitignore`, `.gitattributes`, `README.md` e guidelines de engenharia (`.claude/`) |

---

## Resumo: o papel de cada arquivo

A tabela abaixo é um índice rápido. **Arquivos gerados (`target/`) e de IDE (`.idea/`) não
são documentados** por não fazerem parte do código versionado relevante para onboarding.

### Raiz do projeto

| Arquivo | Objetivo |
|---------|----------|
| `pom.xml` | Definição do projeto Maven: dependências, plugins (Checkstyle, Prettier), perfis e build. |
| `Dockerfile` | Build multi-stage da imagem Docker do serviço (compila com Maven e roda com JRE). |
| `Makefile` | Atalhos de linha de comando para subir/parar o ambiente local e interagir com a infra. |
| `checkstyle.xml` | Regras de estilo de código Java (convenções Sun + boas práticas). |
| `README.md` | Apresentação do microsserviço e instruções básicas de uso. |
| `mvnw` / `mvnw.cmd` | Maven Wrapper: executam o Maven na versão correta sem instalação manual (Unix/Windows). |
| `.gitignore` | Arquivos e pastas que o Git deve ignorar. |
| `.gitattributes` | Normalização de fim de linha (EOL) por tipo de arquivo. |

### `src/` — código-fonte da aplicação

| Arquivo | Objetivo |
|---------|----------|
| `src/main/java/.../StructureApplication.java` | Classe principal: ponto de entrada da aplicação Spring Boot. |
| `src/main/resources/application.yaml` | Configuração da aplicação (banco, Redis, AWS, SSL, métricas, etc.). |
| `src/test/java/.../StructureApplicationTests.java` | Teste base que valida se o contexto Spring sobe corretamente. |

### `.mvn/` — Maven Wrapper

| Arquivo | Objetivo |
|---------|----------|
| `.mvn/wrapper/maven-wrapper.properties` | Define a versão do Maven baixada e usada pelo wrapper. |

### `setup/` — ambiente local e infraestrutura

| Arquivo | Objetivo |
|---------|----------|
| `setup/docker-compose.yml` | Orquestra os serviços de apoio locais: LocalStack (AWS), Redis, PostgreSQL e Prometheus. |
| `setup/localstack/init/init.sh` | Script de inicialização do LocalStack: cria buckets S3, filas SQS e secrets. |
| `setup/db/dumps/V_0_init.sql` | Placeholder para o script de criação de schema/tabelas do banco. |
| `setup/db/dumps/V_0_insert.sql` | Placeholder para o script de carga inicial (seed) de dados. |
| `setup/observability/prometheus/prometheus.yml` | Configuração de scraping de métricas do Prometheus. |
| `setup/observability/grafana/.gitkeep` | Mantém a pasta `grafana/` versionada (ainda vazia). |

### `.claude/` — guidelines de engenharia

| Arquivo | Objetivo |
|---------|----------|
| `.claude/claude.md` | Padrões de engenharia, arquitetura e regras do domínio financeiro do laboratório. |
| `.claude/skills/skills.md` | Catálogo de competências de engenharia esperadas e seus entregáveis. |

---

## Avisos importantes para quem vai usar este template

Este repositório é um **esqueleto**. Alguns pontos merecem atenção (detalhados nos
documentos específicos):

1. **O `application.yaml` configura mais do que o `pom.xml` declara.** O arquivo de
   configuração já prevê PostgreSQL, JPA/Hibernate, Redis, AWS S3, Feign e SSL, mas o
   `pom.xml` atualmente só inclui `spring-boot-starter` e `spring-boot-starter-test`. Ao
   criar um serviço real, será preciso **adicionar as dependências correspondentes** (driver
   PostgreSQL, Spring Data JPA, Spring Data Redis, SDK AWS, Spring Cloud OpenFeign, etc.).

2. **Vários valores de configuração vêm de variáveis de ambiente** (ex.: `DB_USER`,
   `DB_PASS`, `REDIS_HOST`, `AWS_REGION`, `SERVER_PORT`). Sem essas variáveis definidas, a
   aplicação não sobe. Veja a lista completa em [`02-codigo-fonte.md`](./02-codigo-fonte.md).

3. **Os scripts SQL (`V_0_init.sql` e `V_0_insert.sql`) estão vazios.** São apenas
   placeholders para o primeiro versionamento de schema.
