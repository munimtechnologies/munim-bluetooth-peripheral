# react-native-bluetooth-peripheral

Bluetooth peripheral

## Installation

```sh
npm install react-native-bluetooth-peripheral
```

## Usage

```js
import { multiply } from 'react-native-bluetooth-peripheral';

// ...

const result = multiply(3, 7);
```

## Usage Example

```js
import {
  startAdvertising,
  stopAdvertising,
  setServices,
} from 'react-native-bluetooth-peripheral';

// Set up a GATT service with a characteristic
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

// To stop advertising
// stopAdvertising();
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
