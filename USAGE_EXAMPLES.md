# React Native Bluetooth Peripheral - Usage Examples

This document provides comprehensive examples of how to use all BLE advertising data types in the React Native Bluetooth Peripheral library.

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

## Advanced Usage with All Advertising Data Types

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

    // 0x0D - Class of Device: Health Device
    classOfDevice: 0x240404,

    // 0x19 - Appearance: Generic Watch
    appearance: 0x03c0,

    // 0x1A - Advertising Interval: 100ms
    advertisingInterval: 100,

    // 0x1C - LE Role: Peripheral
    leRole: 'peripheral',

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

### Example 2: Smart Home Device (Smart Light Bulb)

```typescript
const smartHomeDeviceExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode
    flags: 0x04,

    // 0x02-0x07 - Service UUIDs: HID and Battery services
    completeServiceUUIDs16: ['1812', '180F'],

    // 0x08-0x09 - Local Name
    completeLocalName: 'Smart Light Bulb',
    shortenedLocalName: 'LightBulb',

    // 0x0A - Tx Power Level: -12 dBm
    txPowerLevel: -12,

    // 0x19 - Appearance: Generic Light Fixture
    appearance: 0x03c1,

    // 0x24 - URI: Web interface
    uri: 'https://myhome.com/light1',

    // 0x25 - Indoor Positioning: Floor and room info
    indoorPositioning: {
      floor: 2,
      room: 15,
      coordinates: {
        x: 12.5,
        y: 8.3,
        z: 1.2,
      },
    },

    // 0x26 - Transport Discovery Data: Multiple transport options
    transportDiscoveryData: [
      { transportType: 'wifi', data: '010203' },
      { transportType: 'bluetooth', data: '040506' },
    ],

    // 0x16 - Service Data: Device state
    serviceData16: [
      { uuid: '1812', data: '01' }, // HID: Keyboard
      { uuid: '180F', data: '64' }, // Battery: 100%
    ],

    // 0xFF - Manufacturer Specific Data: Light state (RGB + brightness)
    manufacturerData: 'FF0000FF', // Red, full brightness
  };

  startAdvertising({
    serviceUUIDs: ['1812', '180F'],
    advertisingData,
  });
};
```

### Example 3: Wearable Device (Smart Watch)

```typescript
const wearableDeviceExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode, BR/EDR Not Supported
    flags: 0x06,

    // 0x02-0x07 - Service UUIDs: Multiple services
    completeServiceUUIDs16: ['180D', '180F', '1812'],
    incompleteServiceUUIDs128: ['0000180D-0000-1000-8000-00805F9B34FB'],

    // 0x08-0x09 - Local Name
    completeLocalName: 'SmartWatch Pro',
    shortenedLocalName: 'WatchPro',

    // 0x0A - Tx Power Level: -6 dBm
    txPowerLevel: -6,

    // 0x0D - Class of Device: Wearable Device
    classOfDevice: 0x240404,

    // 0x19 - Appearance: Generic Watch
    appearance: 0x03c0,

    // 0x1A - Advertising Interval: 50ms (faster for wearables)
    advertisingInterval: 50,

    // 0x1C - LE Role: Peripheral
    leRole: 'peripheral',

    // 0x12 - Slave Connection Interval Range: Preferred intervals
    slaveConnectionIntervalRange: {
      min: 6, // 7.5ms
      max: 3200, // 4000ms
    },

    // 0x14-0x15 - Service Solicitation: Looking for specific services
    serviceSolicitationUUIDs16: ['180D', '180F'],

    // 0x16 - Service Data: Health and device data
    serviceData16: [
      { uuid: '180D', data: '7200' }, // Heart rate: 114 bpm
      { uuid: '180F', data: '32' }, // Battery: 50%
      { uuid: '1812', data: '02' }, // HID: Mouse
    ],

    // 0x27 - LE Supported Features: Advanced features
    leSupportedFeatures: [0x01, 0x02, 0x04], // 2M PHY, Coded PHY, Extended Advertising

    // 0xFF - Manufacturer Specific Data: Custom watch data
    manufacturerData: '0102030405060708090A0B0C',
  };

  startAdvertising({
    serviceUUIDs: ['180D', '180F', '1812'],
    advertisingData,
  });
};
```

### Example 4: Industrial IoT Device (Sensor Node)

```typescript
const industrialIoTExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode
    flags: 0x04,

    // 0x02-0x07 - Service UUIDs: Environmental and battery services
    completeServiceUUIDs16: ['181A', '180F'],
    completeServiceUUIDs32: ['0000181A-0000-1000-8000-00805F9B34FB'],

    // 0x08-0x09 - Local Name
    completeLocalName: 'Environmental Sensor Node',
    shortenedLocalName: 'EnvSensor',

    // 0x0A - Tx Power Level: -15 dBm (lower power for IoT)
    txPowerLevel: -15,

    // 0x19 - Appearance: Generic Sensor
    appearance: 0x03c2,

    // 0x1A - Advertising Interval: 1000ms (slower for battery life)
    advertisingInterval: 1000,

    // 0x1C - LE Role: Peripheral
    leRole: 'peripheral',

    // 0x1B - LE Bluetooth Device Address: Static address
    leBluetoothDeviceAddress: '112233445566',

    // 0x24 - URI: Data endpoint
    uri: 'https://api.iot.com/sensor/001',

    // 0x25 - Indoor Positioning: Precise location
    indoorPositioning: {
      floor: 1,
      room: 23,
      coordinates: {
        x: 45.2,
        y: 67.8,
        z: 2.1,
      },
    },

    // 0x16 - Service Data: Sensor readings
    serviceData16: [
      { uuid: '181A', data: '1A2B3C4D' }, // Temperature: 26.7°C, Humidity: 43.2%
      { uuid: '180F', data: '1E' }, // Battery: 30%
    ],

    // 0x27 - LE Supported Features: IoT features
    leSupportedFeatures: [0x01, 0x08], // 2M PHY, Periodic Advertising

    // 0x28 - Channel Map Update Indication: Mesh networking
    channelMapUpdateIndication: '0102030405',

    // 0xFF - Manufacturer Specific Data: Sensor calibration data
    manufacturerData: 'A1B2C3D4E5F6789012345678901234567890ABCDEF',
  };

  startAdvertising({
    serviceUUIDs: ['181A', '180F'],
    advertisingData,
  });
};
```

### Example 5: Audio Device (Wireless Headphones)

```typescript
const audioDeviceExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode, BR/EDR Not Supported
    flags: 0x06,

    // 0x02-0x07 - Service UUIDs: Audio and battery services
    completeServiceUUIDs16: ['1812', '180F'],
    completeServiceUUIDs128: ['00001812-0000-1000-8000-00805F9B34FB'],

    // 0x08-0x09 - Local Name
    completeLocalName: 'Wireless Headphones Pro',
    shortenedLocalName: 'Headphones',

    // 0x0A - Tx Power Level: -10 dBm
    txPowerLevel: -10,

    // 0x0D - Class of Device: Audio Device
    classOfDevice: 0x240404,

    // 0x19 - Appearance: Generic Headphones
    appearance: 0x03c3,

    // 0x1A - Advertising Interval: 200ms
    advertisingInterval: 200,

    // 0x1C - LE Role: Peripheral
    leRole: 'peripheral',

    // 0x2C-0x2D - LE Audio: Broadcast and audio data
    bigInfo:
      '19202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F',
    broadcastCode:
      '404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F',

    // 0x30 - Broadcast Isochronous Stream Data: Audio stream info
    bisData:
      '606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F8081',

    // 0x16 - Service Data: Audio state and battery
    serviceData16: [
      { uuid: '1812', data: '03' }, // HID: Gamepad
      { uuid: '180F', data: '64' }, // Battery: 100%
    ],

    // 0x27 - LE Supported Features: Audio features
    leSupportedFeatures: [0x01, 0x02, 0x04, 0x08], // 2M PHY, Coded PHY, Extended Advertising, Periodic Advertising

    // 0xFF - Manufacturer Specific Data: Audio codec and quality info
    manufacturerData:
      '808182838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9F',
  };

  startAdvertising({
    serviceUUIDs: ['1812', '180F'],
    advertisingData,
  });
};
```

### Example 6: Security Device (Smart Lock)

```typescript
const securityDeviceExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode
    flags: 0x04,

    // 0x02-0x07 - Service UUIDs: Security and battery services
    completeServiceUUIDs16: ['1812', '180F'],

    // 0x08-0x09 - Local Name
    completeLocalName: 'Smart Lock Secure',
    shortenedLocalName: 'SmartLock',

    // 0x0A - Tx Power Level: -8 dBm
    txPowerLevel: -8,

    // 0x19 - Appearance: Generic Lock
    appearance: 0x03c4,

    // 0x1A - Advertising Interval: 500ms
    advertisingInterval: 500,

    // 0x1C - LE Role: Peripheral
    leRole: 'peripheral',

    // 0x0E-0x0F - Simple Pairing: Security pairing data
    simplePairingHashC:
      'A1B2C3D4E5F6789012345678901234567890ABCDEF1234567890ABCDEF123456',
    simplePairingRandomizerR:
      '1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF',

    // 0x10-0x11 - Security Manager: Security manager data
    securityManagerTKValue:
      '0000000000000000000000000000000000000000000000000000000000000000',
    securityManagerOOFlags: 0x01,

    // 0x1D-0x1E - Simple Pairing (256-bit): Enhanced security
    simplePairingHashC256:
      'A1B2C3D4E5F6789012345678901234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF123456',
    simplePairingRandomizerR256:
      '1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF123456',

    // 0x22-0x23 - LE Secure Connections: Secure connection data
    leSecureConnectionsConfirmationValue:
      'A1B2C3D4E5F6789012345678901234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF123456',
    leSecureConnectionsRandomValue:
      '1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF123456',

    // 0x16 - Service Data: Lock state and battery
    serviceData16: [
      { uuid: '1812', data: '04' }, // HID: Touch Screen
      { uuid: '180F', data: '50' }, // Battery: 80%
    ],

    // 0xFF - Manufacturer Specific Data: Lock status and security info
    manufacturerData:
      'A0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBF',
  };

  startAdvertising({
    serviceUUIDs: ['1812', '180F'],
    advertisingData,
  });
};
```

### Example 7: Mesh Network Device

```typescript
const meshDeviceExample = () => {
  const advertisingData: AdvertisingDataTypes = {
    // 0x01 - Flags: LE General Discoverable Mode
    flags: 0x04,

    // 0x02-0x07 - Service UUIDs: Mesh services
    completeServiceUUIDs16: ['1827', '180F'],

    // 0x08-0x09 - Local Name
    completeLocalName: 'Mesh Node 001',
    shortenedLocalName: 'Mesh001',

    // 0x0A - Tx Power Level: -12 dBm
    txPowerLevel: -12,

    // 0x19 - Appearance: Generic Sensor
    appearance: 0x03c2,

    // 0x1A - Advertising Interval: 100ms
    advertisingInterval: 100,

    // 0x1C - LE Role: Peripheral
    leRole: 'peripheral',

    // 0x28 - Channel Map Update Indication: Mesh channel updates
    channelMapUpdateIndication:
      '0102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F',

    // 0x29-0x2B - Mesh: Mesh-specific data
    pbAdv:
      '202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F4041',
    meshMessage:
      '42434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F6061',
    meshBeacon:
      '62636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F8081',

    // 0x16 - Service Data: Mesh node info
    serviceData16: [
      { uuid: '1827', data: '010203' }, // Mesh service data
      { uuid: '180F', data: '64' }, // Battery: 100%
    ],

    // 0xFF - Manufacturer Specific Data: Mesh network info
    manufacturerData:
      '82838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9FA0A1',
  };

  startAdvertising({
    serviceUUIDs: ['1827', '180F'],
    advertisingData,
  });
};
```

## Dynamic Advertising Data Updates

### Example 8: Real-time Data Updates

```typescript
const dynamicAdvertisingExample = () => {
  // Start with basic advertising
  startAdvertising({
    serviceUUIDs: ['180D', '180F'],
    advertisingData: {
      flags: 0x06,
      completeLocalName: 'Dynamic Device',
      txPowerLevel: -10,
      appearance: 0x03c0,
      serviceData16: [
        { uuid: '180D', data: '6400' }, // Heart rate: 100 bpm
        { uuid: '180F', data: '64' }, // Battery: 100%
      ],
    },
  });

  // Update advertising data every 5 seconds
  setInterval(async () => {
    // Get current advertising data
    const currentData = await getAdvertisingData();
    console.log('Current advertising data:', currentData);

    // Update with new values
    updateAdvertisingData({
      txPowerLevel: Math.floor(Math.random() * 20) - 20, // Random power level
      serviceData16: [
        { uuid: '180D', data: `${Math.floor(Math.random() * 200) + 40}00` }, // Random heart rate
        { uuid: '180F', data: `${Math.floor(Math.random() * 100)}` }, // Random battery level
      ],
      manufacturerData: `${Math.floor(Math.random() * 256)
        .toString(16)
        .padStart(2, '0')}${Math.floor(Math.random() * 256)
        .toString(16)
        .padStart(2, '0')}${Math.floor(Math.random() * 256)
        .toString(16)
        .padStart(2, '0')}${Math.floor(Math.random() * 256)
        .toString(16)
        .padStart(2, '0')}`,
    });
  }, 5000);
};
```

## Complete Advertising Data Types Demo

### Example 9: All Types in One Example

```typescript
const completeAdvertisingDemo = () => {
  const completeAdvertisingData: AdvertisingDataTypes = {
    // All advertising data types in one example
    flags: 0x06,
    incompleteServiceUUIDs16: ['180D'],
    completeServiceUUIDs16: ['180F', '1812'],
    incompleteServiceUUIDs32: ['0000180D-0000-1000-8000-00805F9B34FB'],
    completeServiceUUIDs32: ['0000180F-0000-1000-8000-00805F9B34FB'],
    incompleteServiceUUIDs128: ['0000180D-0000-1000-8000-00805F9B34FB'],
    completeServiceUUIDs128: ['0000180F-0000-1000-8000-00805F9B34FB'],
    shortenedLocalName: 'DemoDev',
    completeLocalName: 'Complete Demo Device',
    txPowerLevel: -8,
    classOfDevice: 0x240404,
    simplePairingHashC: 'A1B2C3D4E5F6',
    simplePairingRandomizerR: '1234567890AB',
    securityManagerTKValue: '000000000000',
    securityManagerOOFlags: 0x01,
    slaveConnectionIntervalRange: { min: 6, max: 3200 },
    serviceSolicitationUUIDs16: ['180D'],
    serviceSolicitationUUIDs128: ['0000180D-0000-1000-8000-00805F9B34FB'],
    serviceData16: [{ uuid: '180D', data: '6400' }],
    serviceData32: [
      { uuid: '0000180D-0000-1000-8000-00805F9B34FB', data: '010203' },
    ],
    serviceData128: [
      { uuid: '0000180D-0000-1000-8000-00805F9B34FB', data: '040506' },
    ],
    publicTargetAddress: '112233445566',
    randomTargetAddress: 'AABBCCDDEEFF',
    appearance: 0x03c0,
    advertisingInterval: 100,
    leBluetoothDeviceAddress: '112233445566',
    leRole: 'peripheral',
    simplePairingHashC256:
      'A1B2C3D4E5F6789012345678901234567890ABCDEF1234567890ABCDEF123456',
    simplePairingRandomizerR256:
      '1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF',
    serviceSolicitationUUIDs32: ['0000180D'],
    leSecureConnectionsConfirmationValue:
      'A1B2C3D4E5F6789012345678901234567890ABCDEF1234567890ABCDEF123456',
    leSecureConnectionsRandomValue:
      '1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF',
    uri: 'https://example.com/demo',
    indoorPositioning: {
      floor: 1,
      room: 10,
      coordinates: { x: 5.5, y: 3.2, z: 1.0 },
    },
    transportDiscoveryData: [
      { transportType: 'usb', data: '010203' },
      { transportType: 'nfc', data: '040506' },
      { transportType: 'wifi', data: '070809' },
      { transportType: 'bluetooth', data: '0A0B0C' },
    ],
    leSupportedFeatures: [0x01, 0x02, 0x04, 0x08],
    channelMapUpdateIndication: '0102030405',
    pbAdv: '060708090A0B0C0D0E0F',
    meshMessage: '10111213141516171819',
    meshBeacon: '1A1B1C1D1E1F20212223',
    bigInfo: '2425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F4041',
    broadcastCode:
      '42434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F6061',
    resolvableSetIdentifier:
      '62636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F8081',
    advertisingIntervalLong: 1000,
    bisData: '82838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9FA0A1',
    threeDInformationData:
      'A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBF',
    manufacturerData:
      'C0C1C2C3C4C5C6C7C8C9CACBCCCDCECFD0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF',
  };

  startAdvertising({
    serviceUUIDs: ['180D', '180F', '1812'],
    advertisingData: completeAdvertisingData,
  });
};
```

## Setting Up GATT Services

```typescript
// Set up GATT services for your device
setServices([
  {
    uuid: '180D', // Heart Rate Service
    characteristics: [
      {
        uuid: '2A37', // Heart Rate Measurement
        properties: ['read', 'notify'],
        value: '6400', // 100 bpm
      },
      {
        uuid: '2A38', // Body Sensor Location
        properties: ['read'],
        value: '01', // Chest
      },
    ],
  },
  {
    uuid: '180F', // Battery Service
    characteristics: [
      {
        uuid: '2A19', // Battery Level
        properties: ['read', 'notify'],
        value: '64', // 100%
      },
    ],
  },
]);
```

## Utility Functions

```typescript
// Stop all advertising
const stopAllAdvertising = () => {
  stopAdvertising();
};

// Get current advertising data
const getCurrentAdvertisingData = async () => {
  try {
    const data = await getAdvertisingData();
    console.log('Current advertising data:', data);
    return data;
  } catch (error) {
    console.error('Failed to get advertising data:', error);
    return null;
  }
};

// Update specific advertising data fields
const updateDeviceName = (newName: string) => {
  updateAdvertisingData({
    completeLocalName: newName,
  });
};

const updateBatteryLevel = (level: number) => {
  updateAdvertisingData({
    serviceData16: [
      { uuid: '180F', data: level.toString(16).padStart(2, '0') },
    ],
  });
};
```

## Best Practices

1. **Use appropriate flags**: Set flags based on your device capabilities
2. **Optimize advertising intervals**: Use shorter intervals for interactive devices, longer for battery-saving
3. **Include relevant service UUIDs**: Only include services that your device actually provides
4. **Use meaningful device names**: Make device names descriptive and user-friendly
5. **Update data dynamically**: Use `updateAdvertisingData` for real-time data changes
6. **Handle errors gracefully**: Always wrap advertising calls in try-catch blocks
7. **Test on real devices**: Verify advertising data on actual BLE scanners
8. **Follow platform guidelines**: Ensure compliance with iOS and Android BLE requirements

## Troubleshooting

### Common Issues

1. **Advertising not starting**: Check Bluetooth permissions and device state
2. **Data not updating**: Ensure you're calling `updateAdvertisingData` correctly
3. **Service UUIDs not showing**: Verify UUID format and service setup
4. **Manufacturer data issues**: Check hex string format and length

### Debug Tips

```typescript
// Enable debug logging
const debugAdvertising = async () => {
  try {
    const currentData = await getAdvertisingData();
    console.log(
      'Current advertising data:',
      JSON.stringify(currentData, null, 2)
    );
  } catch (error) {
    console.error('Debug error:', error);
  }
};

// Monitor advertising state
setInterval(debugAdvertising, 1000);
```

This comprehensive set of examples demonstrates how to use all BLE advertising data types in your React Native Bluetooth Peripheral applications. Each example is tailored for specific use cases and shows best practices for implementation.
