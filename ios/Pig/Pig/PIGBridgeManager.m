//
//  PIGBridgeManager.m
//  RibotApp
//
//  Created by Manuel Marcos Regalado on 05/06/2014.
//  Copyright (c) 2014 Manuel Marcos Regalado. All rights reserved.
//

#import "PIGBridgeManager.h"
#import "PIGBridgeCallBack.h"
#import "PIGBridgeStrings.h"

@interface PIGBridgeManager ()

@property (nonatomic,strong) JSContext *context;
@property (nonatomic, strong) NSMutableArray *callBacksArray;

@end

@implementation PIGBridgeManager

#pragma mark - Class Life

- (instancetype) init
{
	self = [super init];
    
    _callBacksArray = [[NSMutableArray alloc] init];
    
    _context = [[JSContext alloc] initWithVirtualMachine:[[JSVirtualMachine alloc] init]];
    
    // Set up an exception handler
    [self setExceptionHandler];
    
    // Get the Javascript file and evaluate it
    NSString *scriptPath = [[NSBundle mainBundle] pathForResource:kJSFileName ofType:kJSFiletype];
    NSString *script = [NSString stringWithContentsOfFile:scriptPath encoding:NSUTF8StringEncoding error:nil];
    // TODO: handle failures if we can't find the JS file or if we can not evaluate the script

    [_context evaluateScript:script];
    
    // We leave the callbacks already set up for further calls from Javascript
    [self loadCallBacks];
    
	return self;
}

- (void)dealloc
{
    _context = nil;
    _callBacksArray   = nil;
}

#pragma mark - Exception Handler

- (void)setExceptionHandler
{
    [_context setExceptionHandler:^(JSContext *context, JSValue *error)
    {
        NSLog(@"Exception[%@]: %@", error[@"lineNumber"], error);
    }];
}

#pragma mark - Call Backs Logic

- (void)loadCallBacks
{
    __weak __typeof__(self) weakSelf = self;
    
    _context[kObjCSuccessBlock] = ^(NSNumber *callBackID, NSString *data)
    {
        __typeof__(self) strongSelf = weakSelf;

        [strongSelf getPIGBridgeCallBackObject:callBackID].successBlock(data);
    };
    
    _context[kObjCFailureBlock] = ^(NSNumber *callBackID, NSString *code, NSString *name, NSString *message)
    {
        __typeof__(self) strongSelf = weakSelf;
        
        [strongSelf getPIGBridgeCallBackObject:callBackID].failureBlock(code, name, message);
        
    };
    _context[kObjCConsoleBlock] = ^(NSString *text)
    {
        NSLog(@"CONSOLE: %@",text);
    };

}

- (PIGBridgeCallBack *)getPIGBridgeCallBackObject:(NSNumber *)callBackID
{
    for (PIGBridgeCallBack *callBackObject in _callBacksArray)
    {
        if ([callBackID intValue] == [callBackObject.callBackID intValue])
        {
            [_callBacksArray removeObject:callBackObject];
            return callBackObject;
        }
    }
    return nil;
}

#pragma mark - Execute JavaScript

- (void)execute:(NSString *)path data:(NSString *)data  success:(void (^)(NSString *data))success failure:(void (^)(NSString *code, NSString *name, NSString *message))failure
{
    
    // TODO: generateRandomNumber function should be included in a utils file
    PIGBridgeCallBack *callBackObject = [[PIGBridgeCallBack alloc] initWithCallBackID:[self generateRandomNumber] success:success failure:failure];
        
    [_callBacksArray addObject:callBackObject];
    
    JSValue *global = [_context globalObject];
    JSValue *pig = global[kJSWindowObject][kJSPigObject];
    [pig invokeMethod:kJSInvokeMethod withArguments:@[callBackObject.callBackID, path, data]];
    
}

- (NSNumber *)generateRandomNumber
{
    // TODO: make sure that id does not exist in the array already
    int randomInt =  arc4random();
    NSNumber *randomNumber = [NSNumber numberWithUnsignedInt:randomInt];
    return randomNumber;
}

#pragma mark - Share Bridge Manager

+ (instancetype)sharedPIGBridgeManager
{
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [self new];
    });
    return instance;
}

@end
