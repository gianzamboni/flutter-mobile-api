# Railway Deployment Guide

This guide will help you deploy your Spring Boot Pokemon API to Railway.

## Prerequisites

1. Railway account (sign up at [railway.app](https://railway.app))
2. GitHub repository with your code
3. Railway CLI (optional but recommended)

## Step 1: Prepare Your Repository

Your `docker-compose.yml` is now configured for Railway deployment. The key changes:

- ✅ Removed hardcoded database credentials
- ✅ Removed database port exposure
- ✅ Added health checks
- ✅ Uses Railway's `DATABASE_URL` environment variable

## Step 2: Deploy to Railway

### Option A: Using Railway Dashboard

1. **Connect Repository**
   - Go to [Railway Dashboard](https://railway.app/dashboard)
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Choose your repository

2. **Add PostgreSQL Database**
   - In your project dashboard, click "New"
   - Select "Database" → "PostgreSQL"
   - Railway will automatically create a PostgreSQL database

3. **Configure Environment Variables**
   Railway will automatically set:
   - `PGHOST` - Database host
   - `PGPORT` - Database port (usually 5432)
   - `PGDATABASE` - Database name
   - `PGUSER` - Database username
   - `PGPASSWORD` - Database password
   - `RAILWAY_ENVIRONMENT` - Set to "production"

   Optional variables you can set:
   - `SPRING_PROFILES_ACTIVE=production`

4. **Deploy**
   - Railway will automatically build and deploy your application
   - Your app will be available at the provided Railway URL

### Option B: Using Railway CLI

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login

# Initialize project
railway init

# Add PostgreSQL database
railway add postgresql

# Deploy
railway up
```

## Step 3: Verify Deployment

1. **Check Health Endpoint**
   - Visit `https://your-app.railway.app/actuator/health`
   - Should return `{"status":"UP"}`

2. **Test API Endpoints**
   - Visit `https://your-app.railway.app/api/pokemon` (or your configured endpoint)
   - Should return your Pokemon data

## Environment Variables Reference

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `PGHOST` | PostgreSQL host | `localhost` | Yes (auto-set by Railway) |
| `PGPORT` | PostgreSQL port | `5432` | No (auto-set by Railway) |
| `PGDATABASE` | Database name | `flutter-capacitacion` | No (auto-set by Railway) |
| `PGUSER` | Database username | `postgres` | No (auto-set by Railway) |
| `PGPASSWORD` | Database password | `postgres` | No (auto-set by Railway) |
| `DB_URL` | Fallback database URL for local development | `jdbc:postgresql://localhost:5432/flutter-capacitacion` | No |
| `DB_USERNAME` | Fallback username for local development | `postgres` | No |
| `DB_PASSWORD` | Fallback password for local development | `postgres` | No |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `production` | No |

## Security Features

✅ **No hardcoded credentials** - All sensitive data uses environment variables
✅ **No exposed database ports** - Database is only accessible within Railway's network
✅ **Health checks** - Automatic monitoring of application health
✅ **Production-ready** - Uses Railway's managed PostgreSQL database

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check that PostgreSQL service is running
   - Verify `DATABASE_URL` is set correctly
   - Check Railway logs for connection errors

2. **Application Won't Start**
   - Check Railway build logs
   - Verify all dependencies are in `pom.xml`
   - Ensure Dockerfile is correct

3. **Health Check Failing**
   - Add Spring Boot Actuator dependency if missing
   - Check that the health endpoint is accessible

### Viewing Logs

```bash
# Using Railway CLI
railway logs

# Or view in Railway Dashboard
# Go to your service → Logs tab
```

## Next Steps

1. **Set up custom domain** (optional)
2. **Configure monitoring** and alerts
3. **Set up CI/CD** for automatic deployments
4. **Configure backup strategy** for your database

## Support

- [Railway Documentation](https://docs.railway.app)
- [Railway Discord](https://discord.gg/railway)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
