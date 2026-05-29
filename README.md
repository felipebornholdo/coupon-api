# Coupon API

REST API para gerenciamento de cupons de desconto.

## Tecnologias

- Java 17
- Spring Boot 4.x
- Spring Data JPA + H2 (in-memory)
- Bean Validation
- Springdoc OpenAPI (Swagger)
- JUnit 5 + Mockito + AssertJ
- Docker + Docker Compose
- Gradle

## Executando localmente

```bash
./gradlew bootRun
```

Acesse: `http://localhost:8080`

## Executando com Docker

```bash
docker-compose up --build
```

## Documentação

- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Endpoints

| Método   | Rota           | Status | Descrição     |
|----------|----------------|--------|---------------|
| `POST`   | `/coupon`      | `201`  | Criar cupom   |
| `GET`    | `/coupon/{id}` | `200`  | Buscar cupom  |
| `DELETE` | `/coupon/{id}` | `204`  | Deletar cupom |

### Exemplo — Criar cupom

**Request:**
```http
POST /coupon
Content-Type: application/json

{
  "code": "AB-C123",
  "description": "10% de desconto",
  "discountValue": 10.00,
  "expirationDate": "2026-12-31T23:59:59",
  "published": false
}
```

**Response `201`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "ABC123",
  "description": "10% de desconto",
  "discountValue": 10.00,
  "expirationDate": "2026-12-31T23:59:59",
  "published": false,
  "deleted": false
}
```

## Testes

```bash
./gradlew test jacocoTestReport
```

Relatório de cobertura: `build/reports/jacoco/test/html/index.html`
