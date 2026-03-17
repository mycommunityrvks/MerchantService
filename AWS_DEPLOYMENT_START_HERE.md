# NikatMerchant - AWS Deployment Guide

## 📚 Documentation Files

This project includes comprehensive AWS deployment documentation:

1. **AWS_DEPLOYMENT_SUMMARY.md** - START HERE! Quick step-by-step guide
2. **AWS_DEPLOYMENT_GUIDE.md** - Complete detailed guide with all phases
3. **AWS_DEPLOYMENT_CHECKLIST.md** - Detailed checklist for tracking progress
4. **AWS_QUICK_REFERENCE.md** - Quick command reference for AWS CLI

## 🎯 Quick Start (30 seconds)

If you're in a hurry, here's what to do:

1. Read: `AWS_DEPLOYMENT_SUMMARY.md` (5 minutes)
2. Follow: Step-by-step instructions in that file (2-3 hours)
3. Reference: `AWS_QUICK_REFERENCE.md` for commands

## 🏗️ Architecture

Your application will be deployed to AWS with:

```
┌─────────────────────────────────────────────────────┐
│            AWS Free Tier Account                    │
├──────────┬──────────────────────┬──────────────────┤
│          │                      │                  │
│   EC2    │   OR                 │   RDS MySQL      │
│          │   Elastic Beanstalk  │   (Shared)       │
│ t2.micro │   (Recommended)      │   db.t3.micro    │
│          │                      │   20GB storage   │
│          │                      │   Port 3306      │
└──────────┴──────────────────────┴──────────────────┘
```

## 💻 Prerequisites

Before you start, ensure you have:

- [ ] AWS Account with Free Tier eligibility
- [ ] Valid payment method (won't be charged)
- [ ] AWS CLI v2 installed
- [ ] Docker installed (for building images)
- [ ] Maven 3.6+ installed
- [ ] Java 17 installed
- [ ] Git installed
- [ ] SSH client (for EC2 access)

### Install Prerequisites (Windows)

```powershell
# AWS CLI v2
# Download from: https://awscli.amazonaws.com/AWSCLIV2.msi

# Verify all are installed
aws --version
docker --version
mvn --version
java -version
```

## 🚀 Deployment Overview

### 5 Main Phases:

1. **AWS Account Setup** (1-2 hours)
   - Create AWS account
   - Setup IAM user
   - Install and configure AWS CLI

2. **Database Setup** (30 minutes)
   - Create RDS MySQL instance
   - Configure security groups
   - Get connection details

3. **Server Setup** (1-2 hours)
   - Choose: Elastic Beanstalk OR EC2
   - Launch and configure instance
   - Install necessary tools

4. **Application Deployment** (30 minutes)
   - Build Docker image
   - Deploy application
   - Configure environment variables

5. **Testing & Monitoring** (30 minutes)
   - Test all API endpoints
   - Setup CloudWatch monitoring
   - Configure billing alerts

**Total Time: ~5 hours (first time)**

## 📖 Step-by-Step Guide

### Phase 1: AWS Account Setup

**Step 1.1: Create AWS Account**
- Go to https://aws.amazon.com/free/
- Click "Create a free account"
- Complete email verification
- Add payment method
- Choose "Basic Plan"

**Step 1.2: Create IAM User**
```powershell
# In AWS Console:
# IAM → Users → Create user
# Username: merchant-deployment
# Check: "Provide user access to AWS Management Console"
# Create password
# Attach policy: AdministratorAccess
# Create access key
# Download CSV file (SAVE IT!)
```

**Step 1.3: Configure AWS CLI**
```powershell
# Install AWS CLI (if not already installed)
# https://awscli.amazonaws.com/AWSCLIV2.msi

# Configure credentials
aws configure

# Enter when prompted:
# AWS Access Key ID: [from CSV]
# AWS Secret Access Key: [from CSV]
# Default region: us-east-1
# Default output format: json

# Verify configuration
aws sts get-caller-identity
```

### Phase 2: Database Setup

**Step 2.1: Create RDS Instance**
```powershell
# In AWS Console:
# RDS → Databases → Create Database
# Engine: MySQL 8.0.37
# Templates: Free tier
# Instance identifier: merchant-db
# Master username: admin
# Master password: [Create strong password - SAVE IT!]
# Instance class: db.t3.micro
# Storage: 20 GB
# Public accessibility: Yes
# Create database

# Wait 5-10 minutes for creation...
```

**Step 2.2: Get Connection Details**
```powershell
# In AWS Console:
# RDS → Databases → merchant-db
# Copy Endpoint (e.g., merchant-db.xxxx.us-east-1.rds.amazonaws.com)
# Port: 3306
# SAVE THESE!
```

**Step 2.3: Configure Security Group**
```powershell
# In AWS Console:
# EC2 → Security Groups → Find RDS security group
# Inbound Rules → Add Rule:
#   Type: MySQL/Aurora
#   Protocol: TCP
#   Port: 3306
#   Source: 0.0.0.0/0 (Open for now, restrict later)
```

### Phase 3: Choose Deployment Option

#### Option A: Elastic Beanstalk (Recommended)

This is easier and more automated. Perfect for beginners.

```powershell
# Step 3A.1: Build application locally
cd C:\Users\91981\IdeaProjects\NikatMerchant
mvn clean package -DskipTests

# Step 3A.2: Install EB CLI
pip install awsebcli

# Step 3A.3: Initialize Elastic Beanstalk
eb init -p "Java 17 running on 64bit Amazon Linux 2" -r us-east-1 NikatMerchant

# Step 3A.4: Create environment
eb create NikatMerchant-prod --instance-type t3.micro

# Wait for environment to be created (5-10 minutes)...

# Step 3A.5: Set environment variables
eb setenv SPRING_DATASOURCE_URL=jdbc:mysql://merchant-db.xxxx.us-east-1.rds.amazonaws.com:3306/merchantdb
eb setenv SPRING_DATASOURCE_USERNAME=admin
eb setenv SPRING_DATASOURCE_PASSWORD=YourRDSPassword
eb setenv SPRING_PROFILES_ACTIVE=prod

# Step 3A.6: Deploy application
eb deploy

# Step 3A.7: Monitor deployment
eb events -f

# Step 3A.8: View logs
eb logs

# Step 3A.9: Get application URL
eb status
# Look for "CNAME" - that's your application URL
```

#### Option B: EC2 + Docker

This gives you more control but requires more setup.

```powershell
# Step 3B.1: Launch EC2 Instance
# AWS Console → EC2 → Instances → Launch Instances
# - Name: merchant-app
# - AMI: Ubuntu Server 24.04 LTS
# - Instance type: t2.micro
# - Create new security group named: merchant-sg
# - Add inbound rules:
#   - SSH (22) from your IP
#   - HTTP (80) from 0.0.0.0/0
#   - Custom TCP 8080 from 0.0.0.0/0
# - Storage: 20 GB
# - Launch & download .pem file

# Step 3B.2: Connect to EC2
ssh -i C:\Path\To\merchant-app.pem ubuntu@<PUBLIC_IP>

# Step 3B.3: Install Docker (run these on EC2)
sudo apt update && sudo apt upgrade -y
sudo apt install -y docker.io
sudo usermod -aG docker ubuntu
exit # and reconnect

# Step 3B.4: Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Step 3B.5: Create docker-compose.yml on EC2
cat > docker-compose.yml << 'EOF'
version: '3.8'
services:
  app:
    image: merchant-app:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://merchant-db.xxxx.us-east-1.rds.amazonaws.com:3306/merchantdb
      - SPRING_DATASOURCE_USERNAME=admin
      - SPRING_DATASOURCE_PASSWORD=YourRDSPassword
      - SPRING_PROFILES_ACTIVE=prod
    restart: unless-stopped
EOF

# Step 3B.6: On your local machine - Build Docker image
docker build -t merchant-app:latest .

# Step 3B.7: Push to Docker Hub (easy option)
docker login
docker tag merchant-app:latest YOUR_DOCKER_HUB/merchant-app:latest
docker push YOUR_DOCKER_HUB/merchant-app:latest

# Step 3B.8: On EC2 - Pull and run
docker pull YOUR_DOCKER_HUB/merchant-app:latest
docker-compose up -d

# Step 3B.9: Check application
docker-compose logs -f app
```

### Phase 4: Test Your Deployment

**Test 1: Health Check**
```powershell
# If using Elastic Beanstalk:
curl http://NikatMerchant-prod.elasticbeanstalk.com/actuator/health

# If using EC2:
curl http://<EC2_PUBLIC_IP>:8080/actuator/health
```

**Test 2: Create Merchant**
```powershell
$url = "http://<YOUR_APP_URL>:8080/api/merchants"
$body = @{
    merchantName = "Test Store"
    merchantAddress = "123 Main St"
    merchantContactNumber = "+1234567890"
    merchantEmailId = "test@example.com"
    merchantCategory = "Retail"
} | ConvertTo-Json

Invoke-WebRequest -Uri $url -Method POST -Body $body -ContentType "application/json"
```

**Test 3: View Swagger UI**
- Open browser: `http://<YOUR_APP_URL>:8080/swagger-ui.html`
- Try out API endpoints through the UI

**Test 4: Run All Tests**
```powershell
# GET all merchants
curl http://<YOUR_APP_URL>:8080/api/merchants

# GET merchant by ID
curl http://<YOUR_APP_URL>:8080/api/merchants/1

# UPDATE merchant
curl -X PUT http://<YOUR_APP_URL>:8080/api/merchants/1 ...

# DELETE merchant
curl -X DELETE http://<YOUR_APP_URL>:8080/api/merchants/1
```

### Phase 5: Monitoring and Alerts

**Setup CloudWatch Logs**
```powershell
# Elastic Beanstalk:
eb logs

# EC2 + Docker:
docker-compose logs -f app
```

**Setup Billing Alert**
```powershell
aws cloudwatch put-metric-alarm \
  --alarm-name "Monthly-Bill-Alert" \
  --metric-name EstimatedCharges \
  --namespace AWS/Billing \
  --statistic Maximum \
  --period 86400 \
  --evaluation-periods 1 \
  --threshold 5 \
  --comparison-operator GreaterThanThreshold
```

## 📊 Monitoring Dashboard

Check your resources regularly:

```powershell
# Check EC2 instances
aws ec2 describe-instances --region us-east-1

# Check RDS instance
aws rds describe-db-instances --region us-east-1

# Check costs
aws ce get-cost-and-usage \
  --time-period Start=2024-01-01,End=2024-01-31 \
  --granularity MONTHLY \
  --metrics "UnblendedCost" \
  --group-by Type=DIMENSION,Key=SERVICE
```

## 🔒 Security Checklist

Before going to production:

- [ ] All hardcoded secrets removed
- [ ] Environment variables used for database credentials
- [ ] Security groups restrict access appropriately
- [ ] SSH key pair stored securely
- [ ] Regular backups enabled on RDS
- [ ] CloudTrail logging enabled
- [ ] MFA enabled on AWS account
- [ ] IAM role restrictions in place

## 💰 Cost Optimization

**Free Tier Limits:**
- EC2 t2.micro: 750 hours/month (~1 instance full month)
- RDS db.t3.micro: 750 hours/month
- 20 GB storage (both services)
- 1 GB data transfer/month free

**To stay in free tier:**
1. Stop instances when not in use
2. Monitor CPU and memory usage
3. Set up billing alarms
4. Delete unused snapshots
5. Use AWS Free Tier calculator

## 🆘 Troubleshooting

### Application won't start
```powershell
# Check logs
eb logs  # for Elastic Beanstalk
docker-compose logs -f  # for EC2

# Check RDS connectivity
mysql -h <RDS_ENDPOINT> -u admin -p -e "SELECT 1;"
```

### Can't connect to database
1. Verify RDS instance is running
2. Check security group allows port 3306
3. Verify environment variables have correct credentials
4. Ensure RDS is in same region

### Out of memory
- Increase heap size in Dockerfile
- Reduce pool size in connection properties
- Use smaller instance type (unlikely with free tier)

## 📚 Additional Resources

- AWS Documentation: https://docs.aws.amazon.com/
- Spring Boot on AWS: https://aws.amazon.com/developer/language/java/
- Elastic Beanstalk: https://docs.aws.amazon.com/elasticbeanstalk/
- RDS: https://docs.aws.amazon.com/rds/
- EC2: https://docs.aws.amazon.com/ec2/

## 📝 Important Files

- `Dockerfile` - Docker image configuration
- `docker-compose.yml` - Local testing with Docker
- `application-prod.yml` - Production configuration
- `.ebextensions/01_aws.config` - Elastic Beanstalk configuration
- `pom.xml` - Maven dependencies and build configuration

## 📋 Project Structure

```
NikatMerchant/
├── src/main/java/org/example/
│   ├── Main.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   └── MerchantController.java
│   ├── service/
│   │   └── MerchantService.java
│   ├── repository/
│   │   └── MerchantRepository.java
│   ├── entity/
│   │   └── Merchant.java
│   ├── dto/
│   │   ├── MerchantRequestDto.java
│   │   └── MerchantResponseDto.java
│   └── exception/
│       ├── ApiError.java
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application.yml
│   └── application-prod.yml
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── AWS_DEPLOYMENT_GUIDE.md
├── AWS_DEPLOYMENT_SUMMARY.md
├── AWS_DEPLOYMENT_CHECKLIST.md
└── AWS_QUICK_REFERENCE.md
```

## ✅ Deployment Checklist

Before deploying:

- [ ] All code committed to Git
- [ ] Unit tests pass
- [ ] Application builds successfully
- [ ] Docker image builds successfully
- [ ] AWS account created and verified
- [ ] IAM user created and configured
- [ ] AWS CLI installed and configured
- [ ] RDS instance created and accessible
- [ ] Security groups configured
- [ ] Ready to deploy!

---

**Need help?** Check the detailed guides:
- `AWS_DEPLOYMENT_SUMMARY.md` - Quick start
- `AWS_DEPLOYMENT_GUIDE.md` - Complete reference
- `AWS_QUICK_REFERENCE.md` - Command cheatsheet

Good luck with your deployment! 🚀


