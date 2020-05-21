//
//  RNHoleView.m
//  RNHoleViewProject
//
//  Created by Stepan Kopylov on 20/05/2020.
//  Copyright © 2020 Stepan Kopylov. All rights reserved.
//

#import "RNHoleView.h"

@implementation RNHoleViewHole

- (instancetype)initWitnX:(CGFloat)x y:(CGFloat)y width:(CGFloat)width height:(CGFloat)height andCornerRadius:(CGFloat)cornerRadius
{
	self = [super init];
	if (self) {
		self.rect = CGRectMake(x, y, width, height);
		self.cornerRadius = cornerRadius;
	}
	return self;
}

@end


@interface RNHoleView()

@property (nonatomic) CAShapeLayer *maskLayer;

@end

@implementation RNHoleView

- (instancetype)init
{
	self = [super init];
	if (self) {
		_maskLayer = [CAShapeLayer layer];
		_maskLayer.fillColor = [UIColor redColor].CGColor;
		_maskLayer.fillRule = kCAFillRuleEvenOdd;
		_maskLayer.shouldRasterize = YES;
		_maskLayer.rasterizationScale = [UIScreen mainScreen].scale;
		
		self.layer.mask = _maskLayer;
	}
	return self;
}


-(void)layoutSubviews{
	_maskLayer.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
	
	_maskLayer.path = self.holePaths.CGPath;
}


-(void)setHoles:(NSArray<NSDictionary *> *)holes{
	NSMutableArray <RNHoleViewHole*> *parsedHoles = @[].mutableCopy;
	
	[holes enumerateObjectsUsingBlock:^(NSDictionary * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
		RNHoleViewHole *hole = [[RNHoleViewHole alloc] initWitnX:[obj[@"x"] floatValue] y:[obj[@"y"] floatValue] width:[obj[@"width"] floatValue] height:[obj[@"height"] floatValue] andCornerRadius:obj[@"borderRadius"] ? [obj[@"borderRadius"] floatValue] : 0.0f];
		
		[parsedHoles addObject:hole];
	}];
	
	self.parsedHoles = parsedHoles;
}


-(void)setParsedHoles:(NSArray<RNHoleViewHole *> *)parsedHoles{
	_parsedHoles = parsedHoles;
	
	_maskLayer.path = self.holePaths.CGPath;
}


- (UIBezierPath *)holePaths
{
	UIBezierPath *currentPath = [UIBezierPath new];
	
	[_parsedHoles enumerateObjectsUsingBlock:^(RNHoleViewHole *hole, NSUInteger idx, BOOL *_Nonnull stop) {
		CGRect rect = hole.rect;
		
		UIBezierPath *path = [UIBezierPath bezierPathWithRoundedRect:rect cornerRadius:hole.cornerRadius];
		
		[currentPath appendPath:path];
	}];
	
	[currentPath appendPath:[UIBezierPath bezierPathWithRect:CGRectMake(0.0, 0.0, self.frame.size.width, self.frame.size.height)]];
	
	return currentPath;
}


- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event
{
	if ( [self pointInRects:point] || !self.userInteractionEnabled) {
		return NO;
	}
	
	BOOL superPoint = [super pointInside:point withEvent:event];
	
	return superPoint;
}


- (BOOL)pointInRects:(CGPoint)point
{
	__block BOOL pointInPath = NO;
	
	[_parsedHoles enumerateObjectsUsingBlock:^(RNHoleViewHole *hole, NSUInteger idx, BOOL *_Nonnull stop) {
		
		if ( CGPathContainsPoint([UIBezierPath bezierPathWithRoundedRect:hole.rect cornerRadius:hole.cornerRadius].CGPath, nil, point, YES) ) {
			pointInPath = YES;
			*stop = YES;
		}
	}];
	
	return pointInPath;
}

@end
