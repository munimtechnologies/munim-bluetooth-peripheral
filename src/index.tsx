import BluetoothPeripheral from './NativeBluetoothPeripheral';

export function multiply(a: number, b: number): number {
  return BluetoothPeripheral.multiply(a, b);
}

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

export function setServices(services: Array<{
  uuid: string;
  characteristics: Array<{
    uuid: string;
    properties: string[];
    value?: string;
  }>;
}>): void {
  return BluetoothPeripheral.setServices(services);
}

export function addListener(eventName: string): void {
  return BluetoothPeripheral.addListener(eventName);
}

export function removeListeners(count: number): void {
  return BluetoothPeripheral.removeListeners(count);
}
