//
//  PigWebViewManagerDelegate.h
//  PigTest
//
//  Created by Matt Oakes on 09/06/2014.
//  Copyright (c) 2014 Matt Oakes. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol PigWebViewManagerDelegate <NSObject>

- (void)successForKey:key withData:data;
- (void)failForKey:key withCode:code name:name message:message;

@end
