let currentCity = '';  // Global variable to store the last searched city
let weatherChart;  // Global chart variable for current weather
let historicalWeatherChart;  // Global chart variable for historical weather
let lat, lon;  // Variables to store latitude and longitude for geolocation
let locationInterval;  // To store the interval for geolocation updates

// Automatically set today's date in the historical date input
document.getElementById('historical-date-input').value = new Date().toISOString().substr(0, 10);

// Use Geolocation API to get user's location
function fetchUserLocation() {
    if (navigator.geolocation) {
        console.log("Geolocation supported. Trying to fetch location...");
        navigator.geolocation.getCurrentPosition((position) => {
            lat = position.coords.latitude;
            lon = position.coords.longitude;
            console.log("Latitude: " + lat + ", Longitude: " + lon); // Debugging log
            fetchWeatherByCoordinates(lat, lon);

            // Automatically fetch weather data for the current location every minute
            locationInterval = setInterval(() => {
                console.log("Fetching weather data every minute for coordinates:", lat, lon);
                fetchWeatherByCoordinates(lat, lon);
            }, 60000);  // Update every 1 minute
        }, (error) => {
            console.error('Error getting location:', error);
            alert('Unable to retrieve location. Please enter a city manually.');
        });
    } else {
        alert('Geolocation is not supported by this browser.');
    }
}

// Fetch weather data by coordinates
function fetchWeatherByCoordinates(lat, lon) {
    fetch(`/weather/coordinates?lat=${lat}&lon=${lon}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Unable to fetch weather data for the current location.');
            }
            return response.json();
        })
        .then(data => {
            console.log("Weather data:", data); // Debugging log
            currentCity = data.city || 'Unknown Location';  // Automatically set the city
            displayWeatherData(data);
            updateWeatherChart(data);
            fetchHistoricalWeatherData();
            // Update "Last updated" time
            const now = new Date();
            document.getElementById('last-updated').textContent = `Last updated: ${now.toLocaleTimeString()} | Next update in 1 minute`;
            console.log("Updating last updated time...");

        })
        .catch(error => {
            console.error('Error fetching weather by coordinates:', error);
        });
}

// Fetch weather data based on the user's input
function fetchWeatherData() {
    const city = document.getElementById('city-input').value;
    const minThreshold = parseFloat(document.getElementById('min-threshold').value);
    const maxThreshold = parseFloat(document.getElementById('max-threshold').value);
    const conditionThreshold = document.getElementById('weather-condition-threshold').value;

    if (city === '') {
        alert('Please enter a city name');
        return;
    }

    currentCity = city;  // Store the current city globally

    document.getElementById('loading-spinner').style.display = 'block';

    const requestBody = {
        minTemp: minThreshold,
        maxTemp: maxThreshold,
        userCondition: conditionThreshold
    };

    fetch(`/weather/alert/${city}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('City Not Found');
            }
            return response.json();
        })
        .then(data => {
            displayWeatherData(data.weatherData, minThreshold, maxThreshold, conditionThreshold);
            updateWeatherChart(data.weatherData);
            fetchHistoricalWeatherData();  // Fetch today's historical weather automatically
            document.getElementById('loading-spinner').style.display = 'none';

            // Update "Last updated" time
            const now = new Date();
            document.getElementById('last-updated').textContent = `Last updated: ${now.toLocaleTimeString()} | Next update in 1 minute`;

            // Clear the geolocation update interval
            stopLocationUpdates();
        })
        .catch(error => {
            console.error('Error fetching weather data:', error);
            document.getElementById('city').innerHTML = '<b>City Not Found. Please Enter Correct City.</b>';
            document.getElementById('loading-spinner').style.display = 'none';
        });
}

// Function to stop automatic updates for geolocation
function stopLocationUpdates() {
    if (locationInterval) {
        clearInterval(locationInterval);
        locationInterval = null;  // Reset the interval variable
    }
}

// Function to display current weather data
function displayWeatherData(data, minThreshold, maxThreshold, conditionThreshold) {
    document.getElementById('city').textContent = data.city || "N/A";
    document.getElementById('temperature').textContent = data.tempCelsius ? `${data.tempCelsius}` : "N/A";
    document.getElementById('feels-like').textContent = data.feelsLikeCelsius ? `${data.feelsLikeCelsius}` : "N/A";
    document.getElementById('condition').textContent = data.weatherCondition || "N/A";
    document.getElementById('max-temp').textContent = data.maxTemperature ? `${data.maxTemperature}` : "N/A";
    document.getElementById('min-temp').textContent = data.minTemperature ? `${data.minTemperature}` : "N/A";
    document.getElementById('avg-temp').textContent = data.averageTemperature ? `${data.averageTemperature.toFixed(2)}` : "N/A";
    document.getElementById('wind-speed').textContent = data.windSpeed ? `${data.windSpeed}` : "N/A";
    document.getElementById('humidity').textContent = data.humidity ? `${data.humidity}` : "N/A";
    document.getElementById('pressure').textContent = data.pressure ? `${data.pressure}` : "N/A"; // Added
    document.getElementById('visibility').textContent = data.visibility ? `${data.visibility}` : "N/A"; // Added
    document.getElementById('cloudiness').textContent = data.cloudiness ? `${data.cloudiness}` : "N/A"; // Added
    document.getElementById('dominant-condition').textContent = data.dominantWeatherCondition || "N/A";

    // Define the icon paths for different weather conditions
    const weatherIcons = {
        "Thunderstorm": "/images/icons8-thunderstorm-50.png",
        "Drizzle": "/images/icons8-rain-48.png",
        "Rain": "/images/icons8-rain-48.png",
        "Snow": "/images/icons8-snow-80.png",
        "Clear": "/images/icons8-weather-50.png",
        "Clouds": "/images/icons8-cloud-40.png",
        "Haze": "/images/icons8-haze-50.png"
    };

    const defaultIcon = "/images/icons8-weather-64.png"; // Default icon in case the condition is not found
    const iconUrl = weatherIcons[data.weatherCondition] || defaultIcon; // Set the icon URL based on condition

    // Update the weather icon image source
    document.querySelector('.weather-icon').src = iconUrl;

    // Update AQI Display
    const aqi = data.aqi || 0;
    const { aqiCategory, aqiDescription } = getAQIDescription(aqi);

    document.getElementById('aqi-display').textContent = aqi;
    document.getElementById('aqi-description').textContent = aqiCategory + ": " + aqiDescription;

    // Alert based on temperature and condition thresholds
    let alertMessage = '';

    if (minThreshold && data.tempCelsius < minThreshold) {
        alertMessage = `Temperature is below the minimum threshold of ${minThreshold}°C.`;
    } else if (maxThreshold && data.tempCelsius > maxThreshold) {
        alertMessage = `Temperature exceeds the maximum threshold of ${maxThreshold}°C.`;
    }

    if (conditionThreshold && data.weatherCondition.toLowerCase() === conditionThreshold.toLowerCase()) {
        alertMessage += ` Condition matches the specified threshold: ${conditionThreshold}.`;
    }

    document.getElementById('alert-message').innerText = alertMessage;
}

// Helper function to return AQI category and description based on value
function getAQIDescription(aqiValue) {
    let aqiCategory;
    let aqiDescription;

    if (aqiValue === 1) {
        aqiCategory = 'Good';
        aqiDescription = 'Air quality is considered satisfactory, and air pollution poses little or no risk.';
    } else if (aqiValue === 2) {
        aqiCategory = 'Fair';
        aqiDescription = 'Air quality is acceptable; however, there may be a risk for some people, particularly those who are unusually sensitive to air pollution.';
    } else if (aqiValue === 3) {
        aqiCategory = 'Moderate';
        aqiDescription = 'Members of sensitive groups may experience health effects. The general public is not likely to be affected.';
    } else if (aqiValue === 4) {
        aqiCategory = 'Poor';
        aqiDescription = 'Everyone may begin to experience health effects; members of sensitive groups may experience more serious health effects.';
    } else if (aqiValue === 5) {
        aqiCategory = 'Very Poor';
        aqiDescription = 'Health alert: everyone may experience more serious health effects.';
    } else {
        aqiCategory = 'Unknown';
        aqiDescription = 'AQI value is not recognized.';
    }

    return { aqiCategory, aqiDescription };
}

// Update current weather chart
function updateWeatherChart(data) {
    const ctx = document.getElementById('weatherChart').getContext('2d');
    const chartData = {
        labels: ['Max Temp', 'Min Temp', 'Avg Temp', 'Wind Speed', 'Humidity', 'Cloudiness'],
        datasets: [{
            label: 'Weather Metrics',
            data: [
                data.maxTemperature,
                data.minTemperature,
                data.averageTemperature.toFixed(2),
                data.windSpeed,
                data.humidity,
                data.cloudiness       // Added
            ],
            backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)',
                'rgba(255, 205, 86, 0.2)',  // Added
                'rgba(54, 162, 235, 0.5)',  // Added
                'rgba(255, 159, 64, 0.5)'    // Added
            ],
            borderColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)',
                'rgba(255, 205, 86, 1)',    // Added
                'rgba(54, 162, 235, 1)',     // Added
                'rgba(255, 159, 64, 1)'      // Added
            ],
            borderWidth: 1
        }]
    };

    if (weatherChart) {
        weatherChart.destroy();
    }

    weatherChart = new Chart(ctx, {
        type: 'bar',
        data: chartData,
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

// Fetch historical weather data and render a line chart
function fetchHistoricalWeatherData() {
    const date = document.getElementById('historical-date-input').value;

    if (currentCity === '' || date === '') {
        alert('Please search for a city first and select a date.');
        return;
    }

    document.getElementById('loading-spinner').style.display = 'block';

    fetch(`/weather/historical/${currentCity}/${date}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('No historical data found for this city on the specified date.');
            }
            return response.json();
        })
        .then(data => {
            if (data.length === 0) {
                alert('No historical data available for this city and date.');
                return;
            }

            const timestamps = data.map(entry => new Date(entry.timestamp).toLocaleTimeString());
            const temperatures = data.map(entry => entry.tempCelsius.toFixed(2));
            const windSpeeds = data.map(entry => entry.windSpeed.toFixed(2));
            const humidities = data.map(entry => entry.humidity.toFixed(2));

            updateHistoricalWeatherChart(timestamps, temperatures, windSpeeds, humidities);
            document.getElementById('loading-spinner').style.display = 'none';
        })
        .catch(error => {
            console.error('Error fetching historical weather data:', error);
            alert(error.message);
            document.getElementById('loading-spinner').style.display = 'none';
        });
}

// Update historical weather chart
function updateHistoricalWeatherChart(timestamps, temperatures, windSpeeds, humidities) {
    const ctx = document.getElementById('historicalWeatherChart').getContext('2d');

    const chartData = {
        labels: timestamps,
        datasets: [{
            label: 'Temperature (°C)',
            data: temperatures,
            borderColor: 'rgba(255, 99, 132, 1)',
            backgroundColor: 'rgba(255, 99, 132, 0.2)',
            fill: false,
            yAxisID: 'y1'
        }, {
            label: 'Wind Speed (m/s)',
            data: windSpeeds,
            borderColor: 'rgba(54, 162, 235, 1)',
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            fill: false,
            yAxisID: 'y2'
        }, {
            label: 'Humidity (%)',
            data: humidities,
            borderColor: 'rgba(75, 192, 192, 1)',
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            fill: false,
            yAxisID: 'y3'
        }]
    };

    const chartOptions = {
        scales: {
            y1: {
                type: 'linear',
                position: 'left',
                beginAtZero: true,
                title: {
                    display: true,
                    text: 'Temperature (°C)'
                }
            },
            y2: {
                type: 'linear',
                position: 'right',
                beginAtZero: true,
                title: {
                    display: true,
                    text: 'Wind Speed (m/s)'
                },
                grid: {
                    drawOnChartArea: false
                }
            },
            y3: {
                type: 'linear',
                position: 'right',
                beginAtZero: true,
                title: {
                    display: true,
                    text: 'Humidity (%)'
                },
                grid: {
                    drawOnChartArea: false
                }
            },
            x: {
                title: {
                    display: true,
                    text: 'Timestamp'
                }
            }
        }
    };

    if (historicalWeatherChart) {
        historicalWeatherChart.destroy();
    }

    historicalWeatherChart = new Chart(ctx, {
        type: 'line',
        data: chartData,
        options: chartOptions
    });
}

// Automatically refresh the data every 1 minute
setInterval(() => {
    if (currentCity) {
        fetchWeatherData();  // Fetch current weather and today's historical data
    }
}, 60000);  // 60000ms = 1 minute

// Call fetchUserLocation when the page loads to get the user's location
window.onload = fetchUserLocation;
