import { requireNativeComponent, ViewProps } from 'react-native';
import * as React from 'react';

const isFabricEnabled = global.nativeFabricUIManager != null;

const RNHoleViewManager = isFabricEnabled ?
     require("./RNHoleViewNativeComponent").default :
     requireNativeComponent<IRNHoleView>('RNHoleView');

const DEFAULT_DURATION = 1000;
const DEFAULT_RADIUS_VALUE = -1;

export class RNHole {
    height: number;
    width: number;
    x: number;
    y: number;
    borderRadius?: number;
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

export const RNHoleView: React.FC<IRNHoleView> = props => {
    const { animation, holes, onAnimationFinished, ...rest } = props;

    const animationProp = animation
      ? {
          duration: typeof animation.duration === 'number' ? animation.duration : DEFAULT_DURATION,
          timingFunction: animation.timingFunction || ERNHoleViewTimingFunction.LINEAR,
      }
      : undefined;

    return (
      <RNHoleViewManager holes={holes} animation={animationProp} onAnimationFinished={onAnimationFinished} {...rest} />
    );
};
