import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  multiply(a: number, b: number): number;
  startAdvertising(options: {
    serviceUUIDs: string[];
    localName?: string;
    manufacturerData?: string;
  }): void;
  stopAdvertising(): void;
  setServices(services: Array<{
    uuid: string;
    characteristics: Array<{
      uuid: string;
      properties: string[];
      value?: string;
    }>;
  }>): void;
  // Event subscription methods (for connection, read, write, etc.)
  addListener(eventName: string): void;
  removeListeners(count: number): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('BluetoothPeripheral');
