//
//  ViewController.m
//  RNHoleViewProject
//
//  Created by Stepan Kopylov on 20/05/2020.
//  Copyright © 2020 Stepan Kopylov. All rights reserved.
//

#import "ViewController.h"
#import <RNHoleView/RNHoleView.h>

@interface ViewController ()

@property (nonatomic) RNHoleView *holeView;

@end

@implementation ViewController

- (void)viewDidLoad {
	[super viewDidLoad];
	
	_holeView = [RNHoleView new];
	_holeView.translatesAutoresizingMaskIntoConstraints = NO;
	
	_holeView.frame = CGRectMake(0, 0, 300, 300);
	_holeView.backgroundColor = [UIColor systemPinkColor];
	
	RNHoleViewHole *hole = [[RNHoleViewHole alloc] initWitnX:0 y:0 width:100 height:100 andCornerRadius:0];
	RNHoleViewHole *hole2 = [[RNHoleViewHole alloc] initWitnX:120 y:120 width:30 height:30 andCornerRadius:15];
	
	_holeView.holes = @[hole,hole2];
	
	[self.view addSubview:_holeView];
	
	self.view.backgroundColor = [UIColor grayColor];
}

-(void)viewDidLayoutSubviews{
	_holeView.frame = self.view.frame;
}

@end
