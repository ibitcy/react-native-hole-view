/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React from 'react';
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

import {RNHoleView} from "react-native-hole-view";

const App = () => {



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
                holes={[{x: 150, y: 390, width: 120, height: 120, borderRadius: 60}]}>
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
                <TouchableOpacity onPress={() => {
                }} style={{backgroundColor: 'pink', padding: 10, borderRadius: 5, bottom: 50}}>
                    <Text>{"Animate!"}</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    scrollView: {
        backgroundColor: Colors.lighter,
    },
    engine: {
        position: 'absolute',
        right: 0,
    },
    body: {
        backgroundColor: Colors.white,
    },
    sectionContainer: {
        marginTop: 32,
        paddingHorizontal: 24,
    },
    sectionTitle: {
        fontSize: 24,
        fontWeight: '600',
        color: Colors.black,
    },
    sectionDescription: {
        marginTop: 8,
        fontSize: 18,
        fontWeight: '400',
        color: Colors.dark,
    },
    highlight: {
        fontWeight: '700',
    },
    footer: {
        color: Colors.dark,
        fontSize: 12,
        fontWeight: '600',
        padding: 4,
        paddingRight: 12,
        textAlign: 'right',
    },
});

export default App;
