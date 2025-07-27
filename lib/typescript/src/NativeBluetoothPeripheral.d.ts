import type { AdvertisingDataTypes } from './BluetoothPeripheralSpec';
/**
 * Start advertising as a Bluetooth peripheral with comprehensive advertising data support.
 *
 * @param options - An object with serviceUUIDs (string[]) and comprehensive advertising data types.
 *                  This must be a plain JS object (no Maps/Sets/functions).
 */
export declare function startAdvertising(options: {
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
export declare function updateAdvertisingData(advertisingData: AdvertisingDataTypes): void;
/**
 * Get current advertising data.
 *
 * @returns Promise resolving to current advertising data.
 */
export declare function getAdvertisingData(): Promise<AdvertisingDataTypes>;
export declare function stopAdvertising(): void;
/**
 * Set GATT services and characteristics for the Bluetooth peripheral.
 *
 * @param services - An array of service objects, each with a uuid and an array of characteristics.
 *                  This must be serializable to a plain JS array (no Maps/Sets/functions).
 */
export declare function setServices(services: Array<{
    uuid: string;
    characteristics: Array<{
        uuid: string;
        properties: string[];
        value?: string;
    }>;
}>): void;
export declare function addListener(_eventName: string): void;
export declare function removeListeners(_count: number): void;
export type { AdvertisingDataTypes };
//# sourceMappingURL=NativeBluetoothPeripheral.d.ts.map