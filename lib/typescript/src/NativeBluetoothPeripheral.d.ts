/**
 * Start advertising as a Bluetooth peripheral.
 *
 * @param options - An object with serviceUUIDs (string[]) and optional localName/manufacturerData.
 *                  This must be a plain JS object (no Maps/Sets/functions).
 */
export declare function startAdvertising(options: {
    serviceUUIDs: string[];
    localName?: string;
    manufacturerData?: string;
}): void;
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
//# sourceMappingURL=NativeBluetoothPeripheral.d.ts.map