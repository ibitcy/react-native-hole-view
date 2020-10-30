/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React, {useCallback, useEffect, useState} from 'react';
import {
    SafeAreaView,
    StyleSheet,
    ScrollView,
    View,
    Text,
    StatusBar, TouchableOpacity,
} from 'react-native';

import {
    Colors,
} from 'react-native/Libraries/NewAppScreen';

import {RNHole, RNHoleView, ERNHoleViewTimingFunction, IRNHoleViewAnimation} from "react-native-hole-view";

import Video from 'react-native-video';

const firstHole: RNHole = {x: 150, y: 390, width: 120, height: 120, borderRadius: 60};
const secondHole: RNHole = {x: 150, y: 40, width: 120, height: 120, borderRadius: 60};

const animationSettings: IRNHoleViewAnimation = {timingFunction: ERNHoleViewTimingFunction.EASE_IN_OUT, duration: 200};

const App = () => {
    const [holes, setHoles] = useState<RNHole[]>([]);
    const [animated, setAnimated] = useState<boolean>(false);
    const [animation, setAnimation] = useState<IRNHoleViewAnimation | undefined>(undefined);

    const onPress = useCallback(() => {
        if (animated) {
            setHoles([firstHole]);
        } else {
            setHoles([secondHole])
        }

        setAnimation({...animationSettings});
        setAnimated(!animated);
    }, [animated, animation])

    useEffect(() => {
        onPress();
    }, []);

    return (
        <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
            <Text style={{flexGrow: 0, flex: 0, padding: 10}}>{"Wow! I'm a text inside a hole!"}</Text>
            <TouchableOpacity onPress={() => {
            }} style={{backgroundColor: 'pink', padding: 10, borderRadius: 5}}>
                <Text>{"Wow! I'm a button inside a hole!"}</Text>
            </TouchableOpacity>
            <ScrollView style={{flexGrow: 0, flex: 0, padding: 10}} horizontal={true}>
                <Text numberOfLines={1}>
                    {
                        "Wow! I'm a ScrollView inside a hole! Wow! I'm a ScrollView inside a hole! Wow! I'm a ScrollView inside a hole!"
                    }
                </Text>
            </ScrollView>
            <RNHoleView
                style={{
                    position: 'absolute',
                    width: '100%',
                    height: '100%',
                    backgroundColor: 'rgba(34,146,231,0.4)'
                }}
                animation={animation}
                holes={holes}
                onAnimationFinished={() => {
                    setAnimation(undefined);
                }}
            >
                <Video source={{uri: 'http://clips.vorwaerts-gmbh.de/VfE_html5.mp4'}}
                       resizeMode={"contain"}
                       style={{flex: 1}}/>
            </RNHoleView>
            <View
                pointerEvents={'box-none'}
                style={{
                    position: 'absolute',
                    flex: 1,
                    width: '100%',
                    height: '100%',
                    alignItems: 'flex-end',
                    flexDirection: 'row',
                    justifyContent: 'center'

                }}>
                <TouchableOpacity onPress={onPress}
                                  style={{backgroundColor: 'pink', padding: 10, borderRadius: 5, bottom: 50}}>
                    <Text>{"Animate!"}</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};

export default App;
