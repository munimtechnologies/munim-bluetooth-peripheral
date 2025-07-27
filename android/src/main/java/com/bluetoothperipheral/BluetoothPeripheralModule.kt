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
    // 0x01 - Flags
    if (dataMap.hasKey("flags")) {
      val flags = dataMap.getInt("flags")
      // Android doesn't directly support flags in AdvertiseData, but we can encode it
      val flagsData = byteArrayOf(flags.toByte())
      dataBuilder.addServiceData(
        ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"), // Heart Rate Service UUID as placeholder
        flagsData
      )
    }
    
    // 0x02-0x07 - Service UUIDs
    addServiceUUIDs(dataMap.getArray("incompleteServiceUUIDs16"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("completeServiceUUIDs16"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("incompleteServiceUUIDs32"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("completeServiceUUIDs32"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("incompleteServiceUUIDs128"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("completeServiceUUIDs128"), dataBuilder)
    
    // 0x08-0x09 - Local Name
    if (dataMap.hasKey("shortenedLocalName")) {
      val name = dataMap.getString("shortenedLocalName")
      if (name != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          name.toByteArray()
        )
      }
    }
    if (dataMap.hasKey("completeLocalName")) {
      val name = dataMap.getString("completeLocalName")
      if (name != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          name.toByteArray()
        )
      }
    }
    
    // 0x0A - Tx Power Level
    if (dataMap.hasKey("txPowerLevel")) {
      val txPower = dataMap.getInt("txPowerLevel")
      val txPowerData = byteArrayOf(txPower.toByte())
      dataBuilder.addServiceData(
        ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
        txPowerData
      )
    }
    
    // 0x0D - Class of Device
    if (dataMap.hasKey("classOfDevice")) {
      val cod = dataMap.getInt("classOfDevice")
      val codData = byteArrayOf(
        (cod and 0xFF).toByte(),
        ((cod shr 8) and 0xFF).toByte(),
        ((cod shr 16) and 0xFF).toByte()
      )
      dataBuilder.addServiceData(
        ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
        codData
      )
    }
    
    // 0x0E-0x0F - Simple Pairing
    if (dataMap.hasKey("simplePairingHashC")) {
      val hashData = hexStringToByteArray(dataMap.getString("simplePairingHashC"))
      if (hashData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          hashData
        )
      }
    }
    if (dataMap.hasKey("simplePairingRandomizerR")) {
      val randomData = hexStringToByteArray(dataMap.getString("simplePairingRandomizerR"))
      if (randomData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          randomData
        )
      }
    }
    
    // 0x10-0x11 - Security Manager
    if (dataMap.hasKey("securityManagerTKValue")) {
      val tkData = hexStringToByteArray(dataMap.getString("securityManagerTKValue"))
      if (tkData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          tkData
        )
      }
    }
    if (dataMap.hasKey("securityManagerOOFlags")) {
      val flags = dataMap.getInt("securityManagerOOFlags")
      val flagsData = byteArrayOf(flags.toByte())
      dataBuilder.addServiceData(
        ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
        flagsData
      )
    }
    
    // 0x12 - Slave Connection Interval Range
    if (dataMap.hasKey("slaveConnectionIntervalRange")) {
      val range = dataMap.getMap("slaveConnectionIntervalRange")
      if (range != null) {
        val min = range.getInt("min")
        val max = range.getInt("max")
        val rangeData = byteArrayOf(
          (min and 0xFF).toByte(),
          ((min shr 8) and 0xFF).toByte(),
          (max and 0xFF).toByte(),
          ((max shr 8) and 0xFF).toByte()
        )
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          rangeData
        )
      }
    }
    
    // 0x14-0x15 - Service Solicitation
    addServiceUUIDs(dataMap.getArray("serviceSolicitationUUIDs16"), dataBuilder)
    addServiceUUIDs(dataMap.getArray("serviceSolicitationUUIDs128"), dataBuilder)
    
    // 0x16, 0x20, 0x21 - Service Data
    addServiceData(dataMap.getArray("serviceData16"), dataBuilder)
    addServiceData(dataMap.getArray("serviceData32"), dataBuilder)
    addServiceData(dataMap.getArray("serviceData128"), dataBuilder)
    
    // 0x17-0x18 - Target Address
    if (dataMap.hasKey("publicTargetAddress")) {
      val addressData = hexStringToByteArray(dataMap.getString("publicTargetAddress"))
      if (addressData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          addressData
        )
      }
    }
    if (dataMap.hasKey("randomTargetAddress")) {
      val addressData = hexStringToByteArray(dataMap.getString("randomTargetAddress"))
      if (addressData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          addressData
        )
      }
    }
    
    // 0x19 - Appearance
    if (dataMap.hasKey("appearance")) {
      val appearance = dataMap.getInt("appearance")
      val appearanceData = byteArrayOf(
        (appearance and 0xFF).toByte(),
        ((appearance shr 8) and 0xFF).toByte()
      )
      dataBuilder.addServiceData(
        ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
        appearanceData
      )
    }
    
    // 0x1A - Advertising Interval
    if (dataMap.hasKey("advertisingInterval")) {
      val interval = dataMap.getInt("advertisingInterval")
      val intervalData = byteArrayOf(
        (interval and 0xFF).toByte(),
        ((interval shr 8) and 0xFF).toByte()
      )
      dataBuilder.addServiceData(
        ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
        intervalData
      )
    }
    
    // 0x1B - LE Bluetooth Device Address
    if (dataMap.hasKey("leBluetoothDeviceAddress")) {
      val addressData = hexStringToByteArray(dataMap.getString("leBluetoothDeviceAddress"))
      if (addressData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          addressData
        )
      }
    }
    
    // 0x1C - LE Role
    if (dataMap.hasKey("leRole")) {
      val role = dataMap.getString("leRole")
      val roleValue = if (role == "central") 0x00.toByte() else 0x01.toByte()
      dataBuilder.addServiceData(
        ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
        byteArrayOf(roleValue)
      )
    }
    
    // 0x1D-0x1E - Simple Pairing (256-bit)
    if (dataMap.hasKey("simplePairingHashC256")) {
      val hashData = hexStringToByteArray(dataMap.getString("simplePairingHashC256"))
      if (hashData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          hashData
        )
      }
    }
    if (dataMap.hasKey("simplePairingRandomizerR256")) {
      val randomData = hexStringToByteArray(dataMap.getString("simplePairingRandomizerR256"))
      if (randomData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          randomData
        )
      }
    }
    
    // 0x1F - Service Solicitation (32-bit)
    addServiceUUIDs(dataMap.getArray("serviceSolicitationUUIDs32"), dataBuilder)
    
    // 0x22-0x23 - LE Secure Connections
    if (dataMap.hasKey("leSecureConnectionsConfirmationValue")) {
      val confirmData = hexStringToByteArray(dataMap.getString("leSecureConnectionsConfirmationValue"))
      if (confirmData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          confirmData
        )
      }
    }
    if (dataMap.hasKey("leSecureConnectionsRandomValue")) {
      val randomData = hexStringToByteArray(dataMap.getString("leSecureConnectionsRandomValue"))
      if (randomData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          randomData
        )
      }
    }
    
    // 0x24 - URI
    if (dataMap.hasKey("uri")) {
      val uri = dataMap.getString("uri")
      if (uri != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          uri.toByteArray()
        )
      }
    }
    
    // 0x25 - Indoor Positioning
    if (dataMap.hasKey("indoorPositioning")) {
      val positioning = dataMap.getMap("indoorPositioning")
      if (positioning != null) {
        val positioningData = mutableListOf<Byte>()
        
        if (positioning.hasKey("floor")) {
          positioningData.add(positioning.getInt("floor").toByte())
        }
        if (positioning.hasKey("room")) {
          positioningData.add(positioning.getInt("room").toByte())
        }
        if (positioning.hasKey("coordinates")) {
          val coords = positioning.getMap("coordinates")
          if (coords != null) {
            val x = coords.getDouble("x").toFloat()
            val y = coords.getDouble("y").toFloat()
            positioningData.addAll(x.toBits().toByteArray().toList())
            positioningData.addAll(y.toBits().toByteArray().toList())
            
            if (coords.hasKey("z")) {
              val z = coords.getDouble("z").toFloat()
              positioningData.addAll(z.toBits().toByteArray().toList())
            }
          }
        }
        
        if (positioningData.isNotEmpty()) {
          dataBuilder.addServiceData(
            ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
            positioningData.toByteArray()
          )
        }
      }
    }
    
    // 0x26 - Transport Discovery Data
    if (dataMap.hasKey("transportDiscoveryData")) {
      val transports = dataMap.getArray("transportDiscoveryData")
      if (transports != null) {
        val transportData = mutableListOf<Byte>()
        for (i in 0 until transports.size()) {
          val transport = transports.getMap(i)
          if (transport != null) {
            val type = transport.getString("transportType")
            val data = transport.getString("data")
            if (type != null && data != null) {
              val typeValue = when (type) {
                "usb" -> 0x01.toByte()
                "nfc" -> 0x02.toByte()
                "wifi" -> 0x03.toByte()
                "bluetooth" -> 0x04.toByte()
                else -> 0x00.toByte()
              }
              transportData.add(typeValue)
              
              val dataBytes = hexStringToByteArray(data)
              if (dataBytes != null) {
                transportData.addAll(dataBytes.toList())
              }
            }
          }
        }
        
        if (transportData.isNotEmpty()) {
          dataBuilder.addServiceData(
            ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
            transportData.toByteArray()
          )
        }
      }
    }
    
    // 0x27 - LE Supported Features
    if (dataMap.hasKey("leSupportedFeatures")) {
      val features = dataMap.getArray("leSupportedFeatures")
      if (features != null) {
        val featuresData = mutableListOf<Byte>()
        for (i in 0 until features.size()) {
          featuresData.add(features.getInt(i).toByte())
        }
        if (featuresData.isNotEmpty()) {
          dataBuilder.addServiceData(
            ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
            featuresData.toByteArray()
          )
        }
      }
    }
    
    // 0x28 - Channel Map Update Indication
    if (dataMap.hasKey("channelMapUpdateIndication")) {
      val channelData = hexStringToByteArray(dataMap.getString("channelMapUpdateIndication"))
      if (channelData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          channelData
        )
      }
    }
    
    // 0x29-0x2B - Mesh
    if (dataMap.hasKey("pbAdv")) {
      val pbAdvData = hexStringToByteArray(dataMap.getString("pbAdv"))
      if (pbAdvData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          pbAdvData
        )
      }
    }
    if (dataMap.hasKey("meshMessage")) {
      val meshData = hexStringToByteArray(dataMap.getString("meshMessage"))
      if (meshData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          meshData
        )
      }
    }
    if (dataMap.hasKey("meshBeacon")) {
      val beaconData = hexStringToByteArray(dataMap.getString("meshBeacon"))
      if (beaconData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          beaconData
        )
      }
    }
    
    // 0x2C-0x2D - LE Audio
    if (dataMap.hasKey("bigInfo")) {
      val bigInfoData = hexStringToByteArray(dataMap.getString("bigInfo"))
      if (bigInfoData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          bigInfoData
        )
      }
    }
    if (dataMap.hasKey("broadcastCode")) {
      val broadcastData = hexStringToByteArray(dataMap.getString("broadcastCode"))
      if (broadcastData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          broadcastData
        )
      }
    }
    
    // 0x2E - Resolvable Set Identifier
    if (dataMap.hasKey("resolvableSetIdentifier")) {
      val rsiData = hexStringToByteArray(dataMap.getString("resolvableSetIdentifier"))
      if (rsiData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          rsiData
        )
      }
    }
    
    // 0x2F - Advertising Interval Long
    if (dataMap.hasKey("advertisingIntervalLong")) {
      val interval = dataMap.getInt("advertisingIntervalLong")
      val intervalData = byteArrayOf(
        (interval and 0xFF).toByte(),
        ((interval shr 8) and 0xFF).toByte(),
        ((interval shr 16) and 0xFF).toByte(),
        ((interval shr 24) and 0xFF).toByte()
      )
      dataBuilder.addServiceData(
        ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
        intervalData
      )
    }
    
    // 0x30 - Broadcast Isochronous Stream Data
    if (dataMap.hasKey("bisData")) {
      val bisData = hexStringToByteArray(dataMap.getString("bisData"))
      if (bisData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          bisData
        )
      }
    }
    
    // 0x3D - 3D Information Data
    if (dataMap.hasKey("threeDInformationData")) {
      val threeDData = hexStringToByteArray(dataMap.getString("threeDInformationData"))
      if (threeDData != null) {
        dataBuilder.addServiceData(
          ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
          threeDData
        )
      }
    }
    
    // 0xFF - Manufacturer Specific Data
    if (dataMap.hasKey("manufacturerData")) {
      val manufacturerData = hexStringToByteArray(dataMap.getString("manufacturerData"))
      if (manufacturerData != null) {
        dataBuilder.addManufacturerData(0x0000, manufacturerData) // Default manufacturer code
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
