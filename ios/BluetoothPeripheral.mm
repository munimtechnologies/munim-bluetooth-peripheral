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

// Defensive: Ensure method signature matches protocol and type check options
- (void)startAdvertising:(id)options {
    if (self.peripheralManager.state != CBManagerStatePoweredOn) {
        // Peripheral manager not ready
        return;
    }
    if (![options isKindOfClass:[NSDictionary class]]) {
        return;
    }
    NSDictionary *dictOptions = (NSDictionary *)options;
    NSString *localName = dictOptions[@"localName"];
    NSArray *serviceUUIDs = dictOptions[@"serviceUUIDs"];
    NSMutableDictionary *advertisingData = [NSMutableDictionary dictionary];
    if ([localName isKindOfClass:[NSString class]]) {
        advertisingData[CBAdvertisementDataLocalNameKey] = localName;
    }
    if ([serviceUUIDs isKindOfClass:[NSArray class]]) {
        NSMutableArray *uuids = [NSMutableArray array];
        for (id uuidStr in serviceUUIDs) {
            if ([uuidStr isKindOfClass:[NSString class]]) {
                [uuids addObject:[CBUUID UUIDWithString:uuidStr]];
            }
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
        if (![serviceDict isKindOfClass:[NSDictionary class]]) continue;
        NSString *uuid = serviceDict[@"uuid"];
        if (![uuid isKindOfClass:[NSString class]]) continue;
        CBMutableService *service = [[CBMutableService alloc] initWithType:[CBUUID UUIDWithString:uuid] primary:YES];
        NSMutableArray *characteristics = [NSMutableArray array];
        NSArray *charArray = serviceDict[@"characteristics"];
        if (![charArray isKindOfClass:[NSArray class]]) charArray = @[];
        for (NSDictionary *charDict in charArray) {
            if (![charDict isKindOfClass:[NSDictionary class]]) continue;
            NSString *charUUID = charDict[@"uuid"];
            NSArray *propertiesArr = charDict[@"properties"];
            CBCharacteristicProperties properties = 0;
            if ([propertiesArr isKindOfClass:[NSArray class]]) {
                for (NSString *prop in propertiesArr) {
                    if (![prop isKindOfClass:[NSString class]]) continue;
                    if ([prop isEqualToString:@"read"]) properties |= CBCharacteristicPropertyRead;
                    if ([prop isEqualToString:@"write"]) properties |= CBCharacteristicPropertyWrite;
                    if ([prop isEqualToString:@"notify"]) properties |= CBCharacteristicPropertyNotify;
                    if ([prop isEqualToString:@"indicate"]) properties |= CBCharacteristicPropertyIndicate;
                }
            }
            NSData *value = nil;
            if ([charDict[@"value"] isKindOfClass:[NSString class]]) {
                value = [charDict[@"value"] dataUsingEncoding:NSUTF8StringEncoding];
                // If value is present, characteristic must be read-only
                properties = CBCharacteristicPropertyRead;
            }
            CBAttributePermissions permissions = CBAttributePermissionsReadable|CBAttributePermissionsWriteable;
            if (value) {
                permissions = CBAttributePermissionsReadable;
            }
            if ([charUUID isKindOfClass:[NSString class]]) {
                CBMutableCharacteristic *characteristic = [[CBMutableCharacteristic alloc] initWithType:[CBUUID UUIDWithString:charUUID] properties:properties value:value permissions:permissions];
                [characteristics addObject:characteristic];
            }
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

#ifdef __cplusplus
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeBluetoothPeripheralSpecJSI>(params);
}
#endif

// Add stubs for required CBPeripheralManagerDelegate methods
- (void)peripheralManagerDidUpdateState:(CBPeripheralManager *)peripheral {
    // Required delegate method. Add logic if needed.
}

@end
