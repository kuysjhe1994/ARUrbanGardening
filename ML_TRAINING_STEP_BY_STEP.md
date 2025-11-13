# ML Training - Step-by-Step Guide (Tagalog)

## Paano Mag-train ng Plant Recognition Model

### Kailangan mo:
1. Computer na may Python 3.7 o mas bago
2. Internet connection
3. Plant images (mga larawan ng halaman)
4. Google Colab account (libre, para sa GPU) O kaya local computer

---

## OPTION 1: Gamit ang Google Colab (Mas Madali - Recommended)

### Step 1: Mag-setup ng Google Colab

1. **Buksan ang Google Colab**
   - Pumunta sa: https://colab.research.google.com/
   - Mag-login gamit ang Google account mo

2. **Gumawa ng bagong notebook**
   - Click "New Notebook"
   - I-rename mo sa "Plant_Recognition_Training"

### Step 2: I-upload ang Training Script

1. **I-copy ang training script**
   - Buksan ang `scripts/train_plant_model.py` sa project mo
   - I-copy ang lahat ng code

2. **I-paste sa Colab notebook**
   - I-paste sa first cell
   - O kaya i-download ang file at i-upload sa Colab

### Step 3: I-organize ang Plant Images

1. **Gumawa ng folder structure sa computer mo:**
   ```
   plant_images/
   ├── basil/
   │   ├── image1.jpg
   │   ├── image2.jpg
   │   └── ... (at least 500 images)
   ├── mint/
   │   ├── image1.jpg
   │   └── ... (at least 500 images)
   ├── tomato/
   │   └── ...
   ├── kangkong/
   │   └── ...
   └── chili/
       └── ...
   ```

2. **Kung saan makakakuha ng images:**
   - **Kumuha ng sariling photos**: Mas maganda kung ikaw mismo kumuha
   - **PlantNet**: https://plantnet.org/ (free plant images)
   - **iNaturalist**: https://www.inaturalist.org/ (real plant photos)
   - **Google Images**: Search "basil plant", "mint plant", etc.

3. **Requirements para sa images:**
   - Minimum 500 images per plant type
   - Dapat malinaw at naka-focus
   - Iba't-ibang lighting (umaga, tanghali, hapon)
   - Iba't-ibang angles (harap, gilid, taas)
   - Iba't-ibang growth stages (maliit, malaki, may bulaklak)

### Step 4: I-upload ang Images sa Colab

1. **Sa Colab notebook, i-run ang code na ito:**
   ```python
   from google.colab import files
   import zipfile
   import os
   
   # I-upload ang zip file ng images
   uploaded = files.upload()
   
   # I-extract
   for filename in uploaded.keys():
       with zipfile.ZipFile(filename, 'r') as zip_ref:
           zip_ref.extractall('data/raw')
   ```

2. **O kaya gamitin ang Google Drive:**
   ```python
   from google.colab import drive
   drive.mount('/content/drive')
   
   # I-copy ang images mula sa Drive
   !cp -r /content/drive/MyDrive/plant_images/* data/raw/
   ```

### Step 5: I-run ang Data Preparation

1. **I-run ang code na ito sa Colab:**
   ```python
   # I-run ang prepare_data.py
   !python scripts/prepare_data.py
   ```

2. **Check kung successful:**
   - Dapat may `data/train/`, `data/val/`, `data/test/` folders
   - Bawat folder dapat may subfolders per plant type

### Step 6: I-install ang Dependencies

1. **I-run sa Colab:**
   ```python
   !pip install tensorflow numpy pillow
   ```

### Step 7: I-run ang Training

1. **I-run ang training script:**
   ```python
   !python scripts/train_plant_model.py
   ```

2. **Maghintay** (maaaring tumagal ng 1-3 oras depende sa data)
   - Makikita mo ang progress
   - Accuracy scores
   - Model size

3. **Kapag tapos na:**
   - Dapat may `models/plant_model.tflite` file
   - I-download ang file

### Step 8: I-download ang Trained Model

1. **I-run sa Colab:**
   ```python
   from google.colab import files
   files.download('models/plant_model.tflite')
   ```

2. **I-save sa computer mo**

---

## OPTION 2: Gamit ang Local Computer

### Step 1: I-install ang Python

1. **I-download ang Python:**
   - Pumunta sa: https://www.python.org/downloads/
   - I-download ang latest version (3.8 o mas bago)
   - I-install (tick ang "Add Python to PATH")

2. **I-verify ang installation:**
   - Buksan ang Command Prompt (Windows) o Terminal (Mac/Linux)
   - I-type: `python --version`
   - Dapat may version number

### Step 2: I-install ang Dependencies

1. **Buksan ang Command Prompt/Terminal**
2. **Pumunta sa project folder:**
   ```bash
   cd ARUrbanGarden
   ```

3. **I-install ang packages:**
   ```bash
   pip install tensorflow numpy pillow
   ```

### Step 3: I-organize ang Images

1. **Gumawa ng folder structure:**
   ```
   ARUrbanGarden/
   └── data/
       └── raw/
           ├── basil/
           │   ├── image1.jpg
           │   └── ...
           ├── mint/
           │   └── ...
           └── ...
   ```

2. **I-lagay ang images sa tamang folders**

### Step 4: I-run ang Data Preparation

1. **Sa Command Prompt/Terminal:**
   ```bash
   python scripts/prepare_data.py
   ```

2. **Check kung may `data/train/`, `data/val/`, `data/test/` folders**

### Step 5: I-run ang Training

1. **I-run ang training:**
   ```bash
   python scripts/train_plant_model.py
   ```

2. **Maghintay** (maaaring tumagal ng ilang oras)

3. **Kapag tapos:**
   - Dapat may `models/plant_model.tflite` file

---

## Pagkatapos ng Training

### Step 1: I-copy ang Model sa App

1. **I-copy ang `plant_model.tflite` file**
2. **I-lagay sa:**
   ```
   app/src/main/assets/models/plant_model.tflite
   ```

3. **Kung wala pa ang `models` folder, gumawa:**
   - `app/src/main/assets/models/`

### Step 2: I-verify

1. **Check kung tama ang location:**
   ```
   app/src/main/assets/models/plant_model.tflite  ✅
   ```

2. **Check ang file size:**
   - Dapat 5-20 MB (kung quantized)
   - O kaya 20-50 MB (kung hindi quantized)

---

## Troubleshooting

### "No module named 'tensorflow'"
**Solution:**
```bash
pip install tensorflow
```

### "Out of memory"
**Solution:**
- Bawasan ang batch size sa script (change `BATCH_SIZE = 32` to `BATCH_SIZE = 16`)
- O kaya gumamit ng Google Colab (may free GPU)

### "No images found"
**Solution:**
- Check kung tama ang folder structure
- Check kung may images sa `data/raw/plant_name/` folders

### "Low accuracy (<80%)"
**Solution:**
- Magdagdag ng mas maraming images (aim for 1000+ per plant)
- Check kung malinaw ang images
- I-train ulit ng mas maraming epochs

### Model file wala
**Solution:**
- Check ang `models/` folder
- I-run ulit ang training
- Check kung may errors sa training

---

## Quick Checklist

- [ ] May Python installed
- [ ] May plant images (≥500 per type)
- [ ] Naka-organize ang images sa `data/raw/`
- [ ] Na-run ang `prepare_data.py`
- [ ] Na-run ang `train_plant_model.py`
- [ ] May `plant_model.tflite` file
- [ ] Na-copy ang model sa `app/src/main/assets/models/`

---

## Tips para sa Mas Magandang Model

1. **Mas maraming data = mas maganda**
   - Aim for 1000+ images per plant type

2. **Quality over quantity**
   - Mas maganda ang 500 malinaw na images kaysa 1000 blurry

3. **Diverse data**
   - Iba't-ibang lighting
   - Iba't-ibang angles
   - Iba't-ibang backgrounds

4. **Test regularly**
   - I-test ang model habang nagta-train
   - I-adjust kung kailangan

---

## Next Steps

Pagkatapos mag-train:
1. I-copy ang model sa app
2. I-build ang app
3. I-test sa device
4. I-collect ang feedback
5. I-retrain kung kailangan

Good luck sa training! 🌱

