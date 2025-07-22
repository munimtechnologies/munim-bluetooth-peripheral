import type { TurboModule } from 'react-native';
export interface Spec extends TurboModule {
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
    addListener(eventName: string): void;
    removeListeners(count: number): void;
}
declare const _default: Spec;
export default _default;
//# sourceMappingURL=NativeBluetoothPeripheral.d.ts.map