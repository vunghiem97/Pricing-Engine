# Pricing Engine

A full-stack pricing engine demo with:

- **Backend**: Spring Boot (Java 17, Maven)
- **Frontend**: Next.js (React + TypeScript)

## Features

- Order total calculation with promotion chain
- Promotions:
  - Percentage discount
  - Buy X Get Y
  - VIP extra discount
  - Coupon fixed amount discount
- Discount breakdown in response
- Seeded products/promotions/coupons for local testing

## Prerequisites

- Java 17
- Node.js (current LTS recommended)
- npm

## Run project

### 1) Start backend

```powershell
cd backend
./mvnw spring-boot:run
```

Backend defaults:

- API base URL: `http://localhost:8080`
- H2 console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:pricingdb`
- Username: `sa`
- Password: *(blank)*

### 2) Start frontend (new terminal)

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:3000`.