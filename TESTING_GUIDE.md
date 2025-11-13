# Testing Guide for AR Urban Garden

## Pre-Testing Checklist

- [ ] App installed on ARCore-compatible device
- [ ] Camera permission granted
- [ ] Location permission granted (for weather)
- [ ] ML models placed in `app/src/main/assets/models/`
- [ ] API key added to `api_keys.xml` (optional, for weather)
- [ ] Test plants or images available

## Testing Procedures

### 1. Onboarding Flow Test

**Steps**:
1. Launch app for first time
2. Complete onboarding screens
3. Grant permissions
4. Complete parental consent

**Expected**:
- All screens display correctly
- Permissions requested properly
- Can proceed to main menu

**Issues to Check**:
- App crashes on permission denial
- Onboarding can be skipped
- Settings saved correctly

### 2. Plant Recognition Test

**Steps**:
1. Tap "Identify Plant"
2. Point camera at plant (or plant image)
3. Wait for recognition
4. Check result display

**Expected**:
- Camera opens successfully
- Plant recognized (if in database)
- Confidence score displayed
- Plant information shown

**Test Cases**:
- Known plant (should recognize)
- Unknown plant (should show "not found")
- Poor lighting (should handle gracefully)
- Multiple plants in frame

**Metrics**:
- Recognition accuracy: ≥80%
- Recognition time: <2 seconds
- False positives: <10%

### 3. AR Measurement Test

**Steps**:
1. Tap "Measure"
2. Find flat surface
3. Tap two points
4. Check measurement display

**Expected**:
- AR session starts
- Planes detected
- Measurement displayed in cm
- Accuracy within 10% of actual

**Test Objects**:
- 10cm pot (should measure 9-11cm)
- 15cm pot (should measure 13.5-16.5cm)
- 20cm pot (should measure 18-22cm)

**Metrics**:
- Measurement error: <10%
- AR stability: No crashes
- Frame rate: ≥30 FPS

### 4. AR Placement Test

**Steps**:
1. Tap "Place Garden"
2. Find flat surface
3. Tap to place virtual pot
4. Check spacing warnings

**Expected**:
- Virtual objects appear
- Anchored to real world
- Spacing calculated correctly
- Warnings shown if too close

**Test Cases**:
- Single pot placement
- Multiple pots
- Overlapping pots (should warn)
- Poor lighting conditions

### 5. Growth Tracking Test

**Steps**:
1. Tap "Track Growth"
2. Take photo of plant
3. Check record saved
4. View timeline

**Expected**:
- Photo captured
- Record saved to database
- Timeline displays correctly
- Can view previous records

**Test Cases**:
- First record
- Multiple records over time
- Delete record
- View specific plant records

### 6. BLE Sensor Test (If Available)

**Steps**:
1. Enable Bluetooth
2. Connect to soil sensor
3. Check readings displayed
4. Verify readings update

**Expected**:
- Sensor discovered
- Connection successful
- Moisture/pH/temperature displayed
- Updates in real-time

**Test Cases**:
- Successful connection
- Connection failure handling
- Sensor disconnection
- Multiple sensors

### 7. Weather API Test

**Steps**:
1. Enable location
2. Check weather displayed
3. Verify care recommendations update

**Expected**:
- Weather data fetched
- Temperature/humidity displayed
- Care recommendations adjust based on weather

**Test Cases**:
- With API key (should work)
- Without API key (should handle gracefully)
- Network error (should handle gracefully)
- Location denied

### 8. Settings Test

**Steps**:
1. Open Settings
2. Toggle language
3. Toggle voice guidance
4. Toggle high contrast

**Expected**:
- Settings save correctly
- Language changes immediately
- Voice guidance works
- UI updates for high contrast

### 9. Child-Friendly UX Test

**Test with Target Users (Grade 5-6)**:

**Tasks**:
1. Complete onboarding independently
2. Identify a plant
3. Place virtual garden
4. Record growth photo

**Metrics**:
- Task completion rate: ≥80%
- Time to complete: <5 min per task
- Error rate: <20%
- User satisfaction: Positive feedback

**Observations**:
- Can students navigate independently?
- Are buttons large enough?
- Is text readable?
- Are instructions clear?

### 10. Performance Test

**Metrics to Monitor**:
- App startup time: <3 seconds
- Memory usage: <200 MB
- Battery drain: Acceptable
- No memory leaks
- Smooth animations (≥30 FPS)

**Tools**:
- Android Studio Profiler
- Logcat for errors
- Battery usage monitor

## Bug Reporting Template

When reporting bugs, include:

1. **Device Info**:
   - Device model
   - Android version
   - ARCore version

2. **Steps to Reproduce**:
   - Detailed steps
   - Expected vs actual behavior

3. **Screenshots/Logs**:
   - Screenshots if applicable
   - Logcat output

4. **Frequency**:
   - Always/Sometimes/Rarely

## Test Results Template

```
Test Date: [Date]
Tester: [Name]
Device: [Model/Version]

Feature: [Feature Name]
Status: [Pass/Fail/Partial]
Notes: [Observations]

Issues Found:
1. [Issue description]
2. [Issue description]

Recommendations:
- [Recommendation]
```

## Continuous Testing

- Test after each major change
- Test on multiple devices if possible
- Test with real users regularly
- Monitor crash reports
- Track user feedback

## Success Criteria

✅ All core features work
✅ ≥80% plant recognition accuracy
✅ <10% measurement error
✅ No critical crashes
✅ Child-friendly UX validated
✅ Performance acceptable

