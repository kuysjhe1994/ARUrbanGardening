"""
Data preparation script for plant recognition model
Organizes images into train/val/test splits
"""

import os
import shutil
from pathlib import Path
import random
from collections import defaultdict

# Configuration
SOURCE_DIR = "data/raw"  # Where your raw images are
OUTPUT_DIR = "data"
TRAIN_RATIO = 0.7
VAL_RATIO = 0.15
TEST_RATIO = 0.15

def organize_images():
    """Organize images into train/val/test directories"""
    
    # Create output directories
    for split in ['train', 'val', 'test']:
        os.makedirs(f"{OUTPUT_DIR}/{split}", exist_ok=True)
    
    # Get all plant folders
    if not os.path.exists(SOURCE_DIR):
        print(f"ERROR: Source directory not found: {SOURCE_DIR}")
        print("Please create this directory and organize images by plant name:")
        print("data/raw/basil/image1.jpg")
        print("data/raw/basil/image2.jpg")
        print("data/raw/mint/image1.jpg")
        print("...")
        return
    
    plant_folders = [f for f in os.listdir(SOURCE_DIR) 
                     if os.path.isdir(os.path.join(SOURCE_DIR, f))]
    
    if not plant_folders:
        print(f"No plant folders found in {SOURCE_DIR}")
        return
    
    print(f"Found {len(plant_folders)} plant types: {plant_folders}")
    
    # Process each plant type
    for plant_name in plant_folders:
        plant_path = os.path.join(SOURCE_DIR, plant_name)
        images = [f for f in os.listdir(plant_path) 
                  if f.lower().endswith(('.jpg', '.jpeg', '.png'))]
        
        if not images:
            print(f"Warning: No images found for {plant_name}")
            continue
        
        print(f"\nProcessing {plant_name}: {len(images)} images")
        
        # Shuffle images
        random.shuffle(images)
        
        # Calculate splits
        total = len(images)
        train_end = int(total * TRAIN_RATIO)
        val_end = train_end + int(total * VAL_RATIO)
        
        # Split images
        train_images = images[:train_end]
        val_images = images[train_end:val_end]
        test_images = images[val_end:]
        
        # Copy images to respective directories
        for split, img_list in [('train', train_images), 
                                ('val', val_images), 
                                ('test', test_images)]:
            split_dir = os.path.join(OUTPUT_DIR, split, plant_name)
            os.makedirs(split_dir, exist_ok=True)
            
            for img in img_list:
                src = os.path.join(plant_path, img)
                dst = os.path.join(split_dir, img)
                shutil.copy2(src, dst)
            
            print(f"  {split}: {len(img_list)} images")
    
    print("\n" + "=" * 50)
    print("Data organization complete!")
    print(f"Train/Val/Test splits created in: {OUTPUT_DIR}")
    print("=" * 50)

if __name__ == "__main__":
    random.seed(42)  # For reproducibility
    organize_images()

