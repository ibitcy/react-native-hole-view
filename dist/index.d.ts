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
export declare enum ERNHoleViewTimingFunction {
    LINEAR = "LINEAR",
    EASE_IN = "EASE_IN",
    EASE_OUT = "EASE_OUT",
    EASE_IN_OUT = "EASE_IN_OUT"
}
export interface IRNHoleViewAnimation {
    duration?: number;
    timingFunction?: ERNHoleViewTimingFunction;
}
export interface IRNHoleView extends ViewProps {
    holes?: RNHole[];
    animation?: IRNHoleViewAnimation;
    onAnimationFinished?: () => void;
}
export declare const RNHoleView: React.FC<IRNHoleView>;
