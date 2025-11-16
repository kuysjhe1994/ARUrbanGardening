package com.arurbangarden.real.sensor

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

/**
 * Soil Moisture Sensor - BLE sensor for real soil moisture data
 * Uses Bluetooth LE to connect to soil moisture sensors
 * 
 * Note: This is a simplified implementation. In production, you would:
 * 1. Scan for BLE devices with specific service UUIDs
 * 2. Connect to the sensor device
 * 3. Read moisture characteristic values
 * 4. Convert raw values to percentage (0-100%)
 */
class SoilMoistureSensor(
    private val context: Context,
    private val onMoistureLevelChanged: (Float?) -> Unit
) {
    
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var isConnected = false
    
    // BLE Service and Characteristic UUIDs for soil moisture sensors
    private val SOIL_MOISTURE_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
    private val MOISTURE_CHARACTERISTIC_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
    
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt?,
            status: Int,
            newState: Int
        ) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnected = true
                // Discover services
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnected = false
            }
        }
        
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt?.getService(SOIL_MOISTURE_SERVICE_UUID)
                val characteristic = service?.getCharacteristic(MOISTURE_CHARACTERISTIC_UUID)
                
                // Enable notifications for moisture updates
                characteristic?.let {
                    gatt.setCharacteristicNotification(it, true)
                    
                    val descriptor = it.getDescriptor(
                        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                    )
                    descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
            }
        }
        
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            characteristic?.let {
                if (it.uuid == MOISTURE_CHARACTERISTIC_UUID) {
                    val moistureValue = readMoistureValue(it)
                    onMoistureLevelChanged(moistureValue)
                }
            }
        }
        
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristic?.let {
                    if (it.uuid == MOISTURE_CHARACTERISTIC_UUID) {
                        val moistureValue = readMoistureValue(it)
                        onMoistureLevelChanged(moistureValue)
                    }
                }
            }
        }
    }
    
    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }
    
    fun connect() {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            onMoistureLevelChanged(null) // Sensor not available
            return
        }
        
        // Start BLE scanning for soil moisture sensors
        startBLEScan()
    }
    
    private fun startBLEScan() {
        if (bluetoothAdapter == null) return
        
        // Scan for BLE devices with soil moisture service
        val scanner = bluetoothAdapter!!.bluetoothLeScanner
        
        val scanCallback = object : android.bluetooth.le.ScanCallback() {
            override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult) {
                val device = result.device
                val scanRecord = result.scanRecord
                
                // Check if device advertises soil moisture service
                val serviceUuids = scanRecord?.serviceUuids
                if (serviceUuids != null && serviceUuids.contains(SOIL_MOISTURE_SERVICE_UUID)) {
                    // Found soil moisture sensor - stop scanning and connect
                    scanner?.stopScan(this)
                    connectToDevice(device)
                }
            }
            
            override fun onScanFailed(errorCode: Int) {
                // Scan failed - sensor not found
                onMoistureLevelChanged(null)
            }
        }
        
        // Create scan filter for soil moisture service
        val scanFilter = android.bluetooth.le.ScanFilter.Builder()
            .setServiceUuid(android.os.ParcelUuid(SOIL_MOISTURE_SERVICE_UUID))
            .build()
        
        val scanSettings = android.bluetooth.le.ScanSettings.Builder()
            .setScanMode(android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        
        // Start scanning
        try {
            scanner?.startScan(listOf(scanFilter), scanSettings, scanCallback)
            
            // Stop scanning after 10 seconds if no device found
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                scanner?.stopScan(scanCallback)
                if (!isConnected) {
                    onMoistureLevelChanged(null) // No sensor found
                }
            }, 10000)
        } catch (e: Exception) {
            e.printStackTrace()
            onMoistureLevelChanged(null)
        }
    }
    
    private fun connectToDevice(device: BluetoothDevice) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
            } else {
                bluetoothGatt = device.connectGatt(context, false, gattCallback)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onMoistureLevelChanged(null)
        }
    }
    
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        isConnected = false
    }
    
    private fun readMoistureValue(characteristic: BluetoothGattCharacteristic): Float? {
        // Read moisture value from characteristic
        // Typical format: Raw sensor value (0-1024 or similar) or percentage (0-100)
        // Convert to percentage if needed
        
        val value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
        
        return value?.toFloat()?.coerceIn(0f, 100f) // Ensure 0-100% range
    }
    
    fun isConnected(): Boolean {
        return isConnected
    }
    
    fun isAvailable(): Boolean {
        return bluetoothAdapter != null && bluetoothAdapter!!.isEnabled
    }
}

