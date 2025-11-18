import type { ViewProps } from 'react-native';

export const DEFAULT_DURATION = 1000;
export const DEFAULT_RADIUS_VALUE = -1;

export class RNHole {
    height: number;
    width: number;
    x: number;
    y: number;
    borderRadius?: number = DEFAULT_RADIUS_VALUE;
    isRTL? = false;
    borderTopLeftRadius? = DEFAULT_RADIUS_VALUE;
    borderTopRightRadius? = DEFAULT_RADIUS_VALUE;
    borderBottomLeftRadius? = DEFAULT_RADIUS_VALUE;
    borderBottomRightRadius? = DEFAULT_RADIUS_VALUE;
    borderTopStartRadius? = DEFAULT_RADIUS_VALUE;
    borderTopEndRadius? = DEFAULT_RADIUS_VALUE;
    borderBottomStartRadius? = DEFAULT_RADIUS_VALUE;
    borderBottomEndRadius? = DEFAULT_RADIUS_VALUE;
}

export enum ERNHoleViewTimingFunction {
    LINEAR = 'LINEAR',
    EASE_IN = 'EASE_IN',
    EASE_OUT = 'EASE_OUT',
    EASE_IN_OUT = 'EASE_IN_OUT',
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

export interface SanitizedHole {
    height: number;
    width: number;
    x: number;
    y: number;
    borderRadius: number;
    isRTL: boolean;
    borderTopLeftRadius: number;
    borderTopRightRadius: number;
    borderBottomLeftRadius: number;
    borderBottomRightRadius: number;
    borderTopStartRadius: number;
    borderTopEndRadius: number;
    borderBottomStartRadius: number;
    borderBottomEndRadius: number;
}

export interface SanitizedAnimation {
    duration: number;
    timingFunction: ERNHoleViewTimingFunction;
}

export const sanitizeAnimationProp = (animation?: IRNHoleViewAnimation): SanitizedAnimation | undefined => {
    if (!animation) {
        return undefined;
    }

    return {
        duration: typeof animation.duration === 'number' ? animation.duration : DEFAULT_DURATION,
        timingFunction: animation.timingFunction || ERNHoleViewTimingFunction.LINEAR,
    };
}

const sanitizeRadius = (value?: number): number => {
    return typeof value === 'number' ? value : DEFAULT_RADIUS_VALUE;
}

export const sanitizeHolesProp = (holes?: RNHole[]): SanitizedHole[] => {
    if (!holes || holes.length === 0) {
        return [];
    }

    return holes.map((hole) => ({
        height: hole.height,
        width: hole.width,
        x: hole.x,
        y: hole.y,
        borderRadius: sanitizeRadius(hole.borderRadius),
        isRTL: !!hole.isRTL,
        borderTopLeftRadius: sanitizeRadius(hole.borderTopLeftRadius),
        borderTopRightRadius: sanitizeRadius(hole.borderTopRightRadius),
        borderBottomLeftRadius: sanitizeRadius(hole.borderBottomLeftRadius),
        borderBottomRightRadius: sanitizeRadius(hole.borderBottomRightRadius),
        borderTopStartRadius: sanitizeRadius(hole.borderTopStartRadius),
        borderTopEndRadius: sanitizeRadius(hole.borderTopEndRadius),
        borderBottomStartRadius: sanitizeRadius(hole.borderBottomStartRadius),
        borderBottomEndRadius: sanitizeRadius(hole.borderBottomEndRadius),
    }));
}


