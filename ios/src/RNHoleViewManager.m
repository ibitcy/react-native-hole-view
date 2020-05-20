//
//  RNHoleViewManager.m
//  RNHoleViewProject
//
//  Created by Stepan Kopylov on 20/05/2020.
//  Copyright © 2020 Stepan Kopylov. All rights reserved.
//

#import "RNHoleViewManager.h"
#import "RNHoleViewWrapper.h"

@implementation RNHoleViewManager

RCT_EXPORT_MODULE(RNHoleView)

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(holes, NSArray)

-(UIView*)view{
    return [RNHoleViewWrapper new];
}

@end
