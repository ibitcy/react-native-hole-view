//
//  RNHoleView.h
//  RNHoleViewProject
//
//  Created by Stepan Kopylov on 20/05/2020.
//  Copyright © 2020 Stepan Kopylov. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNHoleViewHole : NSObject

@property (nonatomic) CGRect rect;
@property (nonatomic) CGFloat cornerRadius;

- (instancetype)initWitnX:(CGFloat)x y:(CGFloat)y width:(CGFloat)width height:(CGFloat)height andCornerRadius:(CGFloat)cornerRadius;

@end

@interface RNHoleView : UIView

@property (nonatomic) NSArray <RNHoleViewHole*> *parsedHoles;

@property (nonatomic) NSArray <NSDictionary*> *holes;

@end
