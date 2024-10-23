package org.example.controller;


import org.example.model.WeatherData;
import org.example.service.WeatherApiService;
import org.example.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WeatherApiService weatherApiService;

    // Fetch live weather data for a city and return calculated summaries (max, min, avg)
    @GetMapping("/live/{city}")
    public ResponseEntity<WeatherData> getLiveWeatherData(@PathVariable String city) {
        WeatherData weatherData = weatherApiService.fetchWeatherData(city);
        if (weatherData != null) {
            return ResponseEntity.ok(weatherData);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Search weather for a city
    @GetMapping("/search")
    public ResponseEntity<WeatherData> searchCityWeather(@RequestParam String city) {
        WeatherData weatherData = weatherApiService.fetchWeatherData(city);
        if (weatherData != null) {
            return ResponseEntity.ok(weatherData);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Fetch all stored weather data
    @GetMapping("/all")
    public ResponseEntity<List<WeatherData>> getAllWeatherData() {
        List<WeatherData> weatherDataList = weatherService.getAllWeatherData();
        return !weatherDataList.isEmpty() ?
                ResponseEntity.ok(weatherDataList) :
                ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    // Fetch stored weather data for a specific city
    @GetMapping("/city/{city}")
    public ResponseEntity<List<WeatherData>> getWeatherByCity(@PathVariable String city) {
        List<WeatherData> weatherDataList = weatherService.getWeatherByCity(city);
        return !weatherDataList.isEmpty() ?
                ResponseEntity.ok(weatherDataList) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // Fetch live weather data and check alerts based on custom thresholds
    @PostMapping("/alert/{city}")
    public ResponseEntity<Map<String, Object>> getWeatherWithAlert(
            @PathVariable String city,
            @RequestBody Map<String, Object> alertThresholds) {

        // Log the request body for debugging
        System.out.println("Received thresholds: " + alertThresholds);

        // Fetch live weather data for the given city
        WeatherData weatherData = weatherApiService.fetchWeatherData(city);

        if (weatherData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Safely handle both Integer and Double types for minTemp and maxTemp
        Number minTempNumber = (Number) alertThresholds.get("minTemp");
        Number maxTempNumber = (Number) alertThresholds.get("maxTemp");

        Double minTemp = minTempNumber != null ? minTempNumber.doubleValue() : null;
        Double maxTemp = maxTempNumber != null ? maxTempNumber.doubleValue() : null;

        String userCondition = (String) alertThresholds.get("userCondition");

        // Create a map for the temperature thresholds
        Map<String, Double> thresholds = new HashMap<>();
        thresholds.put("minTemp", minTemp);
        thresholds.put("maxTemp", maxTemp);

        // Check for alerts based on thresholds and userCondition
        String alertMessage = weatherApiService.checkForAlerts(city, weatherData, thresholds, userCondition);

        // Prepare a response containing the weather data and alert (if applicable)
        Map<String, Object> response = weatherApiService.prepareWeatherSummary(weatherData, alertMessage);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/historical/{city}/{date}")
    public ResponseEntity<List<WeatherData>> getHistoricalWeatherData(
            @PathVariable String city,
            @PathVariable String date) {
        // Parse the date, assuming format is YYYY-MM-DD
        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDate.parse(date).atStartOfDay(); // Set time to start of day
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Invalid date format
        }

        // Set the end date as the start of the next day
        LocalDateTime endDate = parsedDate.plusDays(1);

        List<WeatherData> historicalData = weatherService.getHistoricalWeatherData(city, parsedDate, endDate);
        if (!historicalData.isEmpty()) {
            return ResponseEntity.ok(historicalData);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/coordinates")
    public ResponseEntity<WeatherData> getWeatherByCoordinates(@RequestParam("lat") double latitude, @RequestParam("lon") double longitude) {
        WeatherData weatherData = weatherApiService.fetchWeatherDataByCoordinates(latitude, longitude);
        if (weatherData != null) {
            return ResponseEntity.ok(weatherData);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}
