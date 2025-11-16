# Reminders + Safety Mode Feature - Documentation

## Overview

The Reminders + Safety Mode feature provides child-safe reminders for plant care and enforces safety restrictions. All reminders use cute notification sounds, and safety mode blocks social sharing, location sharing, and data upload by default.

## Features Implemented

### ✅ Reminders System

1. **Reminder Types**
   - Watering Reminder: Daily at 9 AM
   - Sunlight Check Reminder: Daily at 12 PM
   - Weekly Photo Reminder: Every Monday at 10 AM

2. **Notification Features**
   - Cute notification sounds (system default, not loud/scary)
   - Kid-friendly messages (Tagalog + English)
   - Tap to open app
   - Auto-cancel after viewing

3. **Parent/Teacher Control**
   - Toggle to enable/disable reminders
   - Stored locally
   - No internet required

### ✅ Safety Mode

1. **Restrictions (Kid-Safe Mode)**
   - ❌ Social sharing blocked (by default)
   - ❌ Location sharing blocked (by default)
   - ❌ Data upload blocked (by default)
   - ✅ All data stored locally
   - ✅ No ads
   - ✅ No third-party tracking

2. **Parental Consent**
   - Full-screen consent activity
   - Explains camera usage
   - Explains AR usage
   - Safe use tips link
   - Agree/Disagree options

3. **PIN Protection**
   - 4-digit PIN for settings access
   - SHA-256 hashed storage
   - Optional (can be set by parent/teacher)
   - Required to access settings if set

4. **Safe Use Tips**
   - 6 kid-friendly tips
   - Simple language (Grade 5-6 level)
   - Bilingual (Tagalog + English)
   - Visual icons

## Files Created

### Data Models
- `app/src/main/java/com/arurbangarden/real/data/model/Reminder.kt`
- `app/src/main/java/com/arurbangarden/real/data/model/SafetyModeSettings.kt`

### Reminder System
- `app/src/main/java/com/arurbangarden/real/data/reminder/ReminderManager.kt`
- `app/src/main/java/com/arurbangarden/real/receiver/ReminderReceiver.kt`

### Safety Features
- `app/src/main/java/com/arurbangarden/real/ui/safety/ParentalConsentActivity.kt`
- `app/src/main/java/com/arurbangarden/real/ui/safety/SafeUseTipsActivity.kt`
- `app/src/main/java/com/arurbangarden/real/ui/safety/PINActivity.kt`
- `app/src/main/java/com/arurbangarden/real/util/SafetyMode.kt`

### Layouts
- `activity_parental_consent.xml`
- `activity_safe_use_tips.xml`
- `activity_pin.xml`
- `item_safe_tip.xml`

### Resources
- `ic_notification.xml` - Notification icon

## Usage

### For Students

1. **Receive Reminders**
   - Get daily watering reminders
   - Get daily sunlight check reminders
   - Get weekly photo reminders
   - Tap notification to open app

2. **View Safe Use Tips**
   - Go to Settings
   - Tap "Safe Use Tips"
   - Read tips for safe app usage

### For Teachers/Parents

1. **Enable/Disable Reminders**
   - Go to Settings
   - Toggle "Enable Reminders"
   - Reminders will be scheduled/cancelled

2. **Set PIN**
   - Go to Settings
   - Tap "Set PIN for Settings"
   - Enter 4-digit PIN
   - Settings will require PIN to access

3. **Parental Consent**
   - Shown during onboarding
   - Explains camera and AR usage
   - Can read safe use tips
   - Agree or disagree

## Safety Features

### Default Restrictions
- Social sharing: ❌ Blocked
- Location sharing: ❌ Blocked
- Data upload: ❌ Blocked
- Ads: ❌ Blocked
- Tracking: ❌ Blocked

### Parental Override
- Can enable sharing with consent
- Can enable location with consent
- Can enable upload with consent
- All require explicit consent

## Reminder Schedule

### Daily Reminders
- **Watering**: 9:00 AM (Daily)
- **Sunlight Check**: 12:00 PM (Daily)

### Weekly Reminders
- **Photo**: 10:00 AM (Monday only)

## Safe Use Tips

1. **Use Carefully** - Don't run while using phone
2. **Ask Adults** - Ask teacher/parent if confused
3. **Privacy** - Don't take photos without permission
4. **Safe Data** - All data saved on device only
5. **Parent/Teacher** - Permission needed for some features
6. **Time** - Don't use phone too long, take breaks

## Technical Details

### Reminder System
- Uses AlarmManager for scheduling
- Exact alarms for reliability
- BroadcastReceiver for notifications
- Local storage only

### PIN System
- SHA-256 hashing
- 4-digit PIN
- Stored in SharedPreferences
- Required for settings access

### Safety Mode
- Enforced at app level
- Checks before any sharing/upload
- No third-party SDKs
- No ads or tracking

## Integration

- Integrated with onboarding flow
- Integrated with settings
- Integrated with all sharing features
- Respects parental consent

## Privacy & Safety

- **No Cloud by Default**: All data local
- **No Tracking**: No third-party analytics
- **No Ads**: Ad-free experience
- **Parental Control**: PIN protection
- **Consent Required**: For sensitive features

## Testing

### Test Cases

1. **Reminders**
   - [ ] Reminders schedule correctly
   - [ ] Notifications appear on time
   - [ ] Toggle enables/disables reminders
   - [ ] Notifications are kid-friendly

2. **Safety Mode**
   - [ ] Social sharing blocked by default
   - [ ] Location sharing blocked by default
   - [ ] Data upload blocked by default
   - [ ] Can be overridden with consent

3. **PIN Protection**
   - [ ] PIN can be set
   - [ ] Settings require PIN if set
   - [ ] PIN is hashed securely
   - [ ] Wrong PIN shows error

4. **Parental Consent**
   - [ ] Shown during onboarding
   - [ ] Explains camera/AR usage
   - [ ] Can read safe use tips
   - [ ] Consent saved correctly

5. **Safe Use Tips**
   - [ ] Tips display correctly
   - [ ] Bilingual support
   - [ ] Easy to read
   - [ ] Kid-friendly language

---

**Status**: ✅ Complete and Ready for Testing

