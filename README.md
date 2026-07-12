# Food Delivery App

Full-stack food delivery application for the App Development Track Week 3 submission.

## Tech Stack

Mobile application:

- Kotlin
- Jetpack Compose
- Navigation Compose
- Retrofit
- Room
- DataStore

Backend:

- Spring Boot
- Spring Web
- Spring Data MongoDB
- Spring Security
- JWT
- Lombok

Database:

- MongoDB

## Architecture Overview

The Android app uses Jetpack Compose screens backed by Retrofit for API calls, DataStore for the JWT/session/profile metadata, and Room for offline cache storage. Restaurant, menu, and order screens load cached Room data first, render it immediately, then fetch fresh backend data and update the cache.

The backend exposes REST APIs for authentication, restaurants, menus, orders, and profile updates. It connects to MongoDB through `SPRING_DATA_MONGODB_URI`, which is supplied automatically by Docker Compose.

## Docker Backend Setup

From the repository root:

```bash
docker-compose up --build
```

Services:

- Backend: `http://localhost:8080`
- MongoDB: `localhost:27017`

Environment variables:

- `SPRING_DATA_MONGODB_URI`: MongoDB connection string. Docker Compose sets this to `mongodb://mongodb:27017/fooddelivery`.
- `SERVER_PORT`: backend port. Defaults to `8080`.

The backend seeds sample restaurants and menu items when MongoDB is empty.

## Local Backend Setup Without Docker

```bash
cd fooddeliverybackend
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/fooddelivery ./mvnw spring-boot:run
```

## Android Setup

1. Open `OrderPrototype` in Android Studio.
2. Start the backend with Docker Compose or the local backend command.
3. Run the app on an emulator or Android device.

Default Retrofit URL:

```kotlin
http://10.0.2.2:8080/
```

For a physical device, update `OrderPrototype/app/src/main/java/com/xyz/orderprototype/data/network/RetrofitClient.kt` to use the computer's LAN IP.

## Cache Strategy

Cached in Room:

- Restaurants
- Menu items
- Orders

Persisted in DataStore:

- JWT token
- User name
- User email
- Local profile image URI

Launch and refresh flow:

1. Observe cached Room data.
2. Render cached restaurants, menus, or orders immediately.
3. Fetch fresh data from the backend.
4. Save fresh data into Room.
5. UI updates automatically from the Room flow.

Offline behavior:

- Previously loaded restaurants remain visible.
- Previously viewed menus remain visible.
- Previously loaded orders remain visible.
- Profile name/email and selected profile image are retained locally.
- User remains logged in after app restart through the saved JWT token.

## Week 3 Features

Required:

- Local caching with Room for restaurants, menu items, and orders.
- Dockerized backend with MongoDB through `docker-compose.yml`.
- Profile screen displays name/email, supports name editing, and profile picture selection.
- Loading, empty, and error states on home, menu, and orders screens.
- Retry/refresh controls on home, menu, and orders screens.
- Restaurant search by name/category.
- Category filters and sorting by rating or delivery time.

Bonus features:

- Promo code section in cart.
- Address management section in cart.

## API Endpoints

Authentication:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `PUT /api/auth/me`

Restaurants:

- `GET /api/restaurants`
- `GET /api/restaurants/{id}`
- `GET /api/restaurants/{restaurantId}/menu`

Orders:

- `POST /api/orders`
- `GET /api/orders`

## Demo Credentials

Register a new user in the app, or use seeded/test credentials if available in your local database.

## Files

- `OrderPrototype`: Kotlin/Compose mobile app
- `fooddeliverybackend`: Spring Boot backend
- `docker-compose.yml`: backend + MongoDB Docker setup
- `README.md`: setup and architecture documentation
- `demo.txt`: public demo video link
