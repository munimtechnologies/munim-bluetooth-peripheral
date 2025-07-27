# React Native Bluetooth Peripheral - Usage Examples

This document provides comprehensive examples of how to use the supported BLE advertising data types in the React Native Bluetooth Peripheral library.

**Note:** This library only supports advertising data types that are properly supported by both Android and iOS platforms. Advanced features like mesh networking, LE Audio, indoor positioning, etc., are not supported due to platform limitations.

## Basic Usage

```typescript
import {
  startAdvertising,
  stopAdvertising,
  setServices,
  updateAdvertisingData,
  getAdvertisingData,
  type AdvertisingDataTypes,
} from 'react-native-bluetooth-peripheral';

// Start basic advertising
startAdvertising({
  serviceUUIDs: ['180D', '180F'],
  localName: 'My Device',
  manufacturerData: '0102030405',
});

// Stop advertising
stopAdvertising();
```

## Supported Advertising Data Types

### Fully Supported Types

These advertising data types work reliably on both Android and iOS:

- **Service UUIDs** (0x02-0x07): 16-bit, 32-bit, and 128-bit service UUIDs
- **Local Name** (0x08-0x09): Device name (shortened and complete)
- **Tx Power Level** (0x0A): Transmission power level
- **Service Solicitation UUIDs** (0x14-0x15, 0x1F): Services the device is seeking
- **Service Data** (0x16, 0x20, 0x21): Data associated with specific services
- **Manufacturer Specific Data** (0xFF): Custom vendor-specific data

### Partially Supported Types

These work with limitations:

- **Flags** (0x01): Basic device capabilities (limited platform support)
- **Appearance** (0x19): Device appearance category (workaround implementation)

## Advanced Usage Examples

### Example 1: Health Device (Heart Rate Monitor)

```typescript
const healthDeviceExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode, BR/EDR Not Supported
    flags: 0x06,

    // 0x02-0x07 - Service UUIDs: Heart Rate and Battery services
    completeServiceUUIDs16: ['180D', '180F'],

    // 0x08-0x09 - Local Name: Device name
    completeLocalName: 'Health Monitor Pro',
    shortenedLocalName: 'HealthPro',

    // 0x0A - Tx Power Level: -8 dBm
    txPowerLevel: -8,

    // 0x19 - Appearance: Generic Heart Rate Sensor
    appearance: 0x0341,

    // 0x16 - Service Data: Heart rate and battery level
    serviceData16: [
      { uuid: '180D', data: '6400' }, // Heart rate: 100 bpm
      { uuid: '180F', data: '64' }, // Battery: 100%
    ],

    // 0xFF - Manufacturer Specific Data: Custom health metrics
    manufacturerData: '0102030405060708',
  };

  startAdvertising({
    serviceUUIDs: ['180D', '180F'],
    advertisingData,
  });
};
```

### Example 2: Smart Home Device (Temperature Sensor)

```typescript
const temperatureSensorExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode
    flags: 0x06,

    // 0x02-0x07 - Service UUIDs: Environmental Sensing
    completeServiceUUIDs16: ['181A'],

    // 0x08-0x09 - Local Name
    completeLocalName: 'Temperature Sensor',
    shortenedLocalName: 'TempSensor',

    // 0x0A - Tx Power Level: 0 dBm
    txPowerLevel: 0,

    // 0x19 - Appearance: Generic Thermometer
    appearance: 0x0300,

    // 0x16 - Service Data: Current temperature
    serviceData16: [
      { uuid: '181A', data: '1A0C' }, // Temperature: 22.5°C (encoded)
    ],

    // 0xFF - Manufacturer Specific Data: Device info
    manufacturerData: '4C000215',
  };

  startAdvertising({
    serviceUUIDs: ['181A'],
    advertisingData,
  });
};
```

### Example 3: Gaming Controller

```typescript
const gamingControllerExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode
    flags: 0x06,

    // 0x02-0x07 - Service UUIDs: HID over GATT
    completeServiceUUIDs16: ['1812'],

    // 0x08-0x09 - Local Name
    completeLocalName: 'Gaming Controller',

    // 0x0A - Tx Power Level: 4 dBm
    txPowerLevel: 4,

    // 0x19 - Appearance: HID Gamepad
    appearance: 0x03c4,

    // 0x14 - Service Solicitation: Looking for gaming services
    serviceSolicitationUUIDs16: ['1234'],

    // 0xFF - Manufacturer Specific Data: Controller capabilities
    manufacturerData: '09ff0001020304',
  };

  startAdvertising({
    serviceUUIDs: ['1812'],
    advertisingData,
  });
};
```

### Example 4: Beacon Device

```typescript
const beaconExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode, BR/EDR Not Supported
    flags: 0x06,

    // 0x08-0x09 - Local Name
    completeLocalName: 'MyBeacon',

    // 0x0A - Tx Power Level: -4 dBm
    txPowerLevel: -4,

    // 0xFF - Manufacturer Specific Data: iBeacon format
    // Apple Company ID (0x004C) + iBeacon data
    manufacturerData:
      '4C000215' +
      'FDA50693A4E24FB1AFCFC6EB07647825' + // UUID
      '0001' + // Major
      '0001' + // Minor
      'C5', // TX Power
  };

  startAdvertising({
    serviceUUIDs: [],
    advertisingData,
  });
};
```

### Example 5: Multiple Service Data

```typescript
const multiServiceExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags
    flags: 0x06,

    // Multiple service UUIDs
    completeServiceUUIDs16: ['180D', '180F', '181A'],
    completeServiceUUIDs128: ['12345678-1234-1234-1234-123456789abc'],

    // Local name
    completeLocalName: 'Multi-Service Device',

    // Multiple service data entries
    serviceData16: [
      { uuid: '180D', data: '6400' }, // Heart rate data
      { uuid: '180F', data: '64' }, // Battery level
      { uuid: '181A', data: '1A0C' }, // Temperature data
    ],

    serviceData128: [
      {
        uuid: '12345678-1234-1234-1234-123456789abc',
        data: '010203040506',
      },
    ],

    // Service solicitation
    serviceSolicitationUUIDs16: ['1234', '5678'],

    // Manufacturer data
    manufacturerData: 'FFFF01020304050607080910',
  };

  startAdvertising({
    serviceUUIDs: ['180D', '180F', '181A'],
    advertisingData,
  });
};
```

## Dynamic Advertising Data Updates

```typescript
const dynamicUpdatesExample = () => {
  // Start with initial advertising data
  const initialData: AdvertisingDataTypes = {
    flags: 0x06,
    completeServiceUUIDs16: ['180F'],
    completeLocalName: 'Battery Monitor',
    serviceData16: [
      { uuid: '180F', data: '64' }, // 100% battery
    ],
  };

  startAdvertising({
    serviceUUIDs: ['180F'],
    advertisingData: initialData,
  });

  // Update battery level every 10 seconds
  const updateBatteryLevel = (level: number) => {
    const updatedData: AdvertisingDataTypes = {
      serviceData16: [
        { uuid: '180F', data: level.toString(16).padStart(2, '0') },
      ],
    };

    updateAdvertisingData(updatedData);
  };

  // Simulate battery drain
  let batteryLevel = 100;
  const interval = setInterval(() => {
    batteryLevel -= 5;
    if (batteryLevel <= 0) {
      clearInterval(interval);
      stopAdvertising();
      return;
    }
    updateBatteryLevel(batteryLevel);
  }, 10000);
};
```

## Platform-Specific Considerations

### Android

- Service UUIDs are fully supported
- Local name is handled automatically (no distinction between shortened/complete)
- Tx Power Level is controlled via advertising settings
- Appearance data is encoded as service data with Generic Access service UUID

### iOS

- All supported features work natively through Core Bluetooth
- Service data is fully supported
- Manufacturer data works with proper formatting
- Appearance data is stored as custom service data

## Best Practices

1. **Keep advertising data small**: BLE advertising packets have size limitations (31 bytes for legacy advertising)

2. **Use appropriate service UUIDs**: Use standard Bluetooth SIG assigned UUIDs when possible

3. **Update data efficiently**: Use `updateAdvertisingData()` for frequent updates rather than stopping/starting advertising

4. **Handle platform differences**: Test on both Android and iOS as behavior may vary slightly

5. **Proper manufacturer data format**: Include company identifier in manufacturer data

6. **Battery optimization**: Consider advertising intervals for battery-powered devices

## Troubleshooting

### Common Issues

1. **Advertising not visible**: Check that flags include discoverable mode (0x04 or 0x06)
2. **Service data not appearing**: Ensure service UUIDs are properly formatted
3. **Manufacturer data issues**: Verify proper hex string formatting
4. **Platform differences**: Some features may work differently on Android vs iOS

### Testing Tools

- **nRF Connect** (Android/iOS): Excellent for viewing advertising data
- **LightBlue** (iOS): Good for testing connections and services
- **Bluetooth Scanner** (Android/iOS): Simple advertising packet viewer
