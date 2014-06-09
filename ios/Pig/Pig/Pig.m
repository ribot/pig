//
//  Pig.m
//  Pods
//
//  Created by Matt Oakes on 09/06/2014.
//
//

#import "Pig.h"
#import "PigWebViewManager.h"
#import "PigCallback.h"

@interface Pig ()

@property (strong) PigWebViewManager *webViewManager;
@property (strong) NSMutableDictionary *callbacksArray;

@end

@implementation Pig

- (instancetype) initWithPage:(NSString *) page {
	if ((self = [super init])) {
        _callbacksArray = [[NSMutableDictionary alloc] init];
        
        NSString* htmlPath = [[NSBundle mainBundle] pathForResource:page ofType:@"html"];
        _webViewManager = [[PigWebViewManager alloc] initWithPath:htmlPath];
        _webViewManager.delegate = self;
    }
	return self;
}

- (void)dealloc {
    _callbacksArray = nil;
}

#pragma mark - Execute

- (void)execute:(NSString *)path data:(NSString *)data  success:(SuccessBlock)success failure:(FailureBlock)failure {
    NSString *key = [self generateKey];
    
     // Generate and store the callback reference
    PigCallback *callback = [[PigCallback alloc] initWithSuccess:success failure:failure];
    [_callbacksArray setObject:callback forKey:key];
    
    // Send the request to the WebViewManager
    [_webViewManager execute:key path:path data:data];
}

- (NSString *)generateKey {
    NSString *key;
    
    do {
        key = [NSString stringWithFormat:@"%d", arc4random()];
    } while ([_callbacksArray objectForKey:key] != nil);
    
    return key;
}

#pragma mark - Callback

- (void)successForKey:key withData:data {
    PigCallback *callbackReference = [_callbacksArray objectForKey:key];
    callbackReference.success(data);
}

- (void)failForKey:key withCode:code name:name message:message {
    PigCallback *callbackReference = [_callbacksArray objectForKey:key];
    callbackReference.failure(code, name, message);
}

@end
