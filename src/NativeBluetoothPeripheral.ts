import { NativeModules } from 'react-native';

const { BluetoothPeripheral } = NativeModules;

/**
 * Start advertising as a Bluetooth peripheral.
 *
 * @param options - An object with serviceUUIDs (string[]) and optional localName/manufacturerData.
 *                  This must be a plain JS object (no Maps/Sets/functions).
 */
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

/**
 * Set GATT services and characteristics for the Bluetooth peripheral.
 *
 * @param services - An array of service objects, each with a uuid and an array of characteristics.
 *                  This must be serializable to a plain JS array (no Maps/Sets/functions).
 */
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
