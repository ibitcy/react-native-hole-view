import { requireNativeComponent, ViewProps } from 'react-native';
import * as React from 'react';

const isFabricEnabled = global.nativeFabricUIManager != null;

const RNHoleViewManager = isFabricEnabled ?
     require("./codegenSpec/RNHoleViewNativeComponent").default :
     requireNativeComponent<IRNHoleView>('RNHoleView');

const DEFAULT_DURATION = 1000;
const DEFAULT_RADIUS_VALUE = -1;

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

const sanitizeAnimationProp = (animation) => {
    const animationProp = animation
    ? {
        duration: typeof animation.duration === 'number' ? animation.duration : DEFAULT_DURATION,
        timingFunction: animation.timingFunction || ERNHoleViewTimingFunction.LINEAR,
    }
    : undefined;
    return animationProp;
}

const sanitizeHolesProp = (holes) => {
    const holesProp = [];
    if (holes!=null) {
        holes.forEach(h => {
            holesProp.push({
                height: h.height,
                width: h.width,
                x: h.x,
                y: h.y,
                borderRadius: typeof h.borderRadius === 'number' ? h.borderRadius : DEFAULT_RADIUS_VALUE,
                isRTL: !!h.isRTL,
                borderTopLeftRadius: typeof h.borderTopLeftRadius === 'number' ? h.borderTopLeftRadius : DEFAULT_RADIUS_VALUE,
                borderTopRightRadius: typeof h.borderTopRightRadius === 'number' ? h.borderTopRightRadius : DEFAULT_RADIUS_VALUE,
                borderBottomLeftRadius: typeof h.borderBottomLeftRadius === 'number' ? h.borderBottomLeftRadius : DEFAULT_RADIUS_VALUE,
                borderBottomRightRadius: typeof h.borderBottomRightRadius === 'number' ? h.borderBottomRightRadius : DEFAULT_RADIUS_VALUE,
                borderTopStartRadius: typeof h.borderTopStartRadius === 'number' ? h.borderTopStartRadius : DEFAULT_RADIUS_VALUE,
                borderTopEndRadius: typeof h.borderTopEndRadius === 'number' ? h.borderTopEndRadius : DEFAULT_RADIUS_VALUE,
                borderBottomStartRadius: typeof h.borderBottomStartRadius === 'number' ? h.borderBottomStartRadius : DEFAULT_RADIUS_VALUE,
                borderBottomEndRadius: typeof h.borderBottomEndRadius === 'number' ? h.borderBottomEndRadius : DEFAULT_RADIUS_VALUE
            });
        });
    }
    return holesProp;
}

export const RNHoleView: React.FC<IRNHoleView> = props => {
    const { animation, holes, onAnimationFinished, ...rest } = props;

    const animationProp = sanitizeAnimationProp(animation);
    const holesProp = sanitizeHolesProp(holes);

    return (
      <RNHoleViewManager holes={holesProp} animation={animationProp} onAnimationFinished={onAnimationFinished} {...rest} />
    );
};
