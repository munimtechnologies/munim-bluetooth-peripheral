import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

// BLE Advertising Data Types - Only Platform-Supported Types
export interface AdvertisingDataTypes {
  // 0x01 - Flags (partial support)
  flags?: number;

  // 0x02-0x07 - Service UUIDs (fully supported)
  incompleteServiceUUIDs16?: string[];
  completeServiceUUIDs16?: string[];
  incompleteServiceUUIDs32?: string[];
  completeServiceUUIDs32?: string[];
  incompleteServiceUUIDs128?: string[];
  completeServiceUUIDs128?: string[];

  // 0x08-0x09 - Local Name (fully supported)
  shortenedLocalName?: string;
  completeLocalName?: string;

  // 0x0A - Tx Power Level (fully supported)
  txPowerLevel?: number;

  // 0x14-0x15 - Service Solicitation (fully supported)
  serviceSolicitationUUIDs16?: string[];
  serviceSolicitationUUIDs128?: string[];

  // 0x16, 0x20, 0x21 - Service Data (fully supported)
  serviceData16?: Array<{
    uuid: string;
    data: string;
  }>;
  serviceData32?: Array<{
    uuid: string;
    data: string;
  }>;
  serviceData128?: Array<{
    uuid: string;
    data: string;
  }>;

  // 0x19 - Appearance (partial support)
  appearance?: number;

  // 0x1F - Service Solicitation (32-bit) (fully supported)
  serviceSolicitationUUIDs32?: string[];

  // 0xFF - Manufacturer Specific Data (fully supported)
  manufacturerData?: string;
}

export interface Spec extends TurboModule {
  /**
   * Start advertising as a Bluetooth peripheral with supported advertising data.
   *
   * @param options - An object with serviceUUIDs (string[]) and supported advertising data types.
   *                  This must be a plain JS object (no Maps/Sets/functions).
   */
  startAdvertising(options: {
    serviceUUIDs: string[];
    localName?: string;
    manufacturerData?: string;
    advertisingData?: AdvertisingDataTypes;
  }): void;

  /**
   * Update advertising data while advertising is active.
   *
   * @param advertisingData - The new advertising data to use.
   */
  updateAdvertisingData(advertisingData: AdvertisingDataTypes): void;

  /**
   * Get current advertising data.
   *
   * @returns Promise resolving to current advertising data.
   */
  getAdvertisingData(): Promise<AdvertisingDataTypes>;

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
