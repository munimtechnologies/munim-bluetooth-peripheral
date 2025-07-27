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
    // 0x01 - Flags
    if (dataDict[@"flags"]) {
        NSNumber *flags = dataDict[@"flags"];
        if ([flags isKindOfClass:[NSNumber class]]) {
            advertisingData[CBAdvertisementDataIsConnectable] = @(flags.intValue & 0x01);
        }
    }
    
    // 0x02-0x07 - Service UUIDs
    [self addServiceUUIDs:dataDict[@"incompleteServiceUUIDs16"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"completeServiceUUIDs16"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"incompleteServiceUUIDs32"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"completeServiceUUIDs32"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"incompleteServiceUUIDs128"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"completeServiceUUIDs128"] to:advertisingData key:CBAdvertisementDataServiceUUIDsKey];
    
    // 0x08-0x09 - Local Name
    if (dataDict[@"shortenedLocalName"]) {
        advertisingData[CBAdvertisementDataLocalNameKey] = dataDict[@"shortenedLocalName"];
    }
    if (dataDict[@"completeLocalName"]) {
        advertisingData[CBAdvertisementDataLocalNameKey] = dataDict[@"completeLocalName"];
    }
    
    // 0x0A - Tx Power Level
    if (dataDict[@"txPowerLevel"]) {
        NSNumber *txPower = dataDict[@"txPowerLevel"];
        if ([txPower isKindOfClass:[NSNumber class]]) {
            advertisingData[CBAdvertisementDataTxPowerLevelKey] = txPower;
        }
    }
    
    // 0x0D - Class of Device
    if (dataDict[@"classOfDevice"]) {
        NSNumber *cod = dataDict[@"classOfDevice"];
        if ([cod isKindOfClass:[NSNumber class]]) {
            advertisingData[CBAdvertisementDataOverflowServiceUUIDsKey] = @(cod.intValue);
        }
    }
    
    // 0x0E-0x0F - Simple Pairing
    if (dataDict[@"simplePairingHashC"]) {
        NSData *hashData = [self hexStringToData:dataDict[@"simplePairingHashC"]];
        if (hashData) {
            advertisingData[@"simplePairingHashC"] = hashData;
        }
    }
    if (dataDict[@"simplePairingRandomizerR"]) {
        NSData *randomData = [self hexStringToData:dataDict[@"simplePairingRandomizerR"]];
        if (randomData) {
            advertisingData[@"simplePairingRandomizerR"] = randomData;
        }
    }
    
    // 0x10-0x11 - Security Manager
    if (dataDict[@"securityManagerTKValue"]) {
        NSData *tkData = [self hexStringToData:dataDict[@"securityManagerTKValue"]];
        if (tkData) {
            advertisingData[@"securityManagerTKValue"] = tkData;
        }
    }
    if (dataDict[@"securityManagerOOFlags"]) {
        NSNumber *flags = dataDict[@"securityManagerOOFlags"];
        if ([flags isKindOfClass:[NSNumber class]]) {
            advertisingData[@"securityManagerOOFlags"] = flags;
        }
    }
    
    // 0x12 - Slave Connection Interval Range
    if (dataDict[@"slaveConnectionIntervalRange"]) {
        NSDictionary *range = dataDict[@"slaveConnectionIntervalRange"];
        if ([range isKindOfClass:[NSDictionary class]]) {
            NSNumber *min = range[@"min"];
            NSNumber *max = range[@"max"];
            if ([min isKindOfClass:[NSNumber class]] && [max isKindOfClass:[NSNumber class]]) {
                NSData *rangeData = [NSData dataWithBytes:(uint8_t[]){min.intValue & 0xFF, (min.intValue >> 8) & 0xFF, max.intValue & 0xFF, (max.intValue >> 8) & 0xFF} length:4];
                advertisingData[@"slaveConnectionIntervalRange"] = rangeData;
            }
        }
    }
    
    // 0x14-0x15 - Service Solicitation
    [self addServiceUUIDs:dataDict[@"serviceSolicitationUUIDs16"] to:advertisingData key:CBAdvertisementDataSolicitedServiceUUIDsKey];
    [self addServiceUUIDs:dataDict[@"serviceSolicitationUUIDs128"] to:advertisingData key:CBAdvertisementDataSolicitedServiceUUIDsKey];
    
    // 0x16, 0x20, 0x21 - Service Data
    [self addServiceData:dataDict[@"serviceData16"] to:advertisingData];
    [self addServiceData:dataDict[@"serviceData32"] to:advertisingData];
    [self addServiceData:dataDict[@"serviceData128"] to:advertisingData];
    
    // 0x17-0x18 - Target Address
    if (dataDict[@"publicTargetAddress"]) {
        NSData *addressData = [self hexStringToData:dataDict[@"publicTargetAddress"]];
        if (addressData) {
            advertisingData[@"publicTargetAddress"] = addressData;
        }
    }
    if (dataDict[@"randomTargetAddress"]) {
        NSData *addressData = [self hexStringToData:dataDict[@"randomTargetAddress"]];
        if (addressData) {
            advertisingData[@"randomTargetAddress"] = addressData;
        }
    }
    
    // 0x19 - Appearance
    if (dataDict[@"appearance"]) {
        NSNumber *appearance = dataDict[@"appearance"];
        if ([appearance isKindOfClass:[NSNumber class]]) {
            advertisingData[CBAdvertisementDataLocalNameKey] = appearance;
        }
    }
    
    // 0x1A - Advertising Interval
    if (dataDict[@"advertisingInterval"]) {
        NSNumber *interval = dataDict[@"advertisingInterval"];
        if ([interval isKindOfClass:[NSNumber class]]) {
            NSData *intervalData = [NSData dataWithBytes:(uint8_t[]){interval.intValue & 0xFF, (interval.intValue >> 8) & 0xFF} length:2];
            advertisingData[@"advertisingInterval"] = intervalData;
        }
    }
    
    // 0x1B - LE Bluetooth Device Address
    if (dataDict[@"leBluetoothDeviceAddress"]) {
        NSData *addressData = [self hexStringToData:dataDict[@"leBluetoothDeviceAddress"]];
        if (addressData) {
            advertisingData[@"leBluetoothDeviceAddress"] = addressData;
        }
    }
    
    // 0x1C - LE Role
    if (dataDict[@"leRole"]) {
        NSString *role = dataDict[@"leRole"];
        if ([role isKindOfClass:[NSString class]]) {
            uint8_t roleValue = [role isEqualToString:@"central"] ? 0x00 : 0x01;
            advertisingData[@"leRole"] = [NSData dataWithBytes:&roleValue length:1];
        }
    }
    
    // 0x1D-0x1E - Simple Pairing (256-bit)
    if (dataDict[@"simplePairingHashC256"]) {
        NSData *hashData = [self hexStringToData:dataDict[@"simplePairingHashC256"]];
        if (hashData) {
            advertisingData[@"simplePairingHashC256"] = hashData;
        }
    }
    if (dataDict[@"simplePairingRandomizerR256"]) {
        NSData *randomData = [self hexStringToData:dataDict[@"simplePairingRandomizerR256"]];
        if (randomData) {
            advertisingData[@"simplePairingRandomizerR256"] = randomData;
        }
    }
    
    // 0x1F - Service Solicitation (32-bit)
    [self addServiceUUIDs:dataDict[@"serviceSolicitationUUIDs32"] to:advertisingData key:CBAdvertisementDataSolicitedServiceUUIDsKey];
    
    // 0x22-0x23 - LE Secure Connections
    if (dataDict[@"leSecureConnectionsConfirmationValue"]) {
        NSData *confirmData = [self hexStringToData:dataDict[@"leSecureConnectionsConfirmationValue"]];
        if (confirmData) {
            advertisingData[@"leSecureConnectionsConfirmationValue"] = confirmData;
        }
    }
    if (dataDict[@"leSecureConnectionsRandomValue"]) {
        NSData *randomData = [self hexStringToData:dataDict[@"leSecureConnectionsRandomValue"]];
        if (randomData) {
            advertisingData[@"leSecureConnectionsRandomValue"] = randomData;
        }
    }
    
    // 0x24 - URI
    if (dataDict[@"uri"]) {
        NSString *uri = dataDict[@"uri"];
        if ([uri isKindOfClass:[NSString class]]) {
            NSData *uriData = [uri dataUsingEncoding:NSUTF8StringEncoding];
            advertisingData[@"uri"] = uriData;
        }
    }
    
    // 0x25 - Indoor Positioning
    if (dataDict[@"indoorPositioning"]) {
        NSDictionary *positioning = dataDict[@"indoorPositioning"];
        if ([positioning isKindOfClass:[NSDictionary class]]) {
            NSMutableData *positioningData = [NSMutableData data];
            if (positioning[@"floor"]) {
                uint8_t floor = [positioning[@"floor"] intValue];
                [positioningData appendBytes:&floor length:1];
            }
            if (positioning[@"room"]) {
                uint8_t room = [positioning[@"room"] intValue];
                [positioningData appendBytes:&room length:1];
            }
            if (positioning[@"coordinates"]) {
                NSDictionary *coords = positioning[@"coordinates"];
                if ([coords isKindOfClass:[NSDictionary class]]) {
                    float x = [coords[@"x"] floatValue];
                    float y = [coords[@"y"] floatValue];
                    [positioningData appendBytes:&x length:4];
                    [positioningData appendBytes:&y length:4];
                    if (coords[@"z"]) {
                        float z = [coords[@"z"] floatValue];
                        [positioningData appendBytes:&z length:4];
                    }
                }
            }
            if (positioningData.length > 0) {
                advertisingData[@"indoorPositioning"] = positioningData;
            }
        }
    }
    
    // 0x26 - Transport Discovery Data
    if (dataDict[@"transportDiscoveryData"]) {
        NSArray *transports = dataDict[@"transportDiscoveryData"];
        if ([transports isKindOfClass:[NSArray class]]) {
            NSMutableData *transportData = [NSMutableData data];
            for (NSDictionary *transport in transports) {
                if ([transport isKindOfClass:[NSDictionary class]]) {
                    NSString *type = transport[@"transportType"];
                    NSString *data = transport[@"data"];
                    if ([type isKindOfClass:[NSString class]] && [data isKindOfClass:[NSString class]]) {
                        uint8_t typeValue = 0;
                        if ([type isEqualToString:@"usb"]) typeValue = 0x01;
                        else if ([type isEqualToString:@"nfc"]) typeValue = 0x02;
                        else if ([type isEqualToString:@"wifi"]) typeValue = 0x03;
                        else if ([type isEqualToString:@"bluetooth"]) typeValue = 0x04;
                        
                        [transportData appendBytes:&typeValue length:1];
                        NSData *transportDataBytes = [self hexStringToData:data];
                        if (transportDataBytes) {
                            [transportData appendData:transportDataBytes];
                        }
                    }
                }
            }
            if (transportData.length > 0) {
                advertisingData[@"transportDiscoveryData"] = transportData;
            }
        }
    }
    
    // 0x27 - LE Supported Features
    if (dataDict[@"leSupportedFeatures"]) {
        NSArray *features = dataDict[@"leSupportedFeatures"];
        if ([features isKindOfClass:[NSArray class]]) {
            NSMutableData *featuresData = [NSMutableData data];
            for (NSNumber *feature in features) {
                if ([feature isKindOfClass:[NSNumber class]]) {
                    uint8_t featureValue = feature.intValue;
                    [featuresData appendBytes:&featureValue length:1];
                }
            }
            if (featuresData.length > 0) {
                advertisingData[@"leSupportedFeatures"] = featuresData;
            }
        }
    }
    
    // 0x28 - Channel Map Update Indication
    if (dataDict[@"channelMapUpdateIndication"]) {
        NSData *channelData = [self hexStringToData:dataDict[@"channelMapUpdateIndication"]];
        if (channelData) {
            advertisingData[@"channelMapUpdateIndication"] = channelData;
        }
    }
    
    // 0x29-0x2B - Mesh
    if (dataDict[@"pbAdv"]) {
        NSData *pbAdvData = [self hexStringToData:dataDict[@"pbAdv"]];
        if (pbAdvData) {
            advertisingData[@"pbAdv"] = pbAdvData;
        }
    }
    if (dataDict[@"meshMessage"]) {
        NSData *meshData = [self hexStringToData:dataDict[@"meshMessage"]];
        if (meshData) {
            advertisingData[@"meshMessage"] = meshData;
        }
    }
    if (dataDict[@"meshBeacon"]) {
        NSData *beaconData = [self hexStringToData:dataDict[@"meshBeacon"]];
        if (beaconData) {
            advertisingData[@"meshBeacon"] = beaconData;
        }
    }
    
    // 0x2C-0x2D - LE Audio
    if (dataDict[@"bigInfo"]) {
        NSData *bigInfoData = [self hexStringToData:dataDict[@"bigInfo"]];
        if (bigInfoData) {
            advertisingData[@"bigInfo"] = bigInfoData;
        }
    }
    if (dataDict[@"broadcastCode"]) {
        NSData *broadcastData = [self hexStringToData:dataDict[@"broadcastCode"]];
        if (broadcastData) {
            advertisingData[@"broadcastCode"] = broadcastData;
        }
    }
    
    // 0x2E - Resolvable Set Identifier
    if (dataDict[@"resolvableSetIdentifier"]) {
        NSData *rsiData = [self hexStringToData:dataDict[@"resolvableSetIdentifier"]];
        if (rsiData) {
            advertisingData[@"resolvableSetIdentifier"] = rsiData;
        }
    }
    
    // 0x2F - Advertising Interval Long
    if (dataDict[@"advertisingIntervalLong"]) {
        NSNumber *interval = dataDict[@"advertisingIntervalLong"];
        if ([interval isKindOfClass:[NSNumber class]]) {
            NSData *intervalData = [NSData dataWithBytes:(uint8_t[]){interval.intValue & 0xFF, (interval.intValue >> 8) & 0xFF, (interval.intValue >> 16) & 0xFF, (interval.intValue >> 24) & 0xFF} length:4];
            advertisingData[@"advertisingIntervalLong"] = intervalData;
        }
    }
    
    // 0x30 - Broadcast Isochronous Stream Data
    if (dataDict[@"bisData"]) {
        NSData *bisData = [self hexStringToData:dataDict[@"bisData"]];
        if (bisData) {
            advertisingData[@"bisData"] = bisData;
        }
    }
    
    // 0x3D - 3D Information Data
    if (dataDict[@"threeDInformationData"]) {
        NSData *threeDData = [self hexStringToData:dataDict[@"threeDInformationData"]];
        if (threeDData) {
            advertisingData[@"threeDInformationData"] = threeDData;
        }
    }
    
    // 0xFF - Manufacturer Specific Data
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
