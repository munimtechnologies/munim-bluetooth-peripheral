"use strict";

import { NativeModules } from 'react-native';
const {
  BluetoothPeripheral
} = NativeModules;

/**
 * Start advertising as a Bluetooth peripheral with comprehensive advertising data support.
 *
 * @param options - An object with serviceUUIDs (string[]) and comprehensive advertising data types.
 *                  This must be a plain JS object (no Maps/Sets/functions).
 */
export function startAdvertising(options) {
  return BluetoothPeripheral.startAdvertising(options);
}

/**
 * Update advertising data while advertising is active.
 *
 * @param advertisingData - The new advertising data to use.
 */
export function updateAdvertisingData(advertisingData) {
  return BluetoothPeripheral.updateAdvertisingData(advertisingData);
}

/**
 * Get current advertising data.
 *
 * @returns Promise resolving to current advertising data.
 */
export function getAdvertisingData() {
  return BluetoothPeripheral.getAdvertisingData();
}
export function stopAdvertising() {
  return BluetoothPeripheral.stopAdvertising();
}

/**
 * Set GATT services and characteristics for the Bluetooth peripheral.
 *
 * @param services - An array of service objects, each with a uuid and an array of characteristics.
 *                  This must be serializable to a plain JS array (no Maps/Sets/functions).
 */
export function setServices(services) {
  return BluetoothPeripheral.setServices(services);
}
export function addListener(_eventName) {
  // No-op for classic bridge; eventing would require NativeEventEmitter
}
export function removeListeners(_count) {
  // No-op for classic bridge
}

// Export the AdvertisingDataTypes interface for use in applications
//# sourceMappingURL=NativeBluetoothPeripheral.js.map