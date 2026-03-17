# AWS Deployment Summary - Step by Step

## 📊 Architecture Overview

```
                         ┌─────────────────────────────┐
                         │    Your Local Machine        │
                         │  - IDE, Git, Maven, Docker   │
                         └──────────────┬────────────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
                    ▼                   ▼                   ▼
         ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
         │  AWS Elastic     │ │   AWS EC2        │ │   AWS RDS MySQL  │
         │   Beanstalk      │ │  (Application    │ │   (Database)     │
         │ (Optional)       │ │   Server)        │ │                  │
         │  - Auto scaling  │ │  - Ubuntu 24.04  │ │  - 20 GB Storage │
         │  - Load balancer │ │  - t2.micro      │ │  - db.t3.micro   │
         │  - Easy deploy   │ │  - Docker        │ │  - Port 3306     │
         └──────────────────┘ └──────────────────┘ └──────────────────┘
                    │                   │                   │
                    └───────────────────┼───────────────────┘
                                        │
                                        ▼
                    ┌──────────────────────────────────┐
                    │     AWS CloudWatch Logs          │
                    │  - Application Logs              │
                    │  - Database Logs                 │
                    │  - Metrics & Monitoring          │
                    └──────────────────────────────────┘
```

## 🚀 Deployment Phases Overview

### Phase 1: Account & Setup (1-2 hours)
- Create AWS Free Tier account
- Setup IAM user
- Install AWS CLI
- Configure credentials

### Phase 2: Database (30 minutes)
- Create RDS MySQL instance
- Configure security group
- Test connection
- Note endpoint details

### Phase 3: Application Server (1-2 hours)
**Choose ONE option:**
- **Option A: Elastic Beanstalk** (Recommended for beginners)
  - Easier deployment
  - Auto-scaling built-in
  - Managed environment
  
- **Option B: EC2 + Docker** (More control)
  - Direct server access
  - Custom configurations
  - More learning curve

### Phase 4: Integration (30 minutes)
- Connect application to RDS
- Setup environment variables
- Deploy application
- Test all endpoints

### Phase 5: Monitoring (1 hour)
- Setup CloudWatch logs
- Configure alarms
- Create backup strategy
- Monitor costs

---

## 📋 Step-by-Step Quick Execution Plan

### STEP 1: AWS Account Setup (Do This First!)

```
1. Go to https://aws.amazon.com/free/
2. Click "Create a free account"
3. Complete email verification
4. Add payment method (won't be charged for free tier)
5. Select "Basic Plan"
```

**After account is ready:**

```
6. Go to AWS Console → IAM → Users
7. Create new user: "merchant-deployment"
8. Enable console access
9. Create access key
10. Download credentials CSV (SAVE THIS!)
11. Install AWS CLI: https://awscli.amazonaws.com/AWSCLIV2.msi
12. Run: aws configure
    - Access Key: [from CSV]
    - Secret Key: [from CSV]
    - Region: us-east-1
    - Output: json
```

### STEP 2: Create RDS MySQL Database

```
1. AWS Console → RDS → Create Database
2. Select MySQL 8.0.37
3. Select "Free Tier"
4. Instance identifier: merchant-db
5. Master username: admin
6. Create password (SAVE THIS!)
7. Instance type: db.t3.micro
8. Storage: 20 GB
9. Publicly accessible: Yes
10. Click Create (wait 5-10 minutes)
```

**Get Connection Details:**

```
1. Go to RDS → Databases → merchant-db
2. Copy the Endpoint (e.g., merchant-db.xxxx.us-east-1.rds.amazonaws.com)
3. Port: 3306
4. Save these details!
```

### STEP 3: Setup Application Server (Choose One)

#### OPTION A: Elastic Beanstalk (Recommended)

```powershell
# On your local machine

# 1. Build application
mvn clean package -DskipTests

# 2. Install EB CLI
pip install awsebcli

# 3. Initialize EB
eb init -p "Java 17 running on 64bit Amazon Linux 2" -r us-east-1 NikatMerchant

# 4. Create environment
eb create NikatMerchant-prod --instance-type t3.micro

# 5. Set environment variables
eb setenv SPRING_DATASOURCE_URL=jdbc:mysql://merchant-db.xxxx.us-east-1.rds.amazonaws.com:3306/merchantdb
eb setenv SPRING_DATASOURCE_USERNAME=admin
eb setenv SPRING_DATASOURCE_PASSWORD=YourRDSPassword
eb setenv SPRING_PROFILES_ACTIVE=prod

# 6. Deploy
eb deploy

# 7. Monitor
eb events -f

# 8. View logs
eb logs

# 9. Open application
eb open
```

#### OPTION B: EC2 + Docker

```powershell
# 1. Launch EC2 Instance
# AWS Console → EC2 → Instances → Launch Instances
# - Name: merchant-app
# - AMI: Ubuntu Server 24.04 LTS
# - Type: t2.micro
# - Create new security group with:
#   - SSH (22) from your IP
#   - HTTP (80) from 0.0.0.0/0
#   - Custom TCP 8080 from 0.0.0.0/0
# - Storage: 20 GB
# - Launch & download key pair

# 2. SSH into EC2
ssh -i merchant-app.pem ubuntu@<PUBLIC_IP>

# 3. Install Docker (on EC2)
sudo apt update && sudo apt upgrade -y
sudo apt install -y docker.io
sudo usermod -aG docker ubuntu

# 4. Install Docker Compose (on EC2)
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 5. On your local machine - Build Docker image
docker build -t merchant-app:latest .

# 6. Copy code to EC2 or use docker-compose
# Create docker-compose.yml with RDS credentials

# 7. Run application
docker-compose up -d

# 8. Check logs
docker-compose logs -f app
```

### STEP 4: Test Your Deployment

```powershell
# Test application health
curl http://<YOUR_APP_URL>:8080/actuator/health

# Test API - Create Merchant
curl -X POST http://<YOUR_APP_URL>:8080/api/merchants `
  -H "Content-Type: application/json" `
  -d '{
    "merchantName": "Test Store",
    "merchantAddress": "123 Main St",
    "merchantContactNumber": "+1234567890",
    "merchantEmailId": "test@example.com",
    "merchantCategory": "Retail"
  }'

# Get all merchants
curl http://<YOUR_APP_URL>:8080/api/merchants

# View Swagger UI
# Open browser: http://<YOUR_APP_URL>:8080/swagger-ui.html
```

### STEP 5: Monitor Your Application

```powershell
# View logs
# Elastic Beanstalk:
eb logs

# EC2 + Docker:
docker-compose logs -f app

# View in CloudWatch
# AWS Console → CloudWatch → Log Groups
```

---

## 🎯 Key Information to Save

Create a text file and save:

```
========== AWS DEPLOYMENT INFO ==========

AWS Account ID: _______________
AWS Region: us-east-1
IAM User: merchant-deployment
Access Key: _______________
Secret Key: _______________

RDS Database:
- Endpoint: _______________
- Port: 3306
- Database: merchantdb
- Username: admin
- Password: _______________

EC2 Instance (if using):
- Public IP: _______________
- Instance ID: _______________
- Key Pair File: merchant-app.pem

Application URL:
- Elastic Beanstalk: http://NikatMerchant-prod.elasticbeanstalk.com
- OR EC2: http://<PUBLIC_IP>:8080
- Swagger UI: {APP_URL}/swagger-ui.html

Credentials Saved In:
- AWS: IAM Console
- RDS: AWS Secrets Manager (SAVE THE PASSWORD!)
- EC2: .pem file (SAVE SAFELY!)

==========================================
```

---

## 💰 Free Tier Resource Limits

```
EC2 (Elastic Compute Cloud):
- t2.micro: 750 hours/month (that's ~1 instance all month!)
- Data transfer: 1 GB/month free (then $0.09 per GB)

RDS (Relational Database):
- db.t3.micro: 750 hours/month
- 20 GB storage (included)
- 20 GB backup (included)
- Data transfer: 1 GB/month free

OTHER:
- 1 Elastic IP address (free if in use)
- CloudWatch: 10 free dashboards
- SNS: 1,000 email notifications/month
```

**IMPORTANT: Monitor your usage!**
```powershell
# Check costs regularly
# AWS Console → Billing → Bills → View your charges

# Set up billing alert
aws cloudwatch put-metric-alarm \
  --alarm-name BillingAlert \
  --metric-name EstimatedCharges \
  --namespace AWS/Billing \
  --statistic Maximum \
  --period 86400 \
  --evaluation-periods 1 \
  --threshold 5 \
  --comparison-operator GreaterThanThreshold
```

---

## 🔒 Security Best Practices

```
1. ✅ NEVER commit secrets to Git
2. ✅ Use environment variables for sensitive data
3. ✅ Restrict RDS to specific security groups
4. ✅ Use strong passwords
5. ✅ Enable MFA for AWS account
6. ✅ Keep EC2 key pair safe (.pem file)
7. ✅ Use IAM roles instead of access keys when possible
8. ✅ Regularly rotate credentials
9. ✅ Enable CloudTrail for audit logs
10. ✅ Update security groups after initial setup
```

---

## ⚠️ Common Mistakes to Avoid

```
❌ Leaving RDS publicly accessible indefinitely
❌ Using simple passwords
❌ Hardcoding database credentials in code
❌ Forgetting to stop instances when not in use
❌ Not monitoring costs
❌ Using default security groups
❌ Forgetting to back up data
❌ Running outside free tier regions
❌ Not reading error logs
❌ Deploying without testing locally first
```

---

## 📞 Getting Help

```
1. AWS Documentation: https://docs.aws.amazon.com/
2. AWS Support: https://console.aws.amazon.com/support/
3. Stack Overflow: Tag with [amazon-aws] [spring-boot]
4. Spring Boot Docs: https://spring.io/projects/spring-boot
5. Docker Docs: https://docs.docker.com/
```

---

## ✅ Deployment Verification Checklist

After deployment, verify:

```
□ Application is running (health check returns 200)
□ Database connection works
□ Can create merchant via POST /api/merchants
□ Can retrieve merchants via GET /api/merchants
□ Can update merchant via PUT /api/merchants/{id}
□ Can delete merchant via DELETE /api/merchants/{id}
□ Swagger UI is accessible
□ Logs are being collected
□ No 5xx errors in CloudWatch
□ Billing is within free tier limits
□ Database has automated backups enabled
```

---

**You're all set! Follow the steps above and your application will be live on AWS!** 🎉

For detailed information, refer to:
- `AWS_DEPLOYMENT_GUIDE.md` - Complete deployment guide
- `AWS_DEPLOYMENT_CHECKLIST.md` - Detailed checklist
- `AWS_QUICK_REFERENCE.md` - Command reference


