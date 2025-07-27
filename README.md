# React Native Bluetooth Peripheral

A React Native library for creating Bluetooth Low Energy (BLE) peripherals that can advertise and provide GATT services.

## Features

- ✅ Start/stop BLE advertising
- ✅ Set GATT services and characteristics
- ✅ **Comprehensive support for all BLE advertising data types**
- ✅ Update advertising data dynamically
- ✅ Cross-platform (iOS & Android)
- ✅ TypeScript support

## Installation

```bash
npm install react-native-bluetooth-peripheral
# or
yarn add react-native-bluetooth-peripheral
```

### iOS

Add the following to your `ios/Podfile`:

```ruby
pod 'react-native-bluetooth-peripheral', :path => '../node_modules/react-native-bluetooth-peripheral'
```

Then run:

```bash
cd ios && pod install
```

### Android

No additional setup required for Android.

## Usage

### Basic Usage

```typescript
import {
  startAdvertising,
  stopAdvertising,
  setServices,
} from 'react-native-bluetooth-peripheral';

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
} from 'react-native-bluetooth-peripheral';

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

## API Reference

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

## Permissions

### iOS

Add the following to your `Info.plist`:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app uses Bluetooth to advertise as a peripheral device.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app uses Bluetooth to advertise as a peripheral device.</string>
```

### Android

Add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

For Android 12+ (API level 31+), also add:

```xml
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
```

## Examples

### Health Device Example

```typescript
import {
  startAdvertising,
  setServices,
} from 'react-native-bluetooth-peripheral';

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
} from 'react-native-bluetooth-peripheral';

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

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
