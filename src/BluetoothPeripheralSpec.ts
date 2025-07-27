import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

// BLE Advertising Data Types
export interface AdvertisingDataTypes {
  // 0x01 - Flags
  flags?: number;

  // 0x02-0x07 - Service UUIDs
  incompleteServiceUUIDs16?: string[];
  completeServiceUUIDs16?: string[];
  incompleteServiceUUIDs32?: string[];
  completeServiceUUIDs32?: string[];
  incompleteServiceUUIDs128?: string[];
  completeServiceUUIDs128?: string[];

  // 0x08-0x09 - Local Name
  shortenedLocalName?: string;
  completeLocalName?: string;

  // 0x0A - Tx Power Level
  txPowerLevel?: number;

  // 0x0D - Class of Device
  classOfDevice?: number;

  // 0x0E-0x0F - Simple Pairing
  simplePairingHashC?: string;
  simplePairingRandomizerR?: string;

  // 0x10-0x11 - Security Manager
  securityManagerTKValue?: string;
  securityManagerOOFlags?: number;

  // 0x12 - Slave Connection Interval Range
  slaveConnectionIntervalRange?: {
    min: number;
    max: number;
  };

  // 0x14-0x15 - Service Solicitation
  serviceSolicitationUUIDs16?: string[];
  serviceSolicitationUUIDs128?: string[];

  // 0x16, 0x20, 0x21 - Service Data
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

  // 0x17-0x18 - Target Address
  publicTargetAddress?: string;
  randomTargetAddress?: string;

  // 0x19 - Appearance
  appearance?: number;

  // 0x1A - Advertising Interval
  advertisingInterval?: number;

  // 0x1B - LE Bluetooth Device Address
  leBluetoothDeviceAddress?: string;

  // 0x1C - LE Role
  leRole?: 'central' | 'peripheral';

  // 0x1D-0x1E - Simple Pairing (256-bit)
  simplePairingHashC256?: string;
  simplePairingRandomizerR256?: string;

  // 0x1F - Service Solicitation (32-bit)
  serviceSolicitationUUIDs32?: string[];

  // 0x22-0x23 - LE Secure Connections
  leSecureConnectionsConfirmationValue?: string;
  leSecureConnectionsRandomValue?: string;

  // 0x24 - URI
  uri?: string;

  // 0x25 - Indoor Positioning
  indoorPositioning?: {
    floor?: number;
    room?: number;
    coordinates?: {
      x: number;
      y: number;
      z?: number;
    };
  };

  // 0x26 - Transport Discovery Data
  transportDiscoveryData?: Array<{
    transportType: 'usb' | 'nfc' | 'wifi' | 'bluetooth';
    data: string;
  }>;

  // 0x27 - LE Supported Features
  leSupportedFeatures?: number[];

  // 0x28 - Channel Map Update Indication
  channelMapUpdateIndication?: string;

  // 0x29-0x2B - Mesh
  pbAdv?: string;
  meshMessage?: string;
  meshBeacon?: string;

  // 0x2C-0x2D - LE Audio
  bigInfo?: string;
  broadcastCode?: string;

  // 0x2E - Resolvable Set Identifier
  resolvableSetIdentifier?: string;

  // 0x2F - Advertising Interval Long
  advertisingIntervalLong?: number;

  // 0x30 - Broadcast Isochronous Stream Data
  bisData?: string;

  // 0x3D - 3D Information Data
  threeDInformationData?: string;

  // 0xFF - Manufacturer Specific Data
  manufacturerData?: string;
}

export interface Spec extends TurboModule {
  /**
   * Start advertising as a Bluetooth peripheral with comprehensive advertising data support.
   *
   * @param options - An object with serviceUUIDs (string[]) and comprehensive advertising data types.
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
