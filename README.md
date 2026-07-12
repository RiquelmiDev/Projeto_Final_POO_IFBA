# Sistema de Gestão de Resíduos Sólidos

Projeto Java com JavaFX e arquitetura em três camadas:

- `presentation`: interface gráfica com JavaFX
- `business`: regras de negócio e validações
- `data`: armazenamento em memória via `ArrayList` e `HashMap`

## Estrutura

- `src/main/java/business`
- `src/main/java/data`
- `src/main/java/presentation`
- `src/test/java/business`

## Regras implementadas

1. Capacidade do Conteiner
   - A coleta não pode ultrapassar a capacidade máxima.
   - Se isso acontecer, a operação é abortada com `IllegalArgumentException`.

2. Prioridade de coleta
   - Resíduos perigosos são processados antes dos demais.
   - A ordenação de listas reforça essa regra.

## Compilação e execução

Pré-requisitos:

- JDK 17+
- Maven 3.9+
- JavaFX 21+

Comandos:

```bash
mvn clean compile
mvn javafx:run
mvn test
```

## Testes

Os testes JUnit cobrem:

- capacidade excedida
- prioridade de coleta perigosa
- cálculo de taxa de reciclagem

## Diagramas

Os diagramas de classes e sequência estão em:

- `docs/diagramaClasses.puml`
- `docs/diagramaSequencia.puml`
