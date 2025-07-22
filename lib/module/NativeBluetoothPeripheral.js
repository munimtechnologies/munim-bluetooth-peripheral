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