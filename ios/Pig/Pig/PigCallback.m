//
//  PigCallback.m
//  PigTest
//
//  Created by Matt Oakes on 09/06/2014.
//  Copyright (c) 2014 Matt Oakes. All rights reserved.
//

#import "PigCallback.h"

@implementation PigCallback

- (instancetype)initWithSuccess:(SuccessBlock)success failure:(FailureBlock)failure {
    if ((self = [super init])) {
        _success = success;
        _failure = failure;
    }
    return self;
}

@end
