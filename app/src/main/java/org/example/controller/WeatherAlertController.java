package org.example.controller;

import org.example.model.AlertPreferences;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alert")
public class WeatherAlertController {
    private AlertPreferences alertPreferences = new AlertPreferences(35.0, 15.0); // Default thresholds

    // Endpoint to get the current alert preferences
    @GetMapping("/preferences")
    public AlertPreferences getAlertPreferences() {
        return alertPreferences;
    }

    // Endpoint to set user-defined alert preferences
    @PostMapping("/preferences")
    public String setAlertPreferences(@RequestBody AlertPreferences preferences) {
        this.alertPreferences = preferences;
        return "Alert preferences updated: High = " + preferences.getHighTempThreshold() + "°C, Low = " + preferences.getLowTempThreshold() + "°C";
    }

    public AlertPreferences getUserAlertPreferences() {
        return alertPreferences;
    }


}
