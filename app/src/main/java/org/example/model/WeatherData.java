package org.example.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "weatherData")
public class WeatherData {

    private String id;  // The unique identifier for each document.
    private String city;
    private double tempCelsius;
    private double feelsLikeCelsius;
    private String weatherCondition;
    private LocalDateTime timestamp;

    private double maxTemperature;
    private double minTemperature;
    private double averageTemperature;

    // New fields for wind and humidity
    private double windSpeed;
    private double humidity;

    private double pressure;
    private int visibility;
    private int cloudiness;

    private double aqi;



    public WeatherData() {}

    public WeatherData(String city, double tempCelsius, double feelsLikeCelsius, String weatherCondition, LocalDateTime timestamp,double windSpeed, double humidity,double pressure,
                       int visibility, int cloudiness, double aqi) {
        this.city = city;
        this.tempCelsius = tempCelsius;
        this.feelsLikeCelsius = feelsLikeCelsius;
        this.weatherCondition = weatherCondition;
        this.timestamp = timestamp;
        this.windSpeed = windSpeed;
        this.humidity= humidity;
        this.pressure = pressure;
        this.visibility= visibility;
        this. cloudiness= cloudiness;
        this.aqi = aqi;
    }
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTempCelsius() {
        return tempCelsius;
    }

    public void setTempCelsius(double tempCelsius) {
        this.tempCelsius = tempCelsius;
    }

    public double getFeelsLikeCelsius() {
        return feelsLikeCelsius;
    }

    public void setFeelsLikeCelsius(double feelsLikeCelsius) {
        this.feelsLikeCelsius = feelsLikeCelsius;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    // Getters and Setters for the new fields
    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    private String dominantWeatherCondition;

    // Add getter and setter for dominant weather condition
    public String getDominantWeatherCondition() {
        return dominantWeatherCondition;
    }

    public void setDominantWeatherCondition(String dominantWeatherCondition) {
        this.dominantWeatherCondition = dominantWeatherCondition;
    }

    // Getters and Setters for the new fields
    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getCloudiness() {
        return cloudiness;
    }
    public void setCloudiness(int cloudiness) {
        this.cloudiness = cloudiness;
    }
    public double getAqi() {
        return aqi;
    }

    public void setAqi(double aqi) {
        this.aqi = aqi;
    }



}





