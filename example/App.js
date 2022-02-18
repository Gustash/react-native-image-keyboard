/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useState, useCallback} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  StatusBar,
  Platform,
  TextInput,
  Image,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';

const App: () => React$Node = () => {
  const [selectedMediaUri, setSelectedMediaUri] = useState<?String>(null);

  const _onImageChange = useCallback(({nativeEvent}) => {
    const {uri} = nativeEvent;

    setSelectedMediaUri(uri);
  }, []);

  return (
    <View style={styles.container}>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView style={styles.container}>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          contentContainerStyle={styles.scrollViewContent}
          style={styles.scrollView}>
          <View style={styles.body}>
            <View style={styles.mediaContainer}>
              {selectedMediaUri && (
                <Image source={{uri: selectedMediaUri}} style={styles.image} />
              )}
            </View>
            <TextInput
              onImageChange={_onImageChange}
              placeholder={Platform.select({
                ios: 'Try to paste an image!',
                android: 'Try to use a GIF from your keyboard!',
              })}
            />
          </View>
        </ScrollView>
      </SafeAreaView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  scrollViewContent: {
    flexGrow: 1,
  },
  mediaContainer: {
    flex: 1,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    flex: 1,
    backgroundColor: Colors.white,
  },
  image: {
    width: '100%',
    aspectRatio: 1,
  },
});

export default App;