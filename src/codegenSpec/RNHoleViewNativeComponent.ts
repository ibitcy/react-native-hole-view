import type {HostComponent,ViewProps} from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type {
    DirectEventHandler,
    WithDefault,
    Int32,
    Float,
    Double,
} from 'react-native/Libraries/Types/CodegenTypes';

type AnimFinishEvent = Readonly<{}>;

type AnimationProp = { 
    duration?: Int32;
    timingFunction?: string;
}
type HoleProp = {
    height: Int32;
    width: Int32;
    x: Int32;
    y: Int32;
    borderRadius?: Int32;
    isRTL?: boolean;
    borderTopLeftRadius?: Int32;
    borderTopRightRadius?: Int32;
    borderBottomLeftRadius?: Int32;
    borderBottomRightRadius?: Int32;
    borderTopStartRadius?: Int32;
    borderTopEndRadius?: Int32;
    borderBottomStartRadius?: Int32;
    borderBottomEndRadius?: Int32;
}

export interface NativeProps extends ViewProps {
    holes?: ReadonlyArray<Readonly<HoleProp>>;
    animation?: Readonly<AnimationProp>;
    onAnimationFinished?: DirectEventHandler<AnimFinishEvent>;
}

export default codegenNativeComponent<NativeProps>(
  'RNHoleView',
) as HostComponent<NativeProps>;