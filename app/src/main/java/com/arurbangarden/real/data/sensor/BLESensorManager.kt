package com.arurbangarden.real.data.sensor

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.arurbangarden.real.data.model.BLESensorDevice
import com.arurbangarden.real.data.model.SensorData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

// Standard BLE GATT UUIDs for soil sensors
object SensorUUIDs {
    val SERVICE_SOIL_SENSOR = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_MOISTURE = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_PH = UUID.fromString("00002a6e-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_TEMPERATURE = UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb")
}

class BLESensorManager(private val context: Context) {
    
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    
    private var gatt: BluetoothGatt? = null
    private val _connectedDevice = MutableStateFlow<BLESensorDevice?>(null)
    val connectedDevice: StateFlow<BLESensorDevice?> = _connectedDevice
    
    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData
    
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectedDevice.value = null
                    _sensorData.value = null
                }
            }
        }
        
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                readSensorCharacteristics(gatt)
            }
        }
        
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                parseCharacteristic(characteristic)
            }
        }
        
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            parseCharacteristic(characteristic)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.S)
    fun connectToDevice(deviceAddress: String): Boolean {
        if (bluetoothAdapter == null) return false
        
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
        gatt = device.connectGatt(context, false, gattCallback)
        
        _connectedDevice.value = BLESensorDevice(
            name = device.name ?: "Unknown Sensor",
            address = deviceAddress,
            isConnected = false
        )
        
        return true
    }
    
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }
    
    private fun readSensorCharacteristics(gatt: BluetoothGatt) {
        val service = gatt.getService(SensorUUIDs.SERVICE_SOIL_SENSOR) ?: return
        
        // Read moisture
        val moistureChar = service.getCharacteristic(SensorUUIDs.CHARACTERISTIC_MOISTURE)
        moistureChar?.let { gatt.readCharacteristic(it) }
        
        // Read pH
        val phChar = service.getCharacteristic(SensorUUIDs.CHARACTERISTIC_PH)
        phChar?.let { gatt.readCharacteristic(it) }
        
        // Read temperature
        val tempChar = service.getCharacteristic(SensorUUIDs.CHARACTERISTIC_TEMPERATURE)
        tempChar?.let { gatt.readCharacteristic(it) }
        
        // Enable notifications
        enableNotifications(gatt, service)
    }
    
    private fun enableNotifications(gatt: BluetoothGatt, service: BluetoothGattService) {
        val characteristics = listOf(
            SensorUUIDs.CHARACTERISTIC_MOISTURE,
            SensorUUIDs.CHARACTERISTIC_PH,
            SensorUUIDs.CHARACTERISTIC_TEMPERATURE
        )
        
        characteristics.forEach { uuid ->
            val characteristic = service.getCharacteristic(uuid)
            characteristic?.let { char ->
                gatt.setCharacteristicNotification(char, true)
                val descriptor = char.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                )
                descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
        }
    }
    
    private fun parseCharacteristic(characteristic: BluetoothGattCharacteristic) {
        val value = characteristic.value
        if (value == null || value.isEmpty()) return
        
        val currentData = _sensorData.value ?: SensorData()
        val newData = when (characteristic.uuid) {
            SensorUUIDs.CHARACTERISTIC_MOISTURE -> {
                val moisture = value[0].toInt() and 0xFF
                currentData.copy(moisture = moisture)
            }
            SensorUUIDs.CHARACTERISTIC_PH -> {
                val ph = value[0].toFloat() / 10f
                currentData.copy(ph = ph)
            }
            SensorUUIDs.CHARACTERISTIC_TEMPERATURE -> {
                val temp = value[0].toInt().toFloat()
                currentData.copy(temperature = temp)
            }
            else -> currentData
        }
        
        _sensorData.value = newData
    }
    
    fun scanForDevices(): List<BluetoothDevice> {
        // In production, implement proper BLE scanning
        return emptyList()
    }
}

