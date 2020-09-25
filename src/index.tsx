import { requireNativeComponent, View, ViewProps } from 'react-native';
import * as React from 'react';

const RNHoleViewManager = requireNativeComponent('RNHoleView');

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

export enum ERNHoleViewAnimationType {
  EASE_IN = 'EASE_IN',
  EASE_OUT = 'EASE_OUT',
  EASE_IN_OUT = 'EASE_IN_OUT',
}

export class IRNHoleViewAnimation {
  duration?: number = DEFAULT_DURATION;
  type: ERNHoleViewAnimationType;
}

export interface IRNHoleView extends ViewProps {
  holes?: RNHole[];
  animation?: IRNHoleViewAnimation;
}

export const RNHoleView: React.FC<IRNHoleView> = props => {
  return <RNHoleViewManager {...props} />;
};
