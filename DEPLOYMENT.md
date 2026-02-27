# Campus Booking System - Render Deployment Guide

## Prerequisites
- Render.com account (free tier available)
- PostgreSQL database (can use Render's managed PostgreSQL)
- GitHub repository with your code pushed

## Step 1: Set Up PostgreSQL Database on Render

1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click **+ New** → **PostgreSQL**
3. Fill in:
   - **Name:** `booking-db`
   - **Database:** `booking_db`
   - **User:** `postgres` (or choose a name)
   - **Region:** Choose closest to your location
   - **Version:** 15 (or latest)
4. Click **Create Database**
5. Wait for database to be created
6. Copy the **Internal Database URL** (you'll need this for your app)

## Step 2: Deploy Your Application on Render

### Option A: Using Git (Recommended)
1. Push your code to GitHub (if not already done)
2. In Render Dashboard, click **+ New** → **Web Service**
3. Select **Deploy existing service from a Git repository**
   - Or manually select **GitHub** and authorize
4. Choose your repository
5. Fill in:
   - **Name:** `campus-booking-system`
   - **Branch:** `main` (or your branch)
   - **Build Command:** `mvn clean package -DskipTests`
   - **Start Command:** `java -jar target/booking-0.0.1-SNAPSHOT.jar`
   - **Instance Type:** Free (or paid, based on your needs)
   - **Region:** Same as your database

### Option B: Manual Deployment
1. In Render Dashboard, click **+ New** → **Web Service**
2. Choose **Docker** or upload your JAR directly
3. Follow same configuration steps as Option A

## Step 3: Configure Environment Variables

In your Web Service settings, add these environment variables:

1. **DATABASE_URL**
   ```
   postgresql://[user]:[password]@[host]:[port]/booking_db
   ```
   (Get this from your PostgreSQL instance details)

2. **DB_USERNAME**
   ```
   postgres  (or your chosen username)
   ```

3. **DB_PASSWORD**
   ```
   (Your database password from setup)
   ```

4. **PORT**
   ```
   8081
   ```

## Step 4: Deploy

1. Click **Deploy** on the Render dashboard
2. Watch the deployment logs in real-time
3. Once green checkmark appears, your app is live!

## Step 5: Test Your Deployment

- **API Base URL:** `https://your-app-name.onrender.com`
- **Test endpoint:** `https://your-app-name.onrender.com/api/facilities`

## Frontend Access

Your frontend files from `frontend/` directory are served automatically if you have them in the static folder. Access at:
```
https://your-app-name.onrender.com/
```

## Troubleshooting

### Build fails
- Check Java version is 17+
- Verify all dependencies in pom.xml

### Database connection fails
- Verify DATABASE_URL environment variable
- Check database credentials are correct
- Ensure database instance is running

### Port issues
- Render assigns a random PORT - our config uses `${PORT:8081}` to handle this

### View logs
- Render Dashboard → Your Service → Logs tab

## Important Notes

- **Free tier services spin down after 15 minutes of inactivity**
- **Persistent databases available on paid plans**
- **For production, upgrade to paid instance**

## Manual JAR Deployment (If Git not available)

1. Download the JAR file from your local `target/booking-0.0.1-SNAPSHOT.jar`
2. On Render, use Web Service with Docker
3. Create a Dockerfile:
   ```dockerfile
   FROM eclipse-temurin:17-jdk
   COPY target/booking-0.0.1-SNAPSHOT.jar app.jar
   ENTRYPOINT ["java", "-jar", "/app.jar"]
   ```
4. Upload to Render with Docker option

## API Documentation

See `API_DOCUMENTATION.md` for endpoint details and `Campus_Facility_Booking_API.postman_collection.json` for Postman testing.
