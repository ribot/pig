//
//  Pig.h
//  Pods
//
//  Created by Matt Oakes on 09/06/2014.
//
//

#import <Foundation/Foundation.h>
#import "PigWebViewManager.h"
#import "PigCallback.h"

/**
 * A thin layer between your Objective-C UI and Javascript logic. This class manages the connection between the two codebases.
 */
@interface Pig : NSObject <PigWebViewManagerDelegate>

/**
 * Create an instance of the Pig manager with the given HTML page. If the file you have included in your bundle is called
 * "app.html" then you would call this method with "app". The page must be included in the resource bundle for this to be
 * successful.
 *
 * In a normal application you would only have one shared instance of this class, but it is left for you to manage this.
 *
 * @param page  The name of the HTML page you wish Pig to use, not including the file extension.
 */
- (instancetype) initWithPage:(NSString *) page;

/**
 * Executes a given handler which is implemented in Javascript.
 * @param path      The path of the handler.
 * @param data      The data to pass to the handler.
 * @param success   The block which is called when the handler is successful, including an NSString of returned data.
 * @param failure   The block which is called when the handler reports a failure, including the error code, name and message.
 */
- (void)execute:(NSString *)path data:(NSString *)data success:(SuccessBlock)success failure:(FailureBlock)failure;

@end
