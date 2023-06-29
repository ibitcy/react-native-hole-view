//
//  RNHoleViewImpl.m
//  RNHoleViewProject
//
//  Created by Stepan Kopylov on 20/05/2020.
//  Copyright © 2020 Stepan Kopylov. All rights reserved.
//

#import "RNHoleViewImpl.h"

#define degreesToRadians(x) ((x) * M_PI / 180.0)

static const CGFloat DEFAULT_BORDER_RADIUS_VALUE = 0.f;
static const CGFloat DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE = -1.f;

@implementation RNHoleViewHole

- (instancetype)initWitnX:(CGFloat)x y:(CGFloat)y
					width:(CGFloat)width
				   height:(CGFloat)height
		  andBorderRadius:(CGFloat)borderRadius
   andBorderTopLeftRadius:(CGFloat)borderTopLeftRadius
  andBorderTopRightRadius:(CGFloat)borderTopRightRadius
andBorderBottomLeftRadius:(CGFloat)borderBottomLeftRadius
andBorderBottomRightRadius:(CGFloat)borderBottomRightRadius
{
	self = [super init];
	if (self) {
		self.rect = CGRectMake(x, y, width, height);
		self.borderRadius = borderRadius;
		
		self.borderTopLeftRadius = borderTopLeftRadius == DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE ? borderRadius : borderTopLeftRadius;
		self.borderTopRightRadius = borderTopRightRadius == DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE ? borderRadius : borderTopRightRadius;
		self.borderBottomLeftRadius = borderBottomLeftRadius == DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE ? borderRadius : borderBottomLeftRadius;
		self.borderBottomRightRadius = borderBottomRightRadius == DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE ? borderRadius : borderBottomRightRadius;
	}
	return self;
}

@end


@interface RNHoleViewImpl()<CAAnimationDelegate>

@property (nonatomic) CAShapeLayer *maskLayer;
@property (nonatomic) UIBezierPath *maskPath;

@property (nonatomic) NSNumber *animationDuration;
@property (nonatomic) CAMediaTimingFunction *animationTimingFunction;

@property (nonatomic) dispatch_source_t holesTimer;

@end

@implementation RNHoleViewImpl

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


-(void)layoutSubviews
{
	_maskLayer.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
	
	[self setMaskPath:self.holePaths skipAnimation:YES];
}


-(void)setHoles:(NSArray<NSDictionary *> *)holes
{
	NSMutableArray <RNHoleViewHole*> *parsedHoles = @[].mutableCopy;
	
	[holes enumerateObjectsUsingBlock:^(NSDictionary * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
		BOOL isRTL = obj[@"isRTL"] && [obj[@"isRTL"] boolValue] == YES;
		
		CGFloat borderRadius = obj[@"borderRadius"] ? [obj[@"borderRadius"] floatValue] : DEFAULT_BORDER_RADIUS_VALUE;
		CGFloat borderTopLeftRadius = obj[@"borderTopLeftRadius"] ? [obj[@"borderTopLeftRadius"] floatValue] : DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE;
		CGFloat borderTopRightRadius = obj[@"borderTopRightRadius"] ? [obj[@"borderTopRightRadius"] floatValue] : DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE;
		CGFloat borderBottomLeftRadius = obj[@"borderBottomLeftRadius"] ? [obj[@"borderBottomLeftRadius"] floatValue] : DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE;
		CGFloat borderBottomRightRadius = obj[@"borderBottomRightRadius"] ? [obj[@"borderBottomRightRadius"] floatValue] : DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE;
		
		if(obj[@"borderTopStartRadius"]){
			CGFloat value = [obj[@"borderTopStartRadius"] floatValue];
			
			if(value!=DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE){
				if(isRTL){
					borderTopRightRadius = value;
				}else{
					borderTopLeftRadius = value;
				}
			}
		}
		
		if(obj[@"borderTopEndRadius"]){
			CGFloat value = [obj[@"borderTopEndRadius"] floatValue];
			
			if(value!=DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE){
				if(isRTL){
					borderTopLeftRadius = value;
				}else{
					borderTopRightRadius = value;
				}
			}
		}
		
		if(obj[@"borderBottomStartRadius"]){
			CGFloat value = [obj[@"borderBottomStartRadius"] floatValue];
			
			if(value!=DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE){
				if(isRTL){
					borderBottomRightRadius = value;
				}else{
					borderBottomLeftRadius = value;
				}
			}
		}
		
		if(obj[@"borderBottomEndRadius"]){
			CGFloat value = [obj[@"borderBottomEndRadius"] floatValue];
			
			if(value!=DEFAULT_SPECIFIC_BORDER_RADIUS_VALUE){
				if(isRTL){
					borderBottomLeftRadius = value;
				}else{
					borderBottomRightRadius = value;
				}
			}
		}
		
		RNHoleViewHole *hole = [[RNHoleViewHole alloc] initWitnX:[obj[@"x"] floatValue] y:[obj[@"y"] floatValue] width:[obj[@"width"] floatValue] height:[obj[@"height"] floatValue] andBorderRadius:borderRadius andBorderTopLeftRadius:borderTopLeftRadius andBorderTopRightRadius:borderTopRightRadius andBorderBottomLeftRadius:borderBottomLeftRadius andBorderBottomRightRadius:borderBottomRightRadius];
		
		[parsedHoles addObject:hole];
	}];
	
	self.parsedHoles = parsedHoles;
}


-(void)setAnimation:(NSDictionary *)animation{
	_animation = animation;
	
	if(_animation){
		_animationDuration = animation[@"duration"];
		NSString *timingFunction = animation[@"timingFunction"];
	
		if([timingFunction isEqualToString:@"LINEAR"]){
			_animationTimingFunction =  [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionLinear];
		}else if([timingFunction isEqualToString:@"EASE_IN"]){
			_animationTimingFunction =  [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseIn];
		}else if([timingFunction isEqualToString:@"EASE_OUT"]){
			_animationTimingFunction =  [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseOut];
		}else if([timingFunction isEqualToString:@"EASE_IN_OUT"]){
			_animationTimingFunction =  [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
		}
		
	}
}


-(void)setParsedHoles:(NSArray<RNHoleViewHole *> *)parsedHoles
{
	_parsedHoles = parsedHoles;
	
	[self stopHolesTimer];
	
	dispatch_source_t timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0,  dispatch_get_main_queue());
	
	if ( timer ) {
		dispatch_source_set_timer(timer, dispatch_time(DISPATCH_TIME_NOW, 0.01 * NSEC_PER_SEC), 0.01 * NSEC_PER_SEC, (1ull * NSEC_PER_SEC) / 10);
		dispatch_source_set_event_handler(timer, ^(){
			[self stopHolesTimer];
			
			[self setMaskPath:self.holePaths skipAnimation:NO];
		});
		dispatch_resume(timer);
		
		_holesTimer = timer;
	}
}


-(void)stopHolesTimer
{
	if(_holesTimer){
		dispatch_source_cancel(_holesTimer);
		_holesTimer = nil;
	}
}


-(void)setMaskPath:(UIBezierPath *)maskPath skipAnimation:(BOOL)skipAnimation{
	UIBezierPath *oldPath = _maskPath;
	
	_maskPath = maskPath;
	
	if(!skipAnimation && self.animation){
		[_maskLayer removeAnimationForKey:@"path"];
		
		CABasicAnimation *pathAnimation = [CABasicAnimation new];
		
		pathAnimation.duration = _animationDuration.doubleValue/1000.0;
		pathAnimation.keyPath = @"path";
		pathAnimation.fromValue = oldPath ? (id)oldPath.CGPath : (id)_maskLayer.path;
		pathAnimation.toValue = (id)_maskPath.CGPath;
		pathAnimation.removedOnCompletion = NO;
		pathAnimation.fillMode = kCAFillModeForwards;
		pathAnimation.timingFunction = _animationTimingFunction;
		pathAnimation.delegate = self;
		
		[_maskLayer addAnimation:pathAnimation forKey:@"path"];
	}else{
		_maskLayer.path = _maskPath.CGPath;
	}
	
}

- (UIBezierPath *)holePaths
{
	UIBezierPath *currentPath = [UIBezierPath new];
	currentPath.usesEvenOddFillRule = YES;
	
	[_parsedHoles enumerateObjectsUsingBlock:^(RNHoleViewHole *hole, NSUInteger idx, BOOL *_Nonnull stop) {
		CGRect rect = hole.rect;
		
		UIBezierPath *path = [UIBezierPath new];
		
		[path addArcWithCenter:CGPointMake(rect.origin.x+rect.size.width-hole.borderTopRightRadius, rect.origin.y+hole.borderTopRightRadius) radius:hole.borderTopRightRadius startAngle:degreesToRadians(-90.f) endAngle:degreesToRadians(0.f)  clockwise:YES];
		[path addArcWithCenter:CGPointMake(rect.origin.x+rect.size.width-hole.borderBottomRightRadius, rect.origin.y+rect.size.height-hole.borderBottomRightRadius) radius:hole.borderBottomRightRadius startAngle:degreesToRadians(0.f) endAngle:degreesToRadians(90.f)  clockwise:YES];
		[path addArcWithCenter:CGPointMake(rect.origin.x+hole.borderBottomLeftRadius, rect.origin.y+rect.size.height-hole.borderBottomLeftRadius) radius:hole.borderBottomLeftRadius startAngle:degreesToRadians(90.f) endAngle:degreesToRadians(180.f)  clockwise:YES];
		[path addArcWithCenter:CGPointMake(rect.origin.x+hole.borderTopLeftRadius, rect.origin.y+hole.borderTopLeftRadius) radius:hole.borderTopLeftRadius startAngle:degreesToRadians(180.f) endAngle:degreesToRadians(270.f)  clockwise:YES];
		
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
	
	if (!CGPathContainsPoint(self.maskPath.CGPath, nil, point, YES) ) {
		pointInPath = YES;
	}
	
	return pointInPath;
}

-(void)animationDidStop:(CAAnimation *)anim finished:(BOOL)finished{
	if(finished){
		if(self.onAnimationFinished){
			self.onAnimationFinished(NULL);
		}
        if(self.onAnimationFinishedFabric){
            self.onAnimationFinishedFabric();
        }
	}
}

@end
