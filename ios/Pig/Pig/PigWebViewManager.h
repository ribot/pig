//
//  PigWebViewManager.h
//  Pods
//
//  Created by Matt Oakes on 09/06/2014.
//
//

#import <Foundation/Foundation.h>
#import "PigWebViewManagerDelegate.h"

@interface PigWebViewManager : NSObject <UIWebViewDelegate>

@property (nonatomic, assign) id<PigWebViewManagerDelegate> delegate;

- (instancetype)initWithPath:(NSString *)path;
- (void)execute:(NSString *) key path:(NSString *)path data:(NSString *)data;

@end
