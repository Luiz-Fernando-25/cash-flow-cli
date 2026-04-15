# Cash Flow CLI - Gerenciador Financeiro (MVP)

O Cash Flow CLI é um backend de gerenciamento financeiro pessoal desenvolvido em Java. O projeto é um MVP (Minimum Viable Product) inspirado em funcionalidades de controle de fluxo de caixa, permitindo o rastreio de despesas, receitas e transferências entre múltiplas contas e cartões de crédito.

## 🚀 Visão Geral

O sistema foi projetado para ser um clone funcional (core backend) do Mobills, focado em um usuário único (Single-tenant). Ele utiliza conceitos avançados de Orientação a Objetos, como herança de classes e polimorfismo, para gerenciar diferentes tipos de contas e transações de forma eficiente.

## 🛠️ Stack Tecnológica

- Linguagem: Java 17

- Gerenciador de Dependências: Maven

- Banco de Dados: H2 Database (Persistência local em arquivo)

- Acesso a Dados: JDBC com Padrão Repository

- Arquitetura: Camadas (Domain, Repository, Config)

## 📂 Estrutura de Pacotes

A arquitetura segue uma organização clara para facilitar a manutenção e futura migração para frameworks como Spring Boot:

- org.example.config.database: Configurações de conexão e inicialização do banco de dados (DDL).

- org.example.domain.models: Entidades de domínio (Contas, Transações, Categorias).

- org.example.domain.enums: Enumeradores para tipos e status.

- org.example.repositories: Implementações de persistência utilizando H2.

## 📋 Requisitos Implementados (ou em Progresso)

- [x] RF-01: Gestão de Contas e Carteiras: Cadastro de contas bancárias e carteiras físicas com saldo consolidado.

- [x] RF-02: Gestão de Cartões de Crédito: Cadastro de cartões vinculados a bancos com controle de limites.

- [x] RF-03: Gestão de Categorias: Classificação de transações por tipo (Receita/Despesa).

- [x] RF-04/06: Registro de Transações: Suporte a entradas, saídas e despesas em cartão de crédito.

- [ ] RF-05: Transferências: Lógica de movimentação entre contas (Saída A -> Entrada B).

- [ ] RF-07: Fechamento de Fatura: Consolidação de gastos de cartão em lote.

## 🔧 Como Executar

1. Pré-requisitos: Ter o JDK 17 e Maven instalados.

2. Clonar o repositório:

`git clone [https://github.com/luiz-fernando-25/cash-flow-cli.git](https://github.com/luiz-fernando-25/cash-flow-cli.git)`

3. Compilar o projeto:

`mvn clean install`

4. Executar a classe Main: O banco de dados H2 será criado automaticamente na pasta ./banco/.

## Console H2

Ao rodar a aplicação, um servidor web para o console do H2 é iniciado na porta 8082.

- URL: http://localhost:8082

- JDBC URL: jdbc:h2:./banco/cashflow

- User: sa | Password: (vazio)

## 🏗️ Próximos Passos (Roadmap)

- Implementar o serviço de lógica de negócios para automatizar o espelhamento de transferências (RF-05).

- Desenvolver a rotina de fechamento de faturas baseada no dia de vencimento (RF-07).

- Migração para Spring Boot para exposição de uma API REST.

Este projeto é parte de um estudo de arquitetura backend e modelagem de dados ORM manual.
