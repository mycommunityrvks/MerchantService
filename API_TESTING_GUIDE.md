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
  "merchantName": "Acme Store",
  "merchantAddress": "123 Main St, City, Country",
  "merchantContactNumber": "+1234567890",
  "merchantEmailId": "contact@acmestore.com",
  "merchantCategory": "Electronics",
  "metadata": "{\"storeType\": \"retail\"}"
}
```

### Success Response (201 Created / 200 OK)
```json
{
  "merchantId": 1,
  "merchantName": "Acme Store",
  "merchantAddress": "123 Main St, City, Country",
  "merchantContactNumber": "+1234567890",
  "merchantEmailId": "contact@acmestore.com",
  "merchantCategory": "Electronics",
  "metadata": "{\"storeType\": \"retail\"}",
  "createdAt": "2026-03-17T10:30:00.000+00:00"
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
    \"merchantName\": \"Acme Store\",
    \"merchantAddress\": \"123 Main St\",
    \"merchantContactNumber\": \"+1234567890\",
    \"merchantEmailId\": \"contact@acmestore.com\",
    \"merchantCategory\": \"Electronics\",
    \"metadata\": \"\"
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
    \"merchantName\": \"Updated Store Name\",
    \"merchantAddress\": \"456 New St\",
    \"merchantContactNumber\": \"+9876543210\",
    \"merchantEmailId\": \"newemail@acmestore.com\",
    \"merchantCategory\": \"Retail\",
    \"metadata\": \"\"
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
- **Table Name**: `merchant`
- **Host**: `localhost`
- **Port**: `3307`
- **Username**: `user`
- **Password**: `password`

### Database Schema
```sql
CREATE TABLE merchant (
  merchant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  merchant_name VARCHAR(255) NOT NULL,
  merchant_address VARCHAR(255) NOT NULL,
  merchant_contact_number VARCHAR(20) NOT NULL,
  merchant_email_id VARCHAR(255) NOT NULL,
  merchant_category VARCHAR(100) NOT NULL,
  metadata LONGTEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
SELECT * FROM merchant;

-- View specific merchant
SELECT * FROM merchant WHERE merchant_id = 1;

-- Count total merchants
SELECT COUNT(*) FROM merchant;

-- View merchant details in formatted way
SELECT merchant_id, merchant_name, merchant_email_id, merchant_category, created_at FROM merchant;

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
4. Browse the `merchantdb` database → `merchant` table
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
docker exec -it mysql-merchant mysql -u user -p merchantdb -e "SELECT * FROM merchant;"
```

---

## ✅ Validation Rules

The API validates input with these rules:
- `merchantName`: Required, cannot be blank
- `merchantAddress`: Required, cannot be blank
- `merchantContactNumber`: Required, cannot be blank
- `merchantEmailId`: Required, must be valid email format
- `merchantCategory`: Required, cannot be blank
- `metadata`: Optional field

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
  -d '{"merchantName":"Store1","merchantAddress":"Addr1","merchantContactNumber":"+111","merchantEmailId":"store1@test.com","merchantCategory":"Cat1"}'
# Returns: {"merchantId":1, ...}

# 2. Create second merchant
curl -X POST http://localhost:8080/api/merchants \
  -H "Content-Type: application/json" \
  -d '{"merchantName":"Store2","merchantAddress":"Addr2","merchantContactNumber":"+222","merchantEmailId":"store2@test.com","merchantCategory":"Cat2"}'
# Returns: {"merchantId":2, ...}

# 3. Get all merchants
curl -X GET http://localhost:8080/api/merchants
# Returns: [{"merchantId":1, ...}, {"merchantId":2, ...}]

# 4. Check data in DB
docker exec -it mysql-merchant mysql -u user -p merchantdb -e "SELECT COUNT(*) FROM merchant;"
# Returns: 2
```

---

## 🎓 Next Steps

1. **Test all endpoints** using Swagger UI or cURL
2. **Verify data persistence** by restarting the application
3. **Check database integrity** using MySQL CLI or GUI tools
4. **Add authentication** (SecurityConfig is already configured)
5. **Add unit/integration tests** in the test folder
