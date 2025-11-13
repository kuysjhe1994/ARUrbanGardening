# Test Plan for AR Urban Garden — Real

## 1. ML Model Testing

### Plant Recognition Model
- **Test Set**: ≥100 images per plant species (basil, mint, tomato, kangkong, chili)
- **Acceptance Criteria**: ≥80% top-1 accuracy
- **Test Environment**: On-device (real Android device)
- **Metrics**:
  - Top-1 accuracy
  - Top-3 accuracy
  - Inference time (<500ms per image)
  - Model size (≤20 MB)

### Pest/Disease Model
- **Test Set**: Labeled images of common issues
- **Acceptance Criteria**: ≥70% accuracy (lower threshold due to complexity)
- **Metrics**: Confidence scores, false positive rate

## 2. AR Measurement Testing

### Accuracy Tests
- **Test Objects**: Common pot sizes (10cm, 15cm, 20cm, 30cm diameter)
- **Method**: 
  1. Measure with AR tool
  2. Compare with physical measurement
  3. Calculate error percentage
- **Acceptance Criteria**: <10% error
- **Test Cases**:
  - Pot diameter measurement
  - Plant height measurement
  - Spacing between plants

### AR Placement Testing
- **Test**: Place virtual pots on flat surfaces
- **Metrics**: 
  - Anchor stability
  - Visual alignment accuracy
  - Spacing calculation accuracy

## 3. BLE Sensor Integration

### Connection Tests
- **Test Devices**: Standard BLE soil sensors
- **Test Cases**:
  - Successful connection
  - Reading moisture values
  - Reading pH values
  - Reading temperature
  - Handling disconnection
- **Acceptance Criteria**: 
  - ≥90% successful connection rate
  - Accurate readings (compare with known values)

## 4. Weather API Integration

### API Tests
- **Test Cases**:
  - Successful API call
  - Handling network errors
  - Parsing response data
  - Updating care recommendations based on weather
- **Acceptance Criteria**: 
  - API calls succeed ≥95% of time
  - Recommendations update correctly

## 5. User Experience Testing

### Onboarding Flow
- **Participants**: 10 Grade 5-6 students
- **Metrics**:
  - Completion rate (target: 100%)
  - Time to complete (target: <5 minutes)
  - Comprehension questions
- **Test Scenarios**:
  - First-time user
  - User with limited tech experience

### Task Completion
- **Tasks**:
  1. Identify a plant
  2. Place virtual garden
  3. Measure pot size
  4. Record growth photo
  5. Connect BLE sensor
- **Metrics**:
  - Success rate per task
  - Time to complete
  - Number of errors/retries
  - User satisfaction (simple survey)

### UI/UX Testing
- **Test Areas**:
  - Button sizes (target: ≥48dp)
  - Text readability
  - Icon clarity
  - Voice guidance effectiveness
  - Language toggle (Tagalog/English)

## 6. Performance Testing

### AR Performance
- **Metrics**:
  - Frame rate (target: ≥30 FPS)
  - AR session stability
  - Memory usage
  - Battery impact

### ML Inference Performance
- **Metrics**:
  - Inference time per image
  - CPU/GPU usage
  - Battery drain during recognition

## 7. Privacy & Safety Testing

### Privacy Flow
- **Test Cases**:
  - Parental consent dialog
  - Cloud sync toggle (requires consent)
  - Data deletion functionality
  - Camera permission handling

### Safety Features
- **Test Cases**:
  - Pest detection safety warnings
  - Adult help prompts
  - Error handling for edge cases

## 8. Classroom Validation

### Pilot Test
- **Participants**: 10 Grade 5-6 students + 1 teacher
- **Duration**: 20-30 minute lesson
- **Activities**:
  1. Onboarding (5 min)
  2. Plant identification (5 min)
  3. AR placement (5 min)
  4. Growth recording (5 min)
  5. Feedback collection (5 min)

### Metrics Collected
- Task completion rates
- Time per task
- Error frequency
- User feedback (simple questionnaire)
- Teacher observations

### Success Criteria
- ≥80% task completion rate
- Average task time within acceptable range
- Positive user feedback
- No critical bugs or crashes

## 9. Regression Testing

### Before Release
- Test all core features
- Verify no regressions from previous versions
- Test on multiple devices (if available)
- Test on different Android versions (API 24+)

## 10. Test Environment

### Devices
- Primary: ARCore-compatible Android device (API 24+)
- Secondary: Emulator (limited AR functionality)

### Test Data
- Plant images: Diverse lighting, angles, growth stages
- Sensor data: Mock BLE sensor or real device
- Weather data: Use real API or mock responses

## Test Execution Schedule

1. **Week 1**: ML model testing, AR measurement testing
2. **Week 2**: BLE sensor testing, Weather API testing
3. **Week 3**: UX testing with small group (3-5 students)
4. **Week 4**: Classroom validation (10 students)
5. **Week 5**: Bug fixes and retesting
6. **Week 6**: Final regression testing

## Reporting

After each test phase:
- Document results
- Identify issues
- Prioritize fixes
- Update acceptance criteria if needed

