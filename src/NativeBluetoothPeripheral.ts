import { NativeModules } from 'react-native';
import type { AdvertisingDataTypes } from './BluetoothPeripheralSpec';

const { BluetoothPeripheral } = NativeModules;

/**
 * Start advertising as a Bluetooth peripheral with comprehensive advertising data support.
 *
 * @param options - An object with serviceUUIDs (string[]) and comprehensive advertising data types.
 *                  This must be a plain JS object (no Maps/Sets/functions).
 */
export function startAdvertising(options: {
  serviceUUIDs: string[];
  localName?: string;
  manufacturerData?: string;
  advertisingData?: AdvertisingDataTypes;
}): void {
  return BluetoothPeripheral.startAdvertising(options);
}

/**
 * Update advertising data while advertising is active.
 *
 * @param advertisingData - The new advertising data to use.
 */
export function updateAdvertisingData(
  advertisingData: AdvertisingDataTypes
): void {
  return BluetoothPeripheral.updateAdvertisingData(advertisingData);
}

/**
 * Get current advertising data.
 *
 * @returns Promise resolving to current advertising data.
 */
export function getAdvertisingData(): Promise<AdvertisingDataTypes> {
  return BluetoothPeripheral.getAdvertisingData();
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

// Export the AdvertisingDataTypes interface for use in applications
export type { AdvertisingDataTypes };
