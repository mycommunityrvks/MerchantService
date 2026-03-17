# AWS Deployment - Quick Reference Commands

## AWS CLI Setup

```powershell
# Install AWS CLI v2 (Windows)
# Download from: https://awscli.amazonaws.com/AWSCLIV2.msi

# Verify installation
aws --version

# Configure AWS credentials
aws configure
# Enter:
# - AWS Access Key ID: [from IAM user]
# - AWS Secret Access Key: [from IAM user]
# - Default region: us-east-1
# - Default output format: json
```

## Building and Testing Locally

```powershell
# Build application
mvn clean package -DskipTests

# Run tests
mvn test

# Run application locally
mvn spring-boot:run

# Build Docker image
docker build -t merchant-app:latest .

# Test Docker image locally
docker run --name merchant-app -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3307/merchantdb \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e SPRING_PROFILES_ACTIVE=prod \
  merchant-app:latest

# Run with docker-compose
docker-compose up -d
docker-compose logs -f
docker-compose down
```

## AWS RDS Database Commands

```powershell
# List RDS instances
aws rds describe-db-instances --region us-east-1

# Get RDS endpoint
aws rds describe-db-instances \
  --db-instance-identifier merchant-db \
  --query 'DBInstances[0].Endpoint.Address' \
  --region us-east-1

# Create RDS instance (via CLI)
aws rds create-db-instance \
  --db-instance-identifier merchant-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 8.0.37 \
  --allocated-storage 20 \
  --master-username admin \
  --master-user-password YourSecurePassword \
  --publicly-accessible true \
  --region us-east-1

# Connect to RDS from local machine
# First, install MySQL client:
# Windows: choco install mysql
# Then connect:
mysql -h <RDS_ENDPOINT> -u admin -p

# Create database and user
# Once connected:
CREATE DATABASE IF NOT EXISTS merchantdb;
USE merchantdb;

# Modify security group
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxxxxx \
  --protocol tcp \
  --port 3306 \
  --cidr 0.0.0.0/0 \
  --region us-east-1

# Create RDS snapshot
aws rds create-db-snapshot \
  --db-instance-identifier merchant-db \
  --db-snapshot-identifier merchant-db-backup-$(Get-Date -Format yyyyMMdd) \
  --region us-east-1

# List snapshots
aws rds describe-db-snapshots \
  --db-instance-identifier merchant-db \
  --region us-east-1
```

## AWS EC2 Commands

```powershell
# List EC2 instances
aws ec2 describe-instances --region us-east-1

# Get instance public IP
aws ec2 describe-instances \
  --instance-ids i-xxxxxxxx \
  --query 'Reservations[0].Instances[0].PublicIpAddress' \
  --region us-east-1

# Launch EC2 instance (via CLI)
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t2.micro \
  --key-name merchant-app \
  --security-groups merchant-sg \
  --region us-east-1

# Stop EC2 instance
aws ec2 stop-instances --instance-ids i-xxxxxxxx --region us-east-1

# Start EC2 instance
aws ec2 start-instances --instance-ids i-xxxxxxxx --region us-east-1

# Terminate EC2 instance
aws ec2 terminate-instances --instance-ids i-xxxxxxxx --region us-east-1

# SSH into EC2
ssh -i merchant-app.pem ubuntu@<PUBLIC_IP>

# Create security group
aws ec2 create-security-group \
  --group-name merchant-sg \
  --description "Security group for merchant app" \
  --region us-east-1

# Add inbound rule
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxxxxx \
  --protocol tcp \
  --port 22 \
  --cidr 0.0.0.0/0 \
  --region us-east-1
```

## AWS Elastic Beanstalk Commands

```powershell
# Install EB CLI
pip install awsebcli

# Initialize EB application
eb init -p "Java 17 running on 64bit Amazon Linux 2" -r us-east-1 NikatMerchant

# Create environment
eb create NikatMerchant-prod --instance-type t3.micro

# Deploy application
eb deploy

# View deployment events
eb events -f

# Check environment status
eb status

# View environment health
eb health

# View logs
eb logs

# SSH into instance
eb ssh

# Set environment variables
eb setenv SPRING_DATASOURCE_URL=jdbc:mysql://merchant-db.xxx.us-east-1.rds.amazonaws.com:3306/merchantdb
eb setenv SPRING_DATASOURCE_USERNAME=admin
eb setenv SPRING_DATASOURCE_PASSWORD=YourPassword
eb setenv SPRING_PROFILES_ACTIVE=prod

# Open application in browser
eb open

# Terminate environment
eb terminate NikatMerchant-prod
```

## AWS ECR (Container Registry) Commands

```powershell
# Create ECR repository
aws ecr create-repository \
  --repository-name merchant-app \
  --region us-east-1

# Get ECR login token
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com

# Tag image for ECR
docker tag merchant-app:latest <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/merchant-app:latest

# Push image to ECR
docker push <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/merchant-app:latest

# List images in repository
aws ecr describe-images --repository-name merchant-app --region us-east-1

# Delete image from ECR
aws ecr batch-delete-image \
  --repository-name merchant-app \
  --image-ids imageTag=latest \
  --region us-east-1
```

## CloudWatch Logs Commands

```powershell
# List log groups
aws logs describe-log-groups --region us-east-1

# Tail logs in real-time
aws logs tail /aws/elasticbeanstalk/NikatMerchant-prod/var/log/eb-engine.log --follow --region us-east-1

# Get log stream names
aws logs describe-log-streams \
  --log-group-name /aws/elasticbeanstalk/NikatMerchant-prod/var/log/eb-engine.log \
  --region us-east-1

# Get log events
aws logs get-log-events \
  --log-group-name /aws/elasticbeanstalk/NikatMerchant-prod/var/log/eb-engine.log \
  --log-stream-name "latest" \
  --region us-east-1
```

## Billing and Cost Management

```powershell
# Get account cost
aws ce get-cost-and-usage \
  --time-period Start=2024-01-01,End=2024-01-31 \
  --granularity MONTHLY \
  --metrics "UnblendedCost" \
  --group-by Type=DIMENSION,Key=SERVICE

# Create billing alarm
aws cloudwatch put-metric-alarm \
  --alarm-name "Monthly-Billing-Limit" \
  --alarm-description "Alert when monthly charges exceed $10" \
  --metric-name EstimatedCharges \
  --namespace AWS/Billing \
  --statistic Maximum \
  --period 86400 \
  --evaluation-periods 1 \
  --threshold 10 \
  --comparison-operator GreaterThanThreshold \
  --alarm-actions arn:aws:sns:us-east-1:<AWS_ACCOUNT_ID>:billing-alert \
  --region us-east-1

# List all resources (to identify unused resources)
aws ec2 describe-instances --region us-east-1
aws rds describe-db-instances --region us-east-1
aws ec2 describe-volumes --region us-east-1
```

## Testing API Endpoints

```powershell
# Test application health
curl http://<APP_URL>:8080/actuator/health

# Create merchant
curl -X POST http://<APP_URL>:8080/api/merchants `
  -H "Content-Type: application/json" `
  -d '{
    "merchantName": "Test Merchant",
    "merchantAddress": "123 Main St",
    "merchantContactNumber": "+1234567890",
    "merchantEmailId": "test@example.com",
    "merchantCategory": "Retail"
  }'

# Get all merchants
curl http://<APP_URL>:8080/api/merchants

# Get merchant by ID
curl http://<APP_URL>:8080/api/merchants/1

# Update merchant
curl -X PUT http://<APP_URL>:8080/api/merchants/1 `
  -H "Content-Type: application/json" `
  -d '{
    "merchantName": "Updated Merchant",
    "merchantAddress": "456 Elm St",
    "merchantContactNumber": "+1987654321",
    "merchantEmailId": "updated@example.com",
    "merchantCategory": "Retail"
  }'

# Delete merchant
curl -X DELETE http://<APP_URL>:8080/api/merchants/1

# View Swagger UI
# Navigate to: http://<APP_URL>:8080/swagger-ui.html

# View API docs
# Navigate to: http://<APP_URL>:8080/v3/api-docs
```

## Troubleshooting Commands

```powershell
# Check RDS instance status
aws rds describe-db-instances \
  --db-instance-identifier merchant-db \
  --query 'DBInstances[0].DBInstanceStatus' \
  --region us-east-1

# Check EC2 instance status
aws ec2 describe-instances \
  --instance-ids i-xxxxxxxx \
  --query 'Reservations[0].Instances[0].State.Name' \
  --region us-east-1

# Test RDS connectivity from local machine
mysql -h <RDS_ENDPOINT> -u admin -p -e "SELECT 1;"

# SSH into EC2 and check Docker logs
ssh -i merchant-app.pem ubuntu@<PUBLIC_IP>
docker ps
docker logs nikat-merchant-app
docker-compose logs app

# Check Docker container health
docker inspect --format='{{.State.Health.Status}}' nikat-merchant-app

# Restart Docker container
docker restart nikat-merchant-app
docker-compose restart app

# Check available free tier resources
aws ec2 describe-account-attributes \
  --attribute-names supported-platforms \
  --region us-east-1

# Clean up unused resources
aws ec2 describe-volumes \
  --filters Name=status,Values=available \
  --region us-east-1
```

## Common Issues and Solutions

### Application won't connect to RDS
```powershell
# 1. Verify RDS is running
aws rds describe-db-instances --db-instance-identifier merchant-db --region us-east-1

# 2. Test connection from EC2
ssh -i merchant-app.pem ubuntu@<PUBLIC_IP>
mysql -h <RDS_ENDPOINT> -u admin -p

# 3. Check security group rules
aws ec2 describe-security-groups --group-ids sg-xxxxxxxx --region us-east-1

# 4. Verify environment variables
eb printenv
docker exec nikat-merchant-app env | grep SPRING_DATASOURCE
```

### Out of memory
```powershell
# Increase heap size in .ebextensions/01_aws.config:
# XX_MAX_HEAP_SIZE: 1024m

# Or in docker-compose.yml add:
# environment:
#   - JAVA_OPTS=-Xmx512m -Xms256m
```

### Application timeout
```powershell
# Check if application is actually running
curl http://<APP_URL>:8080/actuator/health

# Check logs
eb logs
docker-compose logs -f app

# Increase timeout in security group rules
```


