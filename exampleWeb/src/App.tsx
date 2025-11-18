import React, {useEffect, useState, useCallback} from 'react';
import {
  SafeAreaView,
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  useWindowDimensions,
} from 'react-native';
import {
  ERNHoleViewTimingFunction,
  IRNHoleViewAnimation,
  RNHole,
  RNHoleView,
} from 'react-native-hole-view';

const firstHole: RNHole = {
  x: 150,
  y: 350,
  width: 120,
  height: 120,
  borderRadius: 60,
};
const secondHole: RNHole = {
  x: 150,
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
    undefined,
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
    <SafeAreaView style={styles.root}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Text style={styles.title}>react-native-hole-view (web)</Text>
        <Text style={styles.description}>
          Click the button to move the highlight between cards. The overlay is rendered with the new web
          implementation that mirrors the native behavior.
        </Text>

        <View style={styles.cards}>
          {['Account', 'Deposit', 'Statistics'].map((label) => (
            <View key={label} style={styles.card}>
              <Text style={styles.cardTitle}>{label}</Text>
              <Text style={styles.cardCopy}>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</Text>
            </View>
          ))}
        </View>
      </ScrollView>

      <RNHoleView
        pointerEvents="box-none"
        style={{
                    position: 'absolute',
          width: '100%',
          height: '100%',
          backgroundColor: 'rgba(34,146,231,0.4)',
        }}
        holes={holes}
        animation={{
          timingFunction: ERNHoleViewTimingFunction.EASE_IN_OUT,
          duration: 220,
        }}
        onAnimationFinished={() => {
          // no-op, just demonstrating the prop
        }}
      >
        <View style={{
          width: 400,
          height: 400,
          backgroundColor: 'blue',
        }} />
      </RNHoleView>
              <TouchableOpacity
          onPress={onPress}
          style={{
            backgroundColor: 'pink',
            padding: 10,
            borderRadius: 5,
            bottom: 50,
          }}
        >
          <Text>{'Animate!'}</Text>
        </TouchableOpacity>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: '#080c16',
  },
  scrollContent: {
    paddingHorizontal: 24,
    paddingTop: 60,
    paddingBottom: 120,
    gap: 24,
  },
  title: {
    fontSize: 28,
    fontWeight: '600',
    color: '#f5f5ff',
  },
  description: {
    fontSize: 16,
    color: '#d2d6f4',
    lineHeight: 22,
  },
  cards: {
    gap: 20,
  },
  card: {
    backgroundColor: '#121a2c',
    padding: 20,
    borderRadius: 18,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: '#1f2940',
  },
  cardTitle: {
    fontSize: 20,
    color: '#fefefe',
    marginBottom: 6,
    fontWeight: '600',
  },
  cardCopy: {
    color: '#aeb4d8',
  },
  button: {
    alignSelf: 'flex-start',
    backgroundColor: '#4f7cff',
    paddingHorizontal: 20,
    paddingVertical: 12,
    borderRadius: 999,
  },
  buttonLabel: {
    color: '#fff',
    fontWeight: '600',
    fontSize: 15,
  },
});

export default App;

