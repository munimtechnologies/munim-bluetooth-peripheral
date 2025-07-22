import { useState } from 'react';
import { Text, View, StyleSheet, Button } from 'react-native';
import {
  startAdvertising,
  stopAdvertising,
  setServices,
} from 'react-native-bluetooth-peripheral';

export default function App() {
  const [advertising, setAdvertising] = useState(false);

  const handleStart = () => {
    setServices([
      {
        uuid: '12345678-1234-5678-1234-56789abcdef0',
        characteristics: [
          {
            uuid: '87654321-4321-6789-4321-0fedcba98765',
            properties: ['read', 'write', 'notify'],
            value: 'hello',
          },
        ],
      },
    ]);
    startAdvertising({
      serviceUUIDs: ['12345678-1234-5678-1234-56789abcdef0'],
      localName: 'RNPeripheral',
    });
    setAdvertising(true);
  };

  const handleStop = () => {
    stopAdvertising();
    setAdvertising(false);
  };

  return (
    <View style={styles.container}>
      <Text style={{ marginBottom: 16 }}>Bluetooth Peripheral Example</Text>
      <Button
        title={advertising ? 'Stop Advertising' : 'Start Advertising'}
        onPress={advertising ? handleStop : handleStart}
      />
      <Text style={{ marginTop: 16 }}>
        Status: {advertising ? 'Advertising' : 'Idle'}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
