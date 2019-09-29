import React, { Component } from 'react'
import {View, Text, TouchableOpacity} from 'react-native'
import {NativeModules} from 'react-native'
const authenticator = NativeModules.BioAuth

class App extends Component {
constructor(props) {
  super(props)
console.log('NativeModules', NativeModules)
}

  render () {
    return (
      <View style={{flex: 1, alignItems: 'center', justifyContent: 'center', }}>
        <TouchableOpacity
          style={{alignItems: 'center', justifyContent: 'center', width: 120, height: 50, borderRadius: 5, backgroundColor: 'grey', }}
          onPress={() => authenticator.authenticate()}
          >
          <Text style={{fontSize: 14}}>
            Authanticate
          </Text>
        </TouchableOpacity>
      </View>
    )
  }
}

export default App
