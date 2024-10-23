package org.example.repository;


import org.example.model.WeatherData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherDataRepository extends MongoRepository<WeatherData, String> {
    List<WeatherData> findByCity(String city);

    // Add this method to support querying between timestamps
    List<WeatherData> findByCityAndTimestampBetween(String city, LocalDateTime start, LocalDateTime end);
}

