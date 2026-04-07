# Exemplos de Uso da API - FuelPump com Múltiplos Combustíveis

## Criar uma Bomba com Múltiplos Combustíveis

### Requisição POST

```bash
POST http://localhost:8080/api/v1/fuel-pumps
Content-Type: application/json

{
  "name": "Bomba A1",
  "fuelTypeIds": [1, 2, 3]
}
```

### Resposta (201 Created)

```json
{
  "id": 1,
  "name": "Bomba A1",
  "fuelTypes": [
    {
      "id": 1,
      "name": "Gasolina Comum",
      "pricePerLiter": 5.890
    },
    {
      "id": 2,
      "name": "Etanol",
      "pricePerLiter": 4.290
    },
    {
      "id": 3,
      "name": "Diesel S10",
      "pricePerLiter": 6.150
    }
  ],
  "createdAt": "2024-03-28T10:30:00",
  "updatedAt": "2024-03-28T10:30:00"
}
```

## Atualizar uma Bomba para Mudar seus Combustíveis

### Requisição PUT

```bash
PUT http://localhost:8080/api/v1/fuel-pumps/1
Content-Type: application/json

{
  "name": "Bomba A1",
  "fuelTypeIds": [1, 2]
}
```

### Resposta (200 OK)

```json
{
  "id": 1,
  "name": "Bomba A1",
  "fuelTypes": [
    {
      "id": 1,
      "name": "Gasolina Comum",
      "pricePerLiter": 5.890
    },
    {
      "id": 2,
      "name": "Etanol",
      "pricePerLiter": 4.290
    }
  ],
  "createdAt": "2024-03-28T10:30:00",
  "updatedAt": "2024-03-28T11:45:00"
}
```

## Listar Todas as Bombas com seus Combustíveis

### Requisição GET

```bash
GET http://localhost:8080/api/v1/fuel-pumps?page=0&size=20&sort=name,asc
```

### Resposta (200 OK)

```json
{
  "content": [
    {
      "id": 1,
      "name": "Bomba A1",
      "fuelTypes": [
        {
          "id": 1,
          "name": "Gasolina Comum",
          "pricePerLiter": 5.890
        },
        {
          "id": 2,
          "name": "Etanol",
          "pricePerLiter": 4.290
        }
      ],
      "createdAt": "2024-03-28T10:30:00",
      "updatedAt": "2024-03-28T10:30:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

## Paginação em Tipos de Combustível e Abastecimentos

```bash
GET http://localhost:8080/api/v1/fuel-types?page=0&size=10&sort=name,asc
GET http://localhost:8080/api/v1/fuelings?pumpId=1&startDate=2024-03-01&endDate=2024-03-31&page=0&size=20&sort=fuelingDate,desc
```

Parâmetros de paginação:
- `page`: índice da página (base 0)
- `size`: quantidade de itens por página
- `sort`: campo e direção (`campo,asc` ou `campo,desc`)

## Validações e Erros

### Erro: fuelTypeIds vazio

```bash
POST http://localhost:8080/api/v1/fuel-pumps
Content-Type: application/json

{
  "name": "Bomba C3",
  "fuelTypeIds": []
}
```

**Resposta (400 Bad Request)**

```json
{
  "status": 400,
  "message": "Validação falhou",
  "timestamp": "2024-03-28T10:45:00",
  "errors": {
    "fuelTypeIds": "A bomba deve ter pelo menos um tipo de combustível."
  }
}
```

### Erro: Combustível não encontrado

```bash
POST http://localhost:8080/api/v1/fuel-pumps
Content-Type: application/json

{
  "name": "Bomba D4",
  "fuelTypeIds": [1, 999]
}
```

**Resposta (404 Not Found)**

```json
{
  "status": 404,
  "message": "Tipo de combustível não encontrado",
  "timestamp": "2024-03-28T10:50:00",
  "details": {
    "field": "id",
    "value": 999
  }
}
```

### Erro: Nome duplicado

```bash
POST http://localhost:8080/api/v1/fuel-pumps
Content-Type: application/json

{
  "name": "Bomba A1",
  "fuelTypeIds": [1]
}
```

**Resposta (409 Conflict)**

```json
{
  "status": 409,
  "message": "Já existe uma bomba com o nome 'Bomba A1'.",
  "timestamp": "2024-03-28T10:55:00"
}
```

## Abastecimentos (Fueling)

Ao registrar um abastecimento, agora a resposta mostra múltiplos combustíveis da bomba:

### Requisição POST para Fueling

```bash
POST http://localhost:8080/api/v1/fuelings
Content-Type: application/json

{
  "pumpId": 1,
  "fuelingDate": "2024-03-28",
  "liters": 50.5,
  "totalValue": 297.44
}
```

### Resposta (201 Created)

```json
{
  "id": 10,
  "fuelingDate": "2024-03-28",
  "liters": 50.5,
  "totalValue": 297.44,
  "pump": {
    "id": 1,
    "name": "Bomba A1",
    "fuelTypes": [
      {
        "id": 1,
        "name": "Gasolina Comum",
        "pricePerLiter": 5.890
      },
      {
        "id": 2,
        "name": "Etanol",
        "pricePerLiter": 4.290
      }
    ]
  },
  "createdAt": "2024-03-28T11:00:00",
  "updatedAt": "2024-03-28T11:00:00"
}
```

## Swagger UI

Acesse a documentação interativa em:

```
http://localhost:8080/swagger-ui.html
```

Os endpoints de FuelPump estão documentados com exemplos e validações.


