import { NativeModules } from 'react-native';

const { BluetoothPeripheral } = NativeModules;

export function startAdvertising(options: {
  serviceUUIDs: string[];
  localName?: string;
  manufacturerData?: string;
}): void {
  return BluetoothPeripheral.startAdvertising(options);
}

export function stopAdvertising(): void {
  return BluetoothPeripheral.stopAdvertising();
}

export function setServices(
  services: Array<{
    uuid: string;
    characteristics: Array<{
      uuid: string;
      properties: string[];
      value?: string;
    }>;
  }>
): void {
  return BluetoothPeripheral.setServices(services);
}

export function addListener(_eventName: string): void {
  // No-op for classic bridge; eventing would require NativeEventEmitter
}

export function removeListeners(_count: number): void {
  // No-op for classic bridge
}
