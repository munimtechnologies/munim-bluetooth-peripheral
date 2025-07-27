<!-- Banner Image -->

<p align="center">
  <a href="https://github.com/munimtechnologies/munim-bluetooth-peripheral">
    <img alt="Munim Technologies Bluetooth Peripheral" height="128" src="./.github/resources/banner.png?v=3">
    <h1 align="center">munim-bluetooth-peripheral</h1>
  </a>
</p>

<p align="center">
   <a aria-label="Package version" href="https://www.npmjs.com/package/munim-bluetooth-peripheral" target="_blank">
    <img alt="Package version" src="https://img.shields.io/npm/v/munim-bluetooth-peripheral.svg?style=flat-square&label=Version&labelColor=000000&color=0066CC" />
  </a>
  <a aria-label="Package is free to use" href="https://github.com/munimtechnologies/munim-bluetooth-peripheral/blob/main/LICENSE" target="_blank">
    <img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-success.svg?style=flat-square&color=33CC12" target="_blank" />
  </a>
  <a aria-label="package downloads" href="https://www.npmtrends.com/munim-bluetooth-peripheral" target="_blank">
    <img alt="Downloads" src="https://img.shields.io/npm/dm/munim-bluetooth-peripheral.svg?style=flat-square&labelColor=gray&color=33CC12&label=Downloads" />
  </a>
</p>

<p align="center">
  <a aria-label="try with expo" href="https://docs.expo.dev/"><b>Works with Expo</b></a>
&ensp;•&ensp;
  <a aria-label="documentation" href="https://github.com/munimtechnologies/munim-bluetooth-peripheral#readme">Read the Documentation</a>
&ensp;•&ensp;
  <a aria-label="report issues" href="https://github.com/munimtechnologies/munim-bluetooth-peripheral/issues">Report Issues</a>
</p>

<h6 align="center">Follow Munim Technologies</h6>
<p align="center">
  <a aria-label="Follow Munim Technologies on GitHub" href="https://github.com/munimtechnologies" target="_blank">
    <img alt="Munim Technologies on GitHub" src="https://img.shields.io/badge/GitHub-222222?style=for-the-badge&logo=github&logoColor=white" target="_blank" />
  </a>&nbsp;
  <a aria-label="Follow Munim Technologies on LinkedIn" href="https://linkedin.com/in/sheehanmunim" target="_blank">
    <img alt="Munim Technologies on LinkedIn" src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" target="_blank" />
  </a>&nbsp;
  <a aria-label="Visit Munim Technologies Website" href="https://munimtech.com" target="_blank">
    <img alt="Munim Technologies Website" src="https://img.shields.io/badge/Website-0066CC?style=for-the-badge&logo=globe&logoColor=white" target="_blank" />
  </a>
</p>

## Introduction

**munim-bluetooth-peripheral** is a React Native library for creating Bluetooth Low Energy (BLE) peripheral devices. This library allows your React Native app to act as a BLE peripheral, advertising services and characteristics that other devices can discover and connect to.

**Fully compatible with Expo!** Works seamlessly with both Expo managed and bare workflows.

## Table of contents

- [📚 Documentation](#-documentation)
- [🚀 Features](#-features)
- [📦 Installation](#-installation)
- [⚡ Quick Start](#-quick-start)
- [🔧 API Reference](#-api-reference)
- [📖 Usage Examples](#-usage-examples)
- [🔍 Troubleshooting](#-troubleshooting)
- [👏 Contributing](#-contributing)
- [📄 License](#-license)

## 📚 Documentation

<p>Learn about building BLE peripheral apps <a aria-label="documentation" href="https://github.com/munimtechnologies/munim-bluetooth-peripheral#readme">in our documentation!</a></p>

- [Getting Started](#-installation)
- [API Reference](#-api-reference)
- [Usage Examples](#-usage-examples)
- [Troubleshooting](#-troubleshooting)

## 🚀 Features

- 🔵 **BLE Peripheral Mode**: Transform your React Native app into a BLE peripheral device
- 📡 **Service Advertising**: Advertise custom GATT services with multiple characteristics
- 🔄 **Real-time Communication**: Support for read, write, and notify operations
- 📱 **Cross-platform**: Works on both iOS and Android
- 🎯 **TypeScript Support**: Full TypeScript definitions included
- ⚡ **High Performance**: Built with React Native's new architecture (Fabric)
- 🚀 **Expo Compatible**: Works seamlessly with Expo managed and bare workflows
- ✅ **Comprehensive BLE Advertising Data Types**: Support for all 31+ BLE advertising data types
- 🔧 **Dynamic Updates**: Update advertising data while advertising is active

## 📦 Installation

### React Native CLI

```bash
npm install munim-bluetooth-peripheral
# or
yarn add munim-bluetooth-peripheral
```

### Expo

```bash
npx expo install munim-bluetooth-peripheral
```

> **Note**: This library requires Expo SDK 50+ and works with both managed and bare workflows.

### iOS Setup

For iOS, the library is automatically linked. However, you need to add the following to your `Info.plist`:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app uses Bluetooth to create a peripheral device</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app uses Bluetooth to create a peripheral device</string>
```

**For Expo projects**, add these permissions to your `app.json`:

```json
{
  "expo": {
    "ios": {
      "infoPlist": {
        "NSBluetoothAlwaysUsageDescription": "This app uses Bluetooth to create a peripheral device",
        "NSBluetoothPeripheralUsageDescription": "This app uses Bluetooth to create a peripheral device"
      }
    }
  }
}
```

### Android Setup

For Android, add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

**For Expo projects**, add these permissions to your `app.json`:

```json
{
  "expo": {
    "android": {
      "permissions": [
        "android.permission.BLUETOOTH",
        "android.permission.BLUETOOTH_ADMIN",
        "android.permission.BLUETOOTH_ADVERTISE",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
      ]
    }
  }
}
```

## ⚡ Quick Start

### Basic Usage

```typescript
import {
  startAdvertising,
  stopAdvertising,
  setServices,
} from 'munim-bluetooth-peripheral';

// Start advertising with basic options
startAdvertising({
  serviceUUIDs: ['180D', '180F'],
  localName: 'My Device',
  manufacturerData: '0102030405',
});

// Set GATT services
setServices([
  {
    uuid: '180D',
    characteristics: [
      {
        uuid: '2A37',
        properties: ['read', 'notify'],
        value: 'Hello World',
      },
    ],
  },
]);

// Stop advertising
stopAdvertising();
```

### Advanced Usage with All Advertising Data Types

```typescript
import {
  startAdvertising,
  updateAdvertisingData,
  getAdvertisingData,
  type AdvertisingDataTypes,
} from 'munim-bluetooth-peripheral';

// Comprehensive advertising data configuration
const advertisingData: AdvertisingDataTypes = {
  // 0x01 - Flags (LE General Discoverable Mode, BR/EDR Not Supported)
  flags: 0x06,

  // 0x02-0x07 - Service UUIDs
  completeServiceUUIDs16: ['180D', '180F'],
  incompleteServiceUUIDs128: ['0000180D-0000-1000-8000-00805F9B34FB'],

  // 0x08-0x09 - Local Name
  completeLocalName: 'My Smart Device',
  shortenedLocalName: 'SmartDev',

  // 0x0A - Tx Power Level (in dBm)
  txPowerLevel: -12,

  // 0x0D - Class of Device
  classOfDevice: 0x240404, // Health Device

  // 0x0E-0x0F - Simple Pairing
  simplePairingHashC: 'A1B2C3D4E5F6',
  simplePairingRandomizerR: '1234567890AB',

  // 0x10-0x11 - Security Manager
  securityManagerTKValue: '000000000000',
  securityManagerOOFlags: 0x01,

  // 0x12 - Slave Connection Interval Range
  slaveConnectionIntervalRange: {
    min: 6, // 7.5ms
    max: 3200, // 4000ms
  },

  // 0x14-0x15 - Service Solicitation
  serviceSolicitationUUIDs16: ['180D'],
  serviceSolicitationUUIDs128: ['0000180D-0000-1000-8000-00805F9B34FB'],

  // 0x16, 0x20, 0x21 - Service Data
  serviceData16: [
    { uuid: '180D', data: '0102030405' },
    { uuid: '180F', data: '060708090A' },
  ],
  serviceData32: [
    { uuid: '0000180D-0000-1000-8000-00805F9B34FB', data: '0B0C0D0E0F' },
  ],

  // 0x17-0x18 - Target Address
  publicTargetAddress: '112233445566',
  randomTargetAddress: 'AABBCCDDEEFF',

  // 0x19 - Appearance
  appearance: 0x03c0, // Generic Watch

  // 0x1A - Advertising Interval
  advertisingInterval: 100, // 100ms

  // 0x1B - LE Bluetooth Device Address
  leBluetoothDeviceAddress: '112233445566',

  // 0x1C - LE Role
  leRole: 'peripheral',

  // 0x1D-0x1E - Simple Pairing (256-bit)
  simplePairingHashC256:
    'A1B2C3D4E5F6789012345678901234567890ABCDEF1234567890ABCDEF123456',
  simplePairingRandomizerR256:
    '1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF',

  // 0x1F - Service Solicitation (32-bit)
  serviceSolicitationUUIDs32: ['0000180D'],

  // 0x22-0x23 - LE Secure Connections
  leSecureConnectionsConfirmationValue:
    'A1B2C3D4E5F6789012345678901234567890ABCDEF1234567890ABCDEF123456',
  leSecureConnectionsRandomValue:
    '1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF',

  // 0x24 - URI
  uri: 'https://example.com/device',

  // 0x25 - Indoor Positioning
  indoorPositioning: {
    floor: 2,
    room: 15,
    coordinates: {
      x: 12.5,
      y: 8.3,
      z: 1.2,
    },
  },

  // 0x26 - Transport Discovery Data
  transportDiscoveryData: [
    { transportType: 'usb', data: '010203' },
    { transportType: 'nfc', data: '040506' },
  ],

  // 0x27 - LE Supported Features
  leSupportedFeatures: [0x01, 0x02, 0x04], // 2M PHY, Coded PHY, Extended Advertising

  // 0x28 - Channel Map Update Indication
  channelMapUpdateIndication: '0102030405',

  // 0x29-0x2B - Mesh
  pbAdv: '0102030405060708',
  meshMessage: '090A0B0C0D0E0F10',
  meshBeacon: '1112131415161718',

  // 0x2C-0x2D - LE Audio
  bigInfo: '1920212223242526',
  broadcastCode: '2728292A2B2C2D2E',

  // 0x2E - Resolvable Set Identifier
  resolvableSetIdentifier: '2F30313233343536',

  // 0x2F - Advertising Interval Long
  advertisingIntervalLong: 1000, // 1000ms

  // 0x30 - Broadcast Isochronous Stream Data
  bisData: '3738393A3B3C3D3E',

  // 0x3D - 3D Information Data
  threeDInformationData: '3F40414243444546',

  // 0xFF - Manufacturer Specific Data
  manufacturerData: '4748494A4B4C4D4E',
};

// Start advertising with comprehensive data
startAdvertising({
  serviceUUIDs: ['180D', '180F'],
  advertisingData: advertisingData,
});

// Update advertising data dynamically
updateAdvertisingData({
  flags: 0x04,
  completeLocalName: 'Updated Device Name',
  txPowerLevel: -8,
});

// Get current advertising data
const currentData = await getAdvertisingData();
console.log('Current advertising data:', currentData);
```

## 🔧 API Reference

### Functions

#### `startAdvertising(options)`

Starts BLE advertising with the specified options.

**Parameters:**

- `options` (object):
  - `serviceUUIDs` (string[]): Array of service UUIDs to advertise
  - `localName?` (string): Device name (legacy support)
  - `manufacturerData?` (string): Manufacturer data in hex format (legacy support)
  - `advertisingData?` (AdvertisingDataTypes): Comprehensive advertising data

#### `updateAdvertisingData(advertisingData)`

Updates the advertising data while advertising is active.

**Parameters:**

- `advertisingData` (AdvertisingDataTypes): New advertising data

#### `getAdvertisingData()`

Returns a Promise that resolves to the current advertising data.

**Returns:** Promise<AdvertisingDataTypes>

#### `stopAdvertising()`

Stops BLE advertising.

#### `setServices(services)`

Sets GATT services and characteristics.

**Parameters:**

- `services` (array): Array of service objects

### Types

#### `AdvertisingDataTypes`

Comprehensive interface for all BLE advertising data types:

```typescript
interface AdvertisingDataTypes {
  // 0x01 - Flags
  flags?: number;

  // 0x02-0x07 - Service UUIDs
  incompleteServiceUUIDs16?: string[];
  completeServiceUUIDs16?: string[];
  incompleteServiceUUIDs32?: string[];
  completeServiceUUIDs32?: string[];
  incompleteServiceUUIDs128?: string[];
  completeServiceUUIDs128?: string[];

  // 0x08-0x09 - Local Name
  shortenedLocalName?: string;
  completeLocalName?: string;

  // 0x0A - Tx Power Level
  txPowerLevel?: number;

  // 0x0D - Class of Device
  classOfDevice?: number;

  // 0x0E-0x0F - Simple Pairing
  simplePairingHashC?: string;
  simplePairingRandomizerR?: string;

  // 0x10-0x11 - Security Manager
  securityManagerTKValue?: string;
  securityManagerOOFlags?: number;

  // 0x12 - Slave Connection Interval Range
  slaveConnectionIntervalRange?: {
    min: number;
    max: number;
  };

  // 0x14-0x15 - Service Solicitation
  serviceSolicitationUUIDs16?: string[];
  serviceSolicitationUUIDs128?: string[];

  // 0x16, 0x20, 0x21 - Service Data
  serviceData16?: Array<{
    uuid: string;
    data: string;
  }>;
  serviceData32?: Array<{
    uuid: string;
    data: string;
  }>;
  serviceData128?: Array<{
    uuid: string;
    data: string;
  }>;

  // 0x17-0x18 - Target Address
  publicTargetAddress?: string;
  randomTargetAddress?: string;

  // 0x19 - Appearance
  appearance?: number;

  // 0x1A - Advertising Interval
  advertisingInterval?: number;

  // 0x1B - LE Bluetooth Device Address
  leBluetoothDeviceAddress?: string;

  // 0x1C - LE Role
  leRole?: 'central' | 'peripheral';

  // 0x1D-0x1E - Simple Pairing (256-bit)
  simplePairingHashC256?: string;
  simplePairingRandomizerR256?: string;

  // 0x1F - Service Solicitation (32-bit)
  serviceSolicitationUUIDs32?: string[];

  // 0x22-0x23 - LE Secure Connections
  leSecureConnectionsConfirmationValue?: string;
  leSecureConnectionsRandomValue?: string;

  // 0x24 - URI
  uri?: string;

  // 0x25 - Indoor Positioning
  indoorPositioning?: {
    floor?: number;
    room?: number;
    coordinates?: {
      x: number;
      y: number;
      z?: number;
    };
  };

  // 0x26 - Transport Discovery Data
  transportDiscoveryData?: Array<{
    transportType: 'usb' | 'nfc' | 'wifi' | 'bluetooth';
    data: string;
  }>;

  // 0x27 - LE Supported Features
  leSupportedFeatures?: number[];

  // 0x28 - Channel Map Update Indication
  channelMapUpdateIndication?: string;

  // 0x29-0x2B - Mesh
  pbAdv?: string;
  meshMessage?: string;
  meshBeacon?: string;

  // 0x2C-0x2D - LE Audio
  bigInfo?: string;
  broadcastCode?: string;

  // 0x2E - Resolvable Set Identifier
  resolvableSetIdentifier?: string;

  // 0x2F - Advertising Interval Long
  advertisingIntervalLong?: number;

  // 0x30 - Broadcast Isochronous Stream Data
  bisData?: string;

  // 0x3D - 3D Information Data
  threeDInformationData?: string;

  // 0xFF - Manufacturer Specific Data
  manufacturerData?: string;
}
```

## BLE Advertising Data Types Reference

| Hex              | Type Name                         | Description                     | Example                                                                   |
| ---------------- | --------------------------------- | ------------------------------- | ------------------------------------------------------------------------- |
| 0x01             | Flags                             | Basic device capabilities       | `flags: 0x06`                                                             |
| 0x02-0x07        | Service UUIDs                     | Service UUIDs offered           | `completeServiceUUIDs16: ['180D']`                                        |
| 0x08-0x09        | Local Name                        | Device name                     | `completeLocalName: 'My Device'`                                          |
| 0x0A             | Tx Power Level                    | Transmit power in dBm           | `txPowerLevel: -12`                                                       |
| 0x0D             | Class of Device                   | Bluetooth device class          | `classOfDevice: 0x240404`                                                 |
| 0x0E-0x0F        | Simple Pairing                    | Secure pairing data             | `simplePairingHashC: 'A1B2C3D4E5F6'`                                      |
| 0x10-0x11        | Security Manager                  | Security manager data           | `securityManagerTKValue: '000000000000'`                                  |
| 0x12             | Slave Connection Interval Range   | Preferred intervals             | `slaveConnectionIntervalRange: {min: 6, max: 3200}`                       |
| 0x14-0x15        | Service Solicitation              | Services being sought           | `serviceSolicitationUUIDs16: ['180D']`                                    |
| 0x16, 0x20, 0x21 | Service Data                      | Data associated with services   | `serviceData16: [{uuid: '180D', data: '010203'}]`                         |
| 0x17-0x18        | Target Address                    | Target peer addresses           | `publicTargetAddress: '112233445566'`                                     |
| 0x19             | Appearance                        | Appearance category             | `appearance: 0x03C0`                                                      |
| 0x1A             | Advertising Interval              | Interval between packets        | `advertisingInterval: 100`                                                |
| 0x1B             | LE Bluetooth Device Address       | LE device's static address      | `leBluetoothDeviceAddress: '112233445566'`                                |
| 0x1C             | LE Role                           | Device's LE role                | `leRole: 'peripheral'`                                                    |
| 0x1D-0x1E        | Simple Pairing (256-bit)          | 256-bit secure pairing          | `simplePairingHashC256: 'A1B2C3D4E5F67890...'`                            |
| 0x1F             | Service Solicitation (32-bit)     | 32-bit services being solicited | `serviceSolicitationUUIDs32: ['0000180D']`                                |
| 0x22-0x23        | LE Secure Connections             | Secure Connections data         | `leSecureConnectionsConfirmationValue: 'A1B2C3D4E5F67890...'`             |
| 0x24             | URI                               | URI (URL, file path)            | `uri: 'https://example.com'`                                              |
| 0x25             | Indoor Positioning                | Indoor location info            | `indoorPositioning: {floor: 2, room: 15, coordinates: {x: 12.5, y: 8.3}}` |
| 0x26             | Transport Discovery Data          | Transport type info             | `transportDiscoveryData: [{transportType: 'usb', data: '010203'}]`        |
| 0x27             | LE Supported Features             | LE features supported           | `leSupportedFeatures: [0x01, 0x02, 0x04]`                                 |
| 0x28             | Channel Map Update Indication     | Channel map updates             | `channelMapUpdateIndication: '0102030405'`                                |
| 0x29-0x2B        | Mesh                              | BLE Mesh data                   | `pbAdv: '0102030405060708'`                                               |
| 0x2C-0x2D        | LE Audio                          | LE Audio data                   | `bigInfo: '1920212223242526'`                                             |
| 0x2E             | Resolvable Set Identifier         | RSI for sync transfer           | `resolvableSetIdentifier: '2F30313233343536'`                             |
| 0x2F             | Advertising Interval Long         | Long interval value             | `advertisingIntervalLong: 1000`                                           |
| 0x30             | Broadcast Isochronous Stream Data | BIS packets                     | `bisData: '3738393A3B3C3D3E'`                                             |
| 0x3D             | 3D Information Data               | 3D positioning support          | `threeDInformationData: '3F40414243444546'`                               |
| 0xFF             | Manufacturer Specific Data        | Vendor-defined data             | `manufacturerData: '4748494A4B4C4D4E'`                                    |

## 📖 Usage Examples

### Health Device Example

```typescript
import { startAdvertising, setServices } from 'munim-bluetooth-peripheral';

// Health device advertising
startAdvertising({
  serviceUUIDs: ['180D', '180F'], // Heart Rate, Battery Service
  advertisingData: {
    flags: 0x06, // LE General Discoverable Mode, BR/EDR Not Supported
    completeLocalName: 'Health Monitor',
    appearance: 0x03c0, // Generic Watch
    txPowerLevel: -8,
    manufacturerData: '0102030405', // Custom health data
    serviceData16: [
      { uuid: '180D', data: '6400' }, // Heart rate: 100 bpm
      { uuid: '180F', data: '64' }, // Battery: 100%
    ],
  },
});

// Set up GATT services
setServices([
  {
    uuid: '180D', // Heart Rate Service
    characteristics: [
      {
        uuid: '2A37', // Heart Rate Measurement
        properties: ['read', 'notify'],
        value: '6400', // 100 bpm
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

### Smart Home Device Example

```typescript
import {
  startAdvertising,
  updateAdvertisingData,
} from 'munim-bluetooth-peripheral';

// Smart home device
startAdvertising({
  serviceUUIDs: ['1812', '180F'], // HID, Battery Service
  advertisingData: {
    flags: 0x04, // LE General Discoverable Mode
    completeLocalName: 'Smart Light Bulb',
    appearance: 0x03c1, // Generic Light Fixture
    uri: 'https://myhome.com/light1',
    manufacturerData: '0102030405', // Custom light data
    serviceData16: [
      { uuid: '1812', data: '01' }, // HID: Keyboard
      { uuid: '180F', data: '64' }, // Battery: 100%
    ],
  },
});

// Update advertising data when light state changes
updateAdvertisingData({
  manufacturerData: '0102030406', // Updated light data
  serviceData16: [
    { uuid: '1812', data: '02' }, // HID: Mouse
    { uuid: '180F', data: '50' }, // Battery: 80%
  ],
});
```

### Basic Peripheral Setup

```js
import React, { useEffect } from 'react';
import {
  startAdvertising,
  stopAdvertising,
  setServices,
  addListener,
  removeListeners,
} from 'munim-bluetooth-peripheral';

const MyPeripheral = () => {
  useEffect(() => {
    // Configure services
    setServices([
      {
        uuid: '1800', // Generic Access Service
        characteristics: [
          {
            uuid: '2a00', // Device Name
            properties: ['read'],
            value: 'MyDevice',
          },
          {
            uuid: '2a01', // Appearance
            properties: ['read'],
            value: '0x03C0', // Generic Computer
          },
        ],
      },
      {
        uuid: '1801', // Generic Attribute Service
        characteristics: [
          {
            uuid: '2a05', // Service Changed
            properties: ['indicate'],
          },
        ],
      },
    ]);

    // Start advertising
    startAdvertising({
      serviceUUIDs: ['1800', '1801'],
      localName: 'MyReactNativePeripheral',
    });

    // Cleanup on unmount
    return () => {
      stopAdvertising();
      removeListeners('connectionStateChanged');
    };
  }, []);

  return <Text>Peripheral is running...</Text>;
};
```

### Custom Service with Notifications

```js
import React, { useState, useEffect } from 'react';
import {
  startAdvertising,
  stopAdvertising,
  setServices,
  addListener,
} from 'munim-bluetooth-peripheral';

const SensorPeripheral = () => {
  const [sensorValue, setSensorValue] = useState(0);

  useEffect(() => {
    // Create a custom sensor service
    setServices([
      {
        uuid: '12345678-1234-5678-1234-56789abcdef0',
        characteristics: [
          {
            uuid: 'abcdefab-1234-5678-1234-56789abcdef0',
            properties: ['read', 'notify'],
            value: sensorValue.toString(),
          },
          {
            uuid: 'fedcbaab-1234-5678-1234-56789abcdef0',
            properties: ['write'],
          },
        ],
      },
    ]);

    startAdvertising({
      serviceUUIDs: ['12345678-1234-5678-1234-56789abcdef0'],
      localName: 'SensorPeripheral',
    });

    // Listen for connection events
    addListener('connectionStateChanged', (state) => {
      console.log('Connection state:', state);
    });

    // Simulate sensor updates
    const interval = setInterval(() => {
      setSensorValue((prev) => prev + 1);
    }, 1000);

    return () => {
      clearInterval(interval);
      stopAdvertising();
    };
  }, [sensorValue]);

  return <Text>Sensor Value: {sensorValue}</Text>;
};
```

## 🔍 Troubleshooting

### Common Issues

1. **Permission Denied**: Ensure you have the necessary Bluetooth permissions in your app
2. **Advertising Not Starting**: Check that Bluetooth is enabled on the device
3. **Services Not Visible**: Verify that your service UUIDs are properly formatted

### Expo-Specific Issues

1. **Development Build Required**: This library requires a development build in Expo. Use `npx expo run:ios` or `npx expo run:android`
2. **Permissions Not Working**: Make sure you've added the permissions to your `app.json` as shown in the setup section
3. **Build Errors**: Ensure you're using Expo SDK 50+ and have the latest Expo CLI

### Debug Mode

Enable debug logging by setting the following environment variable:

```bash
export REACT_NATIVE_BLUETOOTH_DEBUG=1
```

## 👏 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details on how to submit pull requests, report issues, and contribute to the project.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<img alt="Star the Munim Technologies repo on GitHub to support the project" src="https://user-images.githubusercontent.com/9664363/185428788-d762fd5d-97b3-4f59-8db7-f72405be9677.gif" width="50%">
