//
//  PigWebViewManager.m
//  Pods
//
//  Created by Matt Oakes on 09/06/2014.
//
//

#import "PigWebViewManager.h"

@interface PigWebViewManager ()

@property (nonatomic, strong) UIWebView *webView;
@property (nonatomic) BOOL webviewDidLoad;
@property (strong) NSMutableArray *requestQueue;

@end

@implementation PigWebViewManager

- (instancetype)initWithPath:(NSString *)path {
    if ((self = [super init])) {
        _webView = [[UIWebView alloc] init];
        _requestQueue = [[NSMutableArray alloc] init];
        _webviewDidLoad = NO;

        // Set ourselves as a delegate
        _webView.delegate = self;

        // Start loading the page
        [_webView loadRequest:[NSURLRequest requestWithURL:[NSURL fileURLWithPath:path]]];
    }
    return self;
}

- (void)dealloc {
    _delegate = nil;
    _webView = nil;
    _requestQueue = nil;
}

#pragma mark - Dispatch commands

- (void)execute:(NSString *) key path:(NSString *)path data:(NSString *)data {
    // TODO: Cleanup inputs
    NSString *command = [NSString stringWithFormat:@"window.pig._execute(%@, '%@', '%@');", key, path, data];
    [self dispatchCommand:command];
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
            [_webView stringByEvaluatingJavaScriptFromString:command];
        } else {
            __strong UIWebView* strongWebView = _webView;
            dispatch_sync(dispatch_get_main_queue(), ^{
                [strongWebView stringByEvaluatingJavaScriptFromString:command];
            });
        }
    }
}

#pragma mark - Callbacks

- (void)flushCallbacks {
    NSString *callbackQueue = [_webView stringByEvaluatingJavaScriptFromString:@"window.ios._getCallbackQueue();"];

    NSArray *callbacks = [NSJSONSerialization JSONObjectWithData:[callbackQueue dataUsingEncoding:NSUTF8StringEncoding]
                                                         options:NSJSONReadingAllowFragments
                                                           error:nil];

    // TODO: Check we have a valid array of callbacks

    for (NSDictionary *callback in callbacks) {
        [self handleCallback:callback];
    }
}

- (void)handleCallback:(NSDictionary *)callback {
    // TODO: Check we have a valid callback object
    // TODO: Check everything is valid at each stage
    NSString *key = callback[@"key"];
    NSString *method = callback[@"method"];

    if ([method isEqualToString:@"success"]) {
        NSString *data = callback[@"data"];

        [_delegate successForKey:key withData:data];
    } else if ([method isEqualToString:@"fail"]) {
        NSString *code = callback[@"code"];
        NSString *name = callback[@"name"];
        NSString *message = callback[@"message"];

        // TODO: Change name
        [_delegate failForKey:key withCode:code name:name message:message];
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
    BOOL hasNativeInterface = [[_webView stringByEvaluatingJavaScriptFromString:@"typeof window.ios !== \"undefined\""] boolValue];
    if (!hasNativeInterface) {
        NSString* iosJSPath = [[NSBundle mainBundle] pathForResource:@"bridge" ofType:@"js"];
        NSString* iosJS = [NSString stringWithContentsOfFile:iosJSPath encoding:NSUTF8StringEncoding error:nil];

        [_webView stringByEvaluatingJavaScriptFromString:iosJS];
    }

    // Execute any command which were queued while we were loading
    if (!_webviewDidLoad) {
        _webviewDidLoad = YES;

        for (NSString *command in _requestQueue) {
            [self dispatchCommand:command];
        }
        _requestQueue = nil;
    }
}

@end
