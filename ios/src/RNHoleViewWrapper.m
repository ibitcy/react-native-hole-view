//
//  RNHoleViewWrapper.m
//  AFNetworking
//
//  Created by Stepan Kopylov on 20/05/2020.
//

#import "RNHoleViewWrapper.h"
#import <RNHoleView.h>

@interface RNHoleViewWrapper()

@property (nonatomic) RNHoleView *holeView;

@end

@implementation RNHoleViewWrapper

- (instancetype)init
{
	self = [super init];
	if (self) {
		_holeView = [RNHoleView new];
		[self addSubview:_holeView];
	}
	return self;
}

-(void)layoutSubviews{
	_holeView.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.width, self.frame.size.height);
}

-(void)setHoles:(NSArray<NSDictionary *> *)holes{
	
	NSMutableArray <RNHoleViewHole*> *parsedHoles = @[].mutableCopy;
	
	[holes enumerateObjectsUsingBlock:^(NSDictionary * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
		RNHoleViewHole *hole = [[RNHoleViewHole alloc] initWitnX:[obj[@"x"] floatValue] y:[obj[@"y"] floatValue] width:[obj[@"width"] floatValue] height:[obj[@"height"] floatValue] andCornerRadius:obj[@"cornerRadius"] ? [obj[@"cornerRadius"] floatValue] : 0.0f];
		
		[parsedHoles addObject:hole];
	}];
	
	_holeView.holes = parsedHoles;
}

-(void)setBackgroundColor:(UIColor *)backgroundColor{
	_holeView.backgroundColor = backgroundColor;
}

- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event
{
	return [_holeView pointInside:point withEvent:event];
}


@end
