"""
Training script for Plant Recognition Model
AR Urban Garden - Real

This script trains a MobileNetV3-based model for plant recognition.
Requires: TensorFlow 2.x, labeled plant images
"""

import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import numpy as np
import os
from pathlib import Path

# Configuration
IMAGE_SIZE = 224
BATCH_SIZE = 32
EPOCHS = 50
NUM_CLASSES = 17  # Update based on your plant database (currently 17 plants)
TRAIN_DIR = "data/train"
VAL_DIR = "data/val"
TEST_DIR = "data/test"
MODEL_OUTPUT = "models/plant_model.tflite"

# Plant classes (update based on your data)
PLANT_CLASSES = [
    "basil",
    "mint",
    "oregano",
    "lemongrass",
    "ginger",
    "tomato",
    "kangkong",
    "chili",
    "eggplant",
    "okra",
    "pechay",
    "lettuce",
    "papaya",
    "banana",
    "calamansi",
    "aloe_vera",
    "sansevieria"
]

def create_model():
    """Create MobileNetV3-based model for plant recognition"""
    
    # Load pre-trained MobileNetV3-Small
    base_model = keras.applications.MobileNetV3Small(
        input_shape=(IMAGE_SIZE, IMAGE_SIZE, 3),
        include_top=False,
        weights='imagenet',
        alpha=1.0,
        minimal_activation=False
    )
    
    # Freeze base model initially
    base_model.trainable = False
    
    # Build model
    model = keras.Sequential([
        base_model,
        layers.GlobalAveragePooling2D(),
        layers.Dropout(0.2),
        layers.Dense(512, activation='relu'),
        layers.Dropout(0.3),
        layers.Dense(NUM_CLASSES, activation='softmax', name='predictions')
    ])
    
    # Compile
    model.compile(
        optimizer=keras.optimizers.Adam(learning_rate=0.001),
        loss='categorical_crossentropy',
        metrics=['accuracy', 'top_3_accuracy']
    )
    
    return model

def create_data_generators():
    """Create data generators with augmentation"""
    
    # Data augmentation for training
    train_datagen = ImageDataGenerator(
        rescale=1./255,
        rotation_range=15,
        width_shift_range=0.2,
        height_shift_range=0.2,
        brightness_range=[0.8, 1.2],
        contrast_range=[0.8, 1.2],
        horizontal_flip=True,
        zoom_range=0.2,
        fill_mode='nearest'
    )
    
    # Validation/test: only rescale
    val_test_datagen = ImageDataGenerator(rescale=1./255)
    
    train_generator = train_datagen.flow_from_directory(
        TRAIN_DIR,
        target_size=(IMAGE_SIZE, IMAGE_SIZE),
        batch_size=BATCH_SIZE,
        class_mode='categorical',
        shuffle=True
    )
    
    val_generator = val_test_datagen.flow_from_directory(
        VAL_DIR,
        target_size=(IMAGE_SIZE, IMAGE_SIZE),
        batch_size=BATCH_SIZE,
        class_mode='categorical',
        shuffle=False
    )
    
    test_generator = val_test_datagen.flow_from_directory(
        TEST_DIR,
        target_size=(IMAGE_SIZE, IMAGE_SIZE),
        batch_size=BATCH_SIZE,
        class_mode='categorical',
        shuffle=False
    )
    
    return train_generator, val_generator, test_generator

def train_model():
    """Train the plant recognition model"""
    
    print("Creating model...")
    model = create_model()
    model.summary()
    
    print("Loading data generators...")
    train_gen, val_gen, test_gen = create_data_generators()
    
    # Callbacks
    callbacks = [
        keras.callbacks.EarlyStopping(
            monitor='val_accuracy',
            patience=10,
            restore_best_weights=True
        ),
        keras.callbacks.ModelCheckpoint(
            'models/plant_model_best.h5',
            monitor='val_accuracy',
            save_best_only=True
        ),
        keras.callbacks.ReduceLROnPlateau(
            monitor='val_loss',
            factor=0.5,
            patience=5,
            min_lr=1e-7
        )
    ]
    
    print("Training model...")
    history = model.fit(
        train_gen,
        epochs=EPOCHS,
        validation_data=val_gen,
        callbacks=callbacks,
        verbose=1
    )
    
    # Fine-tuning: unfreeze base model
    print("Fine-tuning...")
    model.layers[0].trainable = True
    model.compile(
        optimizer=keras.optimizers.Adam(learning_rate=0.0001),
        loss='categorical_crossentropy',
        metrics=['accuracy', 'top_3_accuracy']
    )
    
    history_fine = model.fit(
        train_gen,
        epochs=20,
        validation_data=val_gen,
        callbacks=callbacks,
        verbose=1
    )
    
    # Evaluate on test set
    print("Evaluating on test set...")
    test_results = model.evaluate(test_gen, verbose=1)
    print(f"Test Accuracy: {test_results[1]:.4f}")
    print(f"Test Top-3 Accuracy: {test_results[2]:.4f}")
    
    return model

def convert_to_tflite(model, quantize=True):
    """Convert Keras model to TensorFlow Lite"""
    
    print("Converting to TFLite...")
    
    if quantize:
        # INT8 Quantization
        converter = tf.lite.TFLiteConverter.from_keras_model(model)
        converter.optimizations = [tf.lite.Optimize.DEFAULT]
        
        # Representative dataset for quantization
        def representative_dataset():
            # Use validation data
            val_gen, _, _ = create_data_generators()
            for i in range(100):
                batch = val_gen.next()
                yield [tf.cast(batch[0], tf.float32)]
        
        converter.representative_dataset = representative_dataset
        converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS_INT8]
        converter.inference_input_type = tf.uint8
        converter.inference_output_type = tf.uint8
    else:
        # Float32 (no quantization)
        converter = tf.lite.TFLiteConverter.from_keras_model(model)
    
    tflite_model = converter.convert()
    
    # Save model
    os.makedirs(os.path.dirname(MODEL_OUTPUT), exist_ok=True)
    with open(MODEL_OUTPUT, 'wb') as f:
        f.write(tflite_model)
    
    # Get model size
    size_mb = len(tflite_model) / (1024 * 1024)
    print(f"Model saved to {MODEL_OUTPUT}")
    print(f"Model size: {size_mb:.2f} MB")
    
    return tflite_model

def main():
    """Main training pipeline"""
    
    print("=" * 50)
    print("Plant Recognition Model Training")
    print("AR Urban Garden - Real")
    print("=" * 50)
    
    # Check if data directories exist
    if not os.path.exists(TRAIN_DIR):
        print(f"ERROR: Training directory not found: {TRAIN_DIR}")
        print("Please organize your images in the following structure:")
        print("data/train/plant_name1/")
        print("data/train/plant_name2/")
        print("...")
        return
    
    # Train model
    model = train_model()
    
    # Convert to TFLite
    convert_to_tflite(model, quantize=True)
    
    print("\n" + "=" * 50)
    print("Training complete!")
    print(f"Model saved to: {MODEL_OUTPUT}")
    print("Copy this file to: app/src/main/assets/models/plant_model.tflite")
    print("=" * 50)

if __name__ == "__main__":
    main()

