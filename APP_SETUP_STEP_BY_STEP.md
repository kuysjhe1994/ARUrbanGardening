# App Setup at Pag-run - Step-by-Step Guide (Tagalog)

## Paano I-setup at I-run ang AR Urban Garden App

### Kailangan mo:
1. Android Studio (latest version)
2. ARCore-compatible Android device O emulator
3. Internet connection
4. Android SDK installed

---

## PART 1: I-setup ang Android Studio

### Step 1: I-download ang Android Studio

1. **Pumunta sa:**
   - https://developer.android.com/studio
   - O kaya: https://developer.android.com/studio/index.html

2. **I-download ang Android Studio**
   - Para sa Windows: `.exe` file
   - Para sa Mac: `.dmg` file
   - Para sa Linux: `.tar.gz` file

3. **I-install:**
   - I-run ang installer
   - Sundin ang instructions
   - I-install ang Android SDK (automatic)

### Step 2: I-open ang Project

1. **Buksan ang Android Studio**

2. **I-open ang project:**
   - Click "Open" o "File > Open"
   - Pumili ng `ARUrbanGarden` folder
   - Click "OK"

3. **Maghintay ng Gradle Sync**
   - Makikita mo sa baba: "Gradle sync in progress..."
   - Maaaring tumagal ng 5-10 minutes (first time)
   - Hintayin hanggang matapos

### Step 3: I-check ang Dependencies

1. **Buksan ang `app/build.gradle`**
   - Dapat may lahat ng dependencies
   - Kung may error, i-click ang "Sync Now"

---

## PART 2: I-setup ang ML Models

### Step 1: Gumawa ng Models Folder

1. **Sa Android Studio:**
   - Right-click sa `app/src/main/assets/`
   - Click "New > Directory"
   - I-type: `models`
   - Press Enter

2. **O kaya manually:**
   - Pumunta sa: `app/src/main/assets/`
   - Gumawa ng folder na `models`

### Step 2: I-lagay ang Trained Model

1. **Kung may trained model ka na:**
   - I-copy ang `plant_model.tflite` file
   - I-paste sa `app/src/main/assets/models/`
   - Dapat: `app/src/main/assets/models/plant_model.tflite`

2. **Kung wala pa:**
   - Pwedeng i-skip muna (app will use fallback)
   - O kaya mag-train muna (see ML_TRAINING_STEP_BY_STEP.md)

### Step 3: I-verify

1. **Check kung tama:**
   ```
   app/src/main/assets/models/plant_model.tflite  ✅
   ```

---

## PART 3: I-setup ang API Key (Optional)

### Step 1: Kumuha ng OpenWeatherMap API Key

1. **Pumunta sa:**
   - https://openweathermap.org/api

2. **Mag-sign up:**
   - Click "Sign Up" (libre)
   - Fill up ang form
   - I-verify ang email

3. **Kumuha ng API key:**
   - Pagkatapos mag-login, pumunta sa "API keys"
   - I-copy ang default key
   - O kaya gumawa ng bagong key

### Step 2: I-lagay ang API Key sa App

1. **Buksan ang:**
   - `app/src/main/res/values/api_keys.xml`

2. **I-replace ang:**
   ```xml
   <string name="openweather_api_key">YOUR_OPENWEATHER_API_KEY_HERE</string>
   ```
   
   **Sa:**
   ```xml
   <string name="openweather_api_key">YOUR_ACTUAL_API_KEY</string>
   ```

3. **I-save ang file**

**Note:** Kung wala pa, pwedeng i-skip. Weather features lang ang hindi gagana.

---

## PART 4: I-setup ang Device/Emulator

### OPTION A: Gamit ang Real Device (Recommended para sa AR)

### Step 1: I-enable ang Developer Options

1. **Sa Android device mo:**
   - Pumunta sa "Settings"
   - Tap "About phone"
   - Tap "Build number" 7 times
   - May message: "You are now a developer!"

### Step 2: I-enable ang USB Debugging

1. **Sa Settings:**
   - Pumunta sa "System" > "Developer options"
   - I-enable ang "USB debugging"
   - I-enable ang "Install via USB" (kung available)

### Step 3: I-connect ang Device

1. **I-connect ang device sa computer gamit ang USB cable**

2. **Sa device:**
   - May popup: "Allow USB debugging?"
   - Click "Allow" o "OK"
   - Tick "Always allow from this computer"
   - Click "OK"

3. **I-verify sa Android Studio:**
   - Sa baba, may device selector
   - Dapat makita ang device mo
   - Status: "Online"

### Step 4: I-check kung ARCore Compatible

1. **I-check ang list:**
   - https://developers.google.com/ar/discover/supported-devices

2. **Kung compatible:**
   - ✅ Pwede gamitin ang AR features

3. **Kung hindi:**
   - ⚠️ Plant recognition lang ang gagana
   - AR features hindi gagana

---

### OPTION B: Gamit ang Emulator (Limited AR Support)

### Step 1: Gumawa ng Virtual Device

1. **Sa Android Studio:**
   - Click "Tools" > "Device Manager"
   - Click "Create Device"

2. **Pumili ng device:**
   - Pumili ng phone (e.g., Pixel 5)
   - Click "Next"

3. **Pumili ng System Image:**
   - Pumili ng API level 24 o mas bago
   - I-download kung kailangan
   - Click "Next"

4. **I-finish:**
   - I-verify ang settings
   - Click "Finish"

### Step 2: I-run ang Emulator

1. **Sa Device Manager:**
   - Click ang play button sa device
   - Maghintay hanggang mag-boot

**Note:** AR features limited sa emulator. Mas maganda kung real device.

---

## PART 5: I-build at I-run ang App

### Step 1: I-build ang App

1. **Sa Android Studio:**
   - Click "Build" > "Make Project"
   - O kaya press `Ctrl+F9` (Windows) o `Cmd+F9` (Mac)
   - Maghintay hanggang matapos

2. **I-check kung may errors:**
   - Kung may errors, i-fix muna
   - Common errors:
     - Missing dependencies → I-sync ang Gradle
     - Syntax errors → I-check ang code

### Step 2: I-run ang App

1. **I-select ang device:**
   - Sa taas, may device selector
   - Pumili ng device (real o emulator)

2. **I-run:**
   - Click ang green "Run" button
   - O kaya press `Shift+F10` (Windows) o `Ctrl+R` (Mac)

3. **Maghintay:**
   - I-compile ang app
   - I-install sa device
   - I-launch ang app

### Step 3: I-test ang App

1. **Onboarding:**
   - Dapat may welcome screen
   - I-grant ang permissions
   - I-complete ang onboarding

2. **Main Menu:**
   - Dapat may 5 buttons:
     - Identify Plant
     - Place Garden
     - Measure
     - Track Growth
     - Settings

3. **I-test ang features:**
   - Try "Identify Plant"
   - Try "Place Garden" (kung ARCore compatible)
   - Try "Measure" (kung ARCore compatible)

---

## PART 6: Troubleshooting

### "Gradle sync failed"

**Solution:**
1. Click "File" > "Invalidate Caches / Restart"
2. Click "Invalidate and Restart"
3. Hintayin mag-restart
4. I-sync ulit

### "Device not found"

**Solution:**
1. I-check kung naka-connect ang device
2. I-check kung naka-enable ang USB debugging
3. I-try i-disconnect at i-connect ulit
4. I-restart ang ADB: `adb kill-server` then `adb start-server`

### "ARCore not available"

**Solution:**
1. I-check kung ARCore compatible ang device
2. I-install ang ARCore app mula sa Play Store
3. I-update ang ARCore app

### "App crashes on launch"

**Solution:**
1. I-check ang Logcat sa Android Studio
2. I-look for error messages
3. Common causes:
   - Missing permissions
   - Missing model file
   - API errors

### "Plant not recognized"

**Solution:**
1. I-check kung may `plant_model.tflite` sa `app/src/main/assets/models/`
2. I-check kung tama ang model file
3. I-try ulit sa ibang plant
4. I-check kung malinaw ang lighting

### "Build failed"

**Solution:**
1. I-check ang error message
2. Common fixes:
   - I-sync ang Gradle: "File" > "Sync Project with Gradle Files"
   - I-clean ang project: "Build" > "Clean Project"
   - I-rebuild: "Build" > "Rebuild Project"

---

## Quick Checklist

### Before Running:
- [ ] Android Studio installed
- [ ] Project opened
- [ ] Gradle sync successful
- [ ] Device connected (real) o emulator running
- [ ] ML model placed (optional)
- [ ] API key added (optional)

### First Run:
- [ ] App installed successfully
- [ ] Onboarding completed
- [ ] Permissions granted
- [ ] Main menu displayed
- [ ] Features working

---

## Next Steps

Pagkatapos ma-run ang app:

1. **I-test ang lahat ng features**
2. **I-collect ang feedback**
3. **I-fix ang bugs**
4. **I-improve ang UI/UX**
5. **I-deploy sa Play Store** (kung ready na)

---

## Tips

1. **Gamitin ang real device** para sa AR features
2. **I-test regularly** habang nagde-develop
3. **I-check ang Logcat** para sa errors
4. **I-backup ang project** regularly
5. **I-update ang dependencies** regularly

Good luck! 🚀

