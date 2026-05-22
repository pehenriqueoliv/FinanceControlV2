# 03 — Build, Dependências e Qualidade de Código

Cobre os arquivos que definem **como o projeto é compilado, empacotado e validado**:
`pom.xml`, o Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`) e o
`checkstyle.xml`.

---

## `pom.xml`

**Objetivo:** é o **coração do projeto Maven**. Declara identidade do artefato, versão do
Java, dependências, perfis de build e os plugins de qualidade (Checkstyle e Prettier).

```xml
<?xml version="1.0" encoding="UTF-8"?>
```
**Linha 1 —** Declaração XML padrão (versão e codificação UTF-8).

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
```
**Linhas 3–5 —** Elemento-raiz `<project>` e os namespaces XML do modelo POM 4.0.0, com o
schema usado para validação do arquivo.

```xml
	<modelVersion>4.0.0</modelVersion>
```
**Linha 6 —** Versão do modelo POM. Para Maven 3.x é sempre `4.0.0`.

```xml
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.3.1</version>
		<relativePath/>
	</parent>
```
**Linhas 7–12 —** Define o **POM pai** como `spring-boot-starter-parent` (versão `3.3.1`).
Herdar dele traz o gerenciamento de versões de dependências (BOM), configurações padrão de
plugins e a versão do Java. `<relativePath/>` vazio diz ao Maven para buscar o pai no
repositório remoto, não em disco.

```xml
	<groupId>com.financial_tech_lab</groupId>
	<artifactId>structure</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>structure</name>
	<description>Financial Tech Lab</description>
```
**Linhas 13–17 —** Identidade do artefato (coordenadas Maven): organização
(`com.financial_tech_lab`), nome do artefato (`structure`), versão (`0.0.1-SNAPSHOT` — o
sufixo `SNAPSHOT` indica versão em desenvolvimento), nome legível e descrição.

```xml
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
```
**Linhas 18–30 —** Metadados **vazios** (URL do projeto, licença, desenvolvedores e
informações de SCM/controle de versão). São placeholders gerados pelo Spring Initializr;
podem ser preenchidos ou removidos.

```xml
	<properties>
		<java.version>21</java.version>
		<atrun.version>3.1.0</atrun.version>
		<prettier.version>0.20</prettier.version>
		<checkstyle.version>3.5.0</checkstyle.version>
		<!-- default value -->
		<prettier.skip>true</prettier.skip>
	</properties>
```
**Linhas 31–38 —** Propriedades reutilizáveis:
- `java.version`: **Java 21** (o parent usa isso para configurar o compilador).
- `atrun.version`, `prettier.version`, `checkstyle.version`: versões dos plugins, isoladas
  aqui para facilitar atualização.
- `prettier.skip` = `true`: por **padrão o Prettier é pulado** (só roda no perfil `local`,
  ver abaixo).

```xml
	<profiles>
	<profile>
		<id>local</id>
		<properties>
			<prettier.skip>false</prettier.skip>
		</properties>
	</profile>
	<profile>
		<id>prod</id>
		<properties>
			<prettier.skip>true</prettier.skip>
		</properties>
	</profile>
	</profiles>
```
**Linhas 39–52 —** Dois **perfis de build**:
- `local`: ativa o Prettier (`prettier.skip=false`) — formata o código durante o
  desenvolvimento.
- `prod`: mantém o Prettier desligado (`prettier.skip=true`) — em produção/CI não se quer
  reformatar, apenas validar.

Ativam-se com `-Plocal` ou `-Pprod` na linha de comando do Maven.

```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
```
**Linhas 53–63 —** As dependências **atuais**:
- `spring-boot-starter`: o núcleo do Spring Boot (contexto, autoconfiguração, logging).
- `spring-boot-starter-test`: ferramentas de teste (JUnit 5, Mockito, AssertJ, Spring Test),
  com `scope` `test` (só disponível em tempo de teste).

> Repare que **não há** ainda dependências de Web (MVC), JPA, PostgreSQL, Redis, AWS ou
> Feign — embora o `application.yaml` já as pressuponha. Essas dependências devem ser
> adicionadas aqui ao desenvolver um serviço concreto. As versões não precisam ser
> informadas porque o `spring-boot-starter-parent` já as gerencia.

```xml
	<build>
		<plugins>
```
**Linhas 64–65 —** Início da seção de build e da lista de plugins.

```xml
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
```
**Linhas 66–77 —** O **plugin do Spring Boot**, responsável por empacotar o `.jar`
executável (com servidor embarcado). A configuração `<excludes>` remove o **Lombok** do
artefato final — Lombok só é necessário em tempo de compilação, então não precisa ir para o
jar de produção. (Lombok ainda não está nas dependências, mas a exclusão já está preparada.)

```xml
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-checkstyle-plugin</artifactId>
				<version>${checkstyle.version}</version>
				<configuration>
					<configLocation>checkstyle.xml</configLocation>
					<failOnViolation>true</failOnViolation>
					<violationSeverity>warning</violationSeverity>
				</configuration>
				<executions>
					<execution>
						<phase>validate</phase>
						<goals>
							<goal>check</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
```
**Linhas 78–95 —** O **plugin Checkstyle**, que valida o estilo do código:
- `configLocation`: usa as regras do arquivo `checkstyle.xml` na raiz.
- `failOnViolation: true` + `violationSeverity: warning`: **falha o build** se houver
  qualquer violação de severidade `warning` ou superior (regra rígida de qualidade).
- A execução está ligada à fase **`validate`** (a primeira fase do ciclo Maven), com o
  objetivo `check`. Ou seja, o estilo é validado **logo no início** do build, antes de
  compilar.

```xml
			<plugin>
				<groupId>com.hubspot.maven.plugins</groupId>
				<artifactId>prettier-maven-plugin</artifactId>
				<version>${prettier.version}</version>
				<configuration>
					<printWidth>90</printWidth>
					<tabWidth>4</tabWidth>
					<useTabs>false</useTabs>
					<ignoreConfigFile>true</ignoreConfigFile>
					<ignoreEditorConfig>true</ignoreEditorConfig>
					<prettierJavaVersion>2.1.0</prettierJavaVersion>
					<skip>${prettier.skip}</skip>
				</configuration>
				<executions>
					<execution>
						<phase>validate</phase>
						<goals>
							<goal>check</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
```
**Linhas 96–117 —** O **plugin Prettier (Java)** para formatação automática:
- `printWidth: 90`: largura máxima da linha alvo da formatação (90 colunas).
- `tabWidth: 4` + `useTabs: false`: indentação de **4 espaços** (não tabs).
- `ignoreConfigFile` / `ignoreEditorConfig`: ignora arquivos `.prettierrc`/`.editorconfig`
  externos, garantindo que só esta configuração valha.
- `prettierJavaVersion: 2.1.0`: versão do formatador específico para Java.
- `skip: ${prettier.skip}`: liga/desliga conforme o perfil (pulado por padrão; ativo no
  perfil `local`).
- Também roda na fase `validate` com o goal `check` (verifica se o código está formatado).

```xml
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-antrun-plugin</artifactId>
				<version>${atrun.version}</version>
				<executions>
					<execution>
						<phase>validate</phase>
						<goals>
							<goal>run</goal>
						</goals>
						<configuration>
							<target>
								<exec executable="mvn">
									<arg value="prettier:write"/>
								</exec>
							</target>
						</configuration>
					</execution>
				</executions>
			</plugin>
```
**Linhas 118–137 —** O **plugin AntRun**, que executa um comando arbitrário durante o build.
Aqui, na fase `validate`, ele dispara `mvn prettier:write` — ou seja, **reescreve o código
já formatado** automaticamente. Em conjunto com o plugin anterior, a intenção é formatar o
código durante o build local.

> **Observação para quem mantém o template:** este AntRun chama `mvn` de forma incondicional
> (não respeita o `prettier.skip` dos perfis) e invoca um novo processo Maven aninhado, o que
> pode causar recursão/lentidão no build de CI. Vale revisar se ele deve mesmo rodar em todos
> os perfis ou apenas no `local`.

```xml
		</plugins>
	</build>

</project>
```
**Linhas 138–141 —** Fecham as seções de plugins, build e o projeto.

---

## Maven Wrapper

O Maven Wrapper permite rodar o build **sem ter o Maven instalado** na máquina: ele baixa e
usa automaticamente a versão correta. Garante que todos os desenvolvedores e a CI usem
exatamente a mesma versão do Maven.

### `mvnw`
**Objetivo:** script **shell (Unix/Linux/macOS)** que executa o Maven Wrapper. É um script
oficial gerado pelo Apache (versão 3.3.4). Em resumo, ele: detecta o `JAVA_HOME`, verifica se
a distribuição do Maven já foi baixada (na versão definida em `maven-wrapper.properties`),
baixa-a se necessário e então repassa os argumentos para o `mvn`. Você o usa como
`./mvnw clean package`. **Não deve ser editado manualmente** — é código boilerplate do
wrapper.

### `mvnw.cmd`
**Objetivo:** equivalente ao `mvnw`, porém em **batch script para Windows**. Mesma função,
para o `cmd`/PowerShell. Também é boilerplate oficial e não deve ser editado à mão.

### `.mvn/wrapper/maven-wrapper.properties`
**Objetivo:** configura **qual versão do Maven** o wrapper usa.

```properties
wrapperVersion=3.3.4
```
**Linha 1 —** Versão do próprio Maven Wrapper.

```properties
distributionType=only-script
```
**Linha 2 —** Tipo de distribuição. `only-script` significa que o wrapper usa apenas os
scripts (`mvnw`/`mvnw.cmd`) sem precisar de um `.jar` auxiliar — por isso o
`maven-wrapper.jar` está no `.gitignore`.

```properties
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.15/apache-maven-3.9.15-bin.zip
```
**Linha 3 —** URL exata da distribuição do Maven a ser baixada: **Apache Maven 3.9.15**.
Fixar a URL garante builds reproduzíveis.

---

## `checkstyle.xml`

**Objetivo:** define as **regras de estilo de código Java** aplicadas pelo plugin Checkstyle.
É baseado nas convenções de código da Sun/Oracle, com algumas boas práticas adicionais. Como
o `pom.xml` configura `failOnViolation=true`, **qualquer violação quebra o build** — isso
padroniza o estilo de todos os serviços derivados deste template.

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
        "-//Puppy Crawl//DTD Check Configuration 1.3//EN"
        "http://www.puppycrawl.com/dtds/configuration_1_3.dtd">
```
**Linhas 1–4 —** Declaração XML e o DOCTYPE que aponta para a DTD oficial do Checkstyle
(formato de configuração 1.3).

**Linhas 6–31 —** Bloco de comentário explicando que a configuração segue as convenções
Sun/Oracle e indicando a documentação oficial do Checkstyle. É informativo.

```xml
<module name="Checker">
```
**Linha 33 —** Módulo-raiz `Checker`. Tudo dentro dele são checagens. Há dois níveis: checks
de **arquivo** (filhos diretos do `Checker`) e checks de **AST/código** (dentro do
`TreeWalker`).

```xml
    <property name="fileExtensions" value="java, properties, xml"/>
```
**Linha 42 —** Restringe as checagens a arquivos `.java`, `.properties` e `.xml`.

```xml
    <module name="NewlineAtEndOfFile"/>
```
**Linha 46 —** Exige que todo arquivo **termine com uma quebra de linha**.

```xml
    <module name="Translation"/>
```
**Linha 50 —** Garante que arquivos de tradução (`.properties` de i18n) tenham as **mesmas
chaves** em todos os idiomas.

```xml
    <module name="FileLength"/>
```
**Linha 54 —** Limita o **tamanho do arquivo** (padrão: 2000 linhas) para evitar classes
gigantes.

```xml
    <module name="FileTabCharacter"/>
```
**Linha 58 —** Proíbe **caracteres de tabulação** no arquivo (força uso de espaços).

```xml
    <module name="RegexpSingleline">
        <property name="format" value="\s+$"/>
        <property name="minimum" value="0"/>
        <property name="maximum" value="0"/>
        <property name="message" value="Line has trailing spaces."/>
    </module>
```
**Linhas 62–67 —** Proíbe **espaços em branco no fim da linha** (trailing spaces), com
mensagem de erro customizada. `minimum/maximum = 0` significa "nenhuma ocorrência permitida".

```xml
    <module name="LineLength">
        <property name="max" value="120"/>
    </module>
```
**Linhas 69–71 —** Limita as linhas a **120 caracteres**.

**Linhas 73–78 —** Comentário mostrando como ativar a checagem de **cabeçalho de arquivo**
(ex.: licença obrigatória). Está desativada.

```xml
    <module name="TreeWalker">
```
**Linha 80 —** Início do `TreeWalker`, que analisa a **árvore sintática (AST)** do código
Java. As checagens a seguir operam sobre a estrutura do código.

```xml
        <module name="ConstantName"/>
        <module name="LocalFinalVariableName"/>
        <module name="LocalVariableName"/>
        <module name="MethodName"/>
        <module name="PackageName"/>
        <module name="ParameterName"/>
        <module name="StaticVariableName"/>
        <module name="TypeName"/>
```
**Linhas 84–91 —** **Convenções de nomenclatura.** Verificam que constantes, variáveis locais
(finais e não-finais), métodos, pacotes, parâmetros, variáveis estáticas e nomes de tipos
sigam os padrões Java (ex.: constantes em `UPPER_SNAKE_CASE`, tipos em `PascalCase`, métodos
em `camelCase`).

```xml
        <module name="AvoidStarImport"/>
        <module name="IllegalImport"/> <!-- defaults to sun.* packages -->
        <module name="RedundantImport"/>
        <module name="UnusedImports">
            <property name="processJavadoc" value="false"/>
        </module>
```
**Linhas 95–100 —** **Regras de imports:**
- `AvoidStarImport`: proíbe `import pacote.*` (força imports explícitos).
- `IllegalImport`: proíbe imports de pacotes proibidos (por padrão, `sun.*`).
- `RedundantImport`: proíbe imports redundantes (ex.: do mesmo pacote ou duplicados).
- `UnusedImports`: proíbe imports não utilizados (`processJavadoc=false` ignora referências
  feitas apenas em Javadoc).

```xml
        <module name="EmptyForIteratorPad"/>
        <module name="GenericWhitespace"/>
        <module name="MethodParamPad"/>
        <module name="NoWhitespaceBefore"/>
        <module name="ParenPad"/>
        <module name="TypecastParenPad"/>
        <module name="WhitespaceAfter"/>
```
**Linhas 107–113 —** **Regras de espaçamento em branco** — padronizam onde pode/não pode
haver espaço: em iteradores `for` vazios, em genéricos (`<>`), entre nome do método e
parêntese, antes de certos tokens, dentro de parênteses, em casts e depois de vírgulas/ponto
e vírgula.

```xml
        <module name="ModifierOrder"/>
        <module name="RedundantModifier"/>
```
**Linhas 117–118 —** **Modificadores:** `ModifierOrder` exige a ordem canônica
(`public static final ...`); `RedundantModifier` proíbe modificadores redundantes (ex.:
`public` em métodos de interface).

```xml
        <module name="AvoidNestedBlocks"/>
        <module name="EmptyBlock"/>
        <module name="LeftCurly"/>
        <module name="NeedBraces"/>
        <module name="RightCurly"/>
```
**Linhas 122–126 —** **Regras de blocos `{}`:** proíbem blocos aninhados desnecessários e
blocos vazios; padronizam a posição das chaves de abertura/fechamento; e exigem chaves mesmo
em `if`/`for`/`while` de uma linha (`NeedBraces`).

```xml
        <module name="EmptyStatement"/>
        <module name="EqualsHashCode"/>
        <module name="HiddenField">
            <property name="ignoreConstructorParameter" value="true"/>
        </module>
        <module name="IllegalInstantiation"/>
        <module name="InnerAssignment"/>
        <module name="MissingSwitchDefault"/>
        <module name="SimplifyBooleanExpression"/>
        <module name="SimplifyBooleanReturn"/>
```
**Linhas 130–139 —** **Problemas comuns de codificação:**
- `EmptyStatement`: proíbe `;` solto.
- `EqualsHashCode`: se sobrescrever `equals()`, deve sobrescrever `hashCode()` (e
  vice-versa).
- `HiddenField`: proíbe variáveis locais que "escondem" campos da classe — mas **permite**
  isso em parâmetros de construtor (`ignoreConstructorParameter=true`), padrão comum.
- `IllegalInstantiation`: evita instanciar diretamente certas classes (ex.: `new Boolean()`).
- `InnerAssignment`: proíbe atribuições embutidas em expressões.
- `MissingSwitchDefault`: exige cláusula `default` em `switch`.
- `SimplifyBooleanExpression` / `SimplifyBooleanReturn`: apontam expressões/retornos
  booleanos que podem ser simplificados.

```xml
        <module name="InterfaceIsType"/>
        <module name="VisibilityModifier"/>
```
**Linhas 143–144 —** **Design de classe:** `InterfaceIsType` impede interfaces usadas apenas
como "saco de constantes"; `VisibilityModifier` exige que campos sejam privados (encapsulamento).

```xml
        <module name="ArrayTypeStyle"/>
        <module name="TodoComment"/>
        <module name="UpperEll"/>
```
**Linhas 148–150 —** **Diversos:** `ArrayTypeStyle` força o estilo Java de arrays
(`String[] x`, não `String x[]`); `TodoComment` sinaliza comentários `TODO` (lembrete de
pendências); `UpperEll` exige `L` maiúsculo em literais `long` (ex.: `100L`, não `100l`).

```xml
    </module>
</module>
```
**Linhas 152–154 —** Fecham o `TreeWalker` e o `Checker`.
