# API Testing Guide - NikatMerchant

## Overview
Your Spring Boot application has 5 REST API endpoints for managing merchants. All data is stored in the MySQL database.

---

## 🎯 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/merchants` | Create a new merchant |
| `GET` | `/api/merchants` | Get all merchants |
| `GET` | `/api/merchants/{id}` | Get a specific merchant by ID |
| `PUT` | `/api/merchants/{id}` | Update a merchant |
| `DELETE` | `/api/merchants/{id}` | Delete a merchant |

---

## 📋 Request/Response Format

### Create/Update Merchant - Request Body
```json
{
  "businessName": "Acme Store",
  "merchantEmail": "merchant@acmestore.com",
  "primaryPhone": "+1234567890",
  "alternatePhone": "+0987654321",
  "category": "Electronics",
  "subCategory": "Mobile",
  "businessType": "Retail",
  "address": "123 Main St, City, Country",
  "area": "Downtown",
  "lat": 12.345,
  "lon": 67.890,
  "personAge": 30,
  "yearsOfExperience": 5,
  "whatAppAvailable": true,
  "homeServiceAvailable": true,
  "workingDays": "Mon,Tue,Wed,Thu,Fri",
  "workingHours": "10AM-7PM",
  "acquisitionSource": "Online",
  "addedBy": "Admin",
  "metaData": "{\"storeType\": \"retail\"}"
}
```

### Success Response (201 Created / 200 OK)
```json
{
  "merchantId": 1,
  "businessName": "Acme Store",
  "merchantEmail": "merchant@acmestore.com",
  "primaryPhone": "+1234567890",
  "alternatePhone": "+0987654321",
  "category": "Electronics",
  "subCategory": "Mobile",
  "businessType": "Retail",
  "address": "123 Main St, City, Country",
  "area": "Downtown",
  "lat": 12.345,
  "lon": 67.890,
  "personAge": 30,
  "yearsOfExperience": 5,
  "whatAppAvailable": true,
  "homeServiceAvailable": true,
  "workingDays": "Mon,Tue,Wed,Thu,Fri",
  "workingHours": "10AM-7PM",
  "acquisitionSource": "Online",
  "addedBy": "Admin",
  "dateAdded": "2026-03-17T10:30:00.000+00:00",
  "metaData": "{\"storeType\": \"retail\"}"
}
```

---

## 🔐 Authentication Credentials

Your application uses **HTTP Basic Authentication** for API endpoints.

### **Default Login Credentials:**
- **Username:** `admin`
- **Password:** `password`

### **Security Configuration:**
- **Swagger UI** (`/swagger-ui.html`) - **No authentication required**
- **API Docs** (`/v3/api-docs`) - **No authentication required**  
- **All API endpoints** (`/api/*`) - **Require authentication**

### **How to Authenticate in Testing:**

#### Swagger UI Authentication:
1. Open: `http://localhost:8080/swagger-ui.html`
2. Click "Authorize" button (lock icon)
3. Enter Username: `admin`, Password: `password`
4. Click "Authorize"
5. Now test any API endpoint - it will use these credentials automatically

#### cURL with Authentication:
```bash
# Add -u admin:password to any API request
curl -X GET http://localhost:8080/api/merchants -u admin:password
```

#### Postman Authentication:
1. Go to "Authorization" tab
2. Select "Basic Auth"
3. Enter Username: `admin`, Password: `password`

---

## 🧪 Testing Methods

### Method 1: Swagger UI (Easiest - No Tools Required)
1. **Start your application**: `mvn spring-boot:run`
2. **Open browser**: Navigate to `http://localhost:8080/swagger-ui.html`
3. **Try it out**: Click on any endpoint and use the "Try it out" button to send requests
4. **View response**: See the result immediately in the UI

---

### Method 2: Using cURL (Command Line)

#### Create a Merchant
```bash
curl -X POST http://localhost:8080/api/merchants \
  -H "Content-Type: application/json" \
  -d "{
    \"businessName\": \"Acme Store\",
    \"merchantEmail\": \"merchant@acmestore.com\",
    \"primaryPhone\": \"+1234567890\",
    \"alternatePhone\": \"+0987654321\",
    \"category\": \"Electronics\",
    \"subCategory\": \"Mobile\",
    \"businessType\": \"Retail\",
    \"address\": \"123 Main St, City, Country\",
    \"area\": \"Downtown\",
    \"lat\": 12.345,
    \"lon\": 67.890,
    \"personAge\": 30,
    \"yearsOfExperience\": 5,
    \"whatAppAvailable\": true,
    \"homeServiceAvailable\": true,
    \"workingDays\": \"Mon,Tue,Wed,Thu,Fri\",
    \"workingHours\": \"10AM-7PM\",
    \"acquisitionSource\": \"Online\",
    \"addedBy\": \"Admin\",
    \"metaData\": \"\"
  }"
```

#### Get All Merchants
```bash
curl -X GET http://localhost:8080/api/merchants
```

#### Get Merchant by ID
```bash
curl -X GET http://localhost:8080/api/merchants/1
```

#### Update a Merchant
```bash
curl -X PUT http://localhost:8080/api/merchants/1 \
  -H "Content-Type: application/json" \
  -d "{
    \"businessName\": \"Updated Store Name\",
    \"merchantEmail\": \"merchant@acmestore.com\",
    \"primaryPhone\": \"+1234567890\",
    \"alternatePhone\": \"+0987654321\",
    \"category\": \"Electronics\",
    \"subCategory\": \"Mobile\",
    \"businessType\": \"Retail\",
    \"address\": \"456 New St, City, Country\",
    \"area\": \"Downtown\",
    \"lat\": 12.346,
    \"lon\": 67.891,
    \"personAge\": 30,
    \"yearsOfExperience\": 5,
    \"whatAppAvailable\": true,
    \"homeServiceAvailable\": true,
    \"workingDays\": \"Mon,Tue,Wed,Thu,Fri\",
    \"workingHours\": \"10AM-7PM\",
    \"acquisitionSource\": \"Online\",
    \"addedBy\": \"Admin\",
    \"metaData\": \"\"
  }"
```

#### Delete a Merchant
```bash
curl -X DELETE http://localhost:8080/api/merchants/1
```

---

### Method 3: Using Postman (Recommended for Production)
1. Download and install [Postman](https://www.postman.com/downloads/)
2. Create a new request with:
   - **Method**: POST/GET/PUT/DELETE
   - **URL**: `http://localhost:8080/api/merchants`
   - **Headers**: `Content-Type: application/json` (for POST/PUT)
   - **Body**: JSON payload (for POST/PUT)
3. Click "Send" to execute

---

## 💾 Where Data is Stored

**Data Location**: MySQL Database running in Docker container
- **Database Name**: `merchantdb`
- **Table Name**: `merchants`
- **Host**: `localhost`
- **Port**: `3307`
- **Username**: `user`
- **Password**: `password`

### Database Schema
```sql
CREATE TABLE merchants (
  merchant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  business_name VARCHAR(255) NOT NULL,
  merchant_email VARCHAR(255) NOT NULL UNIQUE,
  primary_phone VARCHAR(20) NOT NULL,
  alternate_phone VARCHAR(20),
  category VARCHAR(100) NOT NULL,
  sub_category VARCHAR(100),
  business_type VARCHAR(100),
  address VARCHAR(255) NOT NULL,
  area VARCHAR(255),
  lat DOUBLE,
  lon DOUBLE,
  person_age INT,
  years_of_experience INT,
  whatsapp_available BOOLEAN,
  home_service_available BOOLEAN,
  working_days VARCHAR(255),
  working_hours VARCHAR(50),
  acquisition_source VARCHAR(255),
  added_by VARCHAR(255),
  date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  meta_data LONGTEXT
)
```

---

## 🔍 How to Check Data in Database

### Method 1: Using MySQL CLI (Command Line)

#### Connect to MySQL Container
```bash
docker exec -it mysql-merchant mysql -u user -p
```
When prompted, enter password: `password`

#### Once Connected
```sql
-- Select the database
USE merchantdb;

-- View all merchants
SELECT * FROM merchants;

-- View specific merchant
SELECT * FROM merchants WHERE merchant_id = 1;

-- Count total merchants
SELECT COUNT(*) FROM merchants;

-- View merchant details in formatted way
SELECT merchant_id, business_name, merchant_email, category, date_added FROM merchants;

-- Exit
EXIT;
```

---

### Method 2: Using MySQL Workbench (GUI)

1. Download [MySQL Workbench](https://www.mysql.com/products/workbench/)
2. Create a new connection with:
   - **Hostname**: `localhost`
   - **Port**: `3307`
   - **Username**: `user`
   - **Password**: `password`
3. Test the connection
4. Browse the `merchantdb` database → `merchants` table
5. View/edit data in the GUI

---

### Method 3: Using DBeaver (GUI - Free & Powerful)

1. Download [DBeaver Community](https://dbeaver.io/)
2. Create new database connection → MySQL
3. Configure with:
   - **Server Host**: `localhost`
   - **Port**: `3307`
   - **Database**: `merchantdb`
   - **Username**: `user`
   - **Password**: `password`
4. Browse and query the data

---

### Method 4: Check Logs in Application

The application logs SQL queries (as configured in `application.yml` with `show-sql: true`). 
When you run requests, you'll see Hibernate SQL queries in the console output showing INSERT/UPDATE/DELETE/SELECT statements.

---

## 📝 Complete Testing Workflow

### Step 1: Start MySQL Container (if not already running)
```bash
docker run --name mysql-merchant -e MYSQL_ROOT_PASSWORD=rootpassword -e MYSQL_DATABASE=merchantdb -e MYSQL_USER=user -e MYSQL_PASSWORD=password -p 3307:3306 -d mysql:8.0
```

### Step 2: Build Application
```bash
mvn clean package
```

### Step 3: Start Spring Boot Application
```bash
mvn spring-boot:run
```
Or run the JAR:
```bash
java -jar target/NikatMerchant-1.0-SNAPSHOT.jar
```

### Step 4: Test via Swagger UI
Open: `http://localhost:8080/swagger-ui.html`

### Step 5: Verify Data in Database
```bash
docker exec -it mysql-merchant mysql -u user -p merchantdb -e "SELECT * FROM merchants;"
```

---

## ✅ Validation Rules

The API validates input with these rules:
- `primaryPhone`: Required, cannot be blank
- `alternatePhone`: Optional
- `merchantEmail`: Required, cannot be blank, must be valid email format
- `whatAppAvailable`: Optional boolean (pass `true` or `false`, not `1` or `0`)
- `businessName`: Required, cannot be blank
- `businessType`: Optional
- `category`: Required, cannot be blank
- `subCategory`: Optional
- `personAge`: Optional integer
- `yearsOfExperience`: Optional integer
- `area`: Optional
- `address`: Required, cannot be blank
- `lat`: Optional double
- `lon`: Optional double
- `homeServiceAvailable`: Optional boolean (pass `true` or `false`, not `1` or `0`)
- `workingDays`: Optional string
- `workingHours`: Optional string
- `acquisitionSource`: Optional
- `addedBy`: Optional
- `metaData`: Optional JSON string

Invalid requests will return **400 Bad Request** with error details.

---

## 🐛 Troubleshooting

### Issue: Connection Refused (Port 3307)
- **Check**: Is MySQL container running? `docker ps`
- **Fix**: Start container: `docker start mysql-merchant`

### Issue: Invalid Credentials
- **Check**: Username/password in `application.yml` match Docker environment variables
- **Current Config**: user=`user`, password=`password`

### Issue: No Data Visible
- **Check**: Tables created? Run: `SHOW TABLES;` in MySQL
- **Note**: Hibernate auto-creates tables with `ddl-auto: update`

### Issue: Application Won't Start
- **Check**: Maven built successfully: `mvn clean install`
- **Check**: MySQL is running and accessible

---

## 📊 Example Test Sequence

```bash
# 1. Create first merchant
curl -X POST http://localhost:8080/api/merchants \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Store1",
    "merchantEmail": "merchant1@test.com",
    "primaryPhone": "+1234567890",
    "alternatePhone": "+0987654321",
    "category": "Electronics",
    "subCategory": "Mobile",
    "businessType": "Retail",
    "address": "123 Main St",
    "area": "Downtown",
    "lat": 12.345,
    "lon": 67.890,
    "personAge": 30,
    "yearsOfExperience": 5,
    "whatAppAvailable": true,
    "homeServiceAvailable": true,
    "workingDays": "Mon,Tue,Wed,Thu,Fri",
    "workingHours": "10AM-7PM",
    "acquisitionSource": "Online",
    "addedBy": "Admin",
    "metaData": "{}"
  }'
# Returns: {"merchantId":1, ...}

# 2. Create second merchant
curl -X POST http://localhost:8080/api/merchants \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Store2",
    "merchantEmail": "merchant2@test.com",
    "primaryPhone": "+0987654321",
    "alternatePhone": "+1234567890",
    "category": "Groceries",
    "subCategory": "Fruits",
    "businessType": "Wholesale",
    "address": "456 New St",
    "area": "Uptown",
    "lat": 13.456,
    "lon": 68.901,
    "personAge": 35,
    "yearsOfExperience": 10,
    "whatAppAvailable": false,
    "homeServiceAvailable": false,
    "workingDays": "Mon,Tue,Wed,Thu,Fri,Sat",
    "workingHours": "9AM-8PM",
    "acquisitionSource": "Referral",
    "addedBy": "Manager",
    "metaData": "{}"
  }'
# Returns: {"merchantId":2, ...}

# 3. Get all merchants
curl -X GET http://localhost:8080/api/merchants
# Returns: [{"merchantId":1, ...}, {"merchantId":2, ...}]

# 4. Check data in DB
docker exec -it mysql-merchant mysql -u user -p merchantdb -e "SELECT COUNT(*) FROM merchants;"
# Returns: 2
```

---

## 🎓 Next Steps

1. **Test all endpoints** using Swagger UI or cURL
2. **Verify data persistence** by restarting the application
3. **Check database integrity** using MySQL CLI or GUI tools
4. **Add authentication** (SecurityConfig is already configured)
5. **Add unit/integration tests** in the test folder

---

## Database Schema Changes for Production

When deploying to AWS RDS, **do not use `ddl-auto: update`** as it can cause issues. Instead:

1. **Set `ddl-auto: validate`** in `application-prod.yml` (already done)
2. **Apply schema changes manually** using SQL scripts

#### Required Schema Changes for Current Deployment:

```sql
-- Connect to your AWS RDS MySQL instance
-- Run these commands to add the new merchant fields:

USE merchantdb;

-- Add new columns to existing merchants table
ALTER TABLE merchants 
ADD COLUMN business_name VARCHAR(255) NOT NULL,
ADD COLUMN merchant_email VARCHAR(255) NOT NULL UNIQUE,
ADD COLUMN primary_phone VARCHAR(20) NOT NULL,
ADD COLUMN alternate_phone VARCHAR(20),
ADD COLUMN category VARCHAR(100) NOT NULL,
ADD COLUMN sub_category VARCHAR(100),
ADD COLUMN business_type VARCHAR(100),
ADD COLUMN address VARCHAR(255) NOT NULL,
ADD COLUMN area VARCHAR(255),
ADD COLUMN lat DOUBLE,
ADD COLUMN lon DOUBLE,
ADD COLUMN person_age INT,
ADD COLUMN years_of_experience INT,
ADD COLUMN whatsapp_available BOOLEAN,
ADD COLUMN home_service_available BOOLEAN,
ADD COLUMN working_days VARCHAR(255),
ADD COLUMN working_hours VARCHAR(50),
ADD COLUMN acquisition_source VARCHAR(255),
ADD COLUMN added_by VARCHAR(255),
ADD COLUMN date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN meta_data LONGTEXT;

-- Note: merchant_id should already be AUTO_INCREMENT PRIMARY KEY
```

#### How to Apply Changes:

1. **Connect to RDS**: Use MySQL Workbench, DBeaver, or AWS RDS console
2. **Backup first**: `mysqldump -h your-rds-endpoint -u username -p merchantdb > backup.sql`
3. **Run the ALTER statements** above
4. **Test the application** with `spring.profiles.active=prod`

#### For Future Changes:

Consider adding **Flyway** or **Liquibase** for proper database migrations:

```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

Then create migration files in `src/main/resources/db/migration/V1__Initial_schema.sql`
