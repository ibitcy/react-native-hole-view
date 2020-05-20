import { NativeModules, requireNativeComponent } from 'react-native';
import * as React from 'react';

const RNHoleViewManager = requireNativeComponent('RNHoleViewManager');

export interface IRNHole {
  x: number;
  y: number;
  width: number;
  height: number;
  cornerRadius?: number;
}

export interface IRNHoleView {
  holes?: IRNHole;
}

export const RNHoleView: React.FC<IRNHoleView> = props => {
  return <RNHoleViewManager {...this.props} />;
};
