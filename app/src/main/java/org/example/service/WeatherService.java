package org.example.service;


import org.example.model.WeatherData;
import org.example.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherService {

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    public WeatherData saveWeatherData(WeatherData weatherData) {
        return weatherDataRepository.save(weatherData);
    }

    public List<WeatherData> getAllWeatherData() {
        return weatherDataRepository.findAll();
    }

    public  List<WeatherData> getWeatherByCity(String city) {
        return weatherDataRepository.findByCity(city);
    }

    // New method to fetch historical weather data
    public List<WeatherData> getHistoricalWeatherData(String city, LocalDateTime start, LocalDateTime end) {
        return weatherDataRepository.findByCityAndTimestampBetween(city, start, end);
    }

}
