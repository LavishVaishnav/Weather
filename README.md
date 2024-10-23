# 🌦️ Weather Monitoring System 🌦️

Access the live application here: [https://lavishvaishnav.github.io/weatherwatch.github.io/](https://lavishvaishnav.github.io/weatherwatch.github.io/).

An advanced real-time weather monitoring system that detects your current location, fetches weather data every minute, and provides comprehensive weather insights. Built with Java Spring Boot, MongoDB, and a user-friendly frontend, this app is a complete solution for tracking weather conditions effectively.

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Screenshots](#screenshots)
- [Contributing](#contributing)

## Features

### **General Features**
- **Auto Location Detection**: Detects user location automatically and fetches relevant weather data.
- **Real-time Weather Updates**: Fetches live weather data every minute using WebSockets.
- **Weather Data for Any City**: Allows users to search for weather data by city name.
- **Custom Alerts**: Users can set personalized alerts for high/low temperatures.
- **Historical Data Visualization**: Displays weather trends using interactive graphs for temperature, wind speed, and humidity.
- **Weather Icons**: Dynamic weather icons that reflect current conditions.

### **Backend Features**
- **Daily Weather Summaries**: 
  - Fetches real-time weather data at regular intervals from the OpenWeatherMap API.
  - Calculates key metrics such as **average**, **maximum**, and **minimum temperatures** based on the collected data throughout the day.
  - Aggregates data to provide accurate daily summaries, ensuring users get a comprehensive overview of the day's weather trends.

- **MongoDB Integration**:
  - Stores every fetched data point in **MongoDB Atlas**, enabling robust historical tracking and analysis.
  - Utilizes MongoDB's efficient querying capabilities to retrieve and analyze past weather data.
  - Ensures accurate calculation of temperature summaries by leveraging both real-time and historical data.

- **Scheduled Fetching**:
  - Implements **scheduled tasks** in Spring Boot to fetch and update weather data every minute, ensuring data remains current and precise.
  - Uses **WebSockets** for live updates, enabling instant data refresh on the frontend without user intervention.
  - Supports continuous real-time data monitoring, making it ideal for tracking sudden weather changes.

- **Real-time Data Aggregation**:
  - Aggregates temperature data points in real-time, calculating average, min, and max temperatures dynamically as new data arrives.
  - Processes weather conditions such as **humidity**, **wind speed**, and other parameters for a well-rounded daily summary.
  - Integrates weather metrics into the MongoDB database to provide a consistent user experience even if real-time fetching is temporarily unavailable.

- **Temperature Conversion**:
  - Allows seamless conversion between **Celsius** and **Fahrenheit**, giving users flexibility in how they view temperature data.
  - The conversion is done on-the-fly in the backend using custom utility functions, ensuring instant display in the preferred unit.

### **Frontend Features**
- **User Interaction**: Clean and responsive UI for searching cities, setting alerts, and viewing data.
- **Data Visualization**: Graphs and charts displaying weather data in real time.
- **Real-time Alerts**: Notifies users of threshold breaches with visual cues.

### **Security & Deployment**
- **AWS & Docker Deployment**: Deployed using Docker on AWS EC2 for scalability and security.

## Tech Stack
- **Backend**: Java Spring Boot, MongoDB, WebSockets
- **Frontend**: HTML, CSS, JavaScript, Chart.js
- **Database**: MongoDB Atlas
- **APIs**: OpenWeatherMap API
- **Deployment**: Docker, AWS EC2
- **Security**: SSL Certificate

## Installation

### **Prerequisites**
- Java 19
- MongoDB
- Gradle
- Docker
- AWS Account (for deployment)

### **Steps to Set Up Locally**
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/weather-monitoring-system.git
   cd weather-monitoring-system
   ```

2. **Set Up Environment Variables**:
   - Create an `.env` file with the following details:
     ```bash
     API_KEY=YOUR_OPENWEATHERMAP_API_KEY
     MONGO_URI=YOUR_MONGODB_URI
     ```

3. **Build the Backend**:
   ```bash
   ./gradlew build
   ```

4. **Run the Backend**:
   ```bash
   ./gradlew bootRun
   ```

5. **Frontend Setup**:
   - Place the frontend files in the `/htdocs/mdocs` directory.
   - Launch a simple HTTP server to view the frontend.

## Usage

### **Auto Location Detection**
1. Open the [Weather Monitoring System](https://lavishvaishnav.github.io/weatherwatch.github.io/).
2. Allow location permissions for automatic weather updates.

### **City Weather Search**
1. Enter the desired city name in the search bar.
2. View real-time data, temperature summaries, and set custom alerts.

### **Custom Alerts**
1. Set high and low temperature thresholds.
2. Receive real-time notifications if the thresholds are crossed.

## API Endpoints

### **Weather API**
- **GET** `/weather?city={city_name}`: Fetches current weather for a specific city.
- **GET** `/weather/coordinates?lat={latitude}&lon={longitude}`: Fetches weather data by coordinates.
- **POST** `/alerts`: Set custom alert thresholds.
- **GET** `/weather/history?city={city_name}`: Fetches historical weather data.

### **Alerts API**
- **POST** `/alerts/create`: Create an alert for specific conditions.
- **GET** `/alerts`: Retrieve active alerts for the user.

## Configuration

### **Application Properties**
Make sure your `application.properties` is properly configured:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/weather_db
spring.datasource.username=root
spring.datasource.password=admin
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

### **Frontend Configuration**
Configure the API endpoint URLs in your frontend JavaScript (`app.js`) to match the backend server details.

## Screenshots

1. **Home Page**: Displaying weather data.
2. **City Search**: User input for specific city weather.
3. **Alert Notification**: Custom alerts when thresholds are breached.
4. **Data Visualization**: Graphs displaying historical trends.

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

### **How to Contribute**
1. Fork the repository.
2. Create a new branch for your feature:
   ```bash
   git checkout -b feature-branch
   ```
3. Commit your changes:
   ```bash
   git commit -m "Added new feature"
   ```
4. Push to the branch:
   ```bash
   git push origin feature-branch
   ```
5. Open a pull request.

---

