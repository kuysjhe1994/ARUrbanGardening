# Kid Missions Feature - Documentation

## Overview

The Kid Missions (Task System) feature provides daily gardening missions for Grade 5-6 students. Each mission is simple (1-3 steps), gamified with badges and points, and includes AR-integrated tasks.

## Features Implemented

### ✅ Core Features

1. **Daily Missions System**
   - Simple 1-3 step missions
   - Auto-generated daily missions
   - Local storage (offline support)

2. **Mission Types**
   - Soil Check: "Check if soil is dry"
   - Sunlight Find: "Find a place with sunlight"
   - Photo Take: "Take a photo of your plant today!"
   - Water Plant: "Give water to your plant"
   - AR Identify: "Point camera to plant" (AR-integrated)
   - Measure Plant: "Measure plant height" (AR-integrated)
   - Place Garden: "Use AR to place virtual garden" (AR-integrated)
   - Growth Record: "Take photo to track growth"
   - Pest Check: "Check for pests/diseases"
   - Fertilize: "Add fertilizer" (with adult help)

3. **Gamification**
   - Points system (10-25 points per mission)
   - Badge rewards (8 badge types)
   - Progress bar
   - Success animations
   - Success sounds (placeholder)

4. **Child-Friendly UI**
   - Large icons
   - Short phrases (Tagalog + English)
   - Step-by-step instructions
   - Visual progress indicators
   - Colorful cards

5. **Parent/Teacher Controls**
   - Enable/disable missions toggle
   - Settings integration
   - Privacy-safe (local storage only)

6. **AR Integration**
   - AR missions launch AR activities
   - Identify plant with AR
   - Measure with AR
   - Place garden with AR

## Files Created

### Data Models
- `app/src/main/java/com/arurbangarden/real/data/model/Mission.kt`
- Mission types, status, badges, progress

### Database
- `app/src/main/java/com/arurbangarden/real/data/database/MissionDatabase.kt`
- Room database for mission storage
- Repository pattern

### Mission Generator
- `app/src/main/java/com/arurbangarden/real/data/mission/MissionGenerator.kt`
- Generates daily missions
- Creates mission by type

### UI Components
- `app/src/main/java/com/arurbangarden/real/ui/missions/MissionsActivity.kt` - Main missions list
- `app/src/main/java/com/arurbangarden/real/ui/missions/MissionDetailActivity.kt` - Mission detail view
- `app/src/main/java/com/arurbangarden/real/ui/missions/MissionAdapter.kt` - RecyclerView adapter

### Layouts
- `app/src/main/res/layout/activity_missions.xml`
- `app/src/main/res/layout/activity_mission_detail.xml`
- `app/src/main/res/layout/item_mission.xml`
- `app/src/main/res/layout/item_mission_step.xml`

### Strings
- Added mission-related strings (Tagalog + English)

## Usage

### For Students

1. **Open Missions**
   - Tap "Mga Mission" from main menu
   - See list of daily missions

2. **Start Mission**
   - Tap a mission card
   - Read instructions
   - Tap "Simulan" (Start)

3. **Complete Steps**
   - Follow step-by-step instructions
   - Complete each step
   - Tap "Tapos na!" (Complete) when done

4. **Earn Rewards**
   - Get points for completion
   - Earn badges for special missions
   - See progress bar update

### For Teachers/Parents

1. **Enable/Disable Missions**
   - Go to Settings
   - Toggle "Enable Daily Missions"
   - Missions will be hidden if disabled

## Mission Examples

### Simple Mission (2 steps)
**"Check Soil"**
1. Touch the soil with your finger
2. Check if it feels dry

### AR Mission (3 steps)
**"Identify with AR"**
1. Open AR camera
2. Point camera to plant
3. Wait for identification

### Photo Mission (2 steps)
**"Take Plant Photo"**
1. Open camera
2. Take a photo of your plant

## Badge System

8 Badge Types:
- **First Mission**: Completed first mission
- **Daily Gardener**: Completed 7 daily missions
- **Soil Expert**: Completed 5 soil check missions
- **Photographer**: Completed 10 photo missions
- **AR Explorer**: Completed 5 AR missions
- **Growth Tracker**: Completed 10 growth records
- **Weekly Champion**: Completed all missions in a week
- **Plant Master**: Completed 50 missions total

## Technical Details

### Database Schema
- MissionEntity table
- Stores mission data locally
- Room database with Flow support

### Mission Status
- PENDING: Not started
- IN_PROGRESS: Started but not completed
- COMPLETED: Completed
- EXPIRED: Past due date

### Points System
- Simple missions: 10 points
- Medium missions: 15 points
- AR missions: 20-25 points

## Future Enhancements

1. **Sound Effects**
   - Add actual success sound files
   - Place in `app/src/main/res/raw/`

2. **Animations**
   - Confetti animation on completion
   - Badge reveal animation
   - Step completion animation

3. **Custom Icons**
   - Replace placeholder icons
   - Create custom mission icons

4. **Mission Scheduling**
   - Time-based missions
   - Weekly missions
   - Special event missions

5. **Social Features** (with parental consent)
   - Share completed missions
   - Compare progress with classmates

## Testing

### Test Cases

1. **Mission Generation**
   - [ ] Daily missions generate correctly
   - [ ] No duplicate missions
   - [ ] Missions reset daily

2. **Mission Completion**
   - [ ] Steps can be completed
   - [ ] Mission status updates
   - [ ] Points awarded
   - [ ] Badges earned

3. **AR Integration**
   - [ ] AR missions launch AR activities
   - [ ] Mission completes after AR task

4. **Settings**
   - [ ] Missions can be disabled
   - [ ] Disabled missions don't show
   - [ ] Settings persist

5. **Offline**
   - [ ] Missions work offline
   - [ ] Data saved locally
   - [ ] No internet required

## Integration with Existing Features

- **Plant Recognition**: AR missions use PlantRecognitionActivity
- **AR Features**: AR missions use ARActivity
- **Growth Tracking**: Photo missions use GrowthTrackingActivity
- **Settings**: Mission toggle in SettingsActivity
- **Main Menu**: Missions button added

## Notes

- All mission data stored locally (privacy-safe)
- No internet required
- Parent/teacher can disable anytime
- Simple 1-3 step missions only
- Child-friendly language (Tagalog + English)

---

**Status**: ✅ Complete and Ready for Testing

