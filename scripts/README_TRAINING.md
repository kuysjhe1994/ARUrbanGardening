# ML Model Training Guide

## Quick Start

### 1. Prepare Your Data

Organize your plant images in the following structure:

```
data/
└── raw/
    ├── basil/
    │   ├── image1.jpg
    │   ├── image2.jpg
    │   └── ...
    ├── mint/
    │   ├── image1.jpg
    │   └── ...
    └── ...
```

**Requirements**:
- Minimum 500 images per plant species
- Diverse lighting conditions
- Different angles and growth stages
- Good quality images (clear, in focus)

### 2. Organize Data into Train/Val/Test

Run the data preparation script:

```bash
python scripts/prepare_data.py
```

This will create:
```
data/
├── train/
│   ├── basil/
│   ├── mint/
│   └── ...
├── val/
│   ├── basil/
│   ├── mint/
│   └── ...
└── test/
    ├── basil/
    ├── mint/
    └── ...
```

### 3. Train the Model

```bash
python scripts/train_plant_model.py
```

**What it does**:
- Creates MobileNetV3-based model
- Trains on your data
- Validates on validation set
- Converts to TensorFlow Lite
- Saves model to `models/plant_model.tflite`

### 4. Deploy to App

Copy the trained model to your app:

```bash
cp models/plant_model.tflite app/src/main/assets/models/plant_model.tflite
```

## Requirements

Install Python dependencies:

```bash
pip install tensorflow numpy pillow
```

## Training Tips

1. **More Data = Better Accuracy**: Aim for 500+ images per class
2. **Data Quality Matters**: Remove blurry, unclear images
3. **Diversity**: Include different lighting, angles, backgrounds
4. **Augmentation**: Script includes automatic augmentation
5. **Patience**: Training can take hours depending on data size

## Expected Results

- **Top-1 Accuracy**: ≥80% (target)
- **Top-3 Accuracy**: ≥90% (target)
- **Model Size**: 5-20 MB (quantized)
- **Inference Time**: <500ms on mobile

## Troubleshooting

### "No images found"
- Check your data directory structure
- Ensure images are .jpg, .jpeg, or .png

### "Out of memory"
- Reduce batch size in script
- Use smaller image size
- Train on fewer classes first

### "Low accuracy"
- Add more training data
- Check data quality
- Try different model architecture
- Increase training epochs

## Next Steps

After training:
1. Test model on test set
2. Deploy to app
3. Test on real device
4. Collect feedback
5. Retrain with more data if needed

