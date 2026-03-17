# Comprehensive checklist for AWS deployment

## Pre-Deployment Checklist

### AWS Account Setup
- [ ] AWS Free Tier account created
- [ ] Payment method added
- [ ] IAM user created (`merchant-deployment`)
- [ ] Access keys generated and saved
- [ ] AWS CLI installed and configured
- [ ] Default region set to `us-east-1` (free tier eligible)

### Application Preparation
- [ ] Source code is complete and tested locally
- [ ] Unit tests pass: `mvn test`
- [ ] Application builds successfully: `mvn clean package`
- [ ] Dockerfile is created and tested locally
- [ ] `application-prod.yml` created with external configuration
- [ ] All hardcoded secrets removed
- [ ] Swagger/API documentation updated
- [ ] README.md updated with deployment instructions

### Database Setup
- [ ] RDS MySQL instance created (`merchant-db`)
- [ ] MySQL 8.0.37 version selected
- [ ] db.t3.micro instance type selected (free tier)
- [ ] 20 GB storage allocated (free tier limit)
- [ ] Security group created and configured
- [ ] Inbound rule for MySQL (port 3306) added
- [ ] RDS endpoint noted (e.g., merchant-db.xxx.us-east-1.rds.amazonaws.com)
- [ ] Master user credentials saved securely
- [ ] Database `merchantdb` created
- [ ] Connection tested from local machine

### Server Setup
- [ ] Choose deployment option:
  - [ ] Option A: Elastic Beanstalk (recommended for beginners)
    - [ ] `.ebextensions/01_aws.config` created
    - [ ] EB CLI installed
    - [ ] `eb init` run successfully
  - [ ] Option B: EC2 + Docker (more control)
    - [ ] EC2 instance (t2.micro) launched
    - [ ] Ubuntu Server 24.04 LTS selected
    - [ ] Security group configured (SSH, HTTP, HTTPS, 8080)
    - [ ] Key pair downloaded and saved
    - [ ] SSH connection tested
    - [ ] Docker and Docker Compose installed on EC2

### Environment Configuration
- [ ] Environment variables configured:
  - [ ] `SPRING_DATASOURCE_URL` = RDS endpoint
  - [ ] `SPRING_DATASOURCE_USERNAME` = admin
  - [ ] `SPRING_DATASOURCE_PASSWORD` = RDS password
  - [ ] `SPRING_PROFILES_ACTIVE` = prod
  - [ ] `JPA_DDL_AUTO` = update

### Security Considerations
- [ ] All hardcoded passwords removed
- [ ] Environment variables used for secrets
- [ ] Security groups restrict access appropriately
- [ ] RDS not publicly accessible (if not needed)
- [ ] EC2 SSH access restricted to your IP (if possible)
- [ ] SSL/TLS certificate obtained (if using domain)
- [ ] Spring Security configured for production

### Monitoring and Logging
- [ ] CloudWatch logging enabled
- [ ] Application logs configured
- [ ] Health check endpoint available (/actuator/health)
- [ ] Monitoring alarms created:
  - [ ] CPU utilization > 80%
  - [ ] Memory utilization > 80%
  - [ ] Database connection pool status
  - [ ] HTTP 5xx error rate

### Backup and Recovery
- [ ] RDS automated backups enabled (7-day retention)
- [ ] RDS snapshot created before first deployment
- [ ] Backup retention policy documented
- [ ] Disaster recovery plan documented

### Cost Management
- [ ] AWS Free Tier limits understood:
  - [ ] EC2: 750 hours/month t2.micro
  - [ ] RDS: 750 hours/month db.t3.micro
  - [ ] Data Transfer: 1 GB/month free
- [ ] Billing alerts configured
- [ ] Cost monitoring plan established
- [ ] Unused resources will be terminated

### Documentation
- [ ] Deployment guide created (AWS_DEPLOYMENT_GUIDE.md)
- [ ] Architecture diagram (optional)
- [ ] API documentation updated
- [ ] Troubleshooting guide prepared
- [ ] Team notified of deployment
- [ ] Contact info for AWS support documented

## Deployment Checklist

### Pre-Deployment
- [ ] All items from Pre-Deployment Checklist completed
- [ ] Database backups taken
- [ ] Team notified
- [ ] Maintenance window scheduled (if needed)

### Deployment Steps
- [ ] Build application: `mvn clean package -DskipTests`
- [ ] Build Docker image: `docker build -t merchant-app:latest .`

**If using Elastic Beanstalk:**
- [ ] Deploy: `eb deploy`
- [ ] Monitor: `eb events -f`
- [ ] Check status: `eb status`

**If using EC2:**
- [ ] SSH into EC2 instance
- [ ] Clone/upload application code
- [ ] Update Docker Compose file with RDS endpoint
- [ ] Run: `docker-compose up -d`
- [ ] Check logs: `docker-compose logs -f app`

### Post-Deployment Verification
- [ ] Application is running
- [ ] Swagger UI accessible: `{APP_URL}/swagger-ui.html`
- [ ] API docs accessible: `{APP_URL}/v3/api-docs`
- [ ] Database connection working
- [ ] Health check passing: `{APP_URL}/actuator/health`
- [ ] Create test merchant via POST /api/merchants
- [ ] Retrieve merchants via GET /api/merchants
- [ ] Update merchant via PUT /api/merchants/{id}
- [ ] Delete merchant via DELETE /api/merchants/{id}
- [ ] CloudWatch logs appearing
- [ ] No errors in application logs

### Post-Deployment Tasks
- [ ] Team notified of successful deployment
- [ ] Production URL documented and shared
- [ ] Monitoring dashboards set up
- [ ] On-call schedule established
- [ ] Runbooks created
- [ ] Performance benchmarks established

## First-Month Monitoring

### Daily
- [ ] Check application health
- [ ] Review CloudWatch logs for errors
- [ ] Monitor database performance

### Weekly
- [ ] Review costs in AWS Billing Console
- [ ] Check RDS backup completion
- [ ] Review security group rules
- [ ] Test API endpoints

### Monthly
- [ ] Review overall costs
- [ ] Analyze performance metrics
- [ ] Update documentation
- [ ] Plan capacity adjustments
- [ ] Review security updates

## Emergency Procedures

### If Application Crashes
1. [ ] SSH into EC2 or check EB logs
2. [ ] Review application logs in CloudWatch
3. [ ] Check database connectivity
4. [ ] Restart container: `docker-compose restart app` or `eb restart`
5. [ ] Check RDS instance status
6. [ ] Restore from backup if necessary

### If Database Issues
1. [ ] Check RDS instance status in AWS Console
2. [ ] Review RDS logs
3. [ ] Verify security group rules
4. [ ] Check database disk space
5. [ ] Restore from most recent snapshot if needed

### If Running Out of Free Tier
1. [ ] Check billing in AWS Billing Console
2. [ ] Identify expensive resources
3. [ ] Reduce instance sizes or counts
4. [ ] Stop non-production resources
5. [ ] Contact AWS Support for options


