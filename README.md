# AR Urban Garden — Real

Android AR mobile app na naglalayong turuan at gabayan ang mga batang nasa Grade 5–6 sa urban gardening gamit ang real-time AR at on-device ML.

## Features

### Core AR Features
- **Real-time Plant Recognition**: On-device ML (TensorFlow Lite) para sa plant identification
- **AR Anchored Overlays**: Dynamic care actions na nakabase sa real sensor data
- **Live Placement Mode**: Visualize containers/beds sa real space
- **AR Measurement Tool**: Sukatin ang pot size, plant height, at spacing
- **Growth Tracking**: Timelapse gamit ang AR anchors at real photos
- **Pest/Disease Detection**: On-device ML para sa problem detection

### Real Data Integrations
- **Weather API**: OpenWeatherMap integration para sa current conditions
- **BLE Soil Sensors**: Optional Bluetooth LE sensors para sa moisture, pH, temperature
- **On-device ML**: TensorFlow Lite models para sa plant recognition at pest detection

### Child-Friendly UX
- **Bilingual**: Tagalog + English support
- **Large Icons**: Malalaking touch targets
- **Voice Guidance**: Optional audio instructions
- **Gamified**: Badges para sa completed tasks
- **Safety**: Parental consent flow

## Technical Stack

- **Platform**: Android (ARCore compatible, minSdk 24)
- **AR**: Google ARCore (Session, Plane Detection, HitTest, Depth API)
- **ML**: TensorFlow Lite, ML Kit (fallback)
- **BLE**: Android Bluetooth LE APIs
- **Networking**: Retrofit + OkHttp
- **Storage**: Local SQLite + optional Firebase
- **Language**: Kotlin

## Setup Instructions

### Prerequisites
1. Android Studio (latest version)
2. ARCore compatible device or emulator
3. OpenWeatherMap API key (optional, for weather features)

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd ARUrbanGarden
```

2. Open in Android Studio

3. Add your OpenWeatherMap API key:
   - Create `app/src/main/res/values/api_keys.xml`:
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <resources>
       <string name="openweather_api_key">YOUR_API_KEY_HERE</string>
   </resources>
   ```

4. Add ML Models:
   - Place `plant_model.tflite` in `app/src/main/assets/models/`
   - Place `pest_model.tflite` in `app/src/main/assets/models/`
   - (Models need to be trained separately - see ML Pipeline section)

5. Build and run:
   - Connect ARCore-compatible device
   - Run the app

## ML Model Pipeline

### Plant Recognition Model

1. **Data Collection**:
   - Collect ≥500 labeled images per plant species
   - Include diverse lighting, angles, growth stages
   - Common plants: basil, mint, tomato, kangkong, chili

2. **Training**:
   - Use TensorFlow/Keras
   - MobileNet or EfficientNet architecture
   - Data augmentation (rotation, brightness, contrast)
   - Target: ≥80% top-1 accuracy

3. **Conversion**:
   - Convert to TensorFlow Lite
   - Quantize to INT8 (target size: ≤10-20 MB)
   - Test on-device performance

4. **Deployment**:
   - Place in `app/src/main/assets/models/plant_model.tflite`
   - Update `PlantRecognitionModel.kt` if needed

### Pest/Disease Model

Similar pipeline for pest/disease detection:
- Collect labeled images of common issues
- Train classifier
- Convert to TFLite
- Place in `app/src/main/assets/models/pest_model.tflite`

## API Integration

### Weather API (OpenWeatherMap)

1. Sign up at https://openweathermap.org/api
2. Get API key
3. Add to `api_keys.xml` (see Setup Instructions)
4. Weather data is used for:
   - Watering recommendations
   - Sunlight advice
   - Growth tracking metadata

### BLE Sensor Integration

The app supports standard BLE GATT characteristics for soil sensors:
- Moisture: UUID `00002a19-0000-1000-8000-00805f9b34fb`
- pH: UUID `00002a6e-0000-1000-8000-00805f9b34fb`
- Temperature: UUID `00002a6f-0000-1000-8000-00805f9b34fb`

## Testing Plan

### Unit Tests
- ML model accuracy (≥80% on test set)
- AR measurement accuracy (<10% error)
- Care recommendation logic

### Integration Tests
- BLE sensor connection
- Weather API integration
- AR anchor persistence

### User Testing (Classroom Validation)
- Test with 10 Grade 5-6 students
- Measure:
  - Time to complete tasks
  - Plant ID accuracy in field
  - BLE sensor connection success rate
  - UX comprehension

### Acceptance Criteria
- ✅ Plant recognition: ≥80% top-1 accuracy
- ✅ AR measurement: <10% error on common pot sizes
- ✅ BLE sensor: Successful connection and reading
- ✅ Onboarding: Completed by target age group
- ✅ Privacy: Parental consent flow working

## Project Structure

```
app/
├── src/main/
│   ├── java/com/arurbangarden/real/
│   │   ├── ar/              # ARCore integration
│   │   ├── data/            # Models, API, repositories
│   │   ├── ml/              # ML models (TFLite)
│   │   └── ui/              # Activities and UI
│   ├── res/
│   │   ├── layout/          # XML layouts
│   │   ├── values/         # Strings, colors, themes
│   │   └── assets/models/  # TFLite models
│   └── AndroidManifest.xml
└── build.gradle
```

## Privacy & Safety

- **On-device processing**: ML models run locally
- **Parental consent**: Required for cloud sync
- **Data retention**: Option to delete photos/anchors
- **No social sharing**: By default, no public posting

## Accessibility

- **Voice guidance**: Optional audio instructions
- **High contrast mode**: For visual accessibility
- **Large touch targets**: Child-friendly UI
- **Bilingual**: Tagalog + English

## License

[Add your license here]

## Contributors

[Add contributors here]

## Support

For issues and questions, please open an issue on GitHub.

