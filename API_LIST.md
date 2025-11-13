# API List for AR Urban Garden

## External APIs

### 1. OpenWeatherMap API

**Purpose**: Get current weather conditions for care recommendations

**Endpoint**: `https://api.openweathermap.org/data/2.5/weather`

**Method**: GET

**Parameters**:
- `lat`: Latitude (required)
- `lon`: Longitude (required)
- `appid`: API key (required)
- `units`: "metric" (optional, default: Kelvin)

**Response Example**:
```json
{
  "main": {
    "temp": 28.5,
    "humidity": 65
  },
  "weather": [{
    "main": "Clear",
    "description": "clear sky"
  }],
  "wind": {
    "speed": 2.5
  },
  "rain": {
    "1h": 0.0
  }
}
```

**Usage in App**:
- Watering recommendations based on rainfall
- Sunlight advice based on weather conditions
- Growth tracking metadata

**Rate Limits**: 
- Free tier: 60 calls/minute
- Paid tier: Higher limits available

**Setup**:
1. Sign up at https://openweathermap.org/api
2. Get API key
3. Add to `app/src/main/res/values/api_keys.xml`

**Alternative Providers**:
- WeatherAPI.com
- AccuWeather API
- (Can be configured in `WeatherService.kt`)

## Internal APIs (App Components)

### 1. Plant Recognition API

**Class**: `PlantRecognitionModel`

**Method**: `recognizePlant(bitmap: Bitmap): PlantRecognitionResult`

**Input**: Bitmap image of plant

**Output**: 
- Plant object (if recognized)
- Confidence score (0.0 - 1.0)
- Bounding box (optional)

**Usage**: Real-time plant identification from camera

### 2. Pest Detection API

**Class**: `PestDetectionModel`

**Method**: `detectPest(bitmap: Bitmap): PestDetectionResult`

**Input**: Close-up photo of plant issue

**Output**:
- Issue name
- Confidence score
- Recommended actions
- Severity level

**Usage**: Assistive tool for identifying plant problems

### 3. AR Measurement API

**Class**: `ARMeasurement`

**Methods**:
- `addMeasurementPoint(frame: Frame, hitResult: HitResult): Boolean`
- `getDistance(): Float?` (in cm)
- `getHeight(): Float?` (in cm)
- `reset()`

**Usage**: Measure pot sizes, plant heights, spacing

### 4. Care Recommendation API

**Class**: `CareRecommendationRepository`

**Method**: `getCareRecommendation(plant: Plant, lat: Double?, lon: Double?): CareRecommendation`

**Input**: 
- Plant object
- Optional location (for weather)

**Output**: 
- List of care actions (water, sunlight, etc.)
- Priority levels
- Status (needed now, OK, etc.)

**Usage**: Generate personalized care advice

### 5. BLE Sensor API

**Class**: `BLESensorManager`

**Methods**:
- `connectToDevice(address: String): Boolean`
- `disconnect()`
- `scanForDevices(): List<BluetoothDevice>`

**State Flows**:
- `connectedDevice: StateFlow<BLESensorDevice?>`
- `sensorData: StateFlow<SensorData?>`

**Usage**: Connect to soil moisture/pH sensors

### 6. Weather Service API

**Class**: `WeatherService`

**Method**: `getCurrentWeather(lat: Double, lon: Double): WeatherData?`

**Input**: Latitude and longitude

**Output**: WeatherData object with temperature, humidity, conditions

**Usage**: Get real-time weather for care recommendations

## Data Models

### Plant
```kotlin
data class Plant(
    val id: String,
    val name: String,
    val nameTagalog: String,
    val scientificName: String,
    val waterFrequency: WaterFrequency,
    val sunlightNeeds: SunlightNeeds,
    val careTips: List<String>,
    val careTipsTagalog: List<String>
)
```

### CareAction
```kotlin
data class CareAction(
    val type: CareActionType,
    val status: CareStatus,
    val message: String,
    val messageTagalog: String,
    val iconRes: Int,
    val priority: Int
)
```

### GrowthRecord
```kotlin
data class GrowthRecord(
    val id: String,
    val plantId: String,
    val anchorId: String?,
    val timestamp: Long,
    val photoPath: String,
    val metadata: GrowthMetadata
)
```

### SensorData
```kotlin
data class SensorData(
    val moisture: Int?,  // 0-100%
    val ph: Float?,       // 0-14
    val temperature: Float?,  // Celsius
    val timestamp: Long
)
```

## API Integration Guide

### Adding a New Weather Provider

1. Create new service class implementing `WeatherService` interface
2. Update `WeatherService.kt` to support multiple providers
3. Add provider selection in settings

### Adding a New Sensor Type

1. Define BLE GATT characteristics
2. Update `BLESensorManager` to read new characteristics
3. Add to `SensorData` model
4. Update UI to display new sensor data

## Error Handling

All APIs should handle:
- Network errors (weather API)
- Device errors (BLE sensors)
- ML inference errors (model loading, processing)
- Permission errors (camera, location, Bluetooth)

## Testing APIs

### Unit Tests
- Mock API responses
- Test error handling
- Test data parsing

### Integration Tests
- Test with real weather API (use test API key)
- Test with mock BLE devices
- Test ML models with test images

### End-to-End Tests
- Test complete user flows
- Test API interactions in real scenarios

