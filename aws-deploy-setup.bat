@echo off
REM AWS Deployment Helper Script for NikatMerchant (Windows)
REM This script automates initial AWS setup

echo =====================================
echo NikatMerchant - AWS Deployment Setup
echo =====================================
echo.

REM Check prerequisites
echo Checking prerequisites...

aws --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: AWS CLI not found. Please install AWS CLI v2
    pause
    exit /b 1
)

mvn --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven not found. Please install Maven
    pause
    exit /b 1
)

docker --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker not found. Please install Docker
    pause
    exit /b 1
)

echo [OK] All prerequisites met
echo.

REM Get AWS account info
echo Retrieving AWS account information...
for /f "tokens=*" %%i in ('aws sts get-caller-identity --query Account --output text') do set AWS_ACCOUNT_ID=%%i
for /f "tokens=*" %%i in ('aws configure get region') do set AWS_REGION=%%i

echo [OK] AWS Account ID: %AWS_ACCOUNT_ID%
echo [OK] AWS Region: %AWS_REGION%
echo.

REM Build application
echo Building NikatMerchant application...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo ERROR: Maven build failed
    pause
    exit /b 1
)
echo [OK] Application built successfully
echo.

REM Build Docker image
echo Building Docker image...
call docker build -t merchant-app:latest .
if errorlevel 1 (
    echo ERROR: Docker build failed
    pause
    exit /b 1
)
echo [OK] Docker image built: merchant-app:latest
echo.

REM Push to ECR (optional)
set /p PUSH_ECR="Do you want to push image to AWS ECR? (y/n): "
if /i "%PUSH_ECR%"=="y" (
    echo Creating ECR repository...
    aws ecr create-repository --repository-name merchant-app --region %AWS_REGION% 2>nul

    echo Logging into ECR...
    for /f "tokens=*" %%i in ('aws ecr get-login-password --region %AWS_REGION%') do (
        echo %%i | docker login --username AWS --password-stdin %AWS_ACCOUNT_ID%.dkr.ecr.%AWS_REGION%.amazonaws.com
    )

    echo Tagging image...
    call docker tag merchant-app:latest %AWS_ACCOUNT_ID%.dkr.ecr.%AWS_REGION%.amazonaws.com/merchant-app:latest

    echo Pushing image to ECR...
    call docker push %AWS_ACCOUNT_ID%.dkr.ecr.%AWS_REGION%.amazonaws.com/merchant-app:latest
    echo [OK] Image pushed to ECR
) else (
    echo Skipping ECR push
)
echo.

REM Elastic Beanstalk setup
set /p SETUP_EB="Do you want to deploy using Elastic Beanstalk? (y/n): "
if /i "%SETUP_EB%"=="y" (
    echo Installing/Updating EB CLI...
    pip install --upgrade awsebcli

    if not exist ".elasticbeanstalk" (
        echo Initializing Elastic Beanstalk...
        call eb init -p "Java 17 running on 64bit Amazon Linux 2" -r %AWS_REGION% NikatMerchant
    )

    echo Creating EB environment...
    call eb create NikatMerchant-prod --instance-type t3.micro

    echo [OK] Elastic Beanstalk setup complete
) else (
    echo Skipping Elastic Beanstalk setup
)
echo.

REM Instructions
echo =====================================
echo Setup Complete!
echo =====================================
echo.
echo Next steps:
echo 1. Configure RDS MySQL database in AWS Console
echo 2. Update environment variables with RDS endpoint
echo 3. Set environment variables:
echo    - SPRING_DATASOURCE_URL
echo    - SPRING_DATASOURCE_USERNAME
echo    - SPRING_DATASOURCE_PASSWORD
echo.
echo To deploy/update your application:
echo   eb deploy
echo.
echo To view logs:
echo   eb logs
echo.
echo To open application in browser:
echo   eb open
echo.

pause

