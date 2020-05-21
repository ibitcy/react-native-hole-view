# react-native-hole-view

Component to made an overlay with touch-through holes!
You can use to make tutorials overlay.

![Screenshot](misc/demo1.gif)

## Getting started

Install the library using either Yarn:

```
yarn add react-native-hole-view
```

or npm:

```
npm install --save react-native-hole-view
```

## Linking

This library fully supports RN's autolinking

## Usage

```js
import { RNHoleView } from 'react-native-hole-view';

function SomeComponent() {
  return (
    <RNHoleView
      style={{ position: 'absolute', width: '100%', height: '100%', backgroundColor: COLORS.BLACK.alpha(0.6) }}
      holes={[{ x: 150, y: 400, width: 120, height: 120, cornerRadius: 60 }]}
    />
  );
}
```

