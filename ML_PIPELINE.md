# ML Model Pipeline for AR Urban Garden

## Overview

This document describes the process for creating and deploying on-device ML models for plant recognition and pest/disease detection.

## 1. Plant Recognition Model

### Data Collection

**Target Plants** (Philippine urban gardening):
- Basil (Bawang)
- Mint (Mentha)
- Tomato (Kamatis)
- Water Spinach (Kangkong)
- Chili Pepper (Sili)
- (Add more as needed)

**Requirements per plant**:
- Minimum 500 images per species
- Diverse conditions:
  - Different lighting (morning, noon, evening)
  - Different angles (top, side, close-up)
  - Different growth stages (seedling, mature, flowering)
  - Different backgrounds
  - Different weather conditions

**Data Sources**:
1. Public datasets:
   - PlantNet (https://plantnet.org/)
   - iNaturalist (https://www.inaturalist.org/)
   - Custom collection in target region

2. Local collection:
   - Take photos in actual urban garden settings
   - Include common variations and edge cases

### Data Preprocessing

1. **Image Resizing**: Resize to 224x224 (standard for mobile models)
2. **Data Augmentation**:
   - Rotation (±15 degrees)
   - Brightness adjustment (±20%)
   - Contrast adjustment (±20%)
   - Horizontal flip
   - Zoom (0.8x - 1.2x)
3. **Normalization**: Normalize pixel values to [0, 1]

### Model Architecture

**Recommended**: MobileNetV3 or EfficientNet-Lite

**MobileNetV3-Small**:
- Input: 224x224x3
- Output: [num_classes] logits
- Size: ~5-10 MB (quantized)
- Speed: <100ms inference on mobile

**Training**:
```python
import tensorflow as tf
from tensorflow import keras

# Load pre-trained MobileNetV3
base_model = keras.applications.MobileNetV3Small(
    input_shape=(224, 224, 3),
    include_top=False,
    weights='imagenet'
)

# Add custom classification head
model = keras.Sequential([
    base_model,
    keras.layers.GlobalAveragePooling2D(),
    keras.layers.Dropout(0.2),
    keras.layers.Dense(num_classes, activation='softmax')
])

# Compile
model.compile(
    optimizer='adam',
    loss='categorical_crossentropy',
    metrics=['accuracy']
)

# Train
model.fit(
    train_dataset,
    validation_data=val_dataset,
    epochs=50,
    callbacks=[early_stopping, model_checkpoint]
)
```

### Model Conversion to TensorFlow Lite

```python
# Convert to TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)

# Quantize to INT8 (reduces size, may slightly reduce accuracy)
converter.optimizations = [tf.lite.Optimize.DEFAULT]

# Representative dataset for quantization
def representative_dataset():
    for image, _ in train_dataset.take(100):
        yield [tf.cast(image, tf.float32)]

converter.representative_dataset = representative_dataset
converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS_INT8]
converter.inference_input_type = tf.uint8
converter.inference_output_type = tf.uint8

# Convert
tflite_model = converter.convert()

# Save
with open('plant_model.tflite', 'wb') as f:
    f.write(tflite_model)
```

### Model Evaluation

**Test Set**: 20% of collected data (held out)

**Metrics**:
- Top-1 Accuracy: Target ≥80%
- Top-3 Accuracy: Target ≥90%
- Per-class accuracy
- Confusion matrix

**On-device Testing**:
- Inference time: <500ms
- Model size: ≤20 MB
- Memory usage: <100 MB

### Deployment

1. Place model in `app/src/main/assets/models/plant_model.tflite`
2. Update `PlantRecognitionModel.kt` if input/output format changes
3. Test on real devices

## 2. Pest/Disease Detection Model

### Data Collection

**Common Issues**:
- Aphids
- Fungal diseases (powdery mildew, leaf spot)
- Nutrient deficiencies
- Overwatering/underwatering symptoms
- (Add more based on local conditions)

**Requirements**:
- Minimum 300 images per issue type
- Close-up photos of affected leaves/stems
- Include healthy plants for comparison

### Model Architecture

Similar to plant recognition but focused on:
- Binary classification: Problem vs. Healthy
- Multi-class: Specific problem types

**Architecture**: MobileNetV3 or custom CNN

### Training & Deployment

Follow similar process as plant recognition model.

## 3. Model Updates

### Versioning
- Version models (e.g., `plant_model_v1.tflite`)
- Track accuracy improvements
- A/B test new models

### Update Pipeline
1. Train new model
2. Evaluate on test set
3. Test on-device performance
4. Deploy via app update or OTA (if cloud sync enabled)

## 4. Privacy-Preserving Training

### Federated Learning (Future)
- Allow teachers/students to contribute labeled data
- Train models without sharing raw images
- Aggregate model updates

### On-Device Fine-tuning (Future)
- Allow users to fine-tune models with their own plants
- Keep data on device

## 5. Tools & Resources

### Training Tools
- TensorFlow/Keras
- Google Colab (free GPU)
- TensorBoard (visualization)

### Datasets
- PlantNet API
- iNaturalist API
- Custom collection tools

### Evaluation Tools
- TensorFlow Lite Benchmark Tool
- Android Profiler
- Custom test scripts

## 6. Troubleshooting

### Low Accuracy
- Collect more diverse data
- Increase data augmentation
- Try different architectures
- Check for class imbalance

### Slow Inference
- Reduce model size (fewer layers)
- Use quantization
- Enable GPU acceleration (if available)
- Optimize input preprocessing

### Large Model Size
- Use quantization (INT8)
- Prune unnecessary layers
- Use smaller architecture (MobileNetV3-Small)

## 7. Best Practices

1. **Data Quality > Quantity**: Better to have 500 high-quality images than 1000 poor ones
2. **Test Early**: Test on-device early in development
3. **Monitor Performance**: Track accuracy and inference time
4. **Version Control**: Keep track of model versions and improvements
5. **Documentation**: Document data sources, training parameters, results

## 8. Example Training Script

See `scripts/train_plant_model.py` (to be created) for a complete training example.

