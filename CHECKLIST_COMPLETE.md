# Complete Checklist - AR Urban Garden Setup

## Pre-Development Checklist

### Software Requirements
- [ ] Android Studio installed
- [ ] Android SDK installed
- [ ] Java JDK installed
- [ ] Git installed (optional)

### Hardware Requirements
- [ ] ARCore-compatible Android device
- [ ] USB cable
- [ ] Computer (Windows/Mac/Linux)

---

## Development Setup Checklist

### Project Setup
- [ ] Project cloned/downloaded
- [ ] Android Studio opened project
- [ ] Gradle sync successful
- [ ] No build errors
- [ ] Dependencies downloaded

### ML Model Setup
- [ ] Python installed (for training)
- [ ] TensorFlow installed
- [ ] Plant images collected (≥500 per type)
- [ ] Images organized in `data/raw/`
- [ ] Data preparation script run
- [ ] Model trained successfully
- [ ] `plant_model.tflite` created
- [ ] Model copied to `app/src/main/assets/models/`

### API Setup
- [ ] OpenWeatherMap account created
- [ ] API key obtained
- [ ] API key added to `api_keys.xml`

### Device Setup
- [ ] Developer options enabled
- [ ] USB debugging enabled
- [ ] Device connected to computer
- [ ] Device recognized by Android Studio
- [ ] ARCore app installed (if needed)

---

## First Run Checklist

### Build & Install
- [ ] Project builds successfully
- [ ] No compilation errors
- [ ] App installed on device
- [ ] App launches without crashes

### Onboarding
- [ ] Welcome screen displayed
- [ ] Camera permission requested
- [ ] Location permission requested
- [ ] Bluetooth permission requested
- [ ] Parental consent dialog shown
- [ ] Onboarding completed

### Main Features
- [ ] Main menu displayed
- [ ] All 5 buttons visible
- [ ] "Identify Plant" works
- [ ] "Place Garden" works (if ARCore)
- [ ] "Measure" works (if ARCore)
- [ ] "Track Growth" works
- [ ] "Settings" works

### AR Features (if ARCore compatible)
- [ ] AR session starts
- [ ] Planes detected
- [ ] Measurement accurate (<10% error)
- [ ] Placement works
- [ ] Anchors stable

### ML Features
- [ ] Plant recognition works
- [ ] Confidence scores displayed
- [ ] Plant information shown
- [ ] Accuracy ≥80% (on test set)

---

## Testing Checklist

### Functional Testing
- [ ] All features work as expected
- [ ] No crashes during normal use
- [ ] Permissions handled correctly
- [ ] Error messages clear
- [ ] App handles offline mode

### Performance Testing
- [ ] App starts in <3 seconds
- [ ] AR runs at ≥30 FPS
- [ ] ML inference <500ms
- [ ] Memory usage <200 MB
- [ ] No memory leaks

### User Experience Testing
- [ ] UI is child-friendly
- [ ] Buttons large enough (≥48dp)
- [ ] Text readable
- [ ] Instructions clear
- [ ] Voice guidance works (if enabled)
- [ ] Language toggle works

### Device Compatibility
- [ ] Works on target devices
- [ ] Works on different Android versions
- [ ] Handles low-end devices gracefully

---

## Pre-Deployment Checklist

### Code Quality
- [ ] No critical bugs
- [ ] Code reviewed
- [ ] Comments added
- [ ] Documentation complete

### Security & Privacy
- [ ] Permissions properly requested
- [ ] Parental consent flow working
- [ ] Data stored securely
- [ ] No sensitive data exposed
- [ ] Privacy policy (if needed)

### Localization
- [ ] Tagalog strings complete
- [ ] English strings complete
- [ ] Language toggle works
- [ ] Voice guidance bilingual

### Assets
- [ ] App icon created
- [ ] Splash screen (if any)
- [ ] Images optimized
- [ ] ML models included

---

## Deployment Checklist

### Play Store Preparation
- [ ] App signed with release key
- [ ] Version number updated
- [ ] Release notes prepared
- [ ] Screenshots taken
- [ ] App description written
- [ ] Privacy policy URL (if needed)

### Testing
- [ ] Release build tested
- [ ] All features tested
- [ ] No critical bugs
- [ ] Performance acceptable

### Submission
- [ ] Play Store account created
- [ ] App uploaded
- [ ] Store listing complete
- [ ] Submitted for review

---

## Post-Deployment Checklist

### Monitoring
- [ ] Crash reports monitored
- [ ] User feedback collected
- [ ] Analytics set up (if any)
- [ ] Performance monitored

### Updates
- [ ] Bug fixes planned
- [ ] Feature improvements planned
- [ ] User feedback addressed

---

## Quick Reference

### Important Files
- `app/build.gradle` - Dependencies
- `app/src/main/AndroidManifest.xml` - Permissions
- `app/src/main/res/values/api_keys.xml` - API keys
- `app/src/main/assets/models/` - ML models

### Important Commands
```bash
# Build project
./gradlew build

# Run tests
./gradlew test

# Clean project
./gradlew clean

# Install on device
./gradlew installDebug
```

### Important URLs
- Android Studio: https://developer.android.com/studio
- ARCore Devices: https://developers.google.com/ar/discover/supported-devices
- OpenWeatherMap: https://openweathermap.org/api
- TensorFlow: https://www.tensorflow.org/

---

## Notes

- Mark items as you complete them
- Add notes for any issues encountered
- Update this checklist as needed
- Share with team members

---

Good luck sa development! 🚀🌱

