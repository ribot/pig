//
//  PigCallback.h
//  PigTest
//
//  Created by Matt Oakes on 09/06/2014.
//  Copyright (c) 2014 Matt Oakes. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Pig.h"

@interface PigCallback : NSObject

- (instancetype)initWithSuccess:(SuccessBlock)success failure:(FailureBlock)failure;

@property (nonatomic, copy, readonly) SuccessBlock success;
@property (nonatomic, copy, readonly) FailureBlock failure;

@end
