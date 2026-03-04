# 📄 DocDigital API

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-orange)
![Maven](https://img.shields.io/badge/Build-Maven-red)

API REST para gerenciamento e digitalização de documentos pessoais.

A **DocDigital API** permite que usuários armazenem, organizem e
gerenciem documentos digitalizados, com upload de arquivos,
categorização, busca por palavras‑chave e alertas de vencimento.

------------------------------------------------------------------------

# 🚀 Tecnologias utilizadas

-   Kotlin
-   Spring Boot
-   Spring Security + JWT
-   PostgreSQL
-   JPA / Hibernate
-   Maven

------------------------------------------------------------------------

# 🧠 Funcionalidades

A API permite:

-   Cadastro de usuários
-   Autenticação com JWT
-   Upload de documentos
-   Download de documentos
-   Visualização de documentos
-   Listagem de documentos do usuário
-   Edição de documentos
-   Exclusão de documentos
-   Busca por palavras‑chave
-   Alertas de documentos vencendo

------------------------------------------------------------------------

# 🏗 Arquitetura

O projeto segue uma arquitetura em camadas:

controller → service → repository → database

### Camadas

**Controller**\
Responsável pelos endpoints da API.

**Service**\
Contém as regras de negócio.

**Repository**\
Responsável pelo acesso ao banco de dados.

**DTO**\
Objetos utilizados para comunicação da API.

------------------------------------------------------------------------

# 🔐 Autenticação

A API utiliza **JWT (JSON Web Token)**.

Fluxo de autenticação:

Login → API gera token JWT → Frontend envia token nas requisições

Header utilizado:

Authorization: Bearer {{token}}

------------------------------------------------------------------------

# 📦 Endpoints da API

## 👤 Usuários

### Criar usuário

POST /usuarios

Body:

``` json
{
  "nome": "usuario",
  "email": "usuario@email.com",
  "senha": "123456"
}
```

------------------------------------------------------------------------

## 🔑 Autenticação

### Login

POST /auth/login

Body:

``` json
{
  "email": "usuario@email.com",
  "senha": "123456"
}
```

Resposta:

``` json
{
  "token": "jwt-token"
}
```

------------------------------------------------------------------------

# 📄 Documentos

Todos os endpoints abaixo exigem **token JWT**.

## 📥 Upload e criação de documento

POST /documentos

Content-Type:

multipart/form-data

Campos:

| Campo | Descrição |
|------|-----------|
| file | arquivo do documento |
| nome | nome do documento |
| descricao | descricao opcional |
| categoria | categoria do documento |
| dataVencimento | data opcional |

------------------------------------------------------------------------

## 📄 Listar documentos

GET /documentos

Retorna todos os documentos do usuário autenticado.

------------------------------------------------------------------------

## 🔎 Buscar documentos

GET /documentos/buscar?palavra={{texto}}

Busca documentos por nome ou descrição.

------------------------------------------------------------------------

## ✏ Editar documento

PUT /documentos/{{id}}

------------------------------------------------------------------------

## 🗑 Excluir documento

DELETE /documentos/{{id}}

------------------------------------------------------------------------

## ⬇ Download de documento

GET /documentos/download/{{nomeArquivo}}

------------------------------------------------------------------------

## 👁 Visualizar documento

GET /documentos/visualizar/{{nomeArquivo}}

------------------------------------------------------------------------

## ⚠ Alertas de vencimento

GET /documentos/alertas

Retorna documentos: - vencidos - vencendo hoje - vencendo em breve

------------------------------------------------------------------------

# 🗂 Modelo de dados

### Documento

Campos principais:

| Campo | Tipo | Descrição |
|------|------|-----------|
| id | Long | Identificador único do documento |
| nome | String | Nome do documento |
| descricao | String | Descrição opcional |
| categoria | Enum | Categoria do documento |
| dataUpload | LocalDateTime | Data em que o documento foi enviado |
| dataVencimento | LocalDate | Data de vencimento do documento (opcional) |
| tipoArquivo | String | Tipo do arquivo enviado |
| caminhoArquivo | String | Caminho onde o arquivo foi armazenado |
| usuario | Usuario | Usuário dono do documento |

------------------------------------------------------------------------

# 📁 Estrutura do projeto
```
src
└── main
    └── kotlin
        └── com
            └── docdigital
                └── api
                    ├── config
                    ├── controller
                    ├── dto
                    ├── exception
                    ├── model
                    ├── repository
                    ├── service
                    └── DocdigitalApplication.kt
```

------------------------------------------------------------------------

# 💾 Banco de dados

Banco utilizado:

PostgreSQL

Configuração realizada no arquivo **application.yaml**.

------------------------------------------------------------------------

# ▶ Como rodar o projeto

1️⃣ Clonar repositório

git clone https://github.com/jaquedb/docdigital-api.git

2️⃣ Configurar banco PostgreSQL

3️⃣ Rodar a aplicação

mvn spring-boot:run

A API ficará disponível em:

http://localhost:8080

------------------------------------------------------------------------

# 📱 Projeto relacionado

Este backend será utilizado pelo aplicativo mobile **DocDigital**, que
permitirá digitalizar e organizar documentos diretamente pelo celular.

------------------------------------------------------------------------

# 👨‍💻 Autor

Projeto desenvolvido por **Jaqueline** para fins acadêmicos.
