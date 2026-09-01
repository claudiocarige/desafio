# Desafio Técnico Java Pleno — Gerenciamento de Clientes

Microsserviço para gerenciamento de clientes, com CRUD, consultas, autenticação/autorização
via Spring Security e integração HTTP com um serviço externo de score.

## Tecnologias utilizadas

- Java 17
- Spring Boot 4.0.8
  - Spring Web / WebFlux (`WebClient` para integração com o serviço externo de score)
  - Spring Security (autenticação HTTP Basic + autorização por role)
  - Spring Data JPA (persistência)
  - Flyway (versionamento e migração do schema)
  - Bean Validation (`spring-boot-starter-validation`)
- Maven (build e gerenciamento de dependências)
- H2 Database (banco em memória)
- Lombok
- JUnit 5, Mockito e Spring Test (`MockMvc`) para testes
- Mockoon (simulação do serviço externo de score)

## 1. Requisitos para execução

- Java 17
- Maven 3.9+ (o projeto já inclui o Maven Wrapper, `mvnw`/`mvnw.cmd`, então não é obrigatório
  ter o Maven instalado globalmente)
- [Mockoon](https://mockoon.com/) (ou outra ferramenta equivalente), para simular o serviço
  externo de score

## 2. Como iniciar a aplicação

Na raiz do projeto (`desafio/`):

```bash
./mvnw spring-boot:run
```

No Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. O schema do banco H2 é criado e populado
automaticamente via Flyway (`src/main/resources/db/migration`), incluindo uma massa de dados
de exemplo com 12 clientes.

O console do H2 fica disponível em `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:desafio`, usuário: `sa`, senha: `sa`).

## 3. Como executar os testes

```bash
./mvnw test
```

No Windows (PowerShell):

```powershell
.\mvnw.cmd test
```

A suíte cobre testes de domínio, casos de uso, adapters de persistência/HTTP e um teste de
integração ponta a ponta (`CustomerControllerIntegrationTest`), que valida autenticação,
autorização por role e paginação usando `MockMvc` com o contexto real do Spring Security.

## 4. Endpoints disponíveis

Todos os endpoints exigem autenticação HTTP Basic. As roles disponíveis são `USER` e `ADMIN`
(ver seção [Configurações necessárias](#5-configurações-necessárias)).

| Método | Path                          | Role exigida    | Descrição                                              |
|--------|-------------------------------|------------------|---------------------------------------------------------|
| POST   | `/customers/create`           | `ADMIN`          | Cria um novo cliente (retorna header `Location` apontando para `/customers/search/{id}`) |
| GET    | `/customers/search/{id}`      | `USER`, `ADMIN`  | Busca um cliente pelo id                                 |
| GET    | `/customers/search`           | `USER`, `ADMIN`  | Lista clientes, paginado; com o parâmetro opcional `name`, filtra por nome (sem ele, lista todos) |
| GET    | `/customers`                  | `USER`, `ADMIN`  | Lista clientes filtrando por `status`, paginado           |
| PUT    | `/customers/update/{id}`      | `ADMIN`          | Atualiza nome, e-mail e/ou status de um cliente (CPF não pode ser alterado) |
| GET    | `/customers/{id}/score`       | `USER`, `ADMIN`  | Consulta o score do cliente no serviço externo           |
| DELETE | `/customers/delete/{id}`      | `USER`, `ADMIN`  | Não implementado propositalmente — retorna erro orientando o uso do PUT para inativar/bloquear o cliente, nenhum dado de cliente deve ser deletado |

Parâmetros de paginação aceitos em `GET /customers/search` e `GET /customers`: `page` (padrão
`0`), `size` (padrão `20`, máximo `50`).

Parâmetro `name` (opcional, em `GET /customers/search`): até 50 caracteres, aceitando letras
(com acentuação), números, espaço, apóstrofo e hífen.

### Exemplo de resposta — `CustomerResponse`

```json
{
  "id": "58acf758-7b6d-4706-a270-afe3f057ffb4",
  "name": "Claudio Carige",
  "cpf": "***.***.250-95",
  "email": "claudio.carige@email.com",
  "status": "ACTIVE"
}
```

### Exemplo de resposta — listagem paginada (`CustomerPageResponse`)

```json
{
  "content": [ { "id": "...", "name": "...", "cpf": "...", "email": "...", "status": "ACTIVE" } ],
  "page": 0,
  "size": 20,
  "totalElements": 12,
  "totalPages": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

### Exemplo de resposta — `GET /customers/{id}/score`

```json
{
  "cpf": "85541025095",
  "score": 750,
  "classification": "BOM"
}
```

## 5. Configurações necessárias

As configurações ficam em `src/main/resources/application.yaml`:

```yaml
score:
  service:
    base-url: http://localhost:3000
    timeout-ms: 5000

app:
  security:
    user:
      name: user-dantum
      password: user123
    admin:
      name: admin-dantum
      password: admin123
```

- `score.service.base-url`: endereço base do serviço externo de score (aponte para o Mockoon
  em execução local).
- `score.service.timeout-ms`: timeout (ms) da chamada HTTP ao serviço externo.
- `app.security.user.*` / `app.security.admin.*`: credenciais das roles `USER` e `ADMIN`
  usadas na autenticação HTTP Basic. Ajuste-as conforme necessário (por exemplo, via variáveis
  de ambiente/`application-*.yaml` de outro perfil) antes de qualquer uso fora do ambiente local.

## 6. Como executar/simular o serviço externo (Mockoon)

O serviço externo de score é consultado em `GET {score.service.base-url}/scores/{cpf}` e deve
responder no formato:

```json
{
  "cpf": "85541025095",
  "score": "837",
  "classification": "LOW_RISK"
}
```

Para simular localmente com o [Mockoon](https://mockoon.com/):

1. Abra o Mockoon (aplicativo desktop ou CLI `@mockoon/cli`).
2. Crie um novo ambiente na porta `3000` (compatível com o `base-url` padrão do
   `application.yaml`).
3. Adicione a rota `GET /scores/:cpf` retornando um corpo JSON com template dinâmico
   (status `200`), usando os helpers do Mockoon para gerar um score aleatório e derivar a
   classificação a partir dele:

   ```json
   {
     "cpf": "{{urlParam 'cpf'}}",
     {{setVar 'score' (faker 'number.int' min=600 max=950)}}
     "score": "{{getVar 'score'}}",
     "classification": "{{#if (gte (getVar 'score') 800)}}LOW_RISK{{else if (gte (getVar 'score') 700)}}MEDIUM_RISK{{else}}HIGH_RISK{{/if}}"
   }
   ```

   Esse template reaproveita o `cpf` recebido na URL, gera um `score` aleatório entre `600` e
   `950` (via `setVar`/`faker`) e classifica o resultado em `LOW_RISK` (≥ 800), `MEDIUM_RISK`
   (≥ 700) ou `HIGH_RISK` (abaixo de 700).
4. Opcionalmente, adicione uma rota de erro (`404`/`500`) retornando
   `{ "status": <codigo>, "response": "<mensagem>" }` para simular falhas do serviço externo
   (formato esperado por `ExternalErrorResponse`).
5. Inicie o ambiente no Mockoon antes de subir a aplicação (ou antes de chamar
   `GET /customers/{id}/score`).

## 7. Exemplos de utilização da API

> Substitua `{id}` pelo UUID real de um cliente retornado nas respostas anteriores.

### Criar cliente (ADMIN)

```bash
curl -u admin-dantum:admin123 -X POST http://localhost:8080/customers/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Novo Cliente","cpf":"11144477735","email":"novo@email.com"}'
```

### Buscar cliente por id (USER ou ADMIN)

```bash
curl -u user-dantum:user123 http://localhost:8080/customers/search/{id}
```

### Listar todos os clientes, paginado (USER ou ADMIN)

```bash
curl -u user-dantum:user123 "http://localhost:8080/customers/search?page=0&size=10"
```

### Buscar clientes por nome (USER ou ADMIN)

```bash
curl -u user-dantum:user123 "http://localhost:8080/customers/search?page=0&size=3&name=Maria"
```

### Listar clientes por status (USER ou ADMIN)

```bash
curl -u user-dantum:user123 "http://localhost:8080/customers?status=ACTIVE"
```

### Atualizar cliente (ADMIN)

```bash
curl -u admin-dantum:admin123 -X PUT http://localhost:8080/customers/update/{id} \
  -H "Content-Type: application/json" \
  -d '{"name":"Cliente Atualizado","email":"atualizado@email.com","status":"ACTIVE"}'
```

### Consultar score do cliente (USER ou ADMIN)

```bash
curl -u user-dantum:user123 http://localhost:8080/customers/{id}/score
```
