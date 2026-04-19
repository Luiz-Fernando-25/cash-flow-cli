# Cash Flow CLI - Gerenciador Financeiro (MVP)

O Cash Flow CLI é um backend de gerenciamento financeiro pessoal desenvolvido em Java. O projeto é um MVP (Minimum Viable Product) inspirado em funcionalidades de controle de fluxo de caixa, permitindo o rastreio de despesas, receitas e transferências entre múltiplas contas e cartões de crédito.

## 🚀 Visão Geral

O sistema foi projetado para ser um clone funcional (core backend) do Mobills, focado em um usuário único (Single-tenant). Ele utiliza conceitos avançados de Orientação a Objetos, como herança de classes e polimorfismo, para gerenciar diferentes tipos de contas e transações de forma eficiente.

## 🛠️ Stack Tecnológica

- Linguagem: Java 17

- Gerenciador de Dependências: Maven

- Banco de Dados: H2 Database (Persistência local em arquivo)

- Acesso a Dados: JDBC com Padrão Repository

- Arquitetura: Camadas (Domain, Repository, Config, Services, UI)

## 📂 Estrutura de Pacotes

A arquitetura foi desenhada utilizando os princípios de Clean Code e Inversão de Dependência, o que facilitará uma futura migração para frameworks como o Spring Boot. O projeto está dividido em:

- **`org.example.config.database`**: Configurações de conexão (JDBC) e scripts de inicialização do banco de dados H2 (DDL/DML).
- **`org.example.domain`**: Contém o coração das regras de negócio.
  - **`.models`**: Entidades de domínio (AbstractAccount, CreditCard, Transactions, etc.) utilizando herança e polimorfismo.
  - **`.enums`**: Tipos padronizados (AccountType, TransactionStatus, etc.).
  - **`.interfaces`**: Contratos de comportamento base (ex: operações de depósito/saque).
- **`org.example.repositories`**: Padrão Repository para abstração do acesso aos dados, com implementações puras em SQL para o H2.
- **`org.example.services`**: Camada de lógica de negócio e orquestração.
  - **`.impl`**: Implementações concretas dos serviços, garantindo a matemática financeira e as regras de estorno automático.
- **`org.example.ui`**: Componentes da Interface de Linha de Comando (CLI), organizados em Menus modulares (AccountMenu, TransactionMenu, etc.).

## 📋 Requisitos Implementados

- [x] **RF-01: Gestão de Contas e Carteiras:** Cadastro de contas bancárias e carteiras físicas com saldo consolidado (Permite saldo negativo).
- [x] **RF-02: Gestão de Cartões de Crédito:** Cadastro de cartões vinculados a bancos com controle de limites, dia de fechamento e vencimento.
- [x] **RF-03: Gestão de Categorias:** Classificação de transações por tipo (Receita, Despesa, Movimentação).
- [x] **RF-04: Registro de Transações:** Suporte a entradas e saídas normais, com alteração dinâmica de status (Pendente/Efetivada) alterando o saldo da conta em tempo real.
- [x] **RF-05: Transferências:** Orquestração de movimentação entre duas contas distintas, com espelhamento de transações (Saída A -> Entrada B) e estorno em cascata.
- [x] **RF-06: Despesas de Cartão:** Registro de transações vinculadas a um cartão de crédito, respeitando a data da fatura atual.

## 🔧 Como Executar

1. Pré-requisitos: Ter o JDK 17 e Maven instalados.

2. Clonar o repositório:

`git clone https://github.com/luiz-fernando-25/cash-flow-cli.git`

3. Compilar o projeto:

`mvn clean install`

4. Executar a classe Main: O banco de dados H2 será criado automaticamente na pasta ./banco/.

## 🏗️ Próximos Passos (Roadmap)

- [ ] Fechamento de Faturas: Desenvolver a rotina de consolidação de gastos de cartão de crédito baseada no dia de vencimento.

- [ ] Migração Web: Migrar o núcleo da aplicação para Spring Boot, substituindo a interface CLI por uma exposição de API REST.

- [ ] Dockerização: Criar um Dockerfile e docker-compose para facilitar a execução de todo o ambiente.

### 🧪 Testes Automatizados (`src/test/java`)

O projeto conta com uma robusta suíte de testes unitários e de integração utilizando **JUnit 5**, cobrindo 100% dos fluxos principais (Caminho Feliz e Exceções) da camada de `Services`. O banco H2 é recriado de forma isolada a cada execução.

---

## 🤖 Sobre o Desenvolvimento

Este projeto é parte de um estudo aprofundado de arquitetura backend, Orientação a Objetos avançada e modelagem de dados ORM manual (sem o uso de frameworks como Hibernate).

Nota de Transparência: Ferramentas de Inteligência Artificial Generativa (Pair Programming) foram utilizadas durante o desenvolvimento com o objetivo exclusivo de gerar boilerplate code (código repetitivo), formatar e estruturar a suíte de testes (Arrange, Act, Assert) e agilizar a criação dos menus da CLI. Toda a arquitetura do sistema, design de banco de dados, regras de negócios e lógica transacional foram concebidas e direcionadas manualmente.
