# @duell10111/react-native-image-keyboard

[![NPM Version Badge](https://badge.fury.io/js/@duell10111%2Freact-native-image-keyboard.svg)](https://www.npmjs.com/package/@duell10111/react-native-image-keyboard)

iOS             |  Android
:--------------:|:----------------:
![iOS Example GIF](https://media.giphy.com/media/U3n3eDqxQDYpfEPwgz/giphy.gif)  |  ![Android Example GIF](https://media.giphy.com/media/dxsUmlobx4vpXhpnf5/giphy.gif)

## About this package

This package extends React Native's TextInput component to enable keyboard image input on:
- Android (e.g. Gboard GIFs)
- iOS (e.g. Pasting images copied to the clipboard)

## Getting started

`$ npm install react-native-image-keyboard --save`

### Mostly automatic installation

`$ react-native link react-native-image-keyboard` (RN < 0.60)

`$ cd ios/ && pod install`

## Usage
```javascript
import {TextInput} from 'react-native';

const App = () => {
  const _onImageChange = (event) => {
    const {uri, linkUri, mime, data} = event.nativeEvent;

    // Do something with this data
  }

  return <TextInput onImageChange={_onImageChange} />;
}
```

## Credits

Original logic from [Gustash] (https://github.com/Gustash/react-native-image-keyboard)

Android logic based on [stwiname](https://github.com/stwiname)'s PR: https://github.com/facebook/react-native/pull/26088
