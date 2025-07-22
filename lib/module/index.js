"use strict";

import BluetoothPeripheral from "./NativeBluetoothPeripheral.js";
export function startAdvertising(options) {
  return BluetoothPeripheral.startAdvertising(options);
}
export function stopAdvertising() {
  return BluetoothPeripheral.stopAdvertising();
}
export function setServices(services) {
  return BluetoothPeripheral.setServices(services);
}
export function addListener(eventName) {
  return BluetoothPeripheral.addListener(eventName);
}
export function removeListeners(count) {
  return BluetoothPeripheral.removeListeners(count);
}
//# sourceMappingURL=index.js.map