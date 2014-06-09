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

typedef NSDictionary PigJSCallback;

@interface PIGBridgeManager ()

@property (nonatomic, strong) UIWebView *webview;
@property (nonatomic) BOOL webviewDidLoad;
@property (strong) NSMutableArray *requestQueue;
@property (strong) NSMutableDictionary *callbacksArray;

@end

@implementation PIGBridgeManager

#pragma mark - Class Life

- (instancetype) initWithWebView:(UIWebView *) webview {
    if ((self = [super init])) {
        _webview = webview;
        _requestQueue = [[NSMutableArray alloc] init];
        _callbacksArray = [[NSMutableDictionary alloc] init];

        _webview.delegate = self;
        _webviewDidLoad = NO;
    }
    return self;
}

- (void)dealloc {
    _webview = nil;
    _requestQueue = nil;
    _callbacksArray = nil;
}

#pragma mark - Execute JavaScript

- (void)execute:(NSString *)path data:(NSString *)data  success:(void (^)(NSString *data))success failure:(void (^)(NSString *code, NSString *name, NSString *message))failure {
    NSString *key = [self generateKey];

    PIGBridgeCallBack *callback = [[PIGBridgeCallBack alloc] initWithSuccess:success failure:failure];
    [_callbacksArray setObject:callback forKey:key];

    // TODO: Cleanup inputs
    NSString *command = [NSString stringWithFormat:@"window.pig._execute(%@, '%@', '%@');", key, path, data];
    [self dispatchCommand:command];
}

- (NSString *)generateKey {
    NSString *key;

    do {
        key = [NSString stringWithFormat:@"%d", arc4random()];
    } while ([_callbacksArray objectForKey:key] != nil);

    return key;
}

- (void)dispatchCommand:(NSString *)command {
    // Add the command to a queue if the webview isn't ready yet
    if (!_webviewDidLoad) {
        [_requestQueue addObject:command];
        return;
    }
    // Execute the Javascript command if it is ready
    else {
        if ([[NSThread currentThread] isMainThread]) {
            [_webview stringByEvaluatingJavaScriptFromString:command];
        } else {
            __strong UIWebView* strongWebView = _webview;
            dispatch_sync(dispatch_get_main_queue(), ^{
                [strongWebView stringByEvaluatingJavaScriptFromString:command];
            });
        }
    }
}

#pragma mark - Call Backs Logic

- (void)flushCallbacks {
    NSString *callbackQueue = [_webview stringByEvaluatingJavaScriptFromString:@"window.ios._getCallbackQueue();"];

    NSArray *callbacks = [NSJSONSerialization JSONObjectWithData:[callbackQueue dataUsingEncoding:NSUTF8StringEncoding]
                                                         options:NSJSONReadingAllowFragments
                                                           error:nil];

    // TODO: Check we have a valid array of callbacks

    for (PigJSCallback *callback in callbacks) {
        [self handleCallback:callback];
    }
}

- (void)handleCallback:(PigJSCallback *)callback {
    // TODO: Check we have a valid callback object
    // TODO: Check everything is valid at each stage
    NSString *callbackId = callback[@"callbackId"];
    NSString *method = callback[@"method"];

    PIGBridgeCallBack *callbackReference = [_callbacksArray objectForKey:callbackId];
    if (callbackReference) {
        [_callbacksArray removeObjectForKey:callbackId];

        if ([method isEqualToString:@"success"]) {
            NSString *data = callback[@"data"];

            callbackReference.successBlock(data);
        } else if ([method isEqualToString:@"fail"]) {
            NSString *code = callback[@"code"];
            NSString *name = callback[@"name"];
            NSString *message = callback[@"message"];

            callbackReference.failureBlock(code, name, message);
        }
    }
}

#pragma mark - WebView delegate

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    NSURL *url = [request URL];

    if ([[url scheme] isEqualToString:@"pig"]) {

        if ([[url host] isEqualToString:@"callback"]) {
            [self flushCallbacks];
        } else {
            NSLog(@"Pig: WARNING: Received unknown command %@", url);
        }

        return NO;
    } else {
        return YES;
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    // Check for and inject the pig-ios native interface
    BOOL hasNativeInterface = [[_webview stringByEvaluatingJavaScriptFromString:@"typeof window.ios !== \"undefined\""] boolValue];
    if (!hasNativeInterface) {
        NSString* iosJSPath = [[NSBundle mainBundle] pathForResource:@"pig-ios" ofType:@"js"];
        NSString* iosJS = [NSString stringWithContentsOfFile:iosJSPath encoding:NSUTF8StringEncoding error:nil];

        [_webview stringByEvaluatingJavaScriptFromString:iosJS];
    }

    _webviewDidLoad = YES;

    // Execute any command which were queued while we were loading
    for (NSString *command in _requestQueue) {
        [self dispatchCommand:command];
    }
}

@end
