import { requireNativeComponent } from 'react-native';
import * as React from 'react';
import {
    sanitizeAnimationProp,
    sanitizeHolesProp,
} from './RNHoleView.types';
import type { IRNHoleView } from './RNHoleView.types';

const isFabricEnabled = (global as {
  nativeFabricUIManager?: unknown | null;
}).nativeFabricUIManager != null;

const RNHoleViewManager = isFabricEnabled ?
     require("./codegenSpec/RNHoleViewNativeComponent").default :
     requireNativeComponent<IRNHoleView>('RNHoleView');

export const RNHoleView = (props: IRNHoleView) => {
    const { animation, holes, onAnimationFinished, ...rest } = props;

    const animationProp = sanitizeAnimationProp(animation);
    const holesProp = sanitizeHolesProp(holes);

    return (
      <RNHoleViewManager holes={holesProp} animation={animationProp} onAnimationFinished={onAnimationFinished} {...rest} />
    );
};

export { RNHole, ERNHoleViewTimingFunction } from './RNHoleView.types';
export type { IRNHoleViewAnimation, IRNHoleView } from './RNHoleView.types';
