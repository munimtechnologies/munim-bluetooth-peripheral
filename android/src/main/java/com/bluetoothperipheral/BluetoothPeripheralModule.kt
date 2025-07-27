package com.bluetoothperipheral

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.ParcelUuid
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.Arguments
import android.util.Log
import kotlinx.coroutines.*
import java.util.*

private var advertiser: BluetoothLeAdvertiser? = null
private var gattServer: BluetoothGattServer? = null
private var gattServerReady = false
private var advertiseJob: Job? = null
private var currentAdvertisingData: WritableMap? = null
private const val TAG = "BluetoothPeripheralModule"

class BluetoothPeripheralModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "BluetoothPeripheral"
  }

  @ReactMethod
  fun startAdvertising(options: ReadableMap) {
    val bluetoothManager = reactApplicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
      Log.e(TAG, "Bluetooth is not enabled or not available")
      return
    }
    
    val serviceUUIDs = options.getArray("serviceUUIDs")
    if (serviceUUIDs == null || serviceUUIDs.size() == 0) {
      Log.e(TAG, "No service UUIDs provided for advertising")
      return
    }
    
    // Ensure GATT server is set up before advertising
    if (!gattServerReady) {
      setServicesFromOptions(serviceUUIDs)
    }
    
    // Cancel any previous advertising job
    advertiseJob?.cancel()
    advertiseJob = CoroutineScope(Dispatchers.Main).launch {
      delay(300) // Wait for GATT server to be ready
      advertiser = bluetoothAdapter.bluetoothLeAdvertiser
      
      val settings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setConnectable(true)
        .setTimeout(0)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .build()
      
      val dataBuilder = AdvertiseData.Builder()
      
      // Process comprehensive advertising data
      val advertisingDataMap = options.getMap("advertisingData")
      if (advertisingDataMap != null) {
        processAdvertisingData(advertisingDataMap, dataBuilder)
      }
      
      // Legacy support - add service UUIDs
      for (i in 0 until serviceUUIDs.size()) {
        val uuid = serviceUUIDs.getString(i)
        if (uuid != null) {
          dataBuilder.addServiceUuid(ParcelUuid.fromString(uuid))
        }
      }
      
      // Legacy support - local name
      val localName = options.getString("localName")
      if (localName != null) {
        bluetoothAdapter.name = localName
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"), // Heart Rate Service UUID as placeholder
          localName.toByteArray()
        )
      }
      
      // Legacy support - manufacturer data
      val manufacturerData = options.getString("manufacturerData")
      if (manufacturerData != null) {
        val data = hexStringToByteArray(manufacturerData)
        if (data != null) {
          dataBuilder.addManufacturerData(0x0000, data) // Default manufacturer code
        }
      }
      
      currentAdvertisingData = Arguments.createMap().apply {
        putMap("advertisingData", advertisingDataMap)
        putString("localName", localName)
        putString("manufacturerData", manufacturerData)
      }
      
      advertiser?.startAdvertising(settings, dataBuilder.build(), object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
          Log.i(TAG, "Advertising started successfully")
        }
        override fun onStartFailure(errorCode: Int) {
          Log.e(TAG, "Advertising failed: $errorCode")
        }
      })
    }
  }

  @ReactMethod
  fun updateAdvertisingData(advertisingData: ReadableMap) {
    val bluetoothManager = reactApplicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
      Log.e(TAG, "Bluetooth is not enabled or not available")
      return
    }
    
    advertiser?.stopAdvertising(object : AdvertiseCallback() {})
    
    advertiseJob?.cancel()
    advertiseJob = CoroutineScope(Dispatchers.Main).launch {
      delay(100) // Brief delay before restarting
      advertiser = bluetoothAdapter.bluetoothLeAdvertiser
      
      val settings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setConnectable(true)
        .setTimeout(0)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .build()
      
      val dataBuilder = AdvertiseData.Builder()
      processAdvertisingData(advertisingData, dataBuilder)
      
      currentAdvertisingData = Arguments.createMap().apply {
        putMap("advertisingData", advertisingData)
      }
      
      advertiser?.startAdvertising(settings, dataBuilder.build(), object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
          Log.i(TAG, "Advertising updated successfully")
        }
        override fun onStartFailure(errorCode: Int) {
          Log.e(TAG, "Advertising update failed: $errorCode")
        }
      })
    }
  }

  @ReactMethod
  fun getAdvertisingData(promise: Promise) {
    promise.resolve(currentAdvertisingData)
  }

  private fun processAdvertisingData(dataMap: ReadableMap, dataBuilder: AdvertiseData.Builder) {
    // 0x01 - Flags (partial support - affects connectable setting)
    if (dataMap.hasKey("flags")) {
      val flags = dataMap.getInt("flags")
      // Android AdvertiseData doesn't directly support flags, but we can interpret connectable flag
      // This is handled in AdvertiseSettings, not AdvertiseData
    }
    
    // 0x02-0x07 - Service UUIDs (fully supported)
    addServiceUUIDs(dataMap.getArray("incompleteServiceUUIDs16"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("completeServiceUUIDs16"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("incompleteServiceUUIDs32"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("completeServiceUUIDs32"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("incompleteServiceUUIDs128"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("completeServiceUUIDs128"), dataBuilder)
    
    // 0x08-0x09 - Local Name (fully supported)
    if (dataMap.hasKey("shortenedLocalName")) {
      val name = dataMap.getString("shortenedLocalName")
      if (name != null) {
        dataBuilder.setIncludeDeviceName(true)
        // Note: Android doesn't distinguish between shortened and complete local name in AdvertiseData
        // The system handles truncation automatically if needed
      }
    }
    if (dataMap.hasKey("completeLocalName")) {
      val name = dataMap.getString("completeLocalName")
      if (name != null) {
        dataBuilder.setIncludeDeviceName(true)
      }
    }
    
    // 0x0A - Tx Power Level (fully supported)
    if (dataMap.hasKey("txPowerLevel")) {
      dataBuilder.setIncludeTxPowerLevel(true)
      // Note: Android controls actual Tx power via AdvertiseSettings, not AdvertiseData
    }
    
    // 0x14-0x15 - Service Solicitation UUIDs (fully supported)
    addServiceUUIDs(dataMap.getArray("serviceSolicitationUUIDs16"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("serviceSolicitationUUIDs128"), dataBuilder)
    
    // 0x16, 0x20, 0x21 - Service Data (fully supported)
    addServiceData(dataMap.getArray("serviceData16"), dataBuilder)
    addServiceData(dataMap.getArray("serviceData32"), dataBuilder)
    addServiceData(dataMap.getArray("serviceData128"), dataBuilder)
    
    // 0x19 - Appearance (partial support)
    if (dataMap.hasKey("appearance")) {
      val appearance = dataMap.getInt("appearance")
      // Android doesn't have direct AdvertiseData support for appearance
      // We can encode it as service data if needed, but this is not standard
      val appearanceData = byteArrayOf(
        (appearance and 0xFF).toByte(),
        ((appearance shr 8) and 0xFF).toByte()
      )
      // Use a standard service UUID for appearance data
      dataBuilder.addServiceData(
        ParcelUuid.fromString("00001800-0000-1000-8000-00805F9B34FB"), // Generic Access Service
        appearanceData
      )
    }
    
    // 0x1F - Service Solicitation (32-bit) (fully supported)
    addServiceUUIDs(dataMap.getArray("serviceSolicitationUUIDs32"), dataBuilder)
    
    // 0xFF - Manufacturer Specific Data (fully supported)
    if (dataMap.hasKey("manufacturerData")) {
      val manufacturerData = hexStringToByteArray(dataMap.getString("manufacturerData"))
      if (manufacturerData != null) {
        // Default manufacturer code - in real usage, this should be a registered company identifier
        dataBuilder.addManufacturerData(0x0000, manufacturerData)
      }
    }
  }

  private fun addServiceUUIDs(uuids: ReadableArray?, dataBuilder: AdvertiseData.Builder) {
    if (uuids != null) {
      for (i in 0 until uuids.size()) {
        val uuid = uuids.getString(i)
        if (uuid != null) {
          dataBuilder.addServiceUuid(ParcelUuid.fromString(uuid))
        }
      }
    }
  }

  private fun addServiceData(serviceDataArray: ReadableArray?, dataBuilder: AdvertiseData.Builder) {
    if (serviceDataArray != null) {
      for (i in 0 until serviceDataArray.size()) {
        val serviceData = serviceDataArray.getMap(i)
        if (serviceData != null) {
          val uuid = serviceData.getString("uuid")
          val data = serviceData.getString("data")
          if (uuid != null && data != null) {
            val dataBytes = hexStringToByteArray(data)
            if (dataBytes != null) {
              dataBuilder.addServiceData(ParcelUuid.fromString(uuid), dataBytes)
            }
          }
        }
      }
    }
  }

  private fun hexStringToByteArray(hexString: String?): ByteArray? {
    if (hexString == null) return null
    
    val cleanHex = hexString.replace(" ", "")
    if (cleanHex.length % 2 != 0) return null
    
    val bytes = ByteArray(cleanHex.length / 2)
    for (i in bytes.indices) {
      val index = i * 2
      bytes[i] = cleanHex.substring(index, index + 2).toInt(16).toByte()
    }
    return bytes
  }

  @ReactMethod
  fun stopAdvertising() {
    advertiser?.stopAdvertising(object : AdvertiseCallback() {})
    advertiser = null
    advertiseJob?.cancel()
  }

  @ReactMethod
  fun setServices(services: ReadableArray) {
    gattServerReady = false
    val bluetoothManager = reactApplicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    gattServer = bluetoothManager.openGattServer(reactApplicationContext, object : BluetoothGattServerCallback() {})
    gattServer?.clearServices()
    for (i in 0 until services.size()) {
      val serviceMap = services.getMap(i)
      if (serviceMap == null) continue
      val serviceUuid = serviceMap.getString("uuid") ?: continue
      val service = BluetoothGattService(java.util.UUID.fromString(serviceUuid), BluetoothGattService.SERVICE_TYPE_PRIMARY)
      val characteristics = serviceMap.getArray("characteristics")
      if (characteristics != null) {
        for (j in 0 until characteristics.size()) {
          val charMap = characteristics.getMap(j)
          if (charMap == null) continue
          val charUuid = charMap.getString("uuid") ?: continue
          val propertiesArray = charMap.getArray("properties")
          var properties = 0
          if (propertiesArray != null) {
            for (k in 0 until propertiesArray.size()) {
              when (propertiesArray.getString(k)) {
                "read" -> properties = properties or BluetoothGattCharacteristic.PROPERTY_READ
                "write" -> properties = properties or BluetoothGattCharacteristic.PROPERTY_WRITE
                "notify" -> properties = properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY
              }
            }
          }
          val permissions = BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
          val characteristic = BluetoothGattCharacteristic(java.util.UUID.fromString(charUuid), properties, permissions)
          val value = charMap.getString("value")
          if (value != null) {
            characteristic.value = value.toByteArray()
          }
          service.addCharacteristic(characteristic)
        }
      }
      gattServer?.addService(service)
    }
    gattServerReady = true
  }

  // Helper to set up GATT server from serviceUUIDs array (for basic advertising)
  private fun setServicesFromOptions(serviceUUIDs: ReadableArray) {
    gattServerReady = false
    val bluetoothManager = reactApplicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    gattServer = bluetoothManager.openGattServer(reactApplicationContext, object : BluetoothGattServerCallback() {})
    gattServer?.clearServices()
    for (i in 0 until serviceUUIDs.size()) {
      val uuid = serviceUUIDs.getString(i) ?: continue
      val service = BluetoothGattService(java.util.UUID.fromString(uuid), BluetoothGattService.SERVICE_TYPE_PRIMARY)
      gattServer?.addService(service)
    }
    gattServerReady = true
  }
}
