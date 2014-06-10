//
//  PigCallback.h
//  PigTest
//
//  Created by Matt Oakes on 09/06/2014.
//  Copyright (c) 2014 Matt Oakes. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * The block used when a handler is successful.
 * @param data The data the handler returned.
 */
typedef void (^SuccessBlock) (NSString *data);
/**
 * The block used when a handler reports a failure.
 * @param code      An application defined error code. You should set sensible error code when reporting errors in your
 *                  Javascript so your Objective-C UI can show the user a relivent message.
 * @param name      The name of the Javascript error reported. For example ParseError or TypeError.
 * @param message   The error message reported by Javascript.
 */
typedef void (^FailureBlock) (NSString *code, NSString *name, NSString *message);

/**
 * Used internally in Pig to keep a copy of the success and failure blocks for each call to a handler.
 */
@interface PigCallback : NSObject

/**
 * Create an instance of the Callback holder.
 * @param success   The success block to store.
 * @param failure   The failure block to store.
 */
- (instancetype)initWithSuccess:(SuccessBlock)success failure:(FailureBlock)failure;

/**
 * The stored success block.
 */
@property (nonatomic, copy, readonly) SuccessBlock success;
/**
 * The stored failure block.
 */
@property (nonatomic, copy, readonly) FailureBlock failure;

@end
