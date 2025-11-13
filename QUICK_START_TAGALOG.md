# Quick Start Guide - Tagalog Version

## Mabilisang Simula sa AR Urban Garden App

### Para sa Developers

---

## STEP 1: I-setup ang Android Studio (5-10 minutes)

1. **I-download ang Android Studio**
   - https://developer.android.com/studio
   - I-install

2. **I-open ang project**
   - File > Open > Pumili ng `ARUrbanGarden` folder

3. **Maghintay ng Gradle Sync**
   - Hintayin hanggang matapos (first time: 5-10 min)

---

## STEP 2: I-lagay ang ML Model (2 minutes)

1. **Gumawa ng folder:**
   ```
   app/src/main/assets/models/
   ```

2. **I-lagay ang trained model:**
   - I-copy ang `plant_model.tflite`
   - I-paste sa `app/src/main/assets/models/`

**Note:** Kung wala pa, pwedeng i-skip. App will use fallback.

---

## STEP 3: I-add ang API Key (1 minute - Optional)

1. **Buksan:**
   ```
   app/src/main/res/values/api_keys.xml
   ```

2. **I-replace:**
   ```xml
   <string name="openweather_api_key">YOUR_ACTUAL_KEY</string>
   ```

**Note:** Kung wala, weather features lang ang hindi gagana.

---

## STEP 4: I-connect ang Device (2 minutes)

1. **I-enable ang Developer Options sa phone:**
   - Settings > About > Tap "Build number" 7 times

2. **I-enable ang USB Debugging:**
   - Settings > Developer options > USB debugging ON

3. **I-connect sa computer:**
   - I-connect ang USB cable
   - Allow USB debugging sa phone

---

## STEP 5: I-run ang App (1 minute)

1. **I-click ang green "Run" button sa Android Studio**

2. **Pumili ng device**

3. **Maghintay hanggang ma-install at ma-launch**

---

## ✅ Tapos na!

Dapat nakikita mo na ang app sa phone mo.

---

## Kung May Problema

### "Gradle sync failed"
→ File > Invalidate Caches / Restart

### "Device not found"
→ I-check ang USB debugging

### "App crashes"
→ I-check ang Logcat para sa errors

---

## Next Steps

1. I-test ang app
2. I-train ang ML model (see ML_TRAINING_STEP_BY_STEP.md)
3. I-customize ang features
4. I-deploy

---

## Para sa ML Training

Sundin ang: `ML_TRAINING_STEP_BY_STEP.md`

---

## Para sa Detailed Setup

Sundin ang: `APP_SETUP_STEP_BY_STEP.md`

---

Good luck! 🌱

