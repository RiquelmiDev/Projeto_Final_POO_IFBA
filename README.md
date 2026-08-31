# Sistema de Gestão de Resíduos Sólidos

Projeto Java com JavaFX para gestão de contêineres, coletas, usuários e auditoria de operações.

## Arquitetura atual

A estrutura foi organizada em camadas e pacotes:

- `presentation`: interface gráfica com JavaFX
- `service`: regras de negócio e orquestração das operações
- `data`: repositórios em memória e abstrações de persistência
- `model`: entidades do domínio
- `exception`: exceções de regra de negócio

## Estrutura principal

- `src/main/java/model`
- `src/main/java/service`
- `src/main/java/data`
- `src/main/java/exception`
- `src/main/java/presentation`
- `src/test/java/service`

## Funcionalidades implementadas

1. Login e autenticação
   - Login com usuário e senha
   - Validação de credenciais
   - Diferenciação de perfis: `ADMIN` e `COLABORADOR`

2. Cadastro de usuários
   - Criação de usuários com nome, username, senha e perfil
   - Proteção de criação de administradores apenas por usuário autenticado com perfil administrativo

3. Cadastro de contêineres
   - Registro de contêiner com capacidade, tipo e localização
   - Controle por responsável

4. Registro de coletas
   - Registro de coleta vinculada a um contêiner
   - Validação de capacidade máxima
   - Prioridade para resíduos perigosos

5. Auditoria
   - Registro de ações com identificador, responsável, data de criação e última atualização
   - Ordenação pela operação mais recente primeiro

6. Dashboard
   - Resumo geral do sistema
   - Lista de atividades recentes
   - Relatório com métricas

## Regras de negócio importantes

1. Capacidade do contêiner
   - A coleta não pode ultrapassar a capacidade máxima do contêiner.
   - Se isso acontecer, a operação é abortada com `IllegalArgumentException`.

2. Prioridade de coleta
   - Resíduos perigosos são priorizados na listagem e no processamento.

3. Controle de acesso
   - Usuários do tipo `COLABORADOR` não têm acesso a telas e ações administrativas.
   - Usuários `ADMIN` podem gerenciar usuários e visualizar auditoria.

## Primeiro login

Ao iniciar a aplicação, o sistema já cria usuários padrão para facilitar o acesso inicial:

- Admin:
  - usuário: `admin`
  - senha: `admin123`

- Colaborador:
  - usuário: `teste`
  - senha: `123456`

## Compilação e execução

Pré-requisitos:

- JDK 17+
- Maven 3.9+
- JavaFX 21+

Comandos:

```bash
export JAVA_HOME=/opt/java/jdk-17.0.12
export PATH="$JAVA_HOME/bin:$PATH"
mvn clean compile
mvn test
```

Para rodar a aplicação JavaFX:

```bash
mvn javafx:run
```

## Testes

Os testes JUnit cobrem:

- autenticação de usuários
- cadastro e bloqueio de permissões
- capacidade excedida
- prioridade de coleta perigosa
- cálculo de taxa de reciclagem
- auditoria e rastreio de responsáveis

## Diagramas

Os diagramas de classes e sequência estão em:

- `docs/diagramaClasses.puml`
- `docs/diagramaSequencia.puml`
