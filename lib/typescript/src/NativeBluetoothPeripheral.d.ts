export declare function startAdvertising(options: {
    serviceUUIDs: string[];
    localName?: string;
    manufacturerData?: string;
}): void;
export declare function stopAdvertising(): void;
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