import { requireNativeComponent, View, ViewProps } from 'react-native';
import * as React from 'react';

const RNHoleViewManager = requireNativeComponent('RNHoleView');

export interface IRNHole {
  x: number;
  y: number;
  width: number;
  height: number;
  borderRadius?: number;
}

export interface IRNHoleView extends ViewProps {
  holes?: IRNHole[];
}

export const RNHoleView: React.FC<IRNHoleView> = props => {
  return <RNHoleViewManager {...props} />;
};
