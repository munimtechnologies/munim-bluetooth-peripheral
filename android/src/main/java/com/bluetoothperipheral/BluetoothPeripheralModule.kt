package com.bluetoothperipheral

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
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

private var advertiser: BluetoothLeAdvertiser? = null
private var gattServer: BluetoothGattServer? = null

@ReactModule(name = BluetoothPeripheralModule.NAME)
class BluetoothPeripheralModule(reactContext: ReactApplicationContext) :
  NativeBluetoothPeripheralSpec(reactContext) {

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  // Bluetooth emitter stubs
  fun startAdvertising(options: Map<String, Any>) {
    val bluetoothManager = reactApplicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    advertiser = bluetoothAdapter.bluetoothLeAdvertiser
    val settings = AdvertiseSettings.Builder()
      .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
      .setConnectable(true)
      .setTimeout(0)
      .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
      .build()
    val dataBuilder = AdvertiseData.Builder()
    (options["serviceUUIDs"] as? List<*>)?.forEach {
      (it as? String)?.let { uuid ->
        dataBuilder.addServiceUuid(ParcelUuid.fromString(uuid))
      }
    }
    (options["localName"] as? String)?.let { name ->
      bluetoothAdapter.name = name
    }
    advertiser?.startAdvertising(settings, dataBuilder.build(), object : AdvertiseCallback() {})
  }

  fun stopAdvertising() {
    advertiser?.stopAdvertising(object : AdvertiseCallback() {})
    advertiser = null
  }

  fun setServices(services: List<Map<String, Any>>) {
    val bluetoothManager = reactApplicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    gattServer = bluetoothManager.openGattServer(reactApplicationContext, object : BluetoothGattServerCallback() {})
    gattServer?.clearServices()
    services.forEach { serviceMap ->
      val serviceUuid = serviceMap["uuid"] as? String ?: return@forEach
      val service = BluetoothGattService(java.util.UUID.fromString(serviceUuid), BluetoothGattService.SERVICE_TYPE_PRIMARY)
      (serviceMap["characteristics"] as? List<*>)?.forEach { charMapAny ->
        val charMap = charMapAny as? Map<*, *> ?: return@forEach
        val charUuid = charMap["uuid"] as? String ?: return@forEach
        val properties = (charMap["properties"] as? List<*>)?.fold(0) { acc, prop ->
          acc or when (prop) {
            "read" -> BluetoothGattCharacteristic.PROPERTY_READ
            "write" -> BluetoothGattCharacteristic.PROPERTY_WRITE
            "notify" -> BluetoothGattCharacteristic.PROPERTY_NOTIFY
            else -> 0
          }
        } ?: 0
        val permissions = BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
        val characteristic = BluetoothGattCharacteristic(java.util.UUID.fromString(charUuid), properties, permissions)
        (charMap["value"] as? String)?.let { value ->
          characteristic.value = value.toByteArray()
        }
        service.addCharacteristic(characteristic)
      }
      gattServer?.addService(service)
    }
  }

  companion object {
    const val NAME = "BluetoothPeripheral"
  }
}
