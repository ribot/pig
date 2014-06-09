//
//  PIGBridgeCallBack.h
//  RibotApp
//
//  Created by Manuel Marcos Regalado on 05/06/2014.
//  Copyright (c) 2014 Manuel Marcos Regalado. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PIGBridgeCallBack : NSObject

/// Initializes a PIGBridgeCallBack with a callBackID, a success block and a failure block.
/// @param callBackID
/// @param success
/// @param failure
- (instancetype)initWithSuccess:(void (^)(NSString *data))success failure:(void (^)(NSString *code, NSString *name, NSString *message))failure;

/// Getter method for successBlock
- (void (^)(NSString *data))successBlock;

/// Getter method for failureBlock
- (void (^)(NSString *code, NSString *name, NSString *message))failureBlock;

@end
