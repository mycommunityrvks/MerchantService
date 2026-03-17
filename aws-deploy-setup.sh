#!/bin/bash

# AWS Deployment Helper Script for NikatMerchant
# This script automates initial AWS setup

set -e

echo "====================================="
echo "NikatMerchant - AWS Deployment Setup"
echo "====================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
echo -e "${YELLOW}Checking prerequisites...${NC}"

command -v aws &> /dev/null || { echo -e "${RED}AWS CLI not found. Please install AWS CLI v2${NC}"; exit 1; }
command -v mvn &> /dev/null || { echo -e "${RED}Maven not found. Please install Maven${NC}"; exit 1; }
command -v docker &> /dev/null || { echo -e "${RED}Docker not found. Please install Docker${NC}"; exit 1; }

echo -e "${GREEN}✓ All prerequisites met${NC}"
echo ""

# Get AWS account info
echo -e "${YELLOW}Retrieving AWS account information...${NC}"
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
AWS_REGION=$(aws configure get region)

echo -e "${GREEN}✓ AWS Account ID: $AWS_ACCOUNT_ID${NC}"
echo -e "${GREEN}✓ AWS Region: $AWS_REGION${NC}"
echo ""

# Build application
echo -e "${YELLOW}Building NikatMerchant application...${NC}"
mvn clean package -DskipTests
echo -e "${GREEN}✓ Application built successfully${NC}"
echo ""

# Build Docker image
echo -e "${YELLOW}Building Docker image...${NC}"
docker build -t merchant-app:latest .
echo -e "${GREEN}✓ Docker image built: merchant-app:latest${NC}"
echo ""

# Create ECR repository (optional)
read -p "Do you want to push image to AWS ECR? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Creating ECR repository...${NC}"
    aws ecr create-repository --repository-name merchant-app --region $AWS_REGION 2>/dev/null || echo "Repository already exists"

    echo -e "${YELLOW}Logging into ECR...${NC}"
    aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

    echo -e "${YELLOW}Tagging image...${NC}"
    docker tag merchant-app:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/merchant-app:latest

    echo -e "${YELLOW}Pushing image to ECR...${NC}"
    docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/merchant-app:latest
    echo -e "${GREEN}✓ Image pushed to ECR${NC}"
else
    echo -e "${YELLOW}Skipping ECR push${NC}"
fi
echo ""

# Elastic Beanstalk setup
read -p "Do you want to deploy using Elastic Beanstalk? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Installing/Updating EB CLI...${NC}"
    pip install --upgrade awsebcli

    if [ ! -d ".elasticbeanstalk" ]; then
        echo -e "${YELLOW}Initializing Elastic Beanstalk...${NC}"
        eb init -p "Java 17 running on 64bit Amazon Linux 2" -r $AWS_REGION NikatMerchant
    fi

    echo -e "${YELLOW}Creating EB environment...${NC}"
    eb create NikatMerchant-prod --instance-type t3.micro || echo "Environment already exists"

    echo -e "${GREEN}✓ Elastic Beanstalk setup complete${NC}"
else
    echo -e "${YELLOW}Skipping Elastic Beanstalk setup${NC}"
fi
echo ""

# Instructions
echo -e "${GREEN}====================================="
echo "Setup Complete!"
echo "=====================================${NC}"
echo ""
echo "Next steps:"
echo "1. Configure RDS MySQL database in AWS Console"
echo "2. Update environment variables with RDS endpoint"
echo "3. Set environment variables:"
echo "   - SPRING_DATASOURCE_URL"
echo "   - SPRING_DATASOURCE_USERNAME"
echo "   - SPRING_DATASOURCE_PASSWORD"
echo ""
echo "To deploy/update your application:"
echo "  eb deploy"
echo ""
echo "To view logs:"
echo "  eb logs"
echo ""
echo "To open application in browser:"
echo "  eb open"
echo ""

