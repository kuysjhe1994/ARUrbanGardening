#!/bin/bash
# Setup script for ML training environment
# Run this before training

echo "=========================================="
echo "AR Urban Garden - ML Training Setup"
echo "=========================================="
echo ""

# Check Python
echo "Checking Python..."
if command -v python3 &> /dev/null; then
    PYTHON_VERSION=$(python3 --version)
    echo "✅ Python found: $PYTHON_VERSION"
else
    echo "❌ Python not found. Please install Python 3.7+"
    exit 1
fi

# Check pip
echo "Checking pip..."
if command -v pip3 &> /dev/null; then
    echo "✅ pip found"
else
    echo "❌ pip not found. Installing..."
    python3 -m ensurepip --upgrade
fi

# Install dependencies
echo ""
echo "Installing dependencies..."
pip3 install tensorflow numpy pillow

# Create directories
echo ""
echo "Creating directories..."
mkdir -p data/raw
mkdir -p data/train
mkdir -p data/val
mkdir -p data/test
mkdir -p models

echo ""
echo "=========================================="
echo "Setup complete!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. Organize your plant images in data/raw/"
echo "2. Run: python3 scripts/prepare_data.py"
echo "3. Run: python3 scripts/train_plant_model.py"
echo ""

