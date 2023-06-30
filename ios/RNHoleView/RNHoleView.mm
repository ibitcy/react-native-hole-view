//
//  RNHoleView.mm
//  RNHoleView
//
//  Created by Thomas FETIVEAU on 22/06/2023.
//  Copyright © 2023 Stepan Kopylov. All rights reserved.
//

// This guard prevent the code from being compiled in the old architecture
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNHoleView.h"
#import "RNHoleViewImpl.h"

#import <react/renderer/components/RNHoleView/ComponentDescriptors.h>
#import <react/renderer/components/RNHoleView/EventEmitters.h>
#import <react/renderer/components/RNHoleView/Props.h>
#import <react/renderer/components/RNHoleView/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface RNHoleView () <RCTRNHoleViewViewProtocol>

@end

@implementation RNHoleView {
    RNHoleViewImpl * _view;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<RNHoleViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        static const auto defaultProps = std::make_shared<const RNHoleViewProps>();
        _props = defaultProps;

        _view = [RNHoleViewImpl new];
        
        __weak __typeof(self) weakSelf = self;
        _view.onAnimationFinishedFabric = ^() {
            [weakSelf onAnimationFinished];
        };
        
        self.contentView = _view;
        
        [self setUserInteractionEnabled:false]; // important to allow touch through holes
    }
     
    return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<RNHoleViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNHoleViewProps const>(props);

	NSMutableArray *oldHoles = [[NSMutableArray alloc] init];
	auto oldHolesIt = oldViewProps.holes.begin();
	
	while (oldHolesIt != oldViewProps.holes.end()) {
		[oldHoles addObject: [self holeStructoDictionary: *oldHolesIt]];
		oldHolesIt++;
	}
	
    NSMutableArray *newHoles = [[NSMutableArray alloc] init];
    auto newHolesIt = newViewProps.holes.begin();
    
    while (newHolesIt != newViewProps.holes.end()) {
        [newHoles addObject: [self holeStructoDictionary: *newHolesIt]];
        newHolesIt++;
    }
	
	if(![newHoles isEqualToArray:oldHoles]){
		[_view setHoles:newHoles];
	}
	
    if (oldViewProps.animation.duration != newViewProps.animation.duration ||
        oldViewProps.animation.timingFunction != newViewProps.animation.timingFunction) {
        NSDictionary * animation = @{
            @"duration": [NSNumber numberWithInt:newViewProps.animation.duration],
            @"timingFunction": @(newViewProps.animation.timingFunction.c_str())
        };
        [_view setAnimation:animation];
    }

    [super updateProps:props oldProps:oldProps];
}

- (void)onAnimationFinished
{
    if(!_eventEmitter) {
        return;
    }
    
    RNHoleViewEventEmitter::OnAnimationFinished event = { };
    
    std::dynamic_pointer_cast<const RNHoleViewEventEmitter>(_eventEmitter)->onAnimationFinished(event);
}

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index
{
    [_view mountChildComponentView:childComponentView index:index];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index
{
    [_view unmountChildComponentView:childComponentView index:index];
}

- holeStructoDictionary:(facebook::react::RNHoleViewHolesStruct) holeStruct
{
    return @{
        @"height": [NSNumber numberWithInt:holeStruct.height],
        @"width": [NSNumber numberWithInt:holeStruct.width],
        @"x": [NSNumber numberWithInt:holeStruct.x],
        @"y": [NSNumber numberWithInt:holeStruct.y],
        @"borderRadius": [NSNumber numberWithInt:holeStruct.borderRadius],
        @"isRTL": [NSNumber numberWithInt:holeStruct.isRTL], // boolean
        @"borderTopLeftRadius": [NSNumber numberWithInt:holeStruct.borderTopLeftRadius],
        @"borderTopRightRadius": [NSNumber numberWithInt:holeStruct.borderTopRightRadius],
        @"borderBottomLeftRadius": [NSNumber numberWithInt:holeStruct.borderBottomLeftRadius],
        @"borderBottomRightRadius": [NSNumber numberWithInt:holeStruct.borderBottomRightRadius],
        @"borderTopStartRadius": [NSNumber numberWithInt:holeStruct.borderTopStartRadius],
        @"borderTopEndRadius": [NSNumber numberWithInt:holeStruct.borderTopEndRadius],
        @"borderBottomStartRadius": [NSNumber numberWithInt:holeStruct.borderBottomStartRadius],
        @"borderBottomEndRadius": [NSNumber numberWithInt:holeStruct.borderBottomEndRadius]
    };
}

@end

Class<RCTComponentViewProtocol> RNHoleViewCls(void)
{
    return RNHoleView.class;
}
#endif
