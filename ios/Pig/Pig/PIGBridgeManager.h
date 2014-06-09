//
//  PIGBridgeManager.h
//  RibotApp
//
//  Created by Manuel Marcos Regalado on 05/06/2014.
//  Copyright (c) 2014 Manuel Marcos Regalado. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <JavaScriptCore/JavaScriptCore.h>

@interface PIGBridgeManager : NSObject <UIWebViewDelegate>

- (instancetype) initWithWebView:(UIWebView *) webview;

/// Executes a given function in Javascript.
/// @param path
/// @param data
/// @param success
/// @param failure
- (void)execute:(NSString *)path data:(NSString *)data  success:(void (^)(NSString *data))success failure:(void (^)(NSString *code, NSString *name, NSString *message))failure;

@end
