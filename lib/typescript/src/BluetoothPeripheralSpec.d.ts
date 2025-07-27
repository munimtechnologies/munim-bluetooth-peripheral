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
    appearance?: number;
    serviceSolicitationUUIDs32?: string[];
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