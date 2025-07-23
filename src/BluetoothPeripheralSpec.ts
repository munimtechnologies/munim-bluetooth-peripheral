import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  /**
   * Start advertising as a Bluetooth peripheral.
   *
   * @param options - An object with serviceUUIDs (string[]) and optional localName/manufacturerData.
   *                  This must be a plain JS object (no Maps/Sets/functions).
   */
  startAdvertising(options: {
    serviceUUIDs: string[];
    localName?: string;
    manufacturerData?: string;
  }): void;
  stopAdvertising(): void;
  /**
   * Set GATT services and characteristics for the Bluetooth peripheral.
   *
   * @param services - An array of service objects, each with a uuid and an array of characteristics.
   *                  This must be serializable to a plain JS array (no Maps/Sets/functions).
   */
  setServices(
    services: Array<{
      uuid: string;
      characteristics: Array<{
        uuid: string;
        properties: string[];
        value?: string;
      }>;
    }>
  ): void;
  addListener(eventName: string): void;
  removeListeners(count: number): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('BluetoothPeripheral');
