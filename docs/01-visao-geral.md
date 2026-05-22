# 01 — Visão Geral

## O que é este projeto

`structure` é o **microsserviço-modelo (boilerplate)** do Financial Tech Lab. Ele não
implementa regras de negócio: existe para ser **copiado como ponto de partida** de novos
serviços, já trazendo a estrutura de build, qualidade de código, containerização, ambiente
local de apoio (banco, cache, AWS simulada) e observabilidade configurados.

Em uma frase: **é o "esqueleto" padronizado a partir do qual todo novo serviço de backend
nasce.**

---

## Stack tecnológica

A stack efetivamente presente no repositório hoje:

- **Java 21** (definido em `pom.xml`, propriedade `java.version`).
- **Spring Boot** (herdado de `spring-boot-starter-parent`, versão `3.3.1`).
- **Maven** como ferramenta de build, acompanhado do **Maven Wrapper** (`mvnw`).
- **Checkstyle** e **Prettier (Java)** como ferramentas de qualidade/formatação.
- **Docker** (build multi-stage) e **Docker Compose** para o ambiente local.
- **PostgreSQL**, **Redis**, **LocalStack** (S3 + SQS + Secrets Manager) e **Prometheus**
  como serviços de apoio locais.

As guidelines internas (`.claude/`) preveem uma stack-alvo maior (DynamoDB, Terraform,
Grafana, Testcontainers, GitHub Actions, etc.), que deve ser incorporada conforme cada
serviço evolui.

---

## Arquitetura pretendida

O documento `.claude/claude.md` define que os serviços devem seguir **Clean Architecture**,
com as camadas:

- `domain` — entidades, objetos de valor e regras de negócio (sem dependência de Spring);
- `application` — casos de uso / serviços de aplicação;
- `infrastructure` — adaptadores (banco, mensageria, integrações externas);
- `entrypoints` — controllers e consumidores (camada fina).

> Importante: **essas pastas ainda não existem** no template. A única classe Java é a de
> bootstrap. Ao criar um serviço real, você criará a estrutura de pacotes conforme a
> arquitetura acima.

---

## Árvore de diretórios (sem `target/` e `.idea/`)

```
structure/
├── docs/                         # Esta documentação
├── src/
│   ├── main/
│   │   ├── java/com/financial_tech_lab/structure/
│   │   │   └── StructureApplication.java     # Ponto de entrada Spring Boot
│   │   └── resources/
│   │       └── application.yaml              # Configuração da aplicação
│   └── test/
│       └── java/com/financial_tech_lab/structure/
│           └── StructureApplicationTests.java # Teste de contexto
├── setup/                        # Ambiente local e infraestrutura
│   ├── docker-compose.yml        # LocalStack, Redis, PostgreSQL, Prometheus
│   ├── localstack/
│   │   └── init/init.sh          # Bootstrap de S3 / SQS / Secrets
│   ├── db/
│   │   └── dumps/
│   │       ├── V_0_init.sql      # (placeholder) criação de schema
│   │       └── V_0_insert.sql    # (placeholder) seed de dados
│   └── observability/
│       ├── prometheus/prometheus.yml
│       └── grafana/.gitkeep
├── .mvn/
│   └── wrapper/maven-wrapper.properties
├── .claude/                      # Guidelines de engenharia
│   ├── claude.md
│   └── skills/skills.md
├── pom.xml                       # Projeto Maven
├── Dockerfile                    # Imagem Docker (multi-stage)
├── Makefile                      # Atalhos de comandos
├── checkstyle.xml                # Regras de estilo Java
├── mvnw / mvnw.cmd               # Maven Wrapper (Unix / Windows)
├── README.md                     # Apresentação do serviço
├── .gitignore
└── .gitattributes
```

---

## Fluxo de execução (do build ao runtime)

1. **Build local:** `./mvnw clean package` baixa as dependências, roda Checkstyle (e
   Prettier, se o perfil `local` estiver ativo) na fase `validate`, compila e empacota o
   `.jar` em `target/`.
2. **Ambiente de apoio:** `make up` sobe, via Docker Compose, os contêineres de PostgreSQL,
   Redis, LocalStack e Prometheus. O LocalStack roda `init.sh` para criar buckets, filas e
   secrets.
3. **Execução da aplicação:** a classe `StructureApplication` inicia o Spring Boot, que lê o
   `application.yaml`, resolve as variáveis de ambiente e sobe o servidor.
4. **Observabilidade:** o Prometheus faz scraping do endpoint `/actuator/prometheus`
   exposto pela aplicação.
5. **Container (produção):** o `Dockerfile` compila o projeto e gera uma imagem enxuta
   baseada em JRE 21, rodando como usuário não-root.

> Para o detalhe linha a linha de cada arquivo citado, consulte os documentos `02` a `05`.
