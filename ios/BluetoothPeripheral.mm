#import "BluetoothPeripheral.h"
#import <CoreBluetooth/CoreBluetooth.h>

@interface BluetoothPeripheral () <CBPeripheralManagerDelegate>
@property (nonatomic, strong) CBPeripheralManager *peripheralManager;
@property (nonatomic, strong) NSMutableArray<CBMutableService *> *services;
@end

@implementation BluetoothPeripheral
RCT_EXPORT_MODULE()

- (instancetype)init {
    if (self = [super init]) {
        _peripheralManager = [[CBPeripheralManager alloc] initWithDelegate:self queue:nil];
        _services = [NSMutableArray array];
    }
    return self;
}

RCT_EXPORT_METHOD(startAdvertising:(NSDictionary *)options) {
    if (self.peripheralManager.state != CBManagerStatePoweredOn) {
        // Peripheral manager not ready
        return;
    }
    NSString *localName = options["localName"];
    NSArray *serviceUUIDs = options["serviceUUIDs"];
    NSMutableDictionary *advertisingData = [NSMutableDictionary dictionary];
    if (localName) {
        advertisingData[CBAdvertisementDataLocalNameKey] = localName;
    }
    if (serviceUUIDs) {
        NSMutableArray *uuids = [NSMutableArray array];
        for (NSString *uuidStr in serviceUUIDs) {
            [uuids addObject:[[CBUUID alloc] initWithString:uuidStr]];
        }
        advertisingData[CBAdvertisementDataServiceUUIDsKey] = uuids;
    }
    [self.peripheralManager startAdvertising:advertisingData];
}

RCT_EXPORT_METHOD(stopAdvertising) {
    [self.peripheralManager stopAdvertising];
}

RCT_EXPORT_METHOD(setServices:(NSArray *)services) {
    [self.services removeAllObjects];
    for (NSDictionary *serviceDict in services) {
        NSString *uuid = serviceDict["uuid"];
        CBMutableService *service = [[CBMutableService alloc] initWithType:[CBUUID UUIDWithString:uuid] primary:YES];
        NSMutableArray *characteristics = [NSMutableArray array];
        for (NSDictionary *charDict in serviceDict["characteristics"]) {
            NSString *charUUID = charDict["uuid"];
            NSArray *propertiesArr = charDict["properties"];
            CBCharacteristicProperties properties = 0;
            for (NSString *prop in propertiesArr) {
                if ([prop isEqualToString:@"read"]) properties |= CBCharacteristicPropertyRead;
                if ([prop isEqualToString:@"write"]) properties |= CBCharacteristicPropertyWrite;
                if ([prop isEqualToString:@"notify"]) properties |= CBCharacteristicPropertyNotify;
            }
            NSData *value = nil;
            if (charDict["value"]) {
                value = [charDict["value"] dataUsingEncoding:NSUTF8StringEncoding];
            }
            CBMutableCharacteristic *characteristic = [[CBMutableCharacteristic alloc] initWithType:[CBUUID UUIDWithString:charUUID] properties:properties value:value permissions:CBAttributePermissionsReadable|CBAttributePermissionsWriteable];
            [characteristics addObject:characteristic];
        }
        service.characteristics = characteristics;
        [self.services addObject:service];
    }
    [self.peripheralManager removeAllServices];
    for (CBMutableService *service in self.services) {
        [self.peripheralManager addService:service];
    }
}

- (NSNumber *)multiply:(double)a b:(double)b {
    NSNumber *result = @(a * b);

    return result;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeBluetoothPeripheralSpecJSI>(params);
}

@end
