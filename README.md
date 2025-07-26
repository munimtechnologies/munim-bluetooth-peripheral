<!-- Banner Image -->

<p align="center">
  <a href="https://github.com/munimtechnologies/munim-bluetooth-peripheral">
    <img alt="Munim Technologies Bluetooth Peripheral" height="128" src="./.github/resources/banner.png">
    <h1 align="center">munim-bluetooth-peripheral</h1>
  </a>
</p>

<p align="center">
   <a aria-label="Package version" href="https://www.npmjs.com/package/munim-bluetooth-peripheral" target="_blank">
    <img alt="Package version" src="https://img.shields.io/npm/v/munim-bluetooth-peripheral.svg?style=flat-square&label=Version&labelColor=000000&color=0066CC" />
  </a>
  <a aria-label="Chat or ask a question" href="https://github.com/munimtechnologies/munim-bluetooth-peripheral/discussions" target="_blank">
    <img alt="Chat or ask a question" src="https://img.shields.io/badge/Discussions-000000.svg?style=flat-square&labelColor=000000&color=0066CC&logo=github&logoColor=FFFFFF&label=Chat%20with%20us" />
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
&ensp;•&ensp;
  <a aria-label="request features" href="https://github.com/munimtechnologies/munim-bluetooth-peripheral/discussions">Request Features</a>
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

## Features

- 🔵 **BLE Peripheral Mode**: Transform your React Native app into a BLE peripheral device
- 📡 **Service Advertising**: Advertise custom GATT services with multiple characteristics
- 🔄 **Real-time Communication**: Support for read, write, and notify operations
- 📱 **Cross-platform**: Works on both iOS and Android
- 🎯 **TypeScript Support**: Full TypeScript definitions included
- ⚡ **High Performance**: Built with React Native's new architecture (Fabric)
- 🚀 **Expo Compatible**: Works seamlessly with Expo managed and bare workflows

## Installation

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

## Quick Start

### React Native CLI

```js
import {
  startAdvertising,
  stopAdvertising,
  setServices,
  addListener,
  removeListeners,
} from 'munim-bluetooth-peripheral';

// Set up your GATT services
setServices([
  {
    uuid: '12345678-1234-5678-1234-56789abcdef0',
    characteristics: [
      {
        uuid: 'abcdefab-1234-5678-1234-56789abcdef0',
        properties: ['read', 'write', 'notify'],
        value: 'Hello World', // Optional initial value
      },
    ],
  },
]);

// Start advertising
startAdvertising({
  serviceUUIDs: ['12345678-1234-5678-1234-56789abcdef0'],
  localName: 'MyPeripheral',
});

// Stop advertising when done
// stopAdvertising();
```

### Expo

For Expo projects, you can use the same API. The library is fully compatible with Expo managed and bare workflows:

```js
import {
  startAdvertising,
  stopAdvertising,
  setServices,
  addListener,
  removeListeners,
} from 'munim-bluetooth-peripheral';

// Same usage as React Native CLI
setServices([
  {
    uuid: '12345678-1234-5678-1234-56789abcdef0',
    characteristics: [
      {
        uuid: 'abcdefab-1234-5678-1234-56789abcdef0',
        properties: ['read', 'write', 'notify'],
        value: 'Hello World',
      },
    ],
  },
]);

startAdvertising({
  serviceUUIDs: ['12345678-1234-5678-1234-56789abcdef0'],
  localName: 'MyExpoPeripheral',
});
```

## API Reference

### `setServices(services: Service[])`

Configure the GATT services that your peripheral will advertise.

```typescript
interface Service {
  uuid: string;
  characteristics: Characteristic[];
}

interface Characteristic {
  uuid: string;
  properties: ('read' | 'write' | 'notify')[];
  value?: string;
}
```

### `startAdvertising(options: AdvertisingOptions)`

Start advertising your peripheral device.

```typescript
interface AdvertisingOptions {
  serviceUUIDs: string[];
  localName?: string;
  manufacturerData?: string;
}
```

### `stopAdvertising()`

Stop advertising your peripheral device.

### `addListener(eventName: string, callback: Function)`

Add event listeners for peripheral events.

### `removeListeners(eventName: string)`

Remove event listeners for peripheral events.

## Usage Examples

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

## Event Handling

The library provides several events you can listen to:

```js
// Connection state changes
addListener('connectionStateChanged', (state) => {
  console.log('Connection state:', state);
});

// Characteristic read requests
addListener('characteristicRead', (characteristic) => {
  console.log('Characteristic read:', characteristic);
});

// Characteristic write requests
addListener('characteristicWrite', (characteristic) => {
  console.log('Characteristic write:', characteristic);
});
```

## Troubleshooting

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

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details on how to submit pull requests, report issues, and contribute to the project.

## Support

- 📖 [Documentation](https://github.com/munimtechnologies/munim-bluetooth-peripheral#readme)
- 🐛 [Report Issues](https://github.com/munimtechnologies/munim-bluetooth-peripheral/issues)
- 💬 [Discussions](https://github.com/munimtechnologies/munim-bluetooth-peripheral/discussions)
- 📱 [Expo Documentation](https://docs.expo.dev/versions/latest/)
- 🔧 [Expo Development Builds](https://docs.expo.dev/develop/development-builds/introduction/)

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a list of changes and version history.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <img alt="Star the Munim Technologies repo on GitHub to support the project" src="https://user-images.githubusercontent.com/9664363/185428788-d762fd5d-97b3-4f59-8db7-f72405be9677.gif" width="50%">
</div>
