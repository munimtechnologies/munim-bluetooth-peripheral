#import "BluetoothPeripheral.h"
#import <CoreBluetooth/CoreBluetooth.h>

@interface BluetoothPeripheral () <CBPeripheralManagerDelegate>
@property (nonatomic, strong) CBPeripheralManager *peripheralManager;
@property (nonatomic, strong) NSMutableArray<CBMutableService *> *services;
@property (nonatomic, strong) NSDictionary *currentAdvertisingData;
@end

@implementation BluetoothPeripheral

RCT_EXPORT_MODULE();

- (instancetype)init {
    if (self = [super init]) {
        _peripheralManager = [[CBPeripheralManager alloc] initWithDelegate:self queue:nil];
        _services = [NSMutableArray array];
        _currentAdvertisingData = [NSDictionary dictionary];
    }
    return self;
}

RCT_EXPORT_METHOD(startAdvertising:(NSDictionary *)options) {
    if (self.peripheralManager.state != CBManagerStatePoweredOn) {
        return;
    }
    
    NSMutableDictionary *advertisingData = [NSMutableDictionary dictionary];
    
    // Handle legacy options for backward compatibility
    NSString *localName = options[@"localName"];
    NSArray *serviceUUIDs = options[@"serviceUUIDs"];
    NSString *manufacturerData = options[@"manufacturerData"];
    
    // Process comprehensive advertising data
    NSDictionary *advertisingDataDict = options[@"advertisingData"];
    if ([advertisingDataDict isKindOfClass:[NSDictionary class]]) {
        [self processAdvertisingData:advertisingDataDict into:advertisingData];
    }
    
    // Legacy support - override with legacy options if provided
    if ([localName isKindOfClass:[NSString class]]) {
        advertisingData[CBAdvertisementDataLocalNameKey] = localName;
    }
    if ([serviceUUIDs isKindOfClass:[NSArray class]] && serviceUUIDs.count > 0) {
        NSMutableArray *uuids = [NSMutableArray array];
        for (NSString *uuidStr in serviceUUIDs) {
            if ([uuidStr isKindOfClass:[NSString class]]) {
                [uuids addObject:[CBUUID UUIDWithString:uuidStr]];
            }
        }
        advertisingData[CBAdvertisementDataServiceUUIDsKey] = uuids;
    }
    if ([manufacturerData isKindOfClass:[NSString class]]) {
        NSData *data = [self hexStringToData:manufacturerData];
        if (data) {
            advertisingData[CBAdvertisementDataManufacturerDataKey] = data;
        }
    }
    
    self.currentAdvertisingData = [advertisingData copy];
    [self.peripheralManager startAdvertising:advertisingData];
}

RCT_EXPORT_METHOD(updateAdvertisingData:(NSDictionary *)advertisingDataDict) {
    if (self.peripheralManager.state != CBManagerStatePoweredOn) {
        return;
    }
    
    [self.peripheralManager stopAdvertising];
    
    NSMutableDictionary *advertisingData = [NSMutableDictionary dictionary];
    [self processAdvertisingData:advertisingDataDict into:advertisingData];
    
    self.currentAdvertisingData = [advertisingData copy];
    [self.peripheralManager startAdvertising:advertisingData];
}

RCT_EXPORT_METHOD(getAdvertisingData:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    resolve(self.currentAdvertisingData ?: @{});
}

- (void)processAdvertisingData:(NSDictionary *)dataDict into:(NSMutableDictionary *)advertisingData {
    // 0x01 - Flags (partial support)
    if (dataDict[@"flags"]) {
        NSNumber *flags = dataDict[@"flags"];
        if ([flags isKindOfClass:[NSNumber class]]) {
            // iOS Core Bluetooth handles most flags automatically
            // The connectable flag is controlled by CBAdvertisementDataIsConnectable
            BOOL isConnectable = (flags.intValue & 0x02) != 0;
            advertisingData[CBAdvertisementDataIsConnectable] = @(isConnectable);
        }
    }
    
    // 0x02-0x07 - Service UUIDs (fully supported)
    [self addServiceUUIDs:dataDict[@"incompleteServiceUUIDs16"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"completeServiceUUIDs16"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"incompleteServiceUUIDs32"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"completeServiceUUIDs32"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"incompleteServiceUUIDs128"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"completeServiceUUIDs128"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    
    // 0x08-0x09 - Local Name (fully supported)
    if (dataDict[@"shortenedLocalName"]) {
        advertisingData[CBAdvertisementDataLocalNameKey] = dataDict[@"shortenedLocalName"];
    }
    if (dataDict[@"completeLocalName"]) {
        advertisingData[CBAdvertisementDataLocalNameKey] = dataDict[@"completeLocalName"];
    }
    
    // 0x0A - Tx Power Level (fully supported)
    if (dataDict[@"txPowerLevel"]) {
        NSNumber *txPower = dataDict[@"txPowerLevel"];
        if ([txPower isKindOfClass:[NSNumber class]]) {
            advertisingData[CBAdvertisementDataTxPowerLevelKey] = txPower;
        }
    }
    
    // 0x14-0x15 - Service Solicitation UUIDs (fully supported)
    [self addServiceUUIDs:dataDict[@"serviceSolicitationUUIDs16"] to:advertisingData key:CBAdvertisementDataSolicitedServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"serviceSolicitationUUIDs128"] to:advertisingData key:CBAdvertisementDataSolicitedServiceUUIDsKey];
    
    // 0x16, 0x20, 0x21 - Service Data (fully supported)
    [self addServiceData:dataDict[@"serviceData16"] to:advertisingData];
    [self addServiceData:dataDict[@"serviceData32"] to:advertisingData];
    [self addServiceData:dataDict[@"serviceData128"] to:advertisingData];
    
    // 0x19 - Appearance (partial support)
    if (dataDict[@"appearance"]) {
        NSNumber *appearance = dataDict[@"appearance"];
        if ([appearance isKindOfClass:[NSNumber class]]) {
            // iOS doesn't have direct CBAdvertisementData support for appearance
            // We can include it as custom service data if needed
            NSData *appearanceData = [NSData dataWithBytes:(uint8_t[]){(uint8_t)(appearance.intValue & 0xFF), (uint8_t)((appearance.intValue >> 8) & 0xFF)} length:2];
            // Store with Generic Access service UUID
            advertisingData[[CBUUID UUIDWithString:@"1800"]] = appearanceData;
        }
    }
    
    // 0x1F - Service Solicitation (32-bit) (fully supported)
    [self addServiceUUIDs:dataDict[@"serviceSolicitationUUIDs32"] to:advertisingData key:CBAdvertisementDataSolicitedServiceUUIDsKey];
    
    // 0xFF - Manufacturer Specific Data (fully supported)
    if (dataDict[@"manufacturerData"]) {
        NSData *manufacturerData = [self hexStringToData:dataDict[@"manufacturerData"]];
        if (manufacturerData) {
            advertisingData[CBAdvertisementDataManufacturerDataKey] = manufacturerData;
        }
    }
}

- (void)addServiceUUIDs:(id)uuids to:(NSMutableDictionary *)advertisingData key:(NSString *)key {
    if ([uuids isKindOfClass:[NSArray class]]) {
        NSMutableArray *uuidArray = [NSMutableArray array];
        for (NSString *uuidStr in uuids) {
            if ([uuidStr isKindOfClass:[NSString class]]) {
                [uuidArray addObject:[CBUUID UUIDWithString:uuidStr]];
            }
        }
        if (uuidArray.count > 0) {
            advertisingData[key] = uuidArray;
        }
    }
}

- (void)addServiceData:(id)serviceDataArray to:(NSMutableDictionary *)advertisingData {
    if ([serviceDataArray isKindOfClass:[NSArray class]]) {
        for (NSDictionary *serviceData in serviceDataArray) {
            if ([serviceData isKindOfClass:[NSDictionary class]]) {
                NSString *uuid = serviceData[@"uuid"];
                NSString *data = serviceData[@"data"];
                if ([uuid isKindOfClass:[NSString class]] && [data isKindOfClass:[NSString class]]) {
                    NSData *dataBytes = [self hexStringToData:data];
                    if (dataBytes) {
                        advertisingData[uuid] = dataBytes;
                    }
                }
            }
        }
    }
}

- (NSData *)hexStringToData:(NSString *)hexString {
    if (![hexString isKindOfClass:[NSString class]]) {
        return nil;
    }
    
    NSString *cleanHex = [hexString stringByReplacingOccurrencesOfString:@" " withString:@""];
    if (cleanHex.length % 2 != 0) {
        return nil;
    }
    
    NSMutableData *data = [NSMutableData data];
    for (int i = 0; i < cleanHex.length; i += 2) {
        NSString *byteString = [cleanHex substringWithRange:NSMakeRange(i, 2)];
        unsigned int byte;
        NSScanner *scanner = [NSScanner scannerWithString:byteString];
        if ([scanner scanHexInt:&byte]) {
            uint8_t byteValue = (uint8_t)byte;
            [data appendBytes:&byteValue length:1];
        }
    }
    return data;
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

// Add stubs for required CBPeripheralManagerDelegate methods
- (void)peripheralManagerDidUpdateState:(CBPeripheralManager *)peripheral {
    // Required delegate method. Add logic if needed.
}

@end
