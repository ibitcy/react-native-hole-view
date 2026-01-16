import React, { useEffect, useState, useCallback } from "react";

import {
  View,
  Text,
  ViewStyle,
  ScrollView,
  TouchableOpacity,
} from "react-native";

import {
  RNHole,
  RNHoleView,
  IRNHoleViewAnimation,
  ERNHoleViewTimingFunction,
} from "react-native-hole-view";

const firstHole: RNHole = {
  x: 120,
  y: 240,
  width: 120,
  height: 120,
  borderRadius: 60,
};
const secondHole: RNHole = {
  x: 120,
  y: 40,
  width: 120,
  height: 120,
  borderRadius: 60,
};

const animationSettings: IRNHoleViewAnimation = {
  timingFunction: ERNHoleViewTimingFunction.EASE_IN_OUT,
  duration: 200,
};

const App: React.FC = () => {
  const [holes, setHoles] = useState<RNHole[]>([]);
  const [animated, setAnimated] = useState<boolean>(false);
  const [animation, setAnimation] = useState<IRNHoleViewAnimation | undefined>(
    undefined
  );

  const onPress = useCallback(() => {
    if (animated) {
      setHoles([firstHole]);
    } else {
      setHoles([secondHole]);
    }

    setAnimation({ ...animationSettings });
    setAnimated(!animated);
  }, [animated, animation]);

  useEffect(() => {
    onPress();
  }, []);

  return (
    <View
      style={
        {
          backgroundColor: "white",
          height: "100vh",
          width: "100vw",
        } as unknown as ViewStyle
      }
    >
      <View
        style={{
          top: 250,
        }}
      >
        <Text style={{ padding: 10 }}>{"Wow! I'm a text inside a hole!"}</Text>
        <TouchableOpacity
          onPress={() => {}}
          style={{ backgroundColor: "pink", padding: 10, borderRadius: 5 }}
        >
          <Text>{"Wow! I'm a button inside a hole!"}</Text>
        </TouchableOpacity>
        <ScrollView style={{ padding: 10 }} horizontal={true}>
          <Text style={{ color: "black" }} numberOfLines={1}>
            {
              "Wow! I'm a ScrollView inside a hole! Wow! I'm a ScrollView inside a hole! Wow! I'm a ScrollView inside a hole!"
            }
          </Text>
        </ScrollView>
      </View>

      <RNHoleView
        style={
          {
            position: "absolute",
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: "rgba(34,146,231,0.4)",
            height: "100vh",
          } as unknown as ViewStyle
        }
        holes={holes}
        animation={{
          timingFunction: ERNHoleViewTimingFunction.EASE_IN_OUT,
          duration: 220,
        }}
      >
        <View style={{ top: 200 }}>
          <video
            loop
            muted
            autoPlay
            playsInline
            disablePictureInPicture
            disableRemotePlayback
            controlsList="nofullscreen nodownload noremoteplayback noplaybackrate"
            style={{
              width: "100vw",
            }}
          >
            <source
              src={
                "https://cdn.vidyard.com/videos/uuAgWcoM5N4eFR4lUxoYKg/480p.mp4?gbzK5DIaQtu334rQMWVKBAGFBwZCVR6SHKgkwVYjsSLvOhADBtM4IZ_NxKjBze8TzK1o0XNrUd3UyZiiIdlaz_8icTZDM15OeUZ7DSsCBsQ"
              }
              type="video/mp4"
            />
          </video>
        </View>
      </RNHoleView>
      <View
        pointerEvents={"box-none"}
        style={
          {
            position: "absolute",
            flex: 1,
            width: "100vw",
            height: "100vh",
            alignItems: "flex-end",
            flexDirection: "row",
            justifyContent: "center",
          } as unknown as ViewStyle
        }
      >
        <TouchableOpacity
          onPress={onPress}
          style={{
            backgroundColor: "pink",
            padding: 10,
            borderRadius: 5,
            bottom: 50,
          }}
        >
          <Text>{"Animate!"}</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

export default App;
