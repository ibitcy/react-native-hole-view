//
//  RNHoleViewManager.mm
//  RNHoleViewProject
//
//  Created by Stepan Kopylov on 20/05/2020.
//  Copyright © 2020 Stepan Kopylov. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>

#import "RNHoleViewImpl.h"

@interface RNHoleViewManager : RCTViewManager
@end

@implementation RNHoleViewManager

RCT_EXPORT_MODULE(RNHoleView)

-(UIView*)view{
    return [RNHoleViewImpl new];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(holes, NSArray)

RCT_EXPORT_VIEW_PROPERTY(animation, NSDictionary)

RCT_EXPORT_VIEW_PROPERTY(onAnimationFinished, RCTDirectEventBlock)

@end
