@echo off
REM Setup script for ML training environment (Windows)
REM Run this before training

echo ==========================================
echo AR Urban Garden - ML Training Setup
echo ==========================================
echo.

REM Check Python
echo Checking Python...
python --version >nul 2>&1
if %errorlevel% == 0 (
    python --version
    echo [OK] Python found
) else (
    echo [ERROR] Python not found. Please install Python 3.7+
    pause
    exit /b 1
)

REM Check pip
echo Checking pip...
python -m pip --version >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] pip found
) else (
    echo [ERROR] pip not found. Installing...
    python -m ensurepip --upgrade
)

REM Install dependencies
echo.
echo Installing dependencies...
python -m pip install tensorflow numpy pillow

REM Create directories
echo.
echo Creating directories...
if not exist "data\raw" mkdir data\raw
if not exist "data\train" mkdir data\train
if not exist "data\val" mkdir data\val
if not exist "data\test" mkdir data\test
if not exist "models" mkdir models

echo.
echo ==========================================
echo Setup complete!
echo ==========================================
echo.
echo Next steps:
echo 1. Organize your plant images in data\raw\
echo 2. Run: python scripts\prepare_data.py
echo 3. Run: python scripts\train_plant_model.py
echo.
pause

