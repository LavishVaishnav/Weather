package org.example.service;


import org.example.controller.WeatherAlertController;
import org.example.model.AlertPreferences;
import org.example.model.WeatherData;
import org.example.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherApiService {

    private Set<String> cities = new HashSet<>();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Autowired
    private WeatherAlertController weatherAlertController;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${geocoding.api.key}")
    private String geocodingApiKey;

    @Value("${geocoding.api.url}")
    private String geocodingApiUrl;

    @Value("${weather.api.aqi.url}")
    private String aqiApiUrl;  // Add this new value for AQI API


    private Map<String, Integer> consecutiveBreaches = new HashMap<>();
    // Predefined list of metro cities
    private final String[] metroCities = {"Mumbai", "Delhi", "Bengaluru", "Chennai", "Kolkata", "Hyderabad", "Ahmedabad", "Jaipur"};

    public WeatherApiService() {
        // Add predefined metro cities to the set
        for (String city : metroCities) {
            addCity(city);
        }
    }

    public void addCity(String city) {
        cities.add(city);
    }

    @Scheduled(fixedRate = 60000)  // Fetch every 2 minutes
    public void fetchWeatherDataForTrackedCities() {
        for (String city : cities) {
            fetchWeatherData(city);  // Fetch and store weather data for each city
        }
    }

    // Method to fetch AQI data based on city coordinates
    public double fetchAQI(double latitude, double longitude) {
        String url = aqiApiUrl + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey;

        String response;
        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("Error fetching AQI data: " + e.getMessage());
            return -1;  // Return -1 in case of an error
        }

        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("list")) {
            JSONObject aqiData = jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main");
            return aqiData.getDouble("aqi");  // Extract AQI value
        } else {
            return -1;  // Return -1 if no data found
        }
    }


    // Fetch weather data for a city
    public WeatherData fetchWeatherData(String city) {
        String url = apiUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric";
        return fetchWeatherDataFromApi(url, city);
    }

    // Fetch city name using coordinates from OpenCage Geocoding API
    private String getCityNameFromCoordinates(double latitude, double longitude) {
        String url = this.geocodingApiUrl + "?q=" + latitude + "+" + longitude + "&key=" + this.geocodingApiKey;
        String response;
        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("Error fetching city name: " + e.getMessage());
            return "Unknown Location";
        }

        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("results") && jsonObject.getJSONArray("results").length() > 0) {
            JSONObject components = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("components");
            if (components.has("city")) {
                return components.getString("city");
            } else if (components.has("town")) {
                return components.getString("town");
            } else if (components.has("village")) {
                return components.getString("village");
            } else if (components.has("state")) {
                return components.getString("state"); // Fallback if city is not found
            }
        }

        return "Unknown Location";
    }
    // Fetch weather data using coordinates
    public WeatherData fetchWeatherDataByCoordinates(double latitude, double longitude) {
        String cityName = getCityNameFromCoordinates(latitude, longitude); // Fetch city name
        String url = apiUrl + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&units=metric";
        return fetchWeatherDataFromApi(url, cityName);
    }
    // Generic method to fetch weather data from the API and store it
    private WeatherData fetchWeatherDataFromApi(String url, String locationIdentifier) {
        String response;
        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("Error fetching weather data for: " + locationIdentifier + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("main")) {
            String weatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            double tempCelsius = jsonObject.getJSONObject("main").getDouble("temp");
            double feelsLikeCelsius = jsonObject.getJSONObject("main").getDouble("feels_like");
            double humidity = jsonObject.getJSONObject("main").getDouble("humidity");
            double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
            // Extract new parameters
            double pressure = jsonObject.getJSONObject("main").getDouble("pressure");
            int visibility = jsonObject.getInt("visibility");
            int cloudiness = jsonObject.getJSONObject("clouds").getInt("all");  // Cloudiness might be in a nested object

            // Fetch AQI
            double latitude = jsonObject.getJSONObject("coord").getDouble("lat");
            double longitude = jsonObject.getJSONObject("coord").getDouble("lon");
            double aqi = fetchAQI(latitude, longitude);

            LocalDateTime now = LocalDateTime.now();
            List<WeatherData> existingData = weatherDataRepository.findByCityAndTimestampBetween(
                    locationIdentifier, now.minusMinutes(5), now);  // Check for recent data

            if (!existingData.isEmpty()) {
                return existingData.get(0);  // Return recent data if it exists
            }

            WeatherData weatherData = new WeatherData();
            weatherData.setCity(locationIdentifier);
            weatherData.setTempCelsius(tempCelsius);
            weatherData.setFeelsLikeCelsius(feelsLikeCelsius);
            weatherData.setWeatherCondition(weatherCondition);
            weatherData.setHumidity(humidity);
            weatherData.setWindSpeed(windSpeed);
            weatherData.setPressure(pressure);
            weatherData.setVisibility(visibility);
            weatherData.setCloudiness(cloudiness);
            weatherData.setAqi(aqi);
            weatherData.setTimestamp(now);

            // Save and calculate temperature summaries
            weatherDataRepository.save(weatherData);
            List<WeatherData> allWeatherDataToday = fetchAllDataForToday(locationIdentifier);
            calculateTemperatureSummaries(weatherData, allWeatherDataToday);

            weatherDataRepository.save(weatherData);  // Save with calculated summaries

            return weatherData;
        } else {
            System.err.println("No weather data found for: " + locationIdentifier);
            return null;
        }
    }


    // Calculate max, min, and average temperatures
    private void calculateTemperatureSummaries(WeatherData weatherData, List<WeatherData> allWeatherDataToday) {
        double maxTemp = allWeatherDataToday.stream()
                .mapToDouble(WeatherData::getTempCelsius).max().orElse(weatherData.getTempCelsius());
        double minTemp = allWeatherDataToday.stream()
                .mapToDouble(WeatherData::getTempCelsius).min().orElse(weatherData.getTempCelsius());
        double avgTemp = allWeatherDataToday.stream()
                .mapToDouble(WeatherData::getTempCelsius).average().orElse(weatherData.getTempCelsius());

        // Add logic to find the most frequent weather condition
        Map<String, Long> conditionFrequency = allWeatherDataToday.stream()
                .collect(Collectors.groupingBy(WeatherData::getWeatherCondition, Collectors.counting()));

        String dominantCondition = conditionFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())  // Find the condition with the highest count
                .map(Map.Entry::getKey).orElse(weatherData.getWeatherCondition());  // Default to current condition if no data

        weatherData.setMaxTemperature(maxTemp);
        weatherData.setMinTemperature(minTemp);
        weatherData.setAverageTemperature(avgTemp);
        weatherData.setDominantWeatherCondition(dominantCondition);
    }

    // Fetch all data points for today for the given city
    private List<WeatherData> fetchAllDataForToday(String city) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        return weatherDataRepository.findByCityAndTimestampBetween(city, startOfDay, now);
    }

    // Prepare weather summary with alerts (if any)
    public Map<String, Object> prepareWeatherSummary(WeatherData weatherData, String alertMessage) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("weatherData", weatherData);
        summary.put("maxTemp", weatherData.getMaxTemperature());
        summary.put("minTemp", weatherData.getMinTemperature());
        summary.put("avgTemp", weatherData.getAverageTemperature());

        if (alertMessage != null) {
            summary.put("alert", alertMessage);
        }

        return summary;
    }

    // Check for alerts based on custom thresholds
    public String checkForAlerts(String city, WeatherData weatherData, Map<String, Double> thresholds, String userCondition) {
        double tempCelsius = weatherData.getTempCelsius();
        Double minTemp = thresholds.get("minTemp");
        Double maxTemp = thresholds.get("maxTemp");

        // Initialize consecutive breaches for the city if not already present
        consecutiveBreaches.putIfAbsent(city, 0);

        StringBuilder alertMessage = new StringBuilder();

        boolean breached = false;

        if (minTemp != null && tempCelsius < minTemp) {
            breached = true;
        }
        if (maxTemp != null && tempCelsius > maxTemp) {
            breached = true;
        }
        // Weather condition-based alerts
        if (userCondition != null && !userCondition.isEmpty()) {
            if (weatherData.getWeatherCondition().equalsIgnoreCase(userCondition)) {
                breached = true;
            }
        }

        if (breached) {
            // Increment the consecutive breach count
            consecutiveBreaches.put(city, consecutiveBreaches.get(city) + 1);
        } else {
            // Reset breach count if not breached
            consecutiveBreaches.put(city, 0);
        }

        // Only trigger an alert if the threshold has been breached twice consecutively
        if (consecutiveBreaches.get(city) >= 2) {
            if (minTemp != null && tempCelsius < minTemp) {
                alertMessage.append("Temperature is below the minimum threshold for two consecutive updates! ");
            }
            if (maxTemp != null && tempCelsius > maxTemp) {
                alertMessage.append("Temperature exceeds the maximum threshold for two consecutive updates! ");
            }
            if (userCondition != null && weatherData.getWeatherCondition().equalsIgnoreCase(userCondition)) {
                alertMessage.append("Weather condition matches your threshold: " + userCondition + " for two consecutive updates! ");
            }
        }

        return alertMessage.length() > 0 ? alertMessage.toString() : null;
    }

}