import { ViewProps } from 'react-native';
import * as React from 'react';
export declare class RNHole {
    height: number;
    width: number;
    x: number;
    y: number;
    borderRadius?: number;
    isRTL?: boolean;
    borderTopLeftRadius?: number;
    borderTopRightRadius?: number;
    borderBottomLeftRadius?: number;
    borderBottomRightRadius?: number;
    borderTopStartRadius?: number;
    borderTopEndRadius?: number;
    borderBottomStartRadius?: number;
    borderBottomEndRadius?: number;
}
export interface IRNHoleView extends ViewProps {
    holes?: RNHole[];
}
export declare const RNHoleView: React.FC<IRNHoleView>;
