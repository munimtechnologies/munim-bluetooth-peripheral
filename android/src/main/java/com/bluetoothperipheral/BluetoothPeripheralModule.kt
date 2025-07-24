package com.bluetoothperipheral

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
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
import android.util.Log
import kotlinx.coroutines.*

private var advertiser: BluetoothLeAdvertiser? = null
private var gattServer: BluetoothGattServer? = null
private var gattServerReady = false
private var advertiseJob: Job? = null
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
      for (i in 0 until serviceUUIDs.size()) {
        val uuid = serviceUUIDs.getString(i)
        if (uuid != null) {
          dataBuilder.addServiceUuid(ParcelUuid.fromString(uuid))
        }
      }
      val localName = options.getString("localName")
      if (localName != null) {
        bluetoothAdapter.name = localName
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
