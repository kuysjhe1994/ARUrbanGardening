# Quick Start Guide - AR Urban Garden

## For Developers

### 1. Prerequisites
- Android Studio (latest version)
- ARCore-compatible Android device (or emulator with ARCore support)
- OpenWeatherMap API key (optional, for weather features)

### 2. Setup Steps

1. **Clone/Download the project**
   ```bash
   cd ARUrbanGarden
   ```

2. **Open in Android Studio**
   - File → Open → Select project folder
   - Wait for Gradle sync to complete

3. **Add API Key** (Optional)
   - Copy `app/src/main/res/values/api_keys.xml.template`
   - Create `app/src/main/res/values/api_keys.xml`
   - Add your OpenWeatherMap API key:
     ```xml
     <string name="openweather_api_key">YOUR_KEY_HERE</string>
     ```

4. **Add ML Models** (Required for plant recognition)
   - Create folder: `app/src/main/assets/models/`
   - Place `plant_model.tflite` in that folder
   - Place `pest_model.tflite` in that folder
   - (See ML_PIPELINE.md for training instructions)

5. **Build and Run**
   - Connect ARCore-compatible device
   - Click Run button in Android Studio
   - Select your device
   - Wait for app to install and launch

### 3. First Run

1. **Onboarding**
   - Grant camera permission
   - Grant location permission (for weather)
   - Grant Bluetooth permission (for sensors)
   - Complete parental consent flow

2. **Test Features**
   - Try "Identify Plant" with a plant image
   - Try "Place Garden" on a flat surface
   - Try "Measure" to test AR measurement

## For Teachers

### Using the App in Classroom

1. **Preparation**
   - Install app on tablets/phones
   - Test on devices before lesson
   - Prepare sample plants or images

2. **First Use**
   - Complete onboarding with students
   - Explain each feature briefly
   - Let students explore

3. **Lesson Activities**
   - See LESSON_PLAN.md for detailed lesson plan
   - Start with plant identification
   - Progress to AR placement
   - End with growth tracking

### Troubleshooting

**AR not working?**
- Check device supports ARCore
- Ensure good lighting
- Find flat surface

**Plant not recognized?**
- Try different angle
- Get closer
- Ensure good lighting

**App crashes?**
- Restart app
- Restart device
- Check device has enough storage

## For Students

### How to Use

1. **Open the app**
   - Tap the app icon
   - Follow instructions on screen

2. **Identify a Plant**
   - Tap "Tukuyin ang Halaman"
   - Point camera at plant
   - Wait for recognition
   - Read plant information

3. **Place a Garden**
   - Tap "Ilagay ang Hardin"
   - Find flat surface
   - Tap to place pots
   - See spacing recommendations

4. **Measure**
   - Tap "Sukatin"
   - Tap two points
   - See distance measurement

5. **Track Growth**
   - Tap "Subaybayan ang Paglago"
   - Take photo
   - See timeline of growth

### Tips

- Hold device steady
- Use in good lighting
- Ask teacher if you need help
- Have fun learning!

## Common Issues

### "ARCore not available"
- Your device may not support ARCore
- Check ARCore compatibility list
- Use plant identification feature only

### "Camera permission denied"
- Go to device Settings
- Find AR Urban Garden app
- Enable Camera permission

### "Plant not found"
- Try different plant
- Get closer to plant
- Ensure good lighting
- Some plants may not be in database yet

## Next Steps

- Read README.md for full documentation
- See TEST_PLAN.md for testing procedures
- See ML_PIPELINE.md for model training
- See LESSON_PLAN.md for classroom use

## Support

For issues or questions:
- Check documentation files
- Review error messages
- Ask teacher or developer

