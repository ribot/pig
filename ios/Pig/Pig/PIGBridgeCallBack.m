//
//  PIGBridgeCallBack.m
//  RibotApp
//
//  Created by Manuel Marcos Regalado on 05/06/2014.
//  Copyright (c) 2014 Manuel Marcos Regalado. All rights reserved.
//

#import "PIGBridgeCallBack.h"

@interface PIGBridgeCallBack ()

@property (nonatomic) NSNumber * callBackID;
@property (nonatomic, copy) void (^successBlock)(NSString *data);
@property (nonatomic, copy) void (^failureBlock)(NSString *code, NSString *name, NSString *message);

@end

@implementation PIGBridgeCallBack

#pragma mark - Constructor

- (instancetype)initWithCallBackID:(NSNumber *)callBackID success:(void (^)(NSString *data))success failure:(void (^)(NSString *code, NSString *name, NSString *message))failure
{
    self = [super init];
    if (self)
    {
        _callBackID   = callBackID;
        _successBlock = success;
        _failureBlock = failure;
        
    }
    return self;
}

#pragma mark - Getters

- (NSNumber *)callBackID
{
//    TODO: return a DEFAULT_CALLBACKID which will flag that there is an error getting the float
//    if (!_callBackID) {
//        return DEFAULT_CALLBACKID;
//    }else{
//        return _scale;
//    }
    return _callBackID;
}

- (void (^)(NSString *data))successBlock
{
//  TODO: do something if the block does not exist
//    if (!_successBlock) {
//        self.successBlock = ^ (NSString *data) {
//            NSLog(@"Do something");
//        };
//    }
    return _successBlock;
}

- (void (^)(NSString *code, NSString *name, NSString *message))failureBlock
{
//  TODO: do something if the block does not exist
//    if (!_failureBlock) {
//        self.failureBlock = ^ (NSString *code, NSString *name, NSString *message) {
//            NSLog(@"Do something");
//        };
//    }
    return _failureBlock;
}

- (void)dealloc
{
    _successBlock = nil;
    _failureBlock = nil;
}

@end
