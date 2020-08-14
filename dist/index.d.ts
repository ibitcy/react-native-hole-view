import { ViewProps } from 'react-native';
import * as React from 'react';
export interface IRNHole {
    x: number;
    y: number;
    width: number;
    height: number;
    borderRadius?: number;
    borderTopLeftRadius?: -1;
    borderTopRightRadius?: -1;
    borderBottomLeftRadius?: -1;
    borderBottomRightRadius?: -1;
}
export interface IRNHoleView extends ViewProps {
    holes?: IRNHole[];
}
export declare const RNHoleView: React.FC<IRNHoleView>;
