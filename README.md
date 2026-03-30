# NikatMerchant - Merchant Onboarding Service

A production-ready Spring Boot 3 application for merchant onboarding with full CRUD operations.

## Tech Stack
- Java 17
- Maven
- Spring Boot 3.x
- Spring Web, Spring Data JPA, MySQL
- Spring Security (Basic Auth)
- Lombok
- Validation (Jakarta)
- Swagger/OpenAPI

## Features
- REST API for merchant CRUD operations
- Layered architecture (Controller, Service, Repository, DTO)
- Input validation
- Global exception handling
- Basic Authentication
- API Documentation with Swagger
- Extensible metadata field for future features

## API Endpoints

### Merchants
- `POST /api/merchants` - Create Merchant
- `GET /api/merchants` - Get All Merchants
- `GET /api/merchants/{id}` - Get Merchant by ID
- `PUT /api/merchants/{id}` - Update Merchant
- `DELETE /api/merchants/{id}` - Delete Merchant

### Categories
- `GET /api/categories` - Get All Categories (returns a tree of top-level categories with their nested sub-categories)

## Database
- MySQL (AWS RDS compatible)
- Table: `merchants`
- Table: `categories` — stores category hierarchy; each row may reference a `parent_category_id` (nullable FK to the same table), allowing unlimited nesting of sub-categories. Schema is auto-managed by Hibernate.

## Security
- Basic Authentication: username `admin`, password `password`
- Secures `/api/**` endpoints

## Build and Run

### Prerequisites
- Java 17
- Maven
- MySQL database

### Local Development
1. Clone the repository
2. Update `application.yml` with your MySQL database details
3. Run `mvn clean install`
4. Run `mvn spring-boot:run`

### Docker Deployment
1. Build JAR: `mvn clean package`
2. Build Docker image: `docker build -t nikatmerchant .`
3. Run container: `docker run -p 8080:8080 nikatmerchant`

### AWS EC2 Deployment
1. Launch EC2 instance with Docker installed
2. Build and push Docker image to ECR or Docker Hub
3. Pull image on EC2: `docker pull your-image`
4. Run: `docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://rds-endpoint:3306/db your-image`

## Swagger UI
Access API documentation at: `http://localhost:8080/swagger-ui.html`

## Future Enhancements
- JWT Authentication
- Role-based Access Control
- Redis Caching
- API Rate Limiting
- AWS CloudWatch Logging
- AWS S3 File Upload
