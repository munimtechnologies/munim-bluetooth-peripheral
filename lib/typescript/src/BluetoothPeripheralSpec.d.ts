import type { TurboModule } from 'react-native';
export interface AdvertisingDataTypes {
    flags?: number;
    incompleteServiceUUIDs16?: string[];
    completeServiceUUIDs16?: string[];
    incompleteServiceUUIDs32?: string[];
    completeServiceUUIDs32?: string[];
    incompleteServiceUUIDs128?: string[];
    completeServiceUUIDs128?: string[];
    shortenedLocalName?: string;
    completeLocalName?: string;
    txPowerLevel?: number;
    classOfDevice?: number;
    simplePairingHashC?: string;
    simplePairingRandomizerR?: string;
    securityManagerTKValue?: string;
    securityManagerOOFlags?: number;
    slaveConnectionIntervalRange?: {
        min: number;
        max: number;
    };
    serviceSolicitationUUIDs16?: string[];
    serviceSolicitationUUIDs128?: string[];
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
    publicTargetAddress?: string;
    randomTargetAddress?: string;
    appearance?: number;
    advertisingInterval?: number;
    leBluetoothDeviceAddress?: string;
    leRole?: 'central' | 'peripheral';
    simplePairingHashC256?: string;
    simplePairingRandomizerR256?: string;
    serviceSolicitationUUIDs32?: string[];
    leSecureConnectionsConfirmationValue?: string;
    leSecureConnectionsRandomValue?: string;
    uri?: string;
    indoorPositioning?: {
        floor?: number;
        room?: number;
        coordinates?: {
            x: number;
            y: number;
            z?: number;
        };
    };
    transportDiscoveryData?: Array<{
        transportType: 'usb' | 'nfc' | 'wifi' | 'bluetooth';
        data: string;
    }>;
    leSupportedFeatures?: number[];
    channelMapUpdateIndication?: string;
    pbAdv?: string;
    meshMessage?: string;
    meshBeacon?: string;
    bigInfo?: string;
    broadcastCode?: string;
    resolvableSetIdentifier?: string;
    advertisingIntervalLong?: number;
    bisData?: string;
    threeDInformationData?: string;
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
    setServices(services: Array<{
        uuid: string;
        characteristics: Array<{
            uuid: string;
            properties: string[];
            value?: string;
        }>;
    }>): void;
    addListener(eventName: string): void;
    removeListeners(count: number): void;
}
declare const _default: Spec;
export default _default;
//# sourceMappingURL=BluetoothPeripheralSpec.d.ts.map