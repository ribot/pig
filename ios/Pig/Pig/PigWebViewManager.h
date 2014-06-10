//
//  PigWebViewManager.h
//  Pods
//
//  Created by Matt Oakes on 09/06/2014.
//
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

/**
 * A protocol used to pass success and failure messages from the internal UIWebView to Pig.
 */
@protocol PigWebViewManagerDelegate <NSObject>

/**
 * Callback method used to report a success message.
 * @param key   The unique key that Javascript was called with.
 * @param data  The data returned from Javascript.
 */
- (void)successForKey:key withData:data;
/**
 * Callback method used to report a failure message.
 * @param key       The unique key that Javascript was called with.
 * @param code      The error code Javascript returned.
 * @param name      The error type Javascript reported.
 * @param message   The error message Javascript reported.
 */
- (void)failForKey:key withCode:code name:name message:message;

@end

/**
 * An internal Pig class which manages the connection with the hidden UIWebView.
 */
@interface PigWebViewManager : NSObject <UIWebViewDelegate>

/**
 * The delegate to send success and failure callbacks to.
 */
@property (nonatomic, assign) id<PigWebViewManagerDelegate> delegate;

/**
 * Create an instance of the class with a UIWebView pointing to the given file path.
 * @param path  The path inside the bundle to the HTML page the UIWebView should load.
 */
- (instancetype)initWithPath:(NSString *)path;

/**
 * Execute a handler with the given key, path and data. The response will be delivered to the delegate.
 * @param key   The key to pass to Javascript.
 * @param path  The path of the handler to execute.
 * @param data  The data to pass to the handler.
 */
- (void)execute:(NSString *) key path:(NSString *)path data:(NSString *)data;

@end
