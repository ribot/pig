//
//  Pig.h
//  Pods
//
//  Created by Matt Oakes on 09/06/2014.
//
//

#import <Foundation/Foundation.h>
#import "PigWebViewManagerDelegate.h"

typedef void (^SuccessBlock) (NSString *data);
typedef void (^FailureBlock) (NSString *code, NSString *name, NSString *message);

@interface Pig : NSObject <PigWebViewManagerDelegate>

- (instancetype) initWithPage:(NSString *) page;

/// Executes a given function in Javascript.
/// @param path
/// @param data
/// @param success
/// @param failure
- (void)execute:(NSString *)path data:(NSString *)data  success:(SuccessBlock)success failure:(FailureBlock)failure;

@end
