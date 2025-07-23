"use strict";

import { NativeModules } from 'react-native';
const {
  BluetoothPeripheral
} = NativeModules;
export function startAdvertising(options) {
  return BluetoothPeripheral.startAdvertising(options);
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
//# sourceMappingURL=NativeBluetoothPeripheral.js.map