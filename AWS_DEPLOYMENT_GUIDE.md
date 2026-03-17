# AWS Deployment Guide for NikatMerchant Application

## Overview
This guide walks you through deploying your Spring Boot application to AWS with free tier resources.

---

## Phase 1: AWS Account Setup

### Step 1: Create AWS Account
1. Go to https://aws.amazon.com/free/
2. Click "Create a free account"
3. Provide email, password, and account name
4. Complete identity verification (phone/SMS)
5. Enter payment method (won't be charged for free tier)
6. Choose "Basic Plan"

### Step 2: Create IAM User (Best Practice)
1. Go to AWS Console → IAM → Users
2. Click "Create user"
3. Username: `merchant-deployment`
4. Enable "Provide user access to AWS Management Console"
5. Create custom password
6. Attach policies: `AdministratorAccess` (for now, can be restricted later)
7. Create access key: Download CSV with Access Key ID and Secret Access Key
8. **Save this safely** - you'll need it for AWS CLI

### Step 3: Install AWS CLI
```powershell
# Download and install AWS CLI v2
# For Windows: https://awscli.amazonaws.com/AWSCLIV2.msi

# After installation, verify
aws --version

# Configure AWS CLI
aws configure

# When prompted, enter:
# AWS Access Key ID: [from IAM user]
# AWS Secret Access Key: [from IAM user]
# Default region: us-east-1 (free tier region)
# Default output format: json
```

---

## Phase 2: Database Setup (RDS MySQL)

### Step 1: Create RDS MySQL Instance
1. Go to AWS Console → RDS → Databases
2. Click "Create database"
3. **Configuration:**
   - Engine: MySQL 8.0.37 (Free tier eligible)
   - Templates: Free tier
   - DB instance identifier: `merchant-db`
   - Master username: `admin`
   - Master password: Create strong password (save it!)
   - DB instance class: db.t3.micro (Free tier)
   - Storage: 20 GB (Free tier limit)
   - Publicly accessible: Yes (for testing)
   - VPC security group: Create new or use default
4. Click "Create database"
5. Wait 5-10 minutes for creation

### Step 2: Configure Security Group
1. Go to EC2 → Security Groups
2. Find the RDS security group created above
3. Inbound rules:
   - Type: MySQL/Aurora
   - Protocol: TCP
   - Port: 3306
   - Source: 0.0.0.0/0 (Open to all - change to your IP later)
4. Add rule for your EC2 instance security group

### Step 3: Get RDS Connection Details
1. Go to RDS → Databases → merchant-db
2. Note the **Endpoint** (e.g., merchant-db.xxx.us-east-1.rds.amazonaws.com)
3. Use these in your application properties

---

## Phase 3: Application Server Setup (EC2)

### Option A: Using Elastic Beanstalk (Recommended for Beginners)

#### Step 1: Prepare Application
1. Build JAR file:
```bash
mvn clean package
```

2. Modify `application-prod.yml` for production:
```yaml
server:
  port: 8080

spring:
  application:
    name: NikatMerchant
  datasource:
    url: jdbc:mysql://<RDS_ENDPOINT>:3306/merchantdb
    username: admin
    password: <RDS_PASSWORD>
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 5
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

#### Step 2: Create Elastic Beanstalk App
1. Go to Elastic Beanstalk → Applications → Create application
2. **Configuration:**
   - Application name: `NikatMerchant`
   - Environment tier: Web server environment
   - Environment name: `NikatMerchant-prod`
   - Platform: Java 17 running on 64bit Amazon Linux 2
   - Application code: Upload JAR or use .ebextensions

3. Create and configure environment variables:
   - `SPRING_DATASOURCE_URL`: Your RDS endpoint
   - `SPRING_DATASOURCE_USERNAME`: admin
   - `SPRING_DATASOURCE_PASSWORD`: Your RDS password
   - `SPRING_PROFILES_ACTIVE`: prod

4. Instance configuration:
   - Instance type: t2.micro (Free tier eligible)
   - Capacity: 1 instance

#### Step 3: Deploy Application
```powershell
# Install EB CLI
pip install awsebcli

# Initialize EB in project directory
eb init -p "Java 17 running on 64bit Amazon Linux 2" -r us-east-1 NikatMerchant

# Create environment
eb create NikatMerchant-prod

# Deploy updates
eb deploy

# Monitor logs
eb logs

# Open application
eb open
```

---

### Option B: Using EC2 + Docker (More Control)

#### Step 1: Launch EC2 Instance
1. Go to EC2 → Instances → Launch instances
2. **Configuration:**
   - Name: `merchant-app`
   - AMI: Ubuntu Server 24.04 LTS (Free tier eligible)
   - Instance type: t2.micro
   - Key pair: Create new (download .pem file)
   - Security group:
     - Inbound: SSH (22) from your IP
     - Inbound: HTTP (80) from 0.0.0.0/0
     - Inbound: HTTPS (443) from 0.0.0.0/0
     - Inbound: Custom TCP 8080 from 0.0.0.0/0
3. Storage: 20 GB (Free tier limit)
4. Launch instance

#### Step 2: Connect to EC2
```powershell
# Change permissions on PEM file
chmod 400 merchant-app.pem

# SSH into instance (replace IP with your instance public IP)
ssh -i merchant-app.pem ubuntu@<PUBLIC_IP>
```

#### Step 3: Setup Environment on EC2
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
sudo apt install -y docker.io

# Add user to docker group
sudo usermod -aG docker ubuntu

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installations
docker --version
docker-compose --version
```

#### Step 4: Create docker-compose.yml
Create on EC2:
```bash
cat > /home/ubuntu/docker-compose.yml << 'EOF'
version: '3.8'

services:
  app:
    image: merchant-app:latest
    container_name: nikat-merchant
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://<RDS_ENDPOINT>:3306/merchantdb
      - SPRING_DATASOURCE_USERNAME=admin
      - SPRING_DATASOURCE_PASSWORD=<RDS_PASSWORD>
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - db
    restart: always

  db:
    image: mysql:8.0
    container_name: merchant-db-local
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=merchantdb
    volumes:
      - mysql-data:/var/lib/mysql
    restart: always

volumes:
  mysql-data:
EOF
```

#### Step 5: Build and Push Docker Image
```powershell
# From your local machine

# Build Docker image
docker build -t merchant-app:latest .

# Tag for AWS ECR (optional)
# aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com
# docker tag merchant-app:latest <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/merchant-app:latest
# docker push <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/merchant-app:latest

# Or use Docker Hub (easier)
# docker login
# docker tag merchant-app:latest <YOUR_DOCKER_HUB_USERNAME>/merchant-app:latest
# docker push <YOUR_DOCKER_HUB_USERNAME>/merchant-app:latest
```

#### Step 6: Deploy on EC2
```bash
# SSH into EC2
ssh -i merchant-app.pem ubuntu@<PUBLIC_IP>

# Download and run
cd /home/ubuntu
docker-compose up -d

# Check logs
docker-compose logs -f app
```

---

## Phase 4: Setup RDS Database Initial Schema

### Create Database and User
```bash
# Connect to RDS from EC2 or local machine
mysql -h <RDS_ENDPOINT> -u admin -p

# Once logged in:
CREATE DATABASE IF NOT EXISTS merchantdb;
USE merchantdb;

# The Merchant table will be created automatically by Hibernate
# (because of ddl-auto: update in application.yml)
```

---

## Phase 5: Configure SSL and Domain (Optional)

### Using AWS Certificate Manager
1. Go to ACM → Request certificate
2. Domain name: your-domain.com
3. Validation: Email or DNS
4. Once issued, attach to Elastic Beanstalk or ALB

### Setup ALB (for EC2)
1. EC2 → Load Balancers → Create Application Load Balancer
2. Configure target group pointing to EC2 instance
3. Add listener for HTTPS with ACM certificate

---

## Phase 6: Monitoring and Logging

### CloudWatch Monitoring
```powershell
# View logs
aws logs tail /aws/elasticbeanstalk/NikatMerchant-prod/var/log/eb-engine.log --follow

# Or use EC2
docker-compose logs -f
```

### Setup CloudWatch Alarms
1. CloudWatch → Alarms → Create alarm
2. Monitor:
   - CPU Utilization (> 80%)
   - Memory Utilization
   - HTTP 5xx errors
   - RDS CPU and connections

---

## Phase 7: Backup and Recovery

### RDS Automated Backups
1. RDS → Databases → merchant-db
2. Backup retention period: 7 days (free)
3. Preferred backup window: 03:00-04:00 UTC

### Create Snapshot
```powershell
aws rds create-db-snapshot \
  --db-instance-identifier merchant-db \
  --db-snapshot-identifier merchant-db-backup-$(date +%Y%m%d)
```

---

## Phase 8: Cost Optimization (Free Tier Important!)

### Free Tier Limits:
- **EC2**: 750 hours/month of t2.micro
- **RDS**: 750 hours/month of db.t3.micro, 20 GB storage
- **Data Transfer**: 1 GB free/month
- **Elastic IP**: 1 free (only when associated)

### Cost-Saving Tips:
1. Stop instances when not in use
2. Use Reserved Instances for longer commitments
3. Monitor with AWS Cost Explorer
4. Set up billing alerts (SNS)

```powershell
# Set up billing alert
aws cloudwatch put-metric-alarm \
  --alarm-name "Monthly-Billing-Alert" \
  --alarm-actions arn:aws:sns:us-east-1:<AWS_ACCOUNT_ID>:billing-alert \
  --metric-name EstimatedCharges \
  --namespace AWS/Billing \
  --statistic Maximum \
  --period 86400 \
  --evaluation-periods 1 \
  --threshold 10
```

---

## Phase 9: Access Your Application

### After Deployment:
1. **Elastic Beanstalk**: `http://NikatMerchant-prod.elasticbeanstalk.com`
2. **EC2**: `http://<PUBLIC_IP>:8080`
3. **Swagger UI**: `{APP_URL}/swagger-ui.html`
4. **API Docs**: `{APP_URL}/v3/api-docs`

### Test Endpoints:
```bash
# Create merchant
curl -X POST http://<APP_URL>/api/merchants \
  -H "Content-Type: application/json" \
  -d '{
    "merchantName": "Test",
    "merchantAddress": "123 St",
    "merchantContactNumber": "+1234567890",
    "merchantEmailId": "test@example.com",
    "merchantCategory": "Retail"
  }'

# Get all merchants
curl http://<APP_URL>/api/merchants

# Get merchant by ID
curl http://<APP_URL>/api/merchants/1

# Update merchant
curl -X PUT http://<APP_URL>/api/merchants/1 \
  -H "Content-Type: application/json" \
  -d '{...}'

# Delete merchant
curl -X DELETE http://<APP_URL>/api/merchants/1
```

---

## Troubleshooting

### Application Won't Start
```bash
# Check logs
docker-compose logs app
# or
eb logs

# Check RDS connectivity
# Verify security groups
# Verify credentials in environment variables
```

### Database Connection Issues
```bash
# Test connection from EC2
mysql -h <RDS_ENDPOINT> -u admin -p merchantdb

# Check RDS security group allows traffic
# Check subnet and VPC configuration
```

### High Costs
```bash
# Use AWS Cost Explorer to identify expensive resources
# Stop unused instances
# Delete unused snapshots
```

---

## Next Steps

1. ✅ Set up AWS Account and IAM user
2. ✅ Create RDS MySQL instance
3. ✅ Launch EC2 instance
4. ✅ Build and push Docker image
5. ✅ Deploy application
6. ✅ Test all endpoints
7. ✅ Setup monitoring and alerts
8. ✅ Configure domain and SSL
9. ✅ Monitor costs regularly

---

## Support and Resources

- AWS Documentation: https://docs.aws.amazon.com/
- Spring Boot on AWS: https://aws.amazon.com/developer/language/java/
- AWS Free Tier: https://aws.amazon.com/free/
- AWS Cost Calculator: https://calculator.aws/


